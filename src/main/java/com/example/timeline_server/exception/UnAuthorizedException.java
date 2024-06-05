package com.example.timeline_server.exception;

public class UnAuthorizedException extends BaseException {

    public UnAuthorizedException() {
        super(401, "unauthorized.");
    }

    public UnAuthorizedException(String message) {
        super(401, message);
    }
}
