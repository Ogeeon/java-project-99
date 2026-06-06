package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelPatchDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LabelMapper {
    public abstract LabelResponseDTO map(Label model);

    public abstract Label map(LabelCreateDTO dto);

    public abstract void update(LabelUpdateDTO update, @MappingTarget Label destination);

    public abstract void patch(LabelPatchDTO patch, @MappingTarget Label destination);
}
