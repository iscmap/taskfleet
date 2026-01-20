package com.mario.alba.taskfleet.notifications.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.alba.taskfleet.contracts.OutboxEventMessage;
import com.mario.alba.taskfleet.notifications.infra.jpa.ProcessedEventEntity;
import com.mario.alba.taskfleet.notifications.infra.repo.ProcessedEventRepository;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Component
public class OrderEventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsConsumer.class);

    private final SqsClient sqs;
    private final ObjectMapper mapper;
    private final ProcessedEventRepository processedRepo;

    private final String queueName;
    private final int maxMessages;
    private final int waitTimeSeconds;

    private volatile String queueUrl;

    public OrderEventsConsumer(
            SqsClient sqs,
            ObjectMapper mapper,
            ProcessedEventRepository processedRepo,
            @Value("${aws.sqs.orderEventsQueue}") String queueName,
            @Value("${consumer.maxMessages:5}") int maxMessages,
            @Value("${consumer.waitTimeSeconds:10}") int waitTimeSeconds
    ) {
        this.sqs = sqs;
        this.mapper = mapper;
        this.processedRepo = processedRepo;
        this.queueName = queueName;
        this.maxMessages = maxMessages;
        this.waitTimeSeconds = waitTimeSeconds;
    }

    @Scheduled(fixedDelayString = "${consumer.pollDelayMs:1000}")
    public void poll() {
        if (queueUrl == null) {
            queueUrl = sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        }

        var resp = sqs.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .build());

        for (var msg : resp.messages()) {
            try {
                handleMessage(msg);
                // delete only after successful processing
                sqs.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build());
            } catch (Exception e) {
                log.error("Failed processing messageId={}. Will retry by not deleting.", msg.messageId(), e);
            }
        }
    }

    @Transactional
    void handleMessage(Message msg) throws Exception {
        OutboxEventMessage event = mapper.readValue(msg.body(), OutboxEventMessage.class);

        // Idempotency check
        if (processedRepo.findByEventId(event.eventId()).isPresent()) {
            log.info("Duplicate event ignored. eventId={}", event.eventId());
            return;
        }

        // "Business action" for demo: just log it
        log.info("Consumed event: type={} eventId={} payload={}",
                event.eventType(), event.eventId(), event.payload());

        // mark processed
        ProcessedEventEntity rec = new ProcessedEventEntity();
        rec.setId(UUID.randomUUID());
        rec.setEventId(event.eventId());
        rec.setProcessedAt(Instant.now());
        processedRepo.save(rec);
    }
}
