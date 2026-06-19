package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final String TASK_NOT_FOUND = "Task with id %d not found";

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification specBuilder;

    @Override
    public List<TaskResponseDTO> getAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();
    }

    @Override
    public TaskResponseDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        return taskMapper.map(task);
    }

    @Override
    public TaskResponseDTO create(TaskCreateDTO dto) {
        var task = taskMapper.map(dto);
        task.setStatus(resolveStatus(dto.getStatus()));
        task.setAssignee(resolveAssignee(dto.getAssigneeId()));
        task.setLabels(resolveLabels(dto.getTaskLabelIds()));
        return taskMapper.map(taskRepository.save(task));
    }

    @Override
    public TaskResponseDTO update(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.update(dto, task);
        if (dto.getStatus() != null) {
            task.setStatus(resolveStatus(dto.getStatus()));
        }
        if (dto.getAssigneeId() != null) {
            task.setAssignee(resolveAssignee(dto.getAssigneeId()));
        }
        if (dto.getTaskLabelIds() != null) {
            task.setLabels(resolveLabels(dto.getTaskLabelIds()));
        }
        return taskMapper.map(taskRepository.save(task));
    }

    @Override
    public TaskResponseDTO partialUpdate(Long id, TaskPatchDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id)));
        taskMapper.patch(dto, task);
        if (dto.getStatus() != null) {
            task.setStatus(resolveStatus(dto.getStatus()));
        }
        if (dto.getAssigneeId() != null) {
            task.setAssignee(resolveAssignee(dto.getAssigneeId()));
        }
        if (dto.getTaskLabelIds() != null) {
            task.setLabels(resolveLabels(dto.getTaskLabelIds()));
        }
        return taskMapper.map(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(TASK_NOT_FOUND.formatted(id));
        }
        taskRepository.deleteById(id);
    }

    private TaskStatus resolveStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TaskStatus not found: " + slug));
    }

    private User resolveAssignee(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User with id %d not found".formatted(assigneeId)));
    }

    private Set<Label> resolveLabels(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Label> labels = labelRepository.findAllById(labelIds);
        if (labels.size() != labelIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more labels not found: " + labelIds);
        }
        return new HashSet<>(labels);
    }
}
