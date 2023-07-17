package com.quest.etna.model;

public class JwtResponse {
    private final Object result;

    public JwtResponse(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
