package hexlet.code.app.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        when(taskStatusRepository.existsById(1L)).thenReturn(false);

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> taskStatusService.delete(1L));
        verify(taskStatusRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRemovesStatus() {
        when(taskStatusRepository.existsById(1L)).thenReturn(true);

        taskStatusService.delete(1L);

        verify(taskStatusRepository).deleteById(1L);
    }
}
