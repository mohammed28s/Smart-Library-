package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.LibraryOrderDto;
import com.smartlibrary.backend.entity.LibraryOrder;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.repository.LibraryOrderRepository;
import com.smartlibrary.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibraryOrderService {

    private final LibraryOrderRepository orderRepository;
    private final UserRepository userRepository;

    public LibraryOrderService(LibraryOrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
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
        order.setBarcode(dto.getBarcode());
        order.setCreatedAt(dto.getCreatedAt());
    }

    private LibraryOrderDto toDto(LibraryOrder order) {
        LibraryOrderDto dto = new LibraryOrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setType(order.getType());
        dto.setBarcode(order.getBarcode());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
