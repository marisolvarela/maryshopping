package es.maryshopping.backend.shared_kernel.exceptions;


public class CustomerDataAccessForbiddenException extends RuntimeException {

    public CustomerDataAccessForbiddenException(String message) {
        super(message);
    }

    public CustomerDataAccessForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

