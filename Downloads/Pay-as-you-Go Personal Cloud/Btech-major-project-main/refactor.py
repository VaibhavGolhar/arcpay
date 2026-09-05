import os
import re

base_dir = "src/main/java/com/btech_major_project/Personal_Cloud"
test_dir = "src/test/java/com/btech_major_project/Personal_Cloud"

domains = {
    "auth": ["AuthController.java", "AuthService.java", "CustomUserDetailsService.java", "UserDetailsImpl.java"],
    "security": ["SecurityConfig.java", "JwtAuthenticationFilter.java", "JwtService.java"],
    "storage": ["FileController.java", "S3StorageService.java", "StorageService.java", "FileMetadata.java", "FileMetadataRepository.java"],
    "billing": ["BillingController.java", "StandardBillingService.java", "BillingService.java"],
    "usage": ["UsageService.java", "UserUsage.java", "UserUsageRepository.java"],
    "user": ["UserService.java", "User.java", "UserRepository.java"],
    "common": ["AppLogger.java", "GlobalExceptionHandler.java", "S3Client.java", "ApiError.java"],
    "dto": ["BillingSummary.java", "FileDownloadResult.java", "UserProfileResponse.java"]
}

# Ensure dto is empty or moved
for root, dirs, files in os.walk(os.path.join(base_dir, "dto")):
    for file in files:
        domains["dto"].append(file)
domains["dto"] = list(set(domains["dto"]))

# Create domain directories
for d in domains:
    os.makedirs(os.path.join(base_dir, d), exist_ok=True)
    os.makedirs(os.path.join(test_dir, d), exist_ok=True)

# Map class to package
class_to_pkg = {}
for d, files in domains.items():
    for f in files:
        cls = f.replace(".java", "")
        class_to_pkg[cls] = f"com.btech_major_project.Personal_Cloud.{d}"

def process_file(filepath, current_domain):
    with open(filepath, 'r') as f:
        content = f.read()

    # Change package declaration
    if current_domain:
        new_pkg = f"package com.btech_major_project.Personal_Cloud.{current_domain};"
    else:
        new_pkg = "package com.btech_major_project.Personal_Cloud;"
        
    # Replace anything that starts with package com.btech_major_project.Personal_Cloud
    content = re.sub(r'package com\.btech_major_project\.Personal_Cloud[a-zA-Z0-9_\.]*;', new_pkg, content)

    # Find used classes
    used_classes = set()
    for cls in class_to_pkg.keys():
        # Match class names only if they aren't part of another word
        if re.search(r'\b' + cls + r'\b', content):
            used_classes.add(cls)
            
    # Add imports
    import_block = ""
    for cls in used_classes:
        pkg = class_to_pkg[cls]
        if current_domain and pkg == f"com.btech_major_project.Personal_Cloud.{current_domain}":
            continue # Same package
        if not current_domain and pkg == "com.btech_major_project.Personal_Cloud":
            continue
        import_stmt = f"import {pkg}.{cls};\n"
        if import_stmt not in content:
            import_block += import_stmt
            
    # Insert imports after package
    if import_block:
        content = content.replace(new_pkg, new_pkg + "\n\n" + import_block.strip())

    with open(filepath, 'w') as f:
        f.write(content)

# Move and process files
all_files = [f for f in os.listdir(base_dir) if f.endswith(".java")]
for f in all_files:
    domain = None
    for d, files in domains.items():
        if f in files:
            domain = d
            break
            
    src = os.path.join(base_dir, f)
    if domain:
        if domain != "dto":
            dst = os.path.join(base_dir, domain, f)
            os.rename(src, dst)
            process_file(dst, domain)
        else:
            # dto already moved by earlier refactor, just process
            pass
    else:
        process_file(src, None)

# Process DTO files that are already in dto/
for f in os.listdir(os.path.join(base_dir, "dto")):
    if f.endswith(".java"):
        process_file(os.path.join(base_dir, "dto", f), "dto")

# Process tests
test_files = [f for f in os.listdir(test_dir) if f.endswith(".java")]
for f in test_files:
    # Find matching domain by removing 'Test' from the end
    base_class = f.replace("Test.java", ".java")
    domain = None
    for d, files in domains.items():
        if base_class in files:
            domain = d
            break
            
    src = os.path.join(test_dir, f)
    if domain:
        dst = os.path.join(test_dir, domain, f)
        os.rename(src, dst)
        process_file(dst, domain)
    else:
        process_file(src, None)

print("Refactoring complete.")
