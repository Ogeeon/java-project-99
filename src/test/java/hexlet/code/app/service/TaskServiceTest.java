package hexlet.code.app.service;

import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.specification.TaskSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskSpecification specBuilder;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void updateThrowsNotFoundWhenTaskMissing() {
        var dto = new TaskUpdateDTO();
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> taskService.update(1L, dto));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteThrowsNotFoundWhenTaskMissing() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> taskService.delete(1L));
        verify(taskRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRemovesTaskWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task()));

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }
}
