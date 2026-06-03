package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.dto.TaskStatusPatchDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.*;

@Mapper(uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskStatusMapper {
    public abstract TaskStatusResponseDTO map(TaskStatus model);

    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    public abstract void update(TaskStatusUpdateDTO update, @MappingTarget TaskStatus destination);

    public abstract void patch(TaskStatusPatchDTO patch, @MappingTarget  TaskStatus destination);
}
