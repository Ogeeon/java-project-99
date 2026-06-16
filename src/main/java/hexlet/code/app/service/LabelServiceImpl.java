package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private static final String LABEL_NOT_FOUND = "Task label with id %d not found";

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelResponseDTO> getAll() {
        return labelRepository.findAll().stream().map(labelMapper::map).toList();
    }

    @Override
    public LabelResponseDTO findById(Long id) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        return labelMapper.map(model);
    }

    @Override
    public LabelResponseDTO create(LabelCreateDTO dto) {
        var model = labelMapper.map(dto);
        return labelMapper.map(labelRepository.save(model));
    }

    @Override
    public LabelResponseDTO update(Long id, LabelUpdateDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.update(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    @Override
    public LabelResponseDTO partialUpdate(Long id, LabelPatchDTO dto) {
        var model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id)));
        labelMapper.patch(dto, model);
        return labelMapper.map(labelRepository.save(model));
    }

    @Override
    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException(LABEL_NOT_FOUND.formatted(id));
        }
        labelRepository.deleteById(id);
    }
}
