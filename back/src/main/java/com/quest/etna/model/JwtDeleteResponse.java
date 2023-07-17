package com.quest.etna.model;

public class JwtDeleteResponse {
    private final boolean success;

    public JwtDeleteResponse(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }
}
