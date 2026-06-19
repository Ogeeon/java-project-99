package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

    @Mapping(source = "status.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "labelsToLabelIds")
    public abstract TaskResponseDTO map(Task model);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    public abstract void patch(TaskPatchDTO patch, @MappingTarget Task destination);

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
