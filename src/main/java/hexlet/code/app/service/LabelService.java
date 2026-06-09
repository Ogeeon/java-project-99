package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelResponseDTO> getAll();

    LabelResponseDTO findById(Long id);

    LabelResponseDTO create(LabelCreateDTO dto);

    LabelResponseDTO update(Long id, LabelUpdateDTO dto);

    LabelResponseDTO partialUpdate(Long id, LabelPatchDTO dto);

    void delete(Long id);
}
