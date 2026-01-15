package com.mario.alba.taskfleet.orders.infra.repo;

import com.mario.alba.taskfleet.orders.infra.jpa.IdempotencyKeyEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKeyEntity, UUID> {
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);
}
