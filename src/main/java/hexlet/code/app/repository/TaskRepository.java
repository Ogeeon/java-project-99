package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByTitle(String name);
    boolean existsByStatusId(Long statusId);
    boolean existsByAssigneeId(Long assigneeId);
    boolean existsByLabelsId(Long id);
}
