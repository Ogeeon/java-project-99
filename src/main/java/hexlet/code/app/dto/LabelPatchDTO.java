package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@NoArgsConstructor
@Getter
@Setter
public class LabelPatchDTO {
    private JsonNullable<String> name = JsonNullable.undefined();
}