package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {
    private TaskStatusRepository taskStatusRepository;

    private LabelRepository labelRepository;

    private UserRepository userRepository;


    @Autowired
    protected void setTaskStatusRepository(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Autowired
    protected void setLabelRepository(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Autowired
    protected void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Mapping(source = "status.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "labelsToLabelIds")
    public abstract TaskResponseDTO map(Task model);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "assigneeIdToUser")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "assigneeIdToUser")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "assigneeIdToUser")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract void patch(TaskPatchDTO patch, @MappingTarget Task destination);

    @Named("slugToTaskStatus")
    public TaskStatus slugToTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TaskStatus not found: " + slug));
    }

    @Named("assigneeIdToUser")
    public User assigneeIdToUser(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id %d not found".formatted(assigneeId)));
    }

    @Named("mapLabels")
    public Set<Label> mapLabels(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return Set.of();
        }
        List<Label> labels = labelRepository.findAllById(labelIds);
        if (labels.size() != labelIds.size()) {
            throw new ResourceNotFoundException("One or more labels not found: " + labelIds);
        }
        return new HashSet<>(labels);
    }

    @Named("labelsToLabelIds")
    public List<Long> labelsToLabelIds(Set<Label> labels) {
        if (labels == null) {
            return List.of();
        }

        return labels.stream()
                .map(Label::getId)
                .toList();
    }
}
