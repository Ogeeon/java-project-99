package hexlet.code.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
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
class LabelsControllerTest {

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
    private LabelRepository labelRepository;

    private Label testLabel;

    @BeforeEach
    void SetUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        testLabel = new Label();
        testLabel.setName(faker.lorem().word());
        labelRepository.save(testLabel);
    }

    @Test
    void testIndex() throws Exception {
        for (int i = 0; i < 5; i++) {
            var l = new Label();
            l.setName("Test label " + i);
            labelRepository.save(l);
        }
        var response = mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<Object>>(){});
        var stored = labelRepository.findAll();
        assertThat(list).hasSize(stored.size());
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void tetShow() throws Exception {
        var response = mockMvc.perform(get("/api/labels/" + testLabel.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName()));
    }

    @Test
    void testCreate() throws Exception {
        var labelMap = new HashMap<String, String>();
        labelMap.put("name", faker.lorem().word());
        var request = post("/api/labels").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelMap));
        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        var response = result.getResponse().getContentAsString();
        assertThatJson(response).and(v -> v.node("id").isPresent());

        var stored = labelRepository.findByName(labelMap.get("name")).orElse(null);
        assertThat(stored).isNotNull();
        assertThat(stored.getName()).isEqualTo(labelMap.get("name"));
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("name", faker.lorem().word());
        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = labelRepository.findById(testLabel.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo(updates.get("name"));
    }

    @Test
    void testPartialUpdate() throws Exception {
        // This test is added just for the sake of a uniform test structure
        var updates = new HashMap<String, String>();
        updates.put("name", faker.lorem().word());
        mockMvc.perform(patch("/api/labels/" + testLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = labelRepository.findById(testLabel.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo(updates.get("name"));
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId()))
                        .andExpect(status().isNoContent());
        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }

    @Test
    void testDeleteWithAssignedTask() throws Exception {
        var draftStatus = taskStatusRepository.findBySlug("draft").orElseGet(() -> {
            var data = new TaskStatus();
            data.setName("Draft");
            data.setSlug("draft");
            taskStatusRepository.save(data);
            return data;
        });
        var testTask = new Task();
        testTask.setIndex(1);
        testTask.setTitle("Test Task");
        testTask.setStatus(draftStatus);
        testTask.setLabels(List.of(testLabel));
        taskRepository.save(testTask);
        mockMvc.perform(delete("/api/labels/" + testLabel.getId()))
                .andExpect(status().isConflict());
    }
}
