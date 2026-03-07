package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.OrderItemDto;
import com.smartlibrary.backend.entity.Book;
import com.smartlibrary.backend.entity.LibraryOrder;
import com.smartlibrary.backend.entity.OrderItem;
import com.smartlibrary.backend.repository.BookRepository;
import com.smartlibrary.backend.repository.LibraryOrderRepository;
import com.smartlibrary.backend.repository.OrderItemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final LibraryOrderRepository orderRepository;
    private final BookRepository bookRepository;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            LibraryOrderRepository orderRepository,
            BookRepository bookRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    public List<OrderItemDto> findAll() {
        return orderItemRepository.findAll().stream().map(this::toDto).toList();
    }

    public OrderItemDto findById(Long id) {
        return toDto(getOrderItem(id));
    }

    public OrderItemDto create(OrderItemDto dto) {
        OrderItem item = new OrderItem();
        apply(item, dto);
        return toDto(orderItemRepository.save(item));
    }

    public OrderItemDto update(Long id, OrderItemDto dto) {
        OrderItem item = getOrderItem(id);
        apply(item, dto);
        return toDto(orderItemRepository.save(item));
    }

    public void delete(Long id) {
        orderItemRepository.delete(getOrderItem(id));
    }

    private OrderItem getOrderItem(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found: " + id));
    }

    private LibraryOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
    }

    private void apply(OrderItem item, OrderItemDto dto) {
        if (dto.getOrderId() != null) {
            item.setOrder(getOrder(dto.getOrderId()));
        } else {
            item.setOrder(null);
        }
        if (dto.getBookId() != null) {
            item.setBook(getBook(dto.getBookId()));
        } else {
            item.setBook(null);
        }
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
    }

    private OrderItemDto toDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrder() != null ? item.getOrder().getId() : null);
        dto.setBookId(item.getBook() != null ? item.getBook().getId() : null);
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
