package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskPatchDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    
    @Mapping(source = "status.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assignee_id")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assignee_id", target = "assignee")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assignee_id", target = "assignee")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);

    @Mapping(source = "status", target = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(source = "assignee_id", target = "assignee")
    public abstract void patch(TaskPatchDTO patch, @MappingTarget Task destination);

    @Named("slugToTaskStatus")
    public TaskStatus slugToTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TaskStatus not found: " + slug));
     }
}
