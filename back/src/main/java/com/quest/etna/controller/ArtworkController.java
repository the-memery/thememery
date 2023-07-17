package com.quest.etna.controller;

import com.quest.etna.model.*;
import com.quest.etna.service.ArtworkService;
import com.quest.etna.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artwork")
public class ArtworkController {

    private ArtworkService artworkService;
    private UserService userService;

    public ArtworkController(ArtworkService artworkService, UserService userService) {
        this.artworkService = artworkService;
        this.userService = userService;
    }

    @PostMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> create(@RequestBody Artwork artwork) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userService.getOneByUsername(username);

        if (artwork.getTitle() == "" || artwork.getTitle() == null
                || artwork.getPrice() == null
                || artwork.getTechnique() == null
                || artwork.getImage() == "" || artwork.getImage() == null) {
            return new ResponseEntity<>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        if (userTemp.getRole() == UserRole.ROLE_USER) {
            return new ResponseEntity<>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }
        artwork.setUser(userTemp);
        return new ResponseEntity<>(artworkService.create(artwork), HttpStatus.CREATED);
    }

    @GetMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getAll() {
        List<Artwork> artworks = artworkService.getList();
        if (artworks == null) {
            return new ResponseEntity<>(new JwtError(404, "No artwork found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(artworks, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Integer id) {

        Artwork artwork = artworkService.getOneById(id);
        if (artwork == null) {
            return new ResponseEntity<>(new JwtError(404, "Artwork not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(artwork, HttpStatus.OK);
    }

    @GetMapping(value = "/artist/{user_id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getByUserId(@PathVariable(value = "user_id") Integer user_id) {

        List<Artwork> artworks = artworkService.getByUserId(user_id);
        if (artworks == null) {
            return new ResponseEntity<>(new JwtError(404, "No artwork found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(artworks, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody Artwork artwork) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userService.getOneByUsername(username);
        Artwork artworkTemp = artworkService.getOneById(id);

        if (artworkTemp == null) {
            return new ResponseEntity<>(new JwtError(404, "Artwork not found"), HttpStatus.NOT_FOUND);
        }

        if (userTemp.getRole() == UserRole.ROLE_USER ||
                userTemp.getRole() == UserRole.ROLE_ARTIST && userTemp.getId() != artworkTemp.getUser().getId()) {
            return new ResponseEntity<>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        if (artwork.getTitle() == "" || artwork.getTitle() == null ||
                artwork.getPrice() == null || artwork.getTechnique() == null || artwork.getImage() == "" ||
                artwork.getImage() == null) {
            return new ResponseEntity<>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artworkService.update(id, artwork), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userService.getOneByUsername(username);
        Artwork artworkTemp = artworkService.getOneById(id);

        if (artworkTemp == null) {
            return new ResponseEntity<>(new JwtError(404, "Artwork not found"), HttpStatus.NOT_FOUND);
        }

        if (userTemp.getRole() == UserRole.ROLE_USER ||
                userTemp.getRole() == UserRole.ROLE_ARTIST && userTemp.getId() != artworkTemp.getUser().getId()) {
            return new ResponseEntity<>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        Boolean success = artworkService.delete(id);
        if (success) {
            return ResponseEntity.ok(new JwtDeleteResponse(true));
        }
        return ResponseEntity.ok(new JwtDeleteResponse(false));
    }
}
