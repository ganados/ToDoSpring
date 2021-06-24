package task.todo.springwebapp.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import task.todo.springwebapp.entities.TaskEntity;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.exceptions.AuthorizationException;
import task.todo.springwebapp.web.exceptions.BadRequestException;
import task.todo.springwebapp.web.utils.UserDecoder;

import javax.naming.AuthenticationException;

@RestController
public class TaskController {

    private final ToDoService toDoService;

    public TaskController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @ResponseBody
    @PostMapping("todo/task")
    public String createTask(@RequestBody TaskEntity taskEntity, @RequestHeader("auth") String header){
        UserEntity userEntity;

        try{
            UserDecoder userDecoder = new UserDecoder();
            userEntity = userDecoder.decodeUser(header);
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing headers or data", exception);
        } catch(BadRequestException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid headers", exception);
        }

        try{
            validateUser(userEntity);
        } catch(AuthorizationException exception){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user or invalid password", exception);
        }

        try{
            validateTask(taskEntity);
        } catch(BadRequestException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing task data", exception);
        }
        taskEntity.setUuid();
        taskEntity.setUsername(userEntity.getUsername());
        toDoService.saveTask(taskEntity);

        return taskEntity.getUuid().toString();
    }

    private void validateTask(TaskEntity taskEntity){
        if(taskEntity == null
        || taskEntity.getDescription() == null || taskEntity.getDescription().isBlank()){
            throw new BadRequestException();
        }
    }

    private void validateUser(UserEntity userEntity){
        UserEntity tempUser;
        try {
            tempUser = toDoService.getUserRepository().query(userEntity.getUsername());
        } catch(NullPointerException exception){
            throw new AuthorizationException();
        }
        if(!(tempUser.getPassword().equals(userEntity.getPassword()))){
            throw new AuthorizationException();
        }
    }
}
