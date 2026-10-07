package com.Usaid_Syed.taskforge.task;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void create_defaultStatusToTdo_whenNoneProvided() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.create(new TaskRequest("Write tests", "desc", null));

        assertThat(response.title()).isEqualTo("Write tests");
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void create_usesProvidedStatus() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.create(new TaskRequest("Task", null, TaskStatus.IN_PROGRESS));

        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void findAll_returnMappedResponses() {
        Task task = new Task();
        task.setTitle("One");
        when(repository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("One");
    }

    @Test
    void findByid_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class).hasMessage("Task 99 not found");
    }
    @Test
    void update_changesFields() {
        Task existing = new Task();
        existing.setTitle("Old");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.update(1L, new TaskRequest("New", "updated", TaskStatus.DONE));

        assertThat(response.title()).isEqualTo("New");
        assertThat(response.description()).isEqualTo("updated");
        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void delete_removesTask_whenFound() {
        Task existing = new Task();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }

    @Test
    void delete_throwsAndDoesNotDelete_whenMissing() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repository, never()).delete(any(Task.class));
    }
}
