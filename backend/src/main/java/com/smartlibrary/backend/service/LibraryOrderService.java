package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.LibraryOrderDto;
import com.smartlibrary.backend.entity.LibraryOrder;
import com.smartlibrary.backend.entity.Payment;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.entity.enums.OrderStatus;
import com.smartlibrary.backend.entity.enums.OrderType;
import com.smartlibrary.backend.entity.enums.PaymentStatus;
import com.smartlibrary.backend.repository.LibraryOrderRepository;
import com.smartlibrary.backend.repository.PaymentRepository;
import com.smartlibrary.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LibraryOrderService {

    private final LibraryOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public LibraryOrderService(
            LibraryOrderRepository orderRepository, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<LibraryOrderDto> findAll() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    public LibraryOrderDto findById(Long id) {
        return toDto(getOrder(id));
    }

    public LibraryOrderDto create(LibraryOrderDto dto) {
        LibraryOrder order = new LibraryOrder();
        apply(order, dto);
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        if (isBlank(order.getBarcode())) {
            order.setBarcode(generateUniqueBarcode());
        }
        return toDto(orderRepository.save(order));
    }

    public LibraryOrderDto update(Long id, LibraryOrderDto dto) {
        LibraryOrder order = getOrder(id);
        apply(order, dto);
        return toDto(orderRepository.save(order));
    }

    public void delete(Long id) {
        orderRepository.delete(getOrder(id));
    }

    public LibraryOrderDto cancelOrder(Long id) {
        LibraryOrder order = getOrder(id);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalArgumentException("Only CREATED orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toDto(orderRepository.save(order));
    }

    public LibraryOrderDto requestRefund(Long id) {
        LibraryOrder order = getOrder(id);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("Only PAID orders can request refund");
        }
        order.setStatus(OrderStatus.REFUND_REQUESTED);
        return toDto(orderRepository.save(order));
    }

    public LibraryOrderDto approveRefund(Long id) {
        LibraryOrder order = getOrder(id);
        if (order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new IllegalArgumentException("Order must be REFUND_REQUESTED before approval");
        }
        order.setStatus(OrderStatus.REFUNDED);
        LibraryOrder saved = orderRepository.save(order);
        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        for (Payment payment : payments) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
        if (!payments.isEmpty()) {
            paymentRepository.saveAll(payments);
        }
        return toDto(saved);
    }

    public LibraryOrderDto findByBarcode(String barcode) {
        LibraryOrder order = orderRepository.findByBarcode(barcode.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found for barcode"));
        return toDto(order);
    }

    private LibraryOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private void apply(LibraryOrder order, LibraryOrderDto dto) {
        if (dto.getUserId() != null) {
            order.setUser(getUser(dto.getUserId()));
        } else {
            order.setUser(null);
        }
        order.setTotal(dto.getTotal());
        order.setStatus(dto.getStatus());
        order.setType(dto.getType());
        order.setBarcode(normalize(dto.getBarcode()));
        order.setRentalStartDate(dto.getRentalStartDate());
        order.setDueDate(dto.getDueDate());
        order.setCreatedAt(dto.getCreatedAt());

        validateRentalFields(order);
    }

    private LibraryOrderDto toDto(LibraryOrder order) {
        LibraryOrderDto dto = new LibraryOrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setType(order.getType());
        dto.setBarcode(order.getBarcode());
        dto.setRentalStartDate(order.getRentalStartDate());
        dto.setDueDate(order.getDueDate());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    private void validateRentalFields(LibraryOrder order) {
        if (order.getType() == OrderType.RENT) {
            if (order.getRentalStartDate() == null || order.getDueDate() == null) {
                throw new IllegalArgumentException("RENT orders require rentalStartDate and dueDate");
            }
            if (order.getDueDate().isBefore(order.getRentalStartDate())) {
                throw new IllegalArgumentException("dueDate must be on or after rentalStartDate");
            }
            return;
        }
        order.setRentalStartDate(null);
        order.setDueDate(null);
    }

    private String generateUniqueBarcode() {
        for (int i = 0; i < 10; i++) {
            String value = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (orderRepository.findByBarcode(value).isEmpty()) {
                return value;
            }
        }
        throw new IllegalStateException("Could not generate unique barcode");
    }

    private String normalize(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
