package hexlet.code.app.controller.api;

import hexlet.code.app.dto.*;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private static final String STATUS_NOT_FOUND = "Task status with id %d not found";

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusController(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        return taskStatusMapper.map(status);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        var data = taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@RequestBody TaskStatusCreateDTO dto) {
        var model = taskStatusMapper.map(dto);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@PathVariable Long id, @RequestBody TaskStatusUpdateDTO dto) {
        var model =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.update(dto, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO partialUpdate(@PathVariable Long id, @RequestBody TaskStatusPatchDTO dto) {
        var model =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.patch(dto, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusRepository.deleteById(id);
    }
}
