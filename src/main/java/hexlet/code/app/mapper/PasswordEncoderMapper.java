package hexlet.code.app.mapper;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderMapper {

    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    public PasswordEncoderMapper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Named("encodePassword")
    public String encode(String password) {
        return encoder.encode(password);
    }
}
