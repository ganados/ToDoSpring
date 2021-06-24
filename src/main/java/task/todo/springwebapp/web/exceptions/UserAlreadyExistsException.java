package task.todo.springwebapp.web.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.io.IOException;

@ControllerAdvice
public class UserAlreadyExistsException extends AuthenticationException {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handle(AuthenticationException exception){
        exception.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
