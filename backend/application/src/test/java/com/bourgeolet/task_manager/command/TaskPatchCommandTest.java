package com.bourgeolet.task_manager.command;

import com.bourgeolet.task_manager.dto.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskPatchCommandTest {

    @Test
    void builder_whenTaskIdIsMissing_shouldThrowRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                TaskPatchCommand.builder()
                        .actor("actor")
                        .statusPresent(true)
                        .status(TaskStatus.DONE)
                        .build()
        );
        assertThat(ex).hasMessage("taskId is required");
    }

    @Test
    void builder_whenActorIsMissing_shouldThrowNullPointerException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                TaskPatchCommand.builder()
                        .taskId(1L)
                        .statusPresent(true)
                        .status(TaskStatus.DONE)
                        .build()
        );
        assertThat(ex).hasMessage("actor is required");
    }

    @Test
    void builder_whenAllFieldsProvided_shouldProduceImmutableRecord() {
        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(10L)
                .actor("actor")
                .statusPresent(true)
                .status(TaskStatus.BLOCKED)
                .titlePresent(true)
                .title("T")
                .descriptionPresent(true)
                .description("D")
                .priorityPresent(true)
                .priority(5)
                .tagsPresent(true)
                .tagIds(List.of(1L, 2L))
                .userAffecteePresent(true)
                .userAffectee("userX")
                .build();

        assertThat(cmd.taskId()).isEqualTo(10L);
        assertThat(cmd.actor()).isEqualTo("actor");
        assertThat(cmd.statusPresent()).isTrue();
        assertThat(cmd.status()).isEqualTo(TaskStatus.BLOCKED);
        assertThat(cmd.titlePresent()).isTrue();
        assertThat(cmd.title()).isEqualTo("T");
        assertThat(cmd.descriptionPresent()).isTrue();
        assertThat(cmd.description()).isEqualTo("D");
        assertThat(cmd.priorityPresent()).isTrue();
        assertThat(cmd.priority()).isEqualTo(5);
        assertThat(cmd.tagsPresent()).isTrue();
        assertThat(cmd.tagIds()).containsExactly(1L, 2L);
        assertThat(cmd.userAffecteePresent()).isTrue();
        assertThat(cmd.userAffectee()).isEqualTo("userX");
    }
}