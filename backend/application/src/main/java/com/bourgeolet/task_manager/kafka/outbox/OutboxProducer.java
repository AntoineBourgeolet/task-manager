package com.bourgeolet.task_manager.kafka.outbox;

import com.bourgeolet.task_manager.entity.Outbox;
import com.bourgeolet.task_manager.repository.OutboxRepository;
import com.bourgeolet.task_manager.service.OutboxService;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Profile("!test")
public class OutboxProducer {


    private final KafkaTemplate<String, String> kafkaTemplate;

    private final OutboxRepository outboxRepository;

    private final OutboxService outboxService;

    private static final String TOPIC = "outbox.events";

    public OutboxProducer(KafkaTemplate<String, String> kafkaTemplate, OutboxRepository outboxRepository, OutboxService outboxService) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.outboxService = outboxService;
    }

    @Scheduled(fixedDelayString = "PT2S")
    @Transactional
    public void publishPending() {
        List<Outbox> outboxList = outboxRepository.getNextBatch(200).orElse(Collections.emptyList());
        for (Outbox e : outboxList) {
            String id = e.getAggregateId().toString();
            kafkaTemplate.send(TOPIC, id, e.getPayload())
                    .whenComplete((metadata, ex) -> {
                        if (ex == null) {
                            outboxService.markPublishedAsync(e.getId());
                        }
                    });
        }
    }

}
