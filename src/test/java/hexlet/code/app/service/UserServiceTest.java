package hexlet.code.app.service;

import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserService userService;

    private static User userWithId(Long id) {
        var user = new User();
        user.setId(id);
        return user;
    }

    @Test
    void updateThrowsNotFoundWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        var dto = new UserUpdateDTO();

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.update(1L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateSaves() {
        var user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.update(1L, new UserUpdateDTO());

        verify(userRepository).save(user);
    }

    @Test
    void partialUpdateThrowsBadRequestWhenPasswordTooShort() {
        var user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        var dto = new UserPatchDTO();
        dto.setPassword("ab");

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> userService.partialUpdate(1L, dto))
                .satisfies(ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(userMapper, never()).patch(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void partialUpdateSavesWhenPasswordValid() {
        var user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        var dto = new UserPatchDTO();
        dto.setPassword("abc");

        userService.partialUpdate(1L, dto);

        verify(userMapper).patch(dto, user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteThrowsConflictWhenUserHasTasks() {
        var user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.existsByAssigneeId(1L)).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> userService.delete(1L))
                .satisfies(ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRemovesUserWhenNoTasks() {
        var user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.existsByAssigneeId(1L)).thenReturn(false);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
