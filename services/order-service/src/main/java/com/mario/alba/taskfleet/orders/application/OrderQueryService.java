package com.mario.alba.taskfleet.orders.application;

import com.mario.alba.taskfleet.orders.api.dto.OrderView;
import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import com.mario.alba.taskfleet.orders.infra.repo.OrderRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderQueryService {

    private final OrderRepository orders;

    public OrderQueryService(OrderRepository orders) {
        this.orders = orders;
    }

    public OrderView getOrder(UUID orderId) {
        var order = orders.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderMappers.toView(order);
    }

    public List<OrderView> search(String email, OrderStatus status) {
        if (email != null && !email.isBlank() && status != null) {
            return orders.findByCustomerEmailAndStatus(email, status.name()).stream().map(OrderMappers::toView).toList();
        }
        if (email != null && !email.isBlank()) {
            return orders.findByCustomerEmail(email).stream().map(OrderMappers::toView).toList();
        }
        if (status != null) {
            return orders.findByStatus(status.name()).stream().map(OrderMappers::toView).toList();
        }
        return orders.findAll().stream().map(OrderMappers::toView).toList();
    }
}
