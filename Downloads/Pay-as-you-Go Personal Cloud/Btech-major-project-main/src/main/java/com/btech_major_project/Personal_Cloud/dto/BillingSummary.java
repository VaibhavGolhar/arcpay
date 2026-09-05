package com.btech_major_project.Personal_Cloud.dto;

import java.math.BigDecimal;

public class BillingSummary {
    private long storageBytes;
    private BigDecimal storageGb;
    private BigDecimal storageCost;
    private long writeRequests;
    private BigDecimal writeCost;
    private long readRequests;
    private BigDecimal readCost;
    private BigDecimal total;
    private String currency;

    public static BillingSummary empty() {
        BillingSummary s = new BillingSummary();
        s.storageBytes = 0L;
        s.storageGb = BigDecimal.ZERO.setScale(6);
        s.storageCost = BigDecimal.ZERO.setScale(6);
        s.writeRequests = 0L;
        s.writeCost = BigDecimal.ZERO.setScale(6);
        s.readRequests = 0L;
        s.readCost = BigDecimal.ZERO.setScale(6);
        s.total = BigDecimal.ZERO.setScale(6);
        s.currency = "USD";
        return s;
    }

    public long getStorageBytes() { return storageBytes; }
    public void setStorageBytes(long storageBytes) { this.storageBytes = storageBytes; }
    public BigDecimal getStorageGb() { return storageGb; }
    public void setStorageGb(BigDecimal storageGb) { this.storageGb = storageGb; }
    public BigDecimal getStorageCost() { return storageCost; }
    public void setStorageCost(BigDecimal storageCost) { this.storageCost = storageCost; }
    public long getWriteRequests() { return writeRequests; }
    public void setWriteRequests(long writeRequests) { this.writeRequests = writeRequests; }
    public BigDecimal getWriteCost() { return writeCost; }
    public void setWriteCost(BigDecimal writeCost) { this.writeCost = writeCost; }
    public long getReadRequests() { return readRequests; }
    public void setReadRequests(long readRequests) { this.readRequests = readRequests; }
    public BigDecimal getReadCost() { return readCost; }
    public void setReadCost(BigDecimal readCost) { this.readCost = readCost; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
