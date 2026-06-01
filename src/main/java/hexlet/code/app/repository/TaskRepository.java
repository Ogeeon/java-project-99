package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTitle(String name);
    boolean existsByStatusId(Long statusId);
    boolean existsByAssigneeId(Long assigneeId);
}
