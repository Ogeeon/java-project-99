package hexlet.code.app.controller.api;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO show(@PathVariable("id") Long id) {
        return labelService.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<LabelResponseDTO>> index() {
        var data = labelService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(data.size())).body(data);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO create(@Valid @RequestBody LabelCreateDTO dto) {
        return labelService.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
        return labelService.update(id, dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody LabelPatchDTO dto) {
        return labelService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }
}
