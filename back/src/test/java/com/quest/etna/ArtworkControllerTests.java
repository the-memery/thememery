package com.quest.etna;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.model.*;
import com.quest.etna.service.ArtworkService;
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
public class ArtworkControllerTests {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ArtworkService artworkService;

    @Test
    @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testArtwork() throws Exception {
        // TEST GetAllArtwork 401
        this.mvc
                .perform(MockMvcRequestBuilders.get("/artwork"))
                .andDo(print())
                .andExpect(status().isOk());

        UserDTO default_user = new UserDTO();
        default_user.setUsername("default_user");
        default_user.setPassword("etna");

        UserDTO default_artist = new UserDTO();
        default_artist.setUsername("default_artist");
        default_artist.setPassword("etna");

        User default_artist_user = new User();
        default_artist_user.setId(3);
        default_artist_user.setUsername("default_artist");
        default_artist_user.setPassword("etna");
        default_artist_user.setRole(UserRole.ROLE_ARTIST);

        UserDTO another_artist = new UserDTO();
        another_artist.setUsername("another_artist");
        another_artist.setPassword("etna");

        UserDTO default_admin = new UserDTO();
        default_admin.setUsername("default_admin");
        default_admin.setPassword("etna");

        // Authenticate default user
        ResultActions result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_user)));

        // Récupérer le token reçu par /authenticate
        String content = result.andReturn().getResponse().getContentAsString();
        String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST GetAllArtwork 200
        this.mvc
                .perform(
                        MockMvcRequestBuilders.get("/artwork")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isOk());

        Artwork artist_artwork = new Artwork();
        artist_artwork.setTitle("simple_artwork");
        artist_artwork.setPrice(25.50F);
        artist_artwork.setTechnique(ArtworkTechnique.DIGITAL);
        artist_artwork.setImage("base64string");
        artist_artwork.setUser(default_artist_user);

        // TEST add 403
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/artwork")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + userToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(artist_artwork))
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // Authenticate default artist
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_artist)));

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String artistToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST add 201
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/artwork")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + artistToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(artist_artwork))
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // Récupérer l'artwork créé
        content = result.andReturn().getResponse().getContentAsString();
        Artwork createdArtwork = (Artwork) mapFromJson(content, Artwork.class);

        // Authenticate another artist
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(another_artist)));

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String anotherArtistToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST Delete address avec artiste qui ne possède pas l'artwork 403
        this.mvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/artwork/"
                                        + createdArtwork.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + anotherArtistToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // Authenticate admin
        result = this.mvc
                .perform(
                        MockMvcRequestBuilders.post("/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(default_admin)));

        // Récupérer le token reçu par /authenticate
        content = result.andReturn().getResponse().getContentAsString();
        String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();

        // TEST Delete address avec default admin
        this.mvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/artwork/"
                                        + createdArtwork.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + adminToken)
                        // mapToJson(user)
                )
                .andDo(print())
                .andExpect(status().isOk());

        userService.delete(4);

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
