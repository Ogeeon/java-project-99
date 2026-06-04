package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.util.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;

    private final TaskRepository taskRepository;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        return userMapper.map(user);
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> index() {
        var data = userRepository.findAll().stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateDTO dto) {
        var model = userMapper.map(dto);
        return userMapper.map(userRepository.save(model));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userMapper.update(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (dto.getPassword().isPresent() && dto.getPassword().get().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 3 characters");
        }
        userMapper.patch(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (taskRepository.existsByAssigneeId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete user: they are assigned to existing tasks");
        }
        userRepository.deleteById(id);
    }
}
