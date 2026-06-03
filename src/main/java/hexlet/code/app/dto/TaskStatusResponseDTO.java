package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class TaskStatusResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}
