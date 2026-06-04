package hexlet.code.app.specification;

import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabel(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("title")), '%'  +titleCont.toLowerCase() + '%');
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? cb.conjunction() :
                cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String statusSlug) {
        return (root, query, cb) -> statusSlug == null ? cb.conjunction() :
            cb.equal(root.get("status").get("slug"), statusSlug);
    }

    private Specification<Task> withLabel(Long labelId) {
        return (root, query, cb) ->
            {
                Objects.requireNonNull(query, "query must not be null");
                query.distinct(true);
                if (labelId == null) {
                    return cb.conjunction();
                }
                Join<Task, Label> labels = root.join("labels");
                return cb.equal(labels.get("id"), labelId);
            };
    }
}
