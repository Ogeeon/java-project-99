package hexlet.code.app;

import hexlet.code.app.model.User;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UsersControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper om;

    private MockMvc mockMvc;

    private final Faker faker = new Faker();

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
        testUser = new User();
        testUser.setEmail(faker.internet().emailAddress());
        testUser.setPassword(faker.internet().password());
        userRepository.save(testUser);
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, List.class);
        var stored = userRepository.findAll();
        assertThat(list).hasSize(stored.size());
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("username").isEqualTo(testUser.getEmail()),
                v -> v.node("password").isNull());
    }

    @Test
    void testCreate() throws Exception {
        var userMap = new HashMap<String, String>();
        userMap.put("firstName", faker.name().firstName());
        userMap.put("email", faker.internet().emailAddress());
        userMap.put("password", faker.internet().password());

        var request = post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userMap));
        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        String response = result.getResponse().getContentAsString();
        assertThat((new ObjectMapper()).readTree(response).get("id")).isNotEmpty();
        assertThat((new ObjectMapper()).readTree(response).get("password")).isNull();

        var stored = userRepository.findByEmail(userMap.get("email")).orElse(null);
        assertNotNull(stored);
        assertThat(stored.getFirstName()).isEqualTo(userMap.get("firstName"));
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("firstName", faker.name().firstName());
        updates.put("email", faker.internet().emailAddress());
        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(stored.getFirstName()).isEqualTo(updates.get("firstName"));
        assertThat(stored.getEmail()).isEqualTo(updates.get("email"));
    }

    @Test
    void testPartialUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("firstName", faker.name().firstName());
        var oldEmail = testUser.getEmail();
        mockMvc.perform(patch("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(stored.getFirstName()).isEqualTo(updates.get("firstName"));
        assertThat(stored.getEmail()).isEqualTo(oldEmail);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(testUser.getId()).isEmpty()).isTrue();
    }
}
