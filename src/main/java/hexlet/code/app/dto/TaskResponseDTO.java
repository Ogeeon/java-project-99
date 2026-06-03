package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class TaskResponseDTO {
    private Long id;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private List<Long> taskLabelIds;
}
