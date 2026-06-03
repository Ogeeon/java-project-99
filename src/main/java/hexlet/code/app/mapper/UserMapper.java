package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import org.mapstruct.*;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class, PasswordEncoderMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    public abstract UserResponseDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract User map(UserCreateDTO model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void update(UserUpdateDTO update, @MappingTarget User destination);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void patch(UserPatchDTO patch, @MappingTarget User destination);
}
