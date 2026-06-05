package hexlet.code.app.controller.api;

import hexlet.code.app.dto.*;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class TaskStatusController {
    private final TaskStatusService taskStatusService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusResponseDTO>> index() {
        var data = taskStatusService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDTO create(@Valid @RequestBody TaskStatusCreateDTO dto) {
        return taskStatusService.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO dto) {
        return taskStatusService.update(id, dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody TaskStatusPatchDTO dto) {
        return taskStatusService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
