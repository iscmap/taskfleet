package com.mario.alba.taskfleet.orders.application;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestHasher {

    private final ObjectMapper mapper;

    public RequestHasher(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String stableJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request for idempotency", e);
        }
    }

    public String sha256(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash idempotency request", e);
        }
    }
}
