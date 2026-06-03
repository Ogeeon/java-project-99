package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {
    private static final String TASK_NOT_FOUND = "Task with id %d not found";
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private final UserRepository userRepository;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO show(@PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        return taskMapper.map(task);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskResponseDTO>> index() {
        var data = taskRepository.findAll().stream().map(taskMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(@Valid @RequestBody TaskCreateDTO taskDTO) {
        Long userId = taskDTO.getAssigneeId();
        if (userId != null && userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND.formatted(userId));
        }
        var task = taskMapper.map(taskDTO);
        return taskMapper.map(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO update) {
        Long userId = update.getAssigneeId();
        if (userId != null && userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND.formatted(userId));
        }
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.update(update, task);
        return taskMapper.map(taskRepository.save(task));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody TaskPatchDTO patch) {
        Long userId = patch.getAssigneeId().orElse(null);
        if (userId != null && userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND.formatted(userId));
        }
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.patch(patch, task);
        return taskMapper.map(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskRepository.deleteById(id);
    }
}
