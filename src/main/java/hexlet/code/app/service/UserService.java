package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;
    private final TaskRepository taskRepository;

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    public UserResponseDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        return userMapper.map(user);
    }

    public UserResponseDTO create(UserCreateDTO dto) {
        var model = userMapper.map(dto);
        return userMapper.map(userRepository.save(model));
    }

    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        var user = getOwnUserOrThrow(id);
        userMapper.update(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    public UserResponseDTO partialUpdate(Long id, UserPatchDTO dto) {
        var user = getOwnUserOrThrow(id);
        if (dto.getPassword().isPresent() && dto.getPassword().get().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 3 characters");
        }
        userMapper.patch(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    public void delete(Long id) {
        getOwnUserOrThrow(id);
        if (taskRepository.existsByAssigneeId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete user: they are assigned to existing tasks");
        }
        userRepository.deleteById(id);
    }

    private User getOwnUserOrThrow(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return user;
    }
}
