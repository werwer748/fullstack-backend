package org.hugo.apiserver.util;

public class CustomJwtException extends RuntimeException {

    public CustomJwtException(String message) {
        super(message);
    }
}
