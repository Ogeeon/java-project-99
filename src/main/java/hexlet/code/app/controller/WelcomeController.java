package hexlet.code.app.controller;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }

    // TODO: временный эндпоинт для проверки интеграции с Sentry — удалить после проверки
    @GetMapping("/sentry-test")
    public String sentryTest() {
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        return "Sentry test event sent";
    }
}
