package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LabelCreateDTO {
    @NotBlank
    @Size(min = 1, max = 1000)
    private String name;
}
