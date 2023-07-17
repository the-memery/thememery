package com.quest.etna.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import com.quest.etna.model.Address;
import com.quest.etna.model.JwtDeleteResponse;
import com.quest.etna.model.JwtError;
import com.quest.etna.model.User;
import com.quest.etna.model.UserRole;
import com.quest.etna.repositories.UserRepository;
//import com.quest.etna.repositories.AddressRepository;
import com.quest.etna.service.AddressService;
import com.quest.etna.service.UserService;

@RestController
@RequestMapping("/address")
public class AddressController {

    // @Autowired
    private AddressService addressService;
    private UserRepository userRepository;

    public AddressController(AddressService addressService, UserRepository userRepository) {

        this.addressService = addressService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    // public ResponseEntity<List<Address>> getList(@RequestParam(defaultValue =
    // "1") Integer page, @RequestParam(defaultValue = "5") Integer limit) {
    // public ResponseEntity<List<Address>> getList() {
    public ResponseEntity<Object> getList() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        List<Address> address = addressService.getList();

        if (userTemp.getRole() == UserRole.ROLE_USER) {
            // if (address == null) {
            // return new ResponseEntity<Object>(new JwtError(404, "You haven't add
            // addresses yet"),
            // HttpStatus.NOT_FOUND);
            // }
            return new ResponseEntity<Object>(userTemp.getAddress(), HttpStatus.OK);
        }

        // if (address == null) {
        // return new ResponseEntity<Object>(new JwtError(404, "No address found"),
        // HttpStatus.NOT_FOUND);
        // }
        return new ResponseEntity<Object>(addressService.getList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> getOneById(@PathVariable(value = "id") Integer id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        Address address = addressService.getOneById(id);

        // if (address == null) {
        // return new ResponseEntity<Object>(new JwtError(404, "Address not found"),
        // HttpStatus.NOT_FOUND);
        // }

        if (userTemp.getRole() == UserRole.ROLE_USER && userTemp.getId() != address.getUser().getId()) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Object>(address, HttpStatus.OK);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createAddress(@RequestBody Address address) {

        if (address.getStreet() == "" || address.getCity() == "" || address.getPostalCode() == ""
                || address.getCountry() == "") {
            return new ResponseEntity<Object>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.getByUsername(username);
            address.setUser(user);
        }

        return new ResponseEntity<Object>(addressService.create(address), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAddress(@PathVariable("id") Integer id, @RequestBody Address address) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        Address addressTemp = addressService.getOneById(id);

        if (addressTemp == null) {
            return new ResponseEntity<Object>(new JwtError(404, "Address not found"), HttpStatus.NOT_FOUND);
        }

        if (userTemp.getRole() == UserRole.ROLE_USER && userTemp.getId() != addressTemp.getUser().getId()) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        if (address.getStreet() == "" || address.getCity() == "" || address.getPostalCode() == ""
                || address.getCountry() == "") {
            return new ResponseEntity<Object>(new JwtError(400, "Invalid request"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Object>(addressService.update(id, address), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAddress(@PathVariable("id") Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userTemp = userRepository.getByUsername(username);
        Address addressTemp = addressService.getOneById(id);

        if (addressTemp == null) {
            // return new ResponseEntity<Object>(new JwtError(404, "Address not found"),
            // HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(new JwtDeleteResponse(false));
        }

        if (userTemp.getRole() == UserRole.ROLE_USER && userTemp.getId() != addressTemp.getUser().getId()) {
            return new ResponseEntity<Object>(new JwtError(403, "You don't have the permission to perform this action"),
                    HttpStatus.FORBIDDEN);
        }

        Boolean success = addressService.delete(id);
        if (success) {
            return ResponseEntity.ok(new JwtDeleteResponse(true));
        }

        return ResponseEntity.ok(new JwtDeleteResponse(false));
    }
}
