package com.bourgeolet.task_manager.config.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Configuration
@EnableKafka
@SuppressWarnings("unused")
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<@NotNull String, @NotNull Object> consumerFactory(KafkaProperties props) {
        Map<String, Object> conf = props.buildConsumerProperties();

        var jsonMapper = JsonMapper.builder().build();
        try (var valueSerde = new JacksonJsonSerde<>(Object.class, jsonMapper)) {
            valueSerde.deserializer().trustedPackages("*");

            return new DefaultKafkaConsumerFactory<>(
                    conf,
                    new StringDeserializer(),
                    valueSerde.deserializer()
            );
        }
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<@NotNull String, @NotNull Object> kafkaListenerContainerFactory(
            ConsumerFactory<@NotNull String, @NotNull Object> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<@NotNull String, @NotNull Object>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}