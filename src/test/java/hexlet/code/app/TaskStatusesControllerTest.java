package hexlet.code.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class TaskStatusesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private TaskStatus testStatus;

    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
        testStatus = new TaskStatus();
        testStatus.setName("TestStatus");
        testStatus.setSlug("test_status");
        taskStatusRepository.save(testStatus);
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<Object>>(){});
        var stored = taskStatusRepository.findAll();
        assertThat(list).hasSize(stored.size());
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses/" + testStatus.getId()))
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
        var response =  mockMvc.perform(post("/api/task_statuses")
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
        mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
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
        mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(statusMap)))
                .andExpect(status().isOk());
        var stored = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo(statusMap.get("name"));
        assertThat(stored.getSlug()).isEqualTo(storedSlug);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId()))
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.findById(testStatus.getId())).isEmpty();
    }
}
