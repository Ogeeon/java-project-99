package hexlet.code.app.controller.api;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.config.TestConfig;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private Task testTask;

    private TaskStatus draftStatus;

    private User testUser;

    @Autowired
    private LabelRepository labelRepository;

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
        var tasks = IntStream.range(0, 10)
                .mapToObj(i -> {
                    var t = new Task();
                    t.setIndex(i);
                    t.setTitle("Test Task" + i);
                    return t;
                })
                .toList();
        taskRepository.saveAll(tasks);
        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<TaskResponseDTO>>(){});
        var stored = taskRepository.findAll();
        var actualIds = list.stream().map(TaskResponseDTO::getId).toList();
        var expectedIds = stored.stream().map(Task::getId).toList();
        assertThat(actualIds).containsExactlyInAnyOrderElementsOf(expectedIds);
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/tasks/" + testTask.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("title").isEqualTo(testTask.getTitle()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("status").isEqualTo(testTask.getStatus().getSlug()),
                v -> v.node("assigneeId").isEqualTo(testUser.getId()));
    }

    @Test
    void testCreate() throws Exception {
        var taskMap = new HashMap<String, Object>();
        taskMap.put("title", faker.lorem().word());
        taskMap.put("content", String.join(" ", faker.lorem().words(3)));
        taskMap.put("status", draftStatus.getSlug());
        taskMap.put("assigneeId", testUser.getId());

        var request = post("/api/tasks").with(jwt()).contentType(MediaType.APPLICATION_JSON)
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
    void testCreateWithMissingAssigneeReturnsNotFound() throws Exception {
        var taskMap = new HashMap<String, Object>();
        taskMap.put("title", faker.lorem().word());
        taskMap.put("status", draftStatus.getSlug());
        taskMap.put("assigneeId", 999999L);

        var request = post("/api/tasks").with(jwt()).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskMap));
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithMissingAssigneeReturnsNotFound() throws Exception {
        var updates = new HashMap<String, Object>();
        updates.put("assigneeId", 999999L);

        mockMvc.perform(put("/api/tasks/" + testTask.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("title", faker.lorem().word());
        updates.put("content", String.join(" ", faker.lorem().words(3)));
        updates.put("status", draftStatus.getSlug());
        mockMvc.perform(put("/api/tasks/" + testTask.getId()).with(jwt())
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
        mockMvc.perform(patch("/api/tasks/" + testTask.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(stored.getTitle()).isEqualTo(updates.get("title"));
        assertThat(stored.getContent()).isEqualTo(oldContent);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(jwt()))
                .andExpect(status().isNoContent());
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @Test
    void testFilters() throws Exception {
        var reviewStatus = taskStatusRepository.findBySlug("to_review").orElseGet(() -> {
            var data = new TaskStatus();
            data.setName("To review");
            data.setSlug("to_review");
            taskStatusRepository.save(data);
            return data;
        });
        var l1 = new Label();
        l1.setName("Test label " + 1);
        labelRepository.save(l1);
        var l2 = new Label();
        l2.setName("Test label " + 2);
        labelRepository.save(l2);
        var testUser2 = new User();
        testUser2.setEmail(faker.internet().emailAddress());
        testUser2.setPasswordDigest("1");
        userRepository.save(testUser2);

        var taskA = new Task();
        taskA.setIndex(2);
        taskA.setTitle("Example");
        taskA.setStatus(reviewStatus);
        taskA.setAssignee(testUser);
        taskA.setLabels(Set.of(l1));
        taskRepository.save(taskA);

        var taskB = new Task();
        taskB.setIndex(3);
        taskB.setTitle("Sample");
        taskB.setStatus(reviewStatus);
        taskB.setAssignee(testUser2);
        taskB.setLabels(Set.of(l1, l2));
        taskRepository.save(taskB);

        var response = mockMvc.perform(get("/api/tasks?titleCont=amp").with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getHeader("X-Total-Count")).isEqualTo("2");
        assertThatJson(response.getContentAsString()).inPath("$[*].title").isArray()
                .containsExactlyInAnyOrder("Example", "Sample");

        response = mockMvc.perform(get("/api/tasks?status=" + reviewStatus.getSlug()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getHeader("X-Total-Count")).isEqualTo("2");
        assertThatJson(response.getContentAsString()).inPath("$[*].title").isArray()
                .containsExactlyInAnyOrder("Example", "Sample");

        response = mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getHeader("X-Total-Count")).isEqualTo("2");
        assertThatJson(response.getContentAsString()).inPath("$[*].title").isArray()
                .containsExactlyInAnyOrder("Test Task", "Example");

        response = mockMvc.perform(get("/api/tasks?labelId=" + l1.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getHeader("X-Total-Count")).isEqualTo("2");
        assertThatJson(response.getContentAsString()).inPath("$[*].title").isArray()
                .containsExactlyInAnyOrder("Example", "Sample");

        response = mockMvc.perform(get("/api/tasks?status=" + reviewStatus.getSlug()
                    + "&assigneeId=" + testUser2.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getHeader("X-Total-Count")).isEqualTo("1");
        assertThatJson(response.getContentAsString()).and(
                v -> v.node("[0].title").isEqualTo("Sample"),
                v -> v.node("[0].assigneeId").isEqualTo(testUser2.getId()));
    }
}
