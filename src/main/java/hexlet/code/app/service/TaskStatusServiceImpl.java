package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusPatchDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private static final String STATUS_NOT_FOUND = "Task status with id %d not found";

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public List<TaskStatusResponseDTO> getAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    @Override
    public TaskStatusResponseDTO findById(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        return taskStatusMapper.map(status);
    }

    @Override
    public TaskStatusResponseDTO create(TaskStatusCreateDTO dto) {
        var model = taskStatusMapper.map(dto);
        return taskStatusMapper.map(taskStatusRepository.save(model));
    }

    @Override
    public TaskStatusResponseDTO update(Long id, TaskStatusUpdateDTO dto) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.update(dto, model);
        return taskStatusMapper.map(taskStatusRepository.save(model));
    }

    @Override
    public TaskStatusResponseDTO partialUpdate(Long id, TaskStatusPatchDTO dto) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusMapper.patch(dto, model);
        return taskStatusMapper.map(taskStatusRepository.save(model));
    }

    @Override
    public void delete(Long id) {
        taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND.formatted(id)));
        taskStatusRepository.deleteById(id);
    }
}
