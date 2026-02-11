package com.bourgeolet.task_manager.model.user;


import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.events.UserCreateCommand;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<@NotNull String, @NotNull UserCreateCommand> kafkaTemplate;

    public void publishUserCreated(UserResponseDTO userResponseDTO) {
        UserCreateCommand evt = new UserCreateCommand();
        UserCreateCommand.Payload payload = new UserCreateCommand.Payload();
        payload.setUsername(userResponseDTO.username());
        payload.setEmail(userResponseDTO.email());
        evt.setData(payload);
        evt.setSource("User creating");
        evt.setOccurredAt(LocalTime.now());
        evt.setEventId("Creating-" + userResponseDTO.username());

        kafkaTemplate.send("user.events", userResponseDTO.username(), evt);
    }
}
