package com.simonatb.bookstore.exceptions;

public class TokenNotFoundException extends ResourceNotFoundException {

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
