package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    private final PasswordEncoder encoder;

    protected UserMapper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public abstract UserDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract User map(UserCreateDTO model);

    public abstract User map(UserDTO model);

    public abstract User map(UserUpdateDTO model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void update(UserUpdateDTO update, @MappingTarget User destination);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void patch(UserPatchDTO patch, @MappingTarget User destination);

    @Named("encodePassword")
    String encodePassword(String password) {
        return encoder.encode(password);
    }
}
