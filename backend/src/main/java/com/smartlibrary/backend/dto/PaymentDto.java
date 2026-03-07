package com.smartlibrary.backend.dto;

import com.smartlibrary.backend.entity.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class PaymentDto {
    private Long id;

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotBlank(message = "provider is required")
    @Size(max = 100, message = "provider must be at most 100 characters")
    private String provider;

    @Size(max = 255, message = "providerPaymentId must be at most 255 characters")
    private String providerPaymentId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "amount must be at least 0")
    private Double amount;

    @NotNull(message = "status is required")
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public void setProviderPaymentId(String providerPaymentId) {
        this.providerPaymentId = providerPaymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
