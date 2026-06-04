package hexlet.code.app.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderMapper {

    private final PasswordEncoder encoder;

    @Named("encodePassword")
    public String encode(String password) {
        return encoder.encode(password);
    }
}
