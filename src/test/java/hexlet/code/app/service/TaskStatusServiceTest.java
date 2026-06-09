package hexlet.code.app.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TaskStatusMapper taskStatusMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskStatusServiceImpl taskStatusService;

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> taskStatusService.findById(1L));
    }

    @Test
    void deleteThrowsNotFoundWhenMissing() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> taskStatusService.delete(1L));
        verify(taskStatusRepository, never()).deleteById(1L);
    }

    @Test
    void deleteThrowsConflictWhenStatusUsedByTasks() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(new TaskStatus()));
        when(taskRepository.existsByStatusId(1L)).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> taskStatusService.delete(1L))
                .satisfies(ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(taskStatusRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRemovesStatusWhenNotUsed() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(new TaskStatus()));
        when(taskRepository.existsByStatusId(1L)).thenReturn(false);

        taskStatusService.delete(1L);

        verify(taskStatusRepository).deleteById(1L);
    }
}
