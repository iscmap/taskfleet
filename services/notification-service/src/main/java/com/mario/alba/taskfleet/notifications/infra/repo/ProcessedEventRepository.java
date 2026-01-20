package com.mario.alba.taskfleet.notifications.infra.repo;

import com.mario.alba.taskfleet.notifications.infra.jpa.ProcessedEventEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    Optional<ProcessedEventEntity> findByEventId(UUID eventId);
}
