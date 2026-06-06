package hexlet.code.app.dto;

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

    @Size(min = 1)
    private String title;

    private String content;

    private String status;

    private List<Long> taskLabelIds;
}
