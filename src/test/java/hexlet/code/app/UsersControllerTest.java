package hexlet.code.app;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private final Faker faker = new Faker();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private User testUser;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = new User();
        testUser.setEmail(faker.internet().emailAddress());
        testUser.setPasswordDigest(encoder.encode(faker.internet().password()));
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        var list = om.readValue(data, new TypeReference<List<Object>>() {});
        var stored = userRepository.findAll();
        assertThat(list).hasSize(stored.size());
        assertThat(response.getHeader("X-Total-Count")).isEqualTo(String.valueOf(stored.size()));
    }

    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var data = response.getContentAsString();
        assertThatJson(data).and(v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("password").isAbsent());
    }

    @Test
    void testCreate() throws Exception {
        var userMap = new HashMap<String, String>();
        userMap.put("firstName", faker.name().firstName());
        userMap.put("email", faker.internet().emailAddress());
        userMap.put("password", faker.internet().password());

        var request = post("/api/users").with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userMap));
        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        String response = result.getResponse().getContentAsString();
        assertThatJson(response).and(v -> v.node("id").isPresent(),
                v -> v.node("password").isAbsent());

        var stored = userRepository.findByEmail(userMap.get("email")).orElse(null);
        assertNotNull(stored);
        assertThat(stored.getFirstName()).isEqualTo(userMap.get("firstName"));
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new HashMap<String, String>();
        updates.put("firstName", faker.name().firstName());
        updates.put("email", faker.internet().emailAddress());
        updates.put("password", faker.internet().password());
        mockMvc.perform(put("/api/users/" + testUser.getId()).with(token)
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
        mockMvc.perform(put("/api/users/" + testUser.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updates)))
                .andExpect(status().isOk());
        var stored = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(stored.getFirstName()).isEqualTo(updates.get("firstName"));
        assertThat(stored.getEmail()).isEqualTo(oldEmail);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
