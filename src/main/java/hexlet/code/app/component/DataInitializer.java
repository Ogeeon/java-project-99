package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${data-initializer.admin-email}")
    private String adminEmail;

    @Value("${data-initializer.admin-password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            var userData = new User();
            userData.setEmail(adminEmail);
            userData.setPasswordDigest(passwordEncoder.encode(adminPassword));
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
        if (labelRepository.findByName("feature").isEmpty()) {
            var label = new Label();
            label.setName("feature");
            labelRepository.save(label);
        }
        if (labelRepository.findByName("bug").isEmpty()) {
            var label = new Label();
            label.setName("bug");
            labelRepository.save(label);
        }
    }
}
