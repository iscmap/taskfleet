package com.mario.alba.taskfleet.orders.infra.repo;

import com.mario.alba.taskfleet.orders.infra.jpa.OrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCustomerEmail(String customerEmail);
    List<OrderEntity> findByStatus(String status);
    List<OrderEntity> findByCustomerEmailAndStatus(String customerEmail, String status);
}
