package com.mario.alba.taskfleet.orders.infra;

import com.mario.alba.taskfleet.orders.infra.repo.OutboxRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class OutboxPublisher {

    private final OutboxRepository outbox;
    private final SqsClient sqs;
    private final String queueName;
    private final int batchSize;

    private volatile String queueUrl; // cached

    public OutboxPublisher(
            OutboxRepository outbox,
            SqsClient sqs,
            @Value("${aws.sqs.orderEventsQueue}") String queueName,
            @Value("${outbox.publish.batchSize:10}") int batchSize
    ) {
        this.outbox = outbox;
        this.sqs = sqs;
        this.queueName = queueName;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${outbox.publish.fixedDelayMs:1000}")
    @Transactional
    public void publish() {
        if (queueUrl == null) {
            queueUrl = sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        }

        var unpublished = outbox.findUnpublished();
        int sent = 0;

        for (var evt : unpublished) {
            if (sent >= batchSize) break;

            sqs.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(evt.getPayloadJson())
                            .build()
            );

            evt.setPublishedAt(Instant.now());
            // JPA will flush at commit
            sent++;
        }
    }
}
