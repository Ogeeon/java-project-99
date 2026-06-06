package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {
    private TaskStatusRepository taskStatusRepository;

    private ReferenceMapper referenceMapper;

    @Autowired
    protected void setTaskStatusRepository(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Autowired
    protected void setReferenceMapper(ReferenceMapper referenceMapper) {
        this.referenceMapper = referenceMapper;
    }

    @Mapping(source = "status.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "labelsToLabelIds")
    public abstract TaskResponseDTO map(Task model);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapLabels")
    public abstract void patch(TaskPatchDTO patch, @MappingTarget Task destination);

    @Named("slugToTaskStatus")
    public TaskStatus slugToTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TaskStatus not found: " + slug));
    }

    @Named("mapLabels")
    public List<Label> mapLabels(List<Long> labelIds) {
        if (labelIds == null) {
            return new ArrayList<>();
        }
        return referenceMapper.toEntities(labelIds, Label.class);
    }

    @Named("labelsToLabelIds")
    public List<Long> labelsToLabelIds(List<Label> labels) {
        if (labels == null) {
            return List.of();
        }

        return labels.stream()
                .map(Label::getId)
                .toList();
    }
}
