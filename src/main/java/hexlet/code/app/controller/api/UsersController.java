package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> index() {
        var data = userService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public UserResponseDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public UserResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        return userService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public void destroy(@PathVariable Long id) {
        userService.delete(id);
    }
}
