package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.analytics.InventoryMetricsDto;
import com.smartlibrary.backend.dto.analytics.RevenueMetricsDto;
import com.smartlibrary.backend.entity.Book;
import com.smartlibrary.backend.entity.LibraryOrder;
import com.smartlibrary.backend.entity.OrderItem;
import com.smartlibrary.backend.entity.Payment;
import com.smartlibrary.backend.entity.enums.OrderStatus;
import com.smartlibrary.backend.entity.enums.OrderType;
import com.smartlibrary.backend.entity.enums.PaymentStatus;
import com.smartlibrary.backend.repository.BookRepository;
import com.smartlibrary.backend.repository.LibraryOrderRepository;
import com.smartlibrary.backend.repository.OrderItemRepository;
import com.smartlibrary.backend.repository.PaymentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final BookRepository bookRepository;
    private final LibraryOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public AnalyticsService(
            BookRepository bookRepository,
            LibraryOrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    public InventoryMetricsDto getInventoryMetrics() {
        InventoryMetricsDto dto = new InventoryMetricsDto();
        var books = bookRepository.findAll();
        var orders = orderRepository.findAll();
        var orderItems = orderItemRepository.findAll();

        dto.setTotalBooks(books.size());
        dto.setAvailableStock(books.stream().mapToLong(book -> safeInt(book.getStock())).sum());

        Map<Long, LibraryOrder> orderById = orders.stream().collect(Collectors.toMap(LibraryOrder::getId, Function.identity()));
        long soldBooks = quantityFor(orderItems, orderById, Set.of(OrderType.BUY));
        long rentedBooks = quantityFor(orderItems, orderById, Set.of(OrderType.RENT));
        dto.setSoldBooks(soldBooks);
        dto.setRentedBooks(rentedBooks);
        return dto;
    }

    public RevenueMetricsDto getRevenueMetrics() {
        RevenueMetricsDto dto = new RevenueMetricsDto();
        var payments = paymentRepository.findAll();
        var orders = orderRepository.findAll();
        LocalDate today = LocalDate.now();

        double totalRevenue = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCEEDED)
                .mapToDouble(payment -> payment.getAmount() == null ? 0.0 : payment.getAmount())
                .sum();
        double dailyRevenue = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCEEDED)
                .filter(payment -> payment.getCreatedAt() != null && today.equals(payment.getCreatedAt().toLocalDate()))
                .mapToDouble(payment -> payment.getAmount() == null ? 0.0 : payment.getAmount())
                .sum();
        long purchaseCount = orders.stream()
                .filter(order -> order.getType() == OrderType.BUY)
                .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REFUND_REQUESTED)
                .count();
        long rentalCount = orders.stream()
                .filter(order -> order.getType() == OrderType.RENT)
                .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REFUND_REQUESTED)
                .count();

        dto.setTotalRevenue(totalRevenue);
        dto.setDailyRevenue(dailyRevenue);
        dto.setPurchaseCount(purchaseCount);
        dto.setRentalCount(rentalCount);
        return dto;
    }

    private long quantityFor(List<OrderItem> orderItems, Map<Long, LibraryOrder> orderById, Set<OrderType> types) {
        return orderItems.stream()
                .filter(item -> item.getOrder() != null)
                .filter(item -> {
                    LibraryOrder order = orderById.get(item.getOrder().getId());
                    if (order == null) {
                        return false;
                    }
                    if (!types.contains(order.getType())) {
                        return false;
                    }
                    return order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REFUND_REQUESTED;
                })
                .mapToLong(item -> safeInt(item.getQuantity()))
                .sum();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
