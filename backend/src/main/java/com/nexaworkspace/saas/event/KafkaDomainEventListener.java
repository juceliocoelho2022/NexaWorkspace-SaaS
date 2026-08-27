package com.nexaworkspace.saas.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "app.events.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaDomainEventListener {
    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventListener.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaDomainEventListener(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(DomainEvent event) {
        String key = event.tenantId() == null ? event.eventId().toString() : event.tenantId().toString();
        kafkaTemplate.send(topic, key, event).whenComplete((result, error) -> {
            if (error != null) {
                log.warn("Could not publish domain event {} to Kafka: {}", event.eventId(), error.getMessage());
            }
        });
    }
}
