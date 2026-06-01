package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskPatchDTO {
    private JsonNullable<Integer> index = JsonNullable.undefined();
    private JsonNullable<Long> assignee_id = JsonNullable.undefined();
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> content = JsonNullable.undefined();
    private JsonNullable<String> status = JsonNullable.undefined();
}
