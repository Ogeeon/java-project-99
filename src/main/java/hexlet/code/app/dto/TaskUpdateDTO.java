package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;

    private Long assigneeId;

    @NotBlank
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    private String status;

    private List<Long> taskLabelIds;
}
