package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskResponseDTO> getAll(TaskParamsDTO params);

    TaskResponseDTO findById(Long id);

    TaskResponseDTO create(TaskCreateDTO dto);

    TaskResponseDTO update(Long id, TaskUpdateDTO dto);

    TaskResponseDTO partialUpdate(Long id, TaskPatchDTO dto);

    void delete(Long id);
}
