package hexlet.code.app.controller.api;

import hexlet.code.app.dto.*;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private static final String STATUS_NOT_FOUND = "Task status with id %d not found";
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    public TaskStatusController(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper,
                                TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO show(@PathVariable Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        return taskStatusMapper.map(status);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusResponseDTO>> index() {
        var data = taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDTO create(@Valid @RequestBody TaskStatusCreateDTO dto) {
        var model = taskStatusMapper.map(dto);
        return taskStatusMapper.map(taskStatusRepository.save(model));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO dto) {
        var model =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.update(dto, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody TaskStatusPatchDTO dto) {
        var model =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.patch(dto, model);
        return taskStatusMapper.map(taskStatusRepository.save(model));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        if (taskRepository.existsByStatusId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete task status: it is used by existing tasks");
        }
        taskStatusRepository.deleteById(id);
    }
}
