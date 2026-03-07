package com.smartlibrary.backend.dto.analytics;

public class InventoryMetricsDto {
    private long totalBooks;
    private long availableStock;
    private long rentedBooks;
    private long soldBooks;

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(long availableStock) {
        this.availableStock = availableStock;
    }

    public long getRentedBooks() {
        return rentedBooks;
    }

    public void setRentedBooks(long rentedBooks) {
        this.rentedBooks = rentedBooks;
    }

    public long getSoldBooks() {
        return soldBooks;
    }

    public void setSoldBooks(long soldBooks) {
        this.soldBooks = soldBooks;
    }
}
