package com.mario.alba.taskfleet.contracts;

public class OrderContract {
    public record OrderCreatedEvent(String orderId, String email) {}
}
