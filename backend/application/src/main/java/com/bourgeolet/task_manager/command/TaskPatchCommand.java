package com.bourgeolet.task_manager.command;

import com.bourgeolet.task_manager.dto.task.TaskStatus;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Objects;

@Builder
@Jacksonized
public record TaskPatchCommand(

        Long taskId,
        String actor,

        Boolean statusPresent,
        TaskStatus status,

        Boolean titlePresent,
        String title,

        Boolean descriptionPresent,
        String description,

        Boolean priorityPresent,
        Integer priority,

        Boolean tagsPresent,
        List<Long> tagIds,

        Boolean userAffecteePresent,
        String userAffectee

) {
    public TaskPatchCommand {
        Objects.requireNonNull(taskId, "taskId is required");
        Objects.requireNonNull(actor, "actor is required");
    }
}
