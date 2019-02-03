package pl.inz.costshare.server.config;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.inz.costshare.server.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolationException;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleError(Exception ex, WebRequest request) {
        if (ex instanceof ResourceNotFoundException) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
        if (ex instanceof UsernameNotFoundException) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        if (ex instanceof ConstraintViolationException) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        ex.printStackTrace();
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}