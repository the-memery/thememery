package com.quest.etna.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

@RestController
public class DefaultController {

    @GetMapping(value = "/testSuccess")
    @ResponseStatus(HttpStatus.OK)
    public String testSuccess() {
        return "success";
    }

    @GetMapping(value = "/testNotFound")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String testNotFound() {
        return "not found";
    }

    @GetMapping(value = "/testError")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String testError() {
        return "error";
    }
}
