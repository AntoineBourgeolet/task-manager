package com.bourgeolet.task_manager.config.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@Profile("!test")
@SuppressWarnings("unused")
public class KafkaMarkerConfig {}
