package task.todo.springwebapp.web.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class AuthorizationException extends RuntimeException {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handle(RuntimeException exception){
        exception.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
