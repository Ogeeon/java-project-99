package hexlet.code.app.controller.api;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {
    private static final String LABEL_NOT_FOUND = "Task label with id %d not found";

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    private final TaskStatusRepository taskStatusRepository;

    private final TaskRepository taskRepository;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO show(@PathVariable("id") Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        return labelMapper.map(model);
    }

    @GetMapping("")
    public ResponseEntity<List<LabelResponseDTO>> index() {
        var data = labelRepository.findAll().stream().map(labelMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO create(@Valid @RequestBody LabelCreateDTO dto) {
        var model = labelMapper.map(dto);
        return labelMapper.map(labelRepository.save(model));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.update(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody LabelPatchDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.patch(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        if (taskRepository.existsByLabelsId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete label: it is used by existing tasks");
        }
        labelRepository.deleteById(id);
    }
}
