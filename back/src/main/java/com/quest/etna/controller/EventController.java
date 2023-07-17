package com.quest.etna.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import com.quest.etna.model.Event;
import com.quest.etna.model.JwtDeleteResponse;
import com.quest.etna.model.JwtError;
import com.quest.etna.model.User;
import com.quest.etna.model.UserRole;
import com.quest.etna.repositories.AddressRepository;
import com.quest.etna.repositories.UserRepository;
import com.quest.etna.service.EventService;

@RestController
@RequestMapping("/event")
public class EventController {

    private EventService eventService;
    private AddressRepository addressRepository;
    private UserRepository userRepository;

    public EventController(EventService eventService, AddressRepository addressRepository,
            UserRepository userRepository) {

        this.eventService = eventService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getList() {
        return new ResponseEntity<Object>(eventService.getList(), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    @ResponseBody
    public ResponseEntity<Object> getAllOrderByDate() {
        return new ResponseEntity<Object>(eventService.getAllOrderByDate(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> getOneById(@PathVariable(value = "id") Integer id) {

        Event event = eventService.getOneById(id);

        return new ResponseEntity<Object>(event, HttpStatus.OK);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createEvent(@RequestBody Event event) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);

        System.out.println(event);

        if (userTemp.getRole() != UserRole.ROLE_ADMIN) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        if (event.getName() == "" || event.getType() == null || event.getDate() == null
            || event.getImage() == "" || event.getImage() == null || event.getAddress() == null) {
            return new ResponseEntity<Object>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Object>(eventService.create(event), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEvent(@PathVariable("id") Integer id, @RequestBody Event event) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        Event eventTemp = eventService.getOneById(id);

        if (eventTemp == null) {
            return new ResponseEntity<Object>(new JwtError(404, "Event not found"), HttpStatus.NOT_FOUND);
        }

        if (userTemp.getRole() != UserRole.ROLE_ADMIN) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        if (event.getName() == "") { // || event.getType() == null || event.getDate() == null || event.getAddress()
                                     // == null) {
            return new ResponseEntity<Object>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Object>(eventService.update(id, event), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("id") Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        Event eventTemp = eventService.getOneById(id);

        if (eventTemp == null) {
            // return new ResponseEntity<Object>(new JwtError(404, "Address not found"),
            // HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(new JwtDeleteResponse(false));
        }

        if (userTemp.getRole() != UserRole.ROLE_ADMIN) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        Boolean success = eventService.delete(id);
        if (success) {
            return ResponseEntity.ok(new JwtDeleteResponse(true));
        }

        return ResponseEntity.ok(new JwtDeleteResponse(false));
    }
}
