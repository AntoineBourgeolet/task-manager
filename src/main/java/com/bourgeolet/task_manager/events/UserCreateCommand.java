package com.bourgeolet.task_manager.events;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class UserCreateCommand {


    private String eventId;
    private LocalTime occurredAt;
    private String source;
    private Payload data;

    @Getter
    @Setter
    public static class Payload {
        private String username;
        private String email;
    }

}