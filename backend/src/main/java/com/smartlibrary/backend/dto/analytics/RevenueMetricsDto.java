package com.smartlibrary.backend.dto.analytics;

public class RevenueMetricsDto {
    private double totalRevenue;
    private double dailyRevenue;
    private long purchaseCount;
    private long rentalCount;

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(double dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public long getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public long getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(long rentalCount) {
        this.rentalCount = rentalCount;
    }
}
