package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {
    private static final String LABEL_NOT_FOUND = "Task label with id %d not found";

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskRepository taskRepository;

    public List<LabelResponseDTO> getAll() {
        return labelRepository.findAll().stream().map(labelMapper::map).toList();
    }

    public LabelResponseDTO findById(Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        return labelMapper.map(model);
    }

    public LabelResponseDTO create(LabelCreateDTO dto) {
        var model = labelMapper.map(dto);
        return labelMapper.map(labelRepository.save(model));
    }

    public LabelResponseDTO update(Long id, LabelUpdateDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.update(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    public LabelResponseDTO partialUpdate(Long id, LabelPatchDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.patch(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    public void delete(Long id) {
        labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        if (taskRepository.existsByLabelsId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete label: it is used by existing tasks");
        }
        labelRepository.deleteById(id);
    }
}
