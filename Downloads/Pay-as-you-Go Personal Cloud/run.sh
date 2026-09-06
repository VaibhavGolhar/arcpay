#!/usr/bin/env bash
# ============================================================================
#  run.sh — Starts the AWS Personal Cloud (backend + frontend)
#
#  Usage:
#    1. Export required environment variables (or create a .env file)
#    2. Run: ./run.sh
#
#  The script will validate all prerequisites before starting anything.
# ============================================================================

set -euo pipefail

# ── Colors for output ───────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/aws-personal-cloud-backend"
FRONTEND_DIR="$SCRIPT_DIR/aws-personal-cloud-frontend"

# ── Load .env file if present ───────────────────────────────────────────────
if [ -f "$SCRIPT_DIR/.env" ]; then
  echo -e "${CYAN}📄 Loading environment variables from .env${NC}"
  set -a
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/.env"
  set +a
fi

# ── Helper functions ────────────────────────────────────────────────────────
fail=0

check_env() {
  local var_name="$1"
  local description="$2"
  local required="${3:-true}"

  if [ -z "${!var_name:-}" ]; then
    if [ "$required" = "true" ]; then
      echo -e "  ${RED}✗ $var_name${NC} — $description"
      fail=1
    else
      echo -e "  ${YELLOW}⚠ $var_name${NC} — $description (optional, has default)"
    fi
  else
    echo -e "  ${GREEN}✓ $var_name${NC} — set"
  fi
}

check_tool() {
  local tool_name="$1"
  local install_hint="$2"

  if command -v "$tool_name" &>/dev/null; then
    local version
    version=$("$tool_name" --version 2>&1 | head -1)
    echo -e "  ${GREEN}✓ $tool_name${NC} — $version"
  else
    echo -e "  ${RED}✗ $tool_name${NC} — NOT FOUND. Install: $install_hint"
    fail=1
  fi
}

# ── Step 1: Check required tools ────────────────────────────────────────────
echo ""
echo -e "${BOLD}═══════════════════════════════════════════════════${NC}"
echo -e "${BOLD}  AWS Personal Cloud — Startup Validator${NC}"
echo -e "${BOLD}═══════════════════════════════════════════════════${NC}"
echo ""

echo -e "${CYAN}[1/4] Checking required tools...${NC}"
check_tool "java"   "Install Java 17+ (https://adoptium.net)"
check_tool "node"   "Install Node.js 18+ (https://nodejs.org)"
check_tool "npm"    "Comes with Node.js"
check_tool "psql"   "Install PostgreSQL (brew install postgresql)"

# Check Java version is 17+
if command -v java &>/dev/null; then
  java_version=$(java -version 2>&1 | head -1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
  if [ "$java_version" -lt 17 ] 2>/dev/null; then
    echo -e "  ${RED}✗ Java version must be 17 or higher (found: $java_version)${NC}"
    fail=1
  fi
fi

echo ""

# ── Step 2: Check project directories ───────────────────────────────────────
echo -e "${CYAN}[2/4] Checking project directories...${NC}"

if [ -d "$BACKEND_DIR" ]; then
  echo -e "  ${GREEN}✓ Backend directory${NC} — $BACKEND_DIR"
else
  echo -e "  ${RED}✗ Backend directory not found at${NC} $BACKEND_DIR"
  fail=1
fi

if [ -d "$FRONTEND_DIR" ]; then
  echo -e "  ${GREEN}✓ Frontend directory${NC} — $FRONTEND_DIR"
else
  echo -e "  ${RED}✗ Frontend directory not found at${NC} $FRONTEND_DIR"
  fail=1
fi

echo ""

# ── Step 3: Check environment variables ─────────────────────────────────────
echo -e "${CYAN}[3/4] Checking environment variables...${NC}"
echo ""
echo -e "  ${BOLD}AWS Credentials (REQUIRED)${NC}"
check_env "AWS_ACCESS_KEY_ID"     "Your IAM Access Key ID from AWS Console"
check_env "AWS_SECRET_ACCESS_KEY" "Your IAM Secret Access Key from AWS Console"
check_env "S3_BUCKET_NAME"        "The name of your S3 bucket (e.g. my-cloud-bucket)"
check_env "AWS_REGION"            "AWS region where your S3 bucket lives (e.g. us-east-1)" "false"

echo ""
echo -e "  ${BOLD}Database (optional — defaults to local PostgreSQL)${NC}"
check_env "DB_HOST"     "PostgreSQL host (default: localhost)"    "false"
check_env "DB_PORT"     "PostgreSQL port (default: 5432)"         "false"
check_env "DB_NAME"     "Database name (default: personal_cloud)" "false"
check_env "DB_USERNAME" "Database user (default: postgres)"       "false"
check_env "DB_PASSWORD" "Database password (default: postgres)"   "false"

echo ""
echo -e "  ${BOLD}Security (optional — has default for dev)${NC}"
check_env "JWT_SECRET"        "Secret key for signing JWTs (min 32 chars)" "false"
check_env "JWT_EXPIRATION_MS" "Token expiry in ms (default: 86400000)"     "false"

echo ""

# ── Step 4: Check reachability (Database & S3) ──────────────────────────────
echo -e "${CYAN}[4/4] Checking resource reachability...${NC}"

db_host="${DB_HOST:-localhost}"
db_port="${DB_PORT:-5432}"
db_name="${DB_NAME:-personal_cloud}"
db_user="${DB_USERNAME:-postgres}"

# Check Database Reachability
if command -v psql &>/dev/null; then
  echo -e "  Testing PostgreSQL connection to ${db_host}:${db_port}..."
  if PGPASSWORD="${DB_PASSWORD:-postgres}" psql -h "$db_host" -p "$db_port" -U "$db_user" -d "$db_name" -c "SELECT 1;" &>/dev/null; then
    echo -e "  ${GREEN}✓ PostgreSQL${NC} — Connected successfully!"
  else
    # Try connecting without specifying database (it might not exist yet)
    if PGPASSWORD="${DB_PASSWORD:-postgres}" psql -h "$db_host" -p "$db_port" -U "$db_user" -d "postgres" -c "SELECT 1;" &>/dev/null; then
      echo -e "  ${YELLOW}⚠ PostgreSQL${NC} — Server reachable but database '${db_name}' may not exist."
      echo -e "    Creating database '${db_name}'..."
      PGPASSWORD="${DB_PASSWORD:-postgres}" psql -h "$db_host" -p "$db_port" -U "$db_user" -d "postgres" -c "CREATE DATABASE \"${db_name}\";" 2>/dev/null || true
      echo -e "  ${GREEN}✓ Database '${db_name}' created (or already exists)${NC}"
    else
      echo -e "  ${RED}✗ Cannot connect to PostgreSQL at ${db_host}:${db_port}${NC}"
      echo -e "    Check your DB_HOST, DB_USERNAME, and DB_PASSWORD."
      fail=1
    fi
  fi
else
  echo -e "  ${YELLOW}⚠ psql CLI not found. Skipping database reachability check.${NC}"
fi

# Check S3 Reachability
if command -v aws &>/dev/null; then
  echo -e "  Testing S3 bucket reachability for '${S3_BUCKET_NAME}'..."
  # Use AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY automatically picked up by AWS CLI
  if aws s3api head-bucket --bucket "$S3_BUCKET_NAME" 2>/dev/null; then
    echo -e "  ${GREEN}✓ AWS S3${NC} — Bucket is reachable!"
  else
    echo -e "  ${RED}✗ Cannot access S3 bucket '${S3_BUCKET_NAME}'${NC}"
    echo -e "    Check your AWS credentials, bucket name, and ensure the IAM user has access."
    fail=1
  fi
else
  echo -e "  ${YELLOW}⚠ AWS CLI not found. Skipping S3 reachability check.${NC}"
fi

echo ""

# ── Abort if any checks failed ──────────────────────────────────────────────
if [ "$fail" -ne 0 ]; then
  echo -e "${RED}═══════════════════════════════════════════════════${NC}"
  echo -e "${RED}  ✗ Pre-flight checks FAILED. Fix the errors above.${NC}"
  echo -e "${RED}═══════════════════════════════════════════════════${NC}"
  echo ""
  echo -e "  ${BOLD}Quick fix:${NC} Create a ${CYAN}.env${NC} file in this directory with:"
  echo ""
  echo -e "    ${BOLD}# AWS S3 Credentials${NC}"
  echo -e "    ${YELLOW}AWS_ACCESS_KEY_ID=your_key_here${NC}"
  echo -e "    ${YELLOW}AWS_SECRET_ACCESS_KEY=your_secret_here${NC}"
  echo -e "    ${YELLOW}S3_BUCKET_NAME=your_bucket_name${NC}"
  echo ""
  echo -e "    ${BOLD}# PostgreSQL Database (e.g. AWS RDS)${NC}"
  echo -e "    ${YELLOW}DB_HOST=your-rds-endpoint.amazonaws.com${NC}"
  echo -e "    ${YELLOW}DB_PORT=5432${NC}"
  echo -e "    ${YELLOW}DB_NAME=postgres${NC}"
  echo -e "    ${YELLOW}DB_USERNAME=postgres${NC}"
  echo -e "    ${YELLOW}DB_PASSWORD=your_secure_password${NC}"
  echo ""
  exit 1
fi

echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  ✓ All pre-flight checks passed!${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo ""

# ── Trap: kill child processes on exit ──────────────────────────────────────
cleanup() {
  echo ""
  echo -e "${YELLOW}Shutting down...${NC}"
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
  wait "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
  echo -e "${GREEN}All processes stopped.${NC}"
}
trap cleanup EXIT INT TERM

# ── Start Backend ───────────────────────────────────────────────────────────
echo -e "${CYAN}🚀 Starting backend (Spring Boot on :8080)...${NC}"

cd "$BACKEND_DIR"

# Install dependencies and build (skip tests for faster startup)
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dserver.port=8080" &
BACKEND_PID=$!

echo -e "  Backend PID: $BACKEND_PID"
echo ""

# ── Start Frontend ──────────────────────────────────────────────────────────
echo -e "${CYAN}🚀 Starting frontend (React on :3000)...${NC}"

cd "$FRONTEND_DIR"

# Install dependencies if node_modules is missing
if [ ! -d "node_modules" ]; then
  echo -e "  ${YELLOW}Installing npm dependencies...${NC}"
  npm install --silent
fi

# Redirect frontend output to frontend.log so it doesn't clutter the terminal
PORT=3000 npm start > frontend.log 2>&1 &
FRONTEND_PID=$!

echo -e "  Frontend PID: $FRONTEND_PID"
echo ""

# ── Print summary ───────────────────────────────────────────────────────────
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  AWS Personal Cloud is starting up!${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo ""
echo -e "  ${BOLD}Frontend:${NC}  ${CYAN}http://localhost:3000${NC}"
echo -e "  ${BOLD}Backend:${NC}   ${CYAN}http://localhost:8080${NC}"
echo -e "  ${BOLD}Swagger:${NC}   ${CYAN}http://localhost:8080/swagger-ui.html${NC}"
echo ""
echo -e "  Press ${BOLD}Ctrl+C${NC} to stop both servers."
echo ""

# Wait for processes to exit
wait "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
