package com.quest.etna;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.model.*;
import com.quest.etna.service.AddressService;
import com.quest.etna.service.ArtworkService;
import com.quest.etna.service.UserService;

import org.junit.jupiter.api.Test;
//import org.junit.Test;
//import org.junit.runner.RunWith;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTests {

        @Autowired
        protected MockMvc mvc;
        @Autowired
        protected UserService userService;
        @Autowired
        protected AddressService addressService;
        @Autowired
        protected ArtworkService artworkService;

        @Test
        @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        public void testAuthenticate() throws Exception {
                UserDTO testUser = new UserDTO();
                testUser.setUsername("test_username");
                testUser.setPassword("test_password");

                // TEST Register 201
                this.mvc
                                .perform(
                                                MockMvcRequestBuilders.post("/register")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(mapToJson(testUser))
                                // mapToJson(user)
                                )
                                .andDo(print())
                                .andExpect(status().isCreated());

                // TEST Register 409
                this.mvc
                                .perform(
                                                MockMvcRequestBuilders.post("/register")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(mapToJson(testUser))
                                // mapToJson(user)
                                )
                                .andDo(print())
                                .andExpect(status().isConflict());

                // TEST Authenticate 200
                ResultActions result = this.mvc
                                .perform(
                                                MockMvcRequestBuilders.post("/authenticate")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(mapToJson(testUser))
                                // mapToJson(user)
                                )
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").isString());

                // Récupérer le token reçu par /authenticate
                String content = result.andReturn().getResponse().getContentAsString();
                String token = (String) mapFromJson(content, JwtResponseToken.class).getToken();

                // Récupérer l'utilisateur de test créé en BDD
                User createdUser = userService.getOneByUsername(testUser.getUsername());

                // TEST Me 200 + résultat
                this.mvc
                                .perform(
                                                MockMvcRequestBuilders.get("/me")
                                                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(mapToJson(testUser))
                                // mapToJson(user)
                                )
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value(createdUser.getUsername()))
                                .andExpect(jsonPath("$.role").value(createdUser.getRole().toString()));

                // Supprimer l'utilisateur en BDD après les tests
                userService.delete(createdUser.getId());
        }

//        @Test
//        public void testUser() throws Exception {
//                // TEST GetAllUser 409
//                this.mvc
//                                .perform(MockMvcRequestBuilders.get("/user"))
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                UserDTO testUser = new UserDTO();
//                testUser.setUsername("test_username");
//                testUser.setPassword("test_password");
//
//                UserDTO testAdmin = new UserDTO();
//                testAdmin.setUsername("default_admin");
//                testAdmin.setPassword("etna");
//
//                // Register
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/register")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(testUser))
//                                // mapToJson(user)
//                                );
//
//                // Authenticate
//                ResultActions result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(testUser))
//                                // mapToJson(user)
//                                );
//
//                // Récupérer le token reçu par /authenticate
//                String content = result.andReturn().getResponse().getContentAsString();
//                String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST GetAllUser 200
//                this.mvc
//                                .perform(MockMvcRequestBuilders.get("/user")
//                                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                // Récupérer l'utilisateur de test créé en BDD
//                User createdUser = userService.getOneByUsername(testUser.getUsername());
//
//                // Créer un utilisateur admin
//                // this.mvc
//                // .perform(
//                // MockMvcRequestBuilders.post("/register")
//                // .contentType(MediaType.APPLICATION_JSON)
//                // .content(mapToJson(testAdmin))
//                // //mapToJson(user)
//                // );
//
//                User createdAdmin = userService.getOneByUsername(testAdmin.getUsername());
//
//                // TEST Delete admin avec user standard 401
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.delete("/user/" + createdAdmin.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + userToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isForbidden());
//
//                // Authenticate
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(testAdmin))
//                                // mapToJson(user)
//                                );
//
//                // Récupérer le token reçu par /authenticate
//                content = result.andReturn().getResponse().getContentAsString();
//                String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // Mettre le rôle à admin
//                createdAdmin.setRole(UserRole.ROLE_ADMIN);
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.put("/user/" + createdAdmin.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + adminToken)
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(createdAdmin)));
//
//                // TEST Delete test user avec admin 200
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.delete("/user/" + createdUser.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + adminToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                // Supprimer l'admin en BDD après les tests
//                // userService.delete(createdAdmin.getId());
//        }

//        @Test
//        public void testAddress() throws Exception {
//                // TEST GetAllAddress 401
//                this.mvc
//                                .perform(MockMvcRequestBuilders.get("/address"))
//                                .andDo(print())
//                                .andExpect(status().isUnauthorized());
//
//                UserDTO default_user = new UserDTO();
//                default_user.setUsername("default_user");
//                default_user.setPassword("etna");
//
//                UserDTO default_admin = new UserDTO();
//                default_admin.setUsername("default_admin");
//                default_admin.setPassword("etna");
//
//                // Authenticate default admin
//                ResultActions result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_admin))
//                                // mapToJson(user)
//                                );
//
//                // Récupérer le token reçu par /authenticate
//                String content = result.andReturn().getResponse().getContentAsString();
//                String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST GetAllAddress 200
//                this.mvc
//                                .perform(MockMvcRequestBuilders.get("/address")
//                                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                // Addresse de default admin qui va ếtre créé
//                Address admin_address = new Address();
//                admin_address.setStreet("admin_street");
//                admin_address.setPostalCode("11111");
//                admin_address.setCity("admin_city");
//                admin_address.setCountry("admin_country");
//
//                // Addresse de default user
//                Address user_address = new Address();
//                user_address.setStreet("user_street");
//                user_address.setPostalCode("00000");
//                user_address.setCity("user_city");
//                user_address.setCountry("user_country");
//
//                // TEST add 200
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/address/")
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + adminToken)
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(admin_address))
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isCreated());
//
//                // Récupérer l'adresse créée
//                content = result.andReturn().getResponse().getContentAsString();
//                Address createdAddress = (Address) mapFromJson(content, Address.class);
//
//                // Authenticate
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_user))
//                                // mapToJson(user)
//                                );
//
//                // Récupérer le token reçu par /authenticate
//                content = result.andReturn().getResponse().getContentAsString();
//                String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // Récupérer les users créés
//                User createdUser = userService.getOneByUsername(default_user.getUsername());
//
//                // TEST Delete address avec user standard 401
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.delete("/address/" + createdAddress.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + userToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isForbidden());
//
//                // Authenticate default admin
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_user))
//                                // mapToJson(user)
//                                );
//
//                // Récupérer le token reçu par /authenticate
//                // content = result.andReturn().getResponse().getContentAsString();
//                // String userToken = (String) mapFromJson(content,
//                // JwtResponseToken.class).getToken();
//
//                // Set up iterator pour récupérer l'addresse de default user
//                // addressService.getOneById(1);
//
//                // TEST Delete address avec user standard 200
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders
//                                                                .delete("/address/"
//                                                                                + addressService.getOneById(1).getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + adminToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                // Supprimer les données après les tests
//                // userService.delete(createdUser.getId());
//                // userService.delete(createdAdmin.getId());
//                addressService.delete(createdAddress.getId());
//        }

//        @Test
//        public void testArtwork() throws Exception {
//                // TEST GetAllArtwork 401
//                this.mvc
//                                .perform(MockMvcRequestBuilders.get("/artwork"))
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                UserDTO default_user = new UserDTO();
//                default_user.setUsername("default_user");
//                default_user.setPassword("etna");
//
//                UserDTO default_artist = new UserDTO();
//                default_artist.setUsername("default_artist");
//                default_artist.setPassword("etna");
//
//                User default_artist_user = new User();
//                default_artist_user.setId(3);
//                default_artist_user.setUsername("default_artist");
//                default_artist_user.setPassword("etna");
//                default_artist_user.setRole(UserRole.ROLE_ARTIST);
//
//                UserDTO another_artist = new UserDTO();
//                another_artist.setUsername("another_artist");
//                another_artist.setPassword("etna");
//
//                UserDTO default_admin = new UserDTO();
//                default_admin.setUsername("default_admin");
//                default_admin.setPassword("etna");
//
//                // Authenticate default user
//                ResultActions result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_user)));
//
//                // Récupérer le token reçu par /authenticate
//                String content = result.andReturn().getResponse().getContentAsString();
//                String userToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST GetAllArtwork 200
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.get("/artwork")
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + userToken))
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                Artwork artist_artwork = new Artwork();
//                artist_artwork.setTitle("simple_artwork");
//                artist_artwork.setPrice(25.50F);
//                artist_artwork.setTechnique(ArtworkTechnique.DIGITAL);
//                artist_artwork.setImage("base64string");
//                artist_artwork.setUser(default_artist_user);
//
//                // TEST add 403
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/artwork")
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + userToken)
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(artist_artwork))
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isForbidden());
//
//                // Authenticate default artist
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_artist)));
//
//                // Récupérer le token reçu par /authenticate
//                content = result.andReturn().getResponse().getContentAsString();
//                String artistToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST add 201
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/artwork")
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + artistToken)
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(artist_artwork))
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isCreated());
//
//                // Récupérer l'artwork créé
//                content = result.andReturn().getResponse().getContentAsString();
//                Artwork createdArtwork = (Artwork) mapFromJson(content, Artwork.class);
//
//                // Authenticate another artist
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(another_artist)));
//
//                // Récupérer le token reçu par /authenticate
//                content = result.andReturn().getResponse().getContentAsString();
//                String anotherArtistToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST Delete address avec artiste qui ne possède pas l'artwork 403
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders
//                                                                .delete("/artwork/"
//                                                                                + createdArtwork.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + anotherArtistToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isForbidden());
//
//                // Authenticate admin
//                result = this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders.post("/authenticate")
//                                                                .contentType(MediaType.APPLICATION_JSON)
//                                                                .content(mapToJson(default_admin)));
//
//                // Récupérer le token reçu par /authenticate
//                content = result.andReturn().getResponse().getContentAsString();
//                String adminToken = (String) mapFromJson(content, JwtResponseToken.class).getToken();
//
//                // TEST Delete address avec default admin
//                this.mvc
//                                .perform(
//                                                MockMvcRequestBuilders
//                                                                .delete("/artwork/"
//                                                                                + createdArtwork.getId())
//                                                                .header(HttpHeaders.AUTHORIZATION,
//                                                                                "Bearer " + adminToken)
//                                // mapToJson(user)
//                                )
//                                .andDo(print())
//                                .andExpect(status().isOk());
//
//                userService.delete(4);
//
//        }

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
