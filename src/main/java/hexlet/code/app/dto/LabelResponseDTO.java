package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class LabelResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}