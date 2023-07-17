package com.quest.etna;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.model.Address;
import com.quest.etna.model.JwtResponseToken;
import com.quest.etna.model.User;
import com.quest.etna.model.UserDTO;
import com.quest.etna.service.AddressService;
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
public class AddressControllerTests {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected AddressService addressService;

    @Test
    @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAddress() throws Exception {
        // TEST GetAllAddress 401
        this.mvc
                .perform(MockMvcRequestBuilders.get("/address"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        UserDTO default_user = new UserDTO();
        default_user.setUsername("default_user");
        default_user.setPassword("etna");

        UserDTO default_admin = new UserDTO();
        default_admin.setUsername("default_admin");
        default_admin.setPassword("etna");

        // Authenticate default admin
        ResultActions result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_admin))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        String content = result.andReturn().getResponse().getContentAsString();
        String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST GetAllAddress 200
        this.mvc
                .perform(MockMvcRequestBuilders.get("/address")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Addresse de default admin qui va ếtre créé
        Address admin_address = new Address();
        admin_address.setStreet("admin_street");
        admin_address.setPostalCode("11111");
        admin_address.setCity("admin_city");
        admin_address.setCountry("admin_country");

        // Addresse de default user
        Address user_address = new Address();
        user_address.setStreet("user_street");
        user_address.setPostalCode("00000");
        user_address.setCity("user_city");
        user_address.setCountry("user_country");

        // TEST add 200
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/address/")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(admin_address))
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // Récupérer l'adresse créée
        content = result.andReturn().getResponse().getContentAsString();
        Address createdAddress = (Address) mapFromJson(content, Address.class);

        // Authenticate
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_user))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // Récupérer les users créés
        User createdUser = userService.getOneByUsername(default_user.getUsername());

        // TEST Delete address avec user standard 401
        this.mvc
                .perform(
                        MockMvcRequestBuilders.delete("/address/" + createdAddress.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + userToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // Authenticate default admin
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_user))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        // content = result.andReturn().getResponse().getContentAsString();
        // String userToken = (String) mapFromJson(content,
        // JwtResponseToken.class).getToken();

        // Set up iterator pour récupérer l'addresse de default user
        // addressService.getOneById(1);

        // TEST Delete address avec user standard 200
        this.mvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/address/"
                                        + addressService.getOneById(1).getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Supprimer les données après les tests
        // userService.delete(createdUser.getId());
        // userService.delete(createdAdmin.getId());
        addressService.delete(createdAddress.getId());
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
