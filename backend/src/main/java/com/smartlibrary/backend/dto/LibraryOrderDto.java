package com.smartlibrary.backend.dto;

import com.smartlibrary.backend.entity.enums.OrderStatus;
import com.smartlibrary.backend.entity.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LibraryOrderDto {
    private Long id;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "total is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "total must be at least 0")
    private Double total;

    @NotNull(message = "status is required")
    private OrderStatus status;

    @NotNull(message = "type is required")
    private OrderType type;

    @Size(max = 255, message = "barcode must be at most 255 characters")
    private String barcode;
    private LocalDate rentalStartDate;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
