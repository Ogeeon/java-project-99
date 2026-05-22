package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusPatchDTO {
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> slug = JsonNullable.undefined();
}
