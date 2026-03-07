package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.PaymentDto;
import com.smartlibrary.backend.entity.LibraryOrder;
import com.smartlibrary.backend.entity.Payment;
import com.smartlibrary.backend.repository.LibraryOrderRepository;
import com.smartlibrary.backend.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LibraryOrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, LibraryOrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(this::toDto).toList();
    }

    public PaymentDto findById(Long id) {
        return toDto(getPayment(id));
    }

    public PaymentDto create(PaymentDto dto) {
        Payment payment = new Payment();
        apply(payment, dto);
        return toDto(paymentRepository.save(payment));
    }

    public PaymentDto update(Long id, PaymentDto dto) {
        Payment payment = getPayment(id);
        apply(payment, dto);
        return toDto(paymentRepository.save(payment));
    }

    public void delete(Long id) {
        paymentRepository.delete(getPayment(id));
    }

    private Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + id));
    }

    private LibraryOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    private void apply(Payment payment, PaymentDto dto) {
        if (dto.getOrderId() != null) {
            payment.setOrder(getOrder(dto.getOrderId()));
        } else {
            payment.setOrder(null);
        }
        payment.setProvider(dto.getProvider());
        payment.setProviderPaymentId(dto.getProviderPaymentId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setCreatedAt(dto.getCreatedAt());
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getId() : null);
        dto.setProvider(payment.getProvider());
        dto.setProviderPaymentId(payment.getProviderPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
