package hexlet.code.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private final Faker faker = new Faker();

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private Task testTask;

    private TaskStatus draftStatus;

    private User testUser;

    @BeforeEach
    void SetUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setEmail(faker.internet().emailAddress());
        testUser.setPasswordDigest("1");
        userRepository.save(testUser);

        draftStatus = taskStatusRepository.findBySlug("draft").orElseGet(() -> {
            var data = new TaskStatus();
            data.setName("Draft");
            data.setSlug("draft");
            taskStatusRepository.save(data);
            return data;
        });
        testTask = new Task();
        testTask.setIndex(1);
        testTask.setTitle("Test Task");
        testTask.setStatus(draftStatus);
        testTask.setAssignee(testUser);
        taskRepository.save(testTask);
    }

    @Test
    void testIndex() throws Exception {
        for (int i = 0; i < 10; i++) {
            var t = new Task();
            t.setIndex(i);
            t.setTitle("Test Task" + i);
            taskRepository.save(t);
        }
        var response = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<Object>>(){});
        var stored = taskRepository.findAll();
        assertThat(list).hasSize(stored.size());
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/tasks/" + testTask.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("title").isEqualTo(testTask.getTitle()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("status").isEqualTo(testTask.getStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testUser.getId()));
    }

    @Test
    void testCreate() throws Exception {
        var taskMap = new HashMap<String, Object>();
        taskMap.put("title", faker.lorem().word());
        taskMap.put("content", String.join(" ", faker.lorem().words(3)));
        taskMap.put("status", draftStatus.getSlug());
        taskMap.put("assignee_id", testUser.getId());

        var request = post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskMap));
        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        String response = result.getResponse().getContentAsString();
        assertThatJson(response).and(v -> v.node("id").isPresent());

        var stored = taskRepository.findByTitle(taskMap.get("title").toString()).orElse(null);
        assertNotNull(stored);
        assertThat(stored.getContent()).isEqualTo(taskMap.get("content"));
        assertThat(stored.getStatus().getSlug()).isEqualTo(taskMap.get("status"));
        assertThat(stored.getAssignee().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("title", faker.lorem().word());
        updates.put("content", String.join(" ", faker.lorem().words(3)));
        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(stored.getTitle()).isEqualTo(updates.get("title"));
        assertThat(stored.getContent()).isEqualTo(updates.get("content"));
    }

    @Test
    void testPartialUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("title", faker.lorem().word());
        var oldContent = testTask.getContent();
        mockMvc.perform(patch("/api/tasks/" + testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(stored.getTitle()).isEqualTo(updates.get("title"));
        assertThat(stored.getContent()).isEqualTo(oldContent);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()))
                .andExpect(status().isNoContent());
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}
