package hexlet.code.app.controller.api;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;

    public UsersController(UserRepository userRepository, UserMapper userMapper, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userUtils = userUtils;
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
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userMapper.update(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (dto.getPassword().isPresent() && dto.getPassword().get().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 3 characters");
        }
        userMapper.patch(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userRepository.deleteById(id);
    }
}
