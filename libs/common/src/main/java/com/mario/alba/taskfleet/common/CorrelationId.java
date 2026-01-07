package com.mario.alba.taskfleet.common;

import java.util.UUID;

public final class CorrelationId {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
