package com.mario.alba.taskfleet.orders.infra.jpa;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "sku", nullable = false, length = 64)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
