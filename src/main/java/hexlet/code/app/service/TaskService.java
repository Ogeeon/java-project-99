package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final String TASK_NOT_FOUND = "Task with id %d not found";
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final TaskSpecification specBuilder;

    public List<TaskResponseDTO> getAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();
    }

    public TaskResponseDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        return taskMapper.map(task);
    }

    public TaskResponseDTO create(TaskCreateDTO dto) {
        validateAssignee(dto.getAssigneeId());
        var task = taskMapper.map(dto);
        return taskMapper.map(taskRepository.save(task));
    }

    public TaskResponseDTO update(Long id, TaskUpdateDTO dto) {
        validateAssignee(dto.getAssigneeId());
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.update(dto, task);
        return taskMapper.map(taskRepository.save(task));
    }

    public TaskResponseDTO partialUpdate(Long id, TaskPatchDTO dto) {
        validateAssignee(dto.getAssigneeId().orElse(null));
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.patch(dto, task);
        return taskMapper.map(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskRepository.deleteById(id);
    }

    private void validateAssignee(Long userId) {
        if (userId != null && userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND.formatted(userId));
        }
    }
}
