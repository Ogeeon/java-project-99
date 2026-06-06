package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskPatchDTO {
    private Integer index;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private List<Long> taskLabelIds;
}
