package task.todo.springwebapp.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import task.todo.springwebapp.SpringwebappApplication;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.exceptions.BadRequestException;
import task.todo.springwebapp.web.exceptions.UserAlreadyExistsException;

import java.util.logging.Logger;

@RestController
public class UserController extends Exception{

    private static final Logger LOGGER = Logger.getLogger(SpringwebappApplication.class.getName());

    private final ToDoService toDoService;

    public UserController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @ResponseBody
    @PostMapping("/todo/user")
    public ResponseEntity<String> createUser(@RequestBody UserEntity userEntity) {

        try {
            toDoService.saveUser(userEntity);
        }catch(BadRequestException exception){
            LOGGER.warning("UserPost::validateUser: BadRequest exception");
            return new ResponseEntity<String>("Invalid body", HttpStatus.BAD_REQUEST);
        }catch(UserAlreadyExistsException exception){
            LOGGER.warning("UserPost::validateUser: Conflict exception");
            return new ResponseEntity<String>("User exists", HttpStatus.CONFLICT);
        }

        LOGGER.info("UserPost::saveUser: Saving user successful");

        return new ResponseEntity<String>(userEntity.getUsername(), HttpStatus.CREATED);
    }
    //TODO: przeniesc walidacje itp do todoService
}
