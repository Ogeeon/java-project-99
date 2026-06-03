package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskPatchDTO {
    private JsonNullable<Integer> index = JsonNullable.undefined();
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> content = JsonNullable.undefined();
    private JsonNullable<String> status = JsonNullable.undefined();
    private JsonNullable<List<Long>> taskLabelIds = JsonNullable.undefined();
}
