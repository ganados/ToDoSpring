package task.todo.springwebapp.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.exceptions.BadRequestException;
import task.todo.springwebapp.web.exceptions.UserAlreadyExistsException;

@RestController
public class UserController extends Exception{


    private final ToDoService toDoService;

    public UserController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @ResponseBody
    @PostMapping("/todo/user")
    public String createUser(@RequestBody UserEntity userEntity) throws Exception {

        try {
            validateUser(userEntity);
        }catch(BadRequestException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing data (username or password)", exception);
        }catch(UserAlreadyExistsException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists", exception);
        }

        toDoService.saveUser(userEntity);

        return userEntity.getUsername();
    }
    private void validateUser(UserEntity userEntity) throws Exception {
        if(userEntity == null
                || userEntity.getUsername() == null || userEntity.getPassword() == null
                || userEntity.getUsername().isBlank() || userEntity.getPassword().isBlank()) {
            throw new BadRequestException();
        }
        if(toDoService.getUserRepository().query(userEntity.getUsername()) != null) {
            throw new UserAlreadyExistsException();
        }
    }
}
