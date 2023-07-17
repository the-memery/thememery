package com.quest.etna;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.model.JwtResponseToken;
import com.quest.etna.model.User;
import com.quest.etna.model.UserDTO;
import com.quest.etna.model.UserRole;
import com.quest.etna.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected UserService userService;

    @Test
    @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUser() throws Exception {
        // TEST GetAllUser 409
        this.mvc
                .perform(MockMvcRequestBuilders.get("/user"))
                .andDo(print())
                .andExpect(status().isOk());

        UserDTO testUser = new UserDTO();
        testUser.setUsername("test_username");
        testUser.setPassword("test_password");

        UserDTO testAdmin = new UserDTO();
        testAdmin.setUsername("default_admin");
        testAdmin.setPassword("etna");

        // Register
        this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(testUser))
                        // mapToJson(user)
                );

        // Authenticate
        ResultActions result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(testUser))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        String content = result.andReturn().getResponse().getContentAsString();
        String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST GetAllUser 200
        this.mvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Récupérer l'utilisateur de test créé en BDD
        User createdUser = userService.getOneByUsername(testUser.getUsername());

        // Créer un utilisateur admin
        // this.mvc
        // .perform(
        // MockMvcRequestBuilders.post("/register")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(mapToJson(testAdmin))
        // //mapToJson(user)
        // );

        User createdAdmin = userService.getOneByUsername(testAdmin.getUsername());

        // TEST Delete admin avec user standard 401
        this.mvc
                .perform(
                        MockMvcRequestBuilders.delete("/user/" + createdAdmin.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + userToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // Authenticate
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(testAdmin))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // Mettre le rôle à admin
        createdAdmin.setRole(UserRole.ROLE_ADMIN);
        this.mvc
                .perform(
                        MockMvcRequestBuilders.put("/user/" + createdAdmin.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(createdAdmin)));

        // TEST Delete test user avec admin 200
        this.mvc
                .perform(
                        MockMvcRequestBuilders.delete("/user/" + createdUser.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Supprimer l'admin en BDD après les tests
        // userService.delete(createdAdmin.getId());
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
