package es.maryshopping.backend.shared_kernel.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    ResponseEntity<String> badRequest(Exception exception){
        return  ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler({BusinessRuleException.class,DuplicatedEntityException.class})
    ResponseEntity<String> conflict(Exception exception){
        return  ResponseEntity.status(409).body(exception.getMessage());
    }
    @ExceptionHandler({Exception.class})
    ResponseEntity<String> anyException(Exception exception){
        return  ResponseEntity.status(500).body(exception.getMessage());
    }
}
