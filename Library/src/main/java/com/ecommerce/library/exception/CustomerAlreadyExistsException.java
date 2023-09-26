package com.ecommerce.library.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String s) {
        super(s);
    }
}
