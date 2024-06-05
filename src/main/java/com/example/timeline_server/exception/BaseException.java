package com.example.timeline_server.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
