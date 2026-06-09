package hexlet.code.app.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
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
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(labelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> labelService.findById(1L));
    }

    @Test
    void deleteThrowsNotFoundWhenMissing() {
        when(labelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> labelService.delete(1L));
        verify(labelRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRemovesLabel() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(new Label()));

        labelService.delete(1L);

        verify(labelRepository).deleteById(1L);
    }
}
