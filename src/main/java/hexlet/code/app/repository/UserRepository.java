package hexlet.code.app.repository;

import hexlet.code.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstName(String firstName);
    Optional<User> findByEmail(String email);
}
