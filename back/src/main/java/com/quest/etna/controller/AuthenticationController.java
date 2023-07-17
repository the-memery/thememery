package com.quest.etna.controller;

import com.quest.etna.config.JwtUserDetailsService;

import com.quest.etna.model.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.quest.etna.config.JwtTokenUtil;
import com.quest.etna.config.WebSecurityConfig;
import com.quest.etna.repositories.UserRepository;

import java.util.Objects;

@RestController
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final WebSecurityConfig webSecurityConfig;

    public AuthenticationController(UserRepository userRepository, AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService,
            WebSecurityConfig webSecurityConfig) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.webSecurityConfig = webSecurityConfig;
    }

    @CrossOrigin
    @PostMapping(value = "/register", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<Object> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO == null || userDTO.getUsername() == null || Objects.equals(userDTO.getUsername(), "")
                || userDTO.getPassword() == null
                || Objects.equals(userDTO.getPassword(), "")) {
            return new ResponseEntity<>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        if (jwtUserDetailsService.loadUserByUsername(userDTO.getUsername()) != null) {
            return new ResponseEntity<>(new JwtError(409, "This username already exists"), HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(webSecurityConfig.passwordEncoder().encode(userDTO.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        User createdUser = userRepository.save(user);
        return new ResponseEntity<>(new UserDetails(createdUser.getUsername(), createdUser.getRole()),
                HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping(value = "/authenticate", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<Object> authenticate(@RequestBody UserDTO userDTO) {
        if (userDTO == null || userDTO.getUsername() == null || Objects.equals(userDTO.getUsername(), "")
                || userDTO.getPassword() == null
                || Objects.equals(userDTO.getPassword(), "")) {
            return new ResponseEntity<>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        try {
            JwtUserDetails jwtUserDetails = jwtUserDetailsService.loadUserByUsername(userDTO.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtUserDetails.getUsername(), userDTO.getPassword()));
            return new ResponseEntity<>(
                    new JwtResponseToken(
                            jwtTokenUtil
                                    .generateToken(jwtUserDetailsService.loadUserByUsername(userDTO.getUsername()))),
                    HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new JwtError(401, "Invalid credentials"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/me", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    private ResponseEntity<UserDetails> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            JwtUserDetails jwtUserDetails = jwtUserDetailsService.loadUserByUsername(username);
            return new ResponseEntity<>(
                    new UserDetails(jwtUserDetails.getUsername(), jwtUserDetails.getRole()), HttpStatus.OK);
        }
        return null;
    }

    @CrossOrigin
    @PostMapping(value = "/validate", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    private ResponseEntity<JwtResponse> isTokenValid(@RequestBody JwtResponseToken jwtToken) {
        if (Objects.equals(jwtToken.getToken(), null)) {
            return new ResponseEntity<>(new JwtResponse(false), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return new ResponseEntity<>(new JwtResponse(false), HttpStatus.UNAUTHORIZED);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JwtUserDetails jwtUserDetails = jwtUserDetailsService.loadUserByUsername(username);

        if (!jwtTokenUtil.validateToken(jwtToken.getToken().toString(), jwtUserDetails)) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return new ResponseEntity<>(new JwtResponse(false), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new JwtResponse(true), HttpStatus.OK);
    }
}
