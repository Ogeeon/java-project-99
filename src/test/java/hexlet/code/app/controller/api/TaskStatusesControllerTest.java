package hexlet.code.app.controller.api;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
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
class TaskStatusesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private TaskStatus testStatus;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        testStatus = new TaskStatus();
        testStatus.setName("TestStatus");
        testStatus.setSlug("test_status");
        taskStatusRepository.save(testStatus);
    }

    @Test
    void testIndex() throws Exception {
        var statuses = IntStream.range(0, 10)
                .mapToObj(i -> {
                    var s = new TaskStatus();
                    s.setName("TestStatus" + i);
                    s.setSlug("test_status" + i);
                    return s;
                })
                .toList();
        taskStatusRepository.saveAll(statuses);
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<TaskStatusResponseDTO>>(){});
        var stored = taskStatusRepository.findAll();
        var actualIds = list.stream().map(TaskStatusResponseDTO::getId).toList();
        var expectedIds = stored.stream().map(TaskStatus::getId).toList();
        assertThat(actualIds).containsExactlyInAnyOrderElementsOf(expectedIds);
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses/" + testStatus.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("name").isEqualTo(testStatus.getName()),
                v -> v.node("slug").isEqualTo(testStatus.getSlug()));
    }

    @Test
    void testCreate() throws Exception {
        var statusMap = new HashMap<String, String>();
        statusMap.put("name", "NewStatus");
        statusMap.put("slug", "new_status");
        var response =  mockMvc.perform(post("/api/task_statuses").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(statusMap)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("id").isPresent());
        var stored = taskStatusRepository.findBySlug(statusMap.get("slug")).orElse(null);
        assertNotNull(stored);
        assertThat(stored.getName()).isEqualTo(statusMap.get("name"));
    }

    @Test
    void testUpdate() throws Exception {
        var statusMap = new HashMap<String, String>();
        statusMap.put("name", "NewStatus");
        statusMap.put("slug", "new_status");
        mockMvc.perform(put("/api/task_statuses/" + testStatus.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(statusMap)))
                .andExpect(status().isOk());
        var stored = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo(statusMap.get("name"));
        assertThat(stored.getSlug()).isEqualTo(statusMap.get("slug"));
    }

    @Test
    void testPartialUpdate() throws Exception {
        var statusMap = new HashMap<String, String>();
        statusMap.put("name", "NewStatus");
        var storedSlug = taskStatusRepository.findById(testStatus.getId()).orElseThrow().getSlug();
        mockMvc.perform(patch("/api/task_statuses/" + testStatus.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(statusMap)))
                .andExpect(status().isOk());
        var stored = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo(statusMap.get("name"));
        assertThat(stored.getSlug()).isEqualTo(storedSlug);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId()).with(jwt()))
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.findById(testStatus.getId())).isEmpty();
    }

    @Test
    void testDeleteWithAssignedTask() throws Exception {
        var testTask = new Task();
        testTask.setIndex(1);
        testTask.setTitle("Test Task");
        testTask.setStatus(testStatus);
        taskRepository.save(testTask);
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId()).with(jwt()))
                .andExpect(status().isConflict());
    }
}
