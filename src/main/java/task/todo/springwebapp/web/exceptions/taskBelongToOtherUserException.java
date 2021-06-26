package task.todo.springwebapp.web.exceptions;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class taskBelongToOtherUserException extends RuntimeException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handle(){
        return ResponseEntity.badRequest().build();
    }
}
