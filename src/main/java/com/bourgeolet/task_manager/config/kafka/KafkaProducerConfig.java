package com.bourgeolet.task_manager.config.kafka;


import com.bourgeolet.task_manager.events.UserCreateCommand;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@SuppressWarnings("unused")
public class KafkaProducerConfig {


    private Map<String, Object> baseProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return config;
    }

    @Bean
    public ProducerFactory<@NotNull String, @NotNull String> outboxCreateProducerFactory() {
        Map<String, Object> config = new HashMap<>(baseProducerConfig());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "outbox-create-producer");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<@NotNull String, @NotNull String> outboxCreateKafkaTemplate(
            ProducerFactory<@NotNull String, @NotNull String> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}
