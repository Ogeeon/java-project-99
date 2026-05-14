package hexlet.code.app.controller.api;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UsersController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        return userMapper.map(user);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        var data = userRepository.findAll().stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        var model = userMapper.map(dto);
        userRepository.save(model);
        return userMapper.map(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        userMapper.update(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        userMapper.patch(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        userRepository.deleteById(id);
    }
}
