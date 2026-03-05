package com.simonatb.bookstore.exceptions;

public class AuthorNotFoundException extends ResourceNotFoundException {

    public AuthorNotFoundException(String message) {
        super(message);
    }

    public AuthorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
