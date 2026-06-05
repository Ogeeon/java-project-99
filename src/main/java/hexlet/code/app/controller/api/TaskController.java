package hexlet.code.app.controller.api;

import hexlet.code.app.dto.*;
import hexlet.code.app.service.TaskService;
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
    private final TaskService taskService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO show(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskResponseDTO>> index(TaskParamsDTO params) {
        var data = taskService.getAll(params);
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(@Valid @RequestBody TaskCreateDTO taskDTO) {
        return taskService.create(taskDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO update) {
        return taskService.update(id, update);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody TaskPatchDTO patch) {
        return taskService.partialUpdate(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskService.delete(id);
    }
}
