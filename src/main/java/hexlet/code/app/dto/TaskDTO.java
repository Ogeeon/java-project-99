package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class TaskDTO {
    private Long id;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assignee_id;
    private String title;
    private String content;
    private String status;
}
