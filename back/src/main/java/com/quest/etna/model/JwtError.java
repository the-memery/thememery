package com.quest.etna.model;

public class JwtError {
    private final int code;
    private final Object error;

    public JwtError(int code, Object error) {
        this.code = code;
        this.error = error;
    }

    public Object getError() {
        return error;
    }

    public int getCode() {
        return code;
    }
}
