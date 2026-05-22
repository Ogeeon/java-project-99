package hexlet.code.app.component;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TaskStatusRepository taskStatusRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var userData = new User();
            userData.setEmail("hexlet@example.com");
            userData.setPasswordDigest(passwordEncoder.encode("qwerty"));
            userRepository.save(userData);
        }
        if (taskStatusRepository.findBySlug("draft").isEmpty()) {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName("Draft");
            taskStatusData.setSlug("draft");
            taskStatusRepository.save(taskStatusData);
        }
        if (taskStatusRepository.findBySlug("to_review").isEmpty()) {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName("To review");
            taskStatusData.setSlug("to_review");
            taskStatusRepository.save(taskStatusData);
        }
        if (taskStatusRepository.findBySlug("to_be_fixed").isEmpty()) {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName("To be fixed");
            taskStatusData.setSlug("to_be_fixed");
            taskStatusRepository.save(taskStatusData);
        }
        if (taskStatusRepository.findBySlug("to_publish").isEmpty()) {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName("To publish");
            taskStatusData.setSlug("to_publish");
            taskStatusRepository.save(taskStatusData);
        }
        if (taskStatusRepository.findBySlug("published").isEmpty()) {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName("Published");
            taskStatusData.setSlug("published");
            taskStatusRepository.save(taskStatusData);
        }
    }
}
