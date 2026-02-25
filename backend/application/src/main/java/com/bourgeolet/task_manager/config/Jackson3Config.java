package com.bourgeolet.task_manager.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class Jackson3Config {
    @Bean
    public ObjectMapper jackson3ObjectMapper() {
        return new ObjectMapper();
    }
}
