package com.quest.etna;

import com.quest.etna.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.model.Address;
import com.quest.etna.model.Event;
import com.quest.etna.model.EventType;
import com.quest.etna.model.JwtResponseToken;
import com.quest.etna.model.User;
import com.quest.etna.model.UserDTO;
import com.quest.etna.model.UserRole;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

//@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected EventService eventService;

    @Test
//    @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    protected void testEvent() throws Exception {

        User default_artist_roth = new User();
        default_artist_roth.setId(3);
        default_artist_roth.setUsername("default_artist");
        default_artist_roth.setPassword("etna");
        default_artist_roth.setRole(UserRole.ROLE_ARTIST);

        UserDTO default_admin_dto = new UserDTO();
        default_admin_dto.setUsername("default_admin");
        default_admin_dto.setPassword("etna");

        // Authenticate default admin
        ResultActions result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_admin_dto))
                        // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        String content = result.andReturn().getResponse().getContentAsString();
        String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        Address default_address = new Address();
        default_address.setId(3);
        default_address.setStreet("tired st");
        default_address.setPostalCode("3001");
        default_address.setCity("chacao");
        default_address.setCountry("test_country");
        default_address.setUser(default_artist_roth);

        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/address/")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_address))
                        // mapToJson(user)
                );

        // Récupérer l'adresse créée
        content = result.andReturn().getResponse().getContentAsString();
        Address createdAddress = (Address) mapFromJson(content, Address.class);

        // TEST GetAllEvent 200
        this.mvc
                .perform(MockMvcRequestBuilders.get("/event"))
                // .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        // Event de default admin qui va ếtre créé
        Event default_event = new Event();
        default_event.setName("admin_exhibit");
        default_event.setType(EventType.TYPE_POPUP);
        default_event.setDate(new Date());
        default_event.setImage("base64string");
        default_event.setAddress(createdAddress);

        // TEST POST 401 sans token

        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/event/")
                                // .header(HttpHeaders.AUTHORIZATION,
                                // "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_event))
                // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        ////////////// TEST create != admin 403 ///////////////////

        // Users à injecter pour le test suivant
        UserDTO default_artist = new UserDTO();
        default_artist.setUsername("default_artist");
        default_artist.setPassword("etna");

//        UserDTO default_admin_dto = new UserDTO();
//        default_admin_dto.setUsername("default_admin_dto");
//        default_admin_dto.setPassword("etna");

        // Authenticate default artist
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_artist))
                // mapToJson(user)
                );

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String artistToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/address/")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + artistToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_address))
                // mapToJson(user)
                );
        // .andDo(print())
        // .andExpect(status().isUnauthorized());

        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/event/")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + artistToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_event))
                // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // Authenticate default admin
//        result = this.mvc
//                .perform(
//                        MockMvcRequestBuilders.post("/authenticate")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(mapToJson(default_admin_dto))
//                // mapToJson(user)
//                );
//
//        // Récupérer le token reçu par /authenticate
//        String adminContent = result.andReturn().getResponse().getContentAsString();
//        String adminToken = (String) mapFromJson(adminContent, JwtResponseToken.class).getToken();

        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/event/")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_event))
                // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        content = result.andReturn().getResponse().getContentAsString();
        Event createdEvent = (Event) mapFromJson(content, Event.class);

        // supprimer l'event de test
        eventService.delete(createdEvent.getId());

        // Récupérer l'adresse créée
        // content = result.andReturn().getResponse().getContentAsString();
        // Event createdEvent = (Event) mapFromJson(content, Event.class);

        // // Authenticate
        // result = this.mvc
        // .perform(
        // MockMvcRequestBuilders.post("/authenticate")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(mapToJson(default_user))
        // // mapToJson(user)
        // );

        // // Récupérer le token reçu par /authenticate
        // content = result.andReturn().getResponse().getContentAsString();
        // String userToken = (String) mapFromJson(content,
        // JwtResponseToken.class).getToken();

        // // Récupérer les users créés
        // User createdUser = userService.getOneByUsername(default_user.getUsername());
        // // User createdAdmin = userService.getOneByUsername(testAdmin.getUsername());

        // // TEST Delete address avec user standard 401
        // this.mvc
        // .perform(
        // MockMvcRequestBuilders.delete("/address/" + createdAddress.getId())
        // .header(HttpHeaders.AUTHORIZATION,
        // "Bearer " + userToken)
        // // mapToJson(user)
        // )
        // .andDo(print())
        // .andExpect(status().isForbidden());
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
