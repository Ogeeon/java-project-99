package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPatchDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
