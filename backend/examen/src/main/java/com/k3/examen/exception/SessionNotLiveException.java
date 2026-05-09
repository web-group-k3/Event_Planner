package com.k3.examen.exception;

public class SessionNotLiveException extends RuntimeException {
    public SessionNotLiveException(String message) {
        super(message);
    }
}
