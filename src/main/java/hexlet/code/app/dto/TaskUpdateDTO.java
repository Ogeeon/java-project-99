package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;

    private Long assignee_id;

    @NotBlank
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    private String status;
}
