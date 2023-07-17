package com.quest.etna.model;

public class JwtResponseToken {
    private Object token;

    public JwtResponseToken() {
    };

    public JwtResponseToken(Object token) {
        this.token = token;
    }

    public Object getToken() {
        return this.token;
    }
}