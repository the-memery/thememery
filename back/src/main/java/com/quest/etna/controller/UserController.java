package com.quest.etna.controller;

import com.quest.etna.model.*;
import com.quest.etna.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getAll() {
        List<User> users = userService.getList();
        if (users == null) {
            return new ResponseEntity<Object>(new JwtError(404, "No user found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Integer id) {

        User user = userService.getOneById(id);
        if (user == null) {
            return new ResponseEntity<Object>(new JwtError(404, "User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody User user) {

        if (user == null || user.getUsername() == "" || user.getUsername() == null) { // || user.getRole() == null) {
            return new ResponseEntity<Object>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userService.getOneByUsername(username);
        if (userTemp == null) {
            return new ResponseEntity<Object>(
                    new JwtError(404, "You modified your username. Please authenticate again"), null,
                    HttpStatus.NOT_FOUND);
        }
        if (userTemp.getRole() == UserRole.ROLE_USER && id != userTemp.getId()
                || (userTemp.getRole() == UserRole.ROLE_ARTIST) && id != userTemp.getId()) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        User updatedUser = userService.update(id, user);
        if (updatedUser == null) {
            return new ResponseEntity<Object>(new JwtError(404, "User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userService.getOneByUsername(username);

        if (userTemp == null) {
            return ResponseEntity.ok(new JwtDeleteResponse(false));
        }

        if (userTemp.getRole() == UserRole.ROLE_USER && id != userTemp.getId()) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        Boolean success = userService.delete(id);
        if (!success) {
            return ResponseEntity.ok(new JwtDeleteResponse(false));
        }
        return ResponseEntity.ok(new JwtDeleteResponse(true));
    }
}
