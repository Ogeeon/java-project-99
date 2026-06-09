package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusPatchDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusResponseDTO> getAll();

    TaskStatusResponseDTO findById(Long id);

    TaskStatusResponseDTO create(TaskStatusCreateDTO dto);

    TaskStatusResponseDTO update(Long id, TaskStatusUpdateDTO dto);

    TaskStatusResponseDTO partialUpdate(Long id, TaskStatusPatchDTO dto);

    void delete(Long id);
}
