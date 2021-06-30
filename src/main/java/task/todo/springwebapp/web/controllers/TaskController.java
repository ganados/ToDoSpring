package task.todo.springwebapp.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import task.todo.springwebapp.SpringwebappApplication;
import task.todo.springwebapp.entities.TaskEntity;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.exceptions.AuthorizationException;
import task.todo.springwebapp.web.exceptions.BadRequestException;
import task.todo.springwebapp.web.exceptions.NoUserException;
import task.todo.springwebapp.web.exceptions.taskBelongToOtherUserException;
import task.todo.springwebapp.web.utils.UserDecoder;

import javax.naming.AuthenticationException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
public class TaskController {

    private static final Logger LOGGER = Logger.getLogger(SpringwebappApplication.class.getName());

    private final ToDoService toDoService;

    public TaskController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @ResponseBody
    @PostMapping("todo/task")
    public ResponseEntity<String> createTask(@RequestBody TaskEntity taskEntity, @RequestHeader("auth") String header){
        UserEntity userEntity;

        try{
            userEntity = getUserEntity(header, "TaskPost");
            validateTask(taskEntity);
        } catch(BadRequestException exception){
            LOGGER.warning("TaskPost::validateTask: BadRequest exception");
            return new ResponseEntity<>("Missing task data", HttpStatus.BAD_REQUEST);
        } catch(NoUserException exception){
            LOGGER.warning("TaskPost::validateUser: Authorization exception");
            return new ResponseEntity<>("User not found or invalid password", HttpStatus.UNAUTHORIZED);
        }
        LOGGER.info("TaskPost::saveTask: Saving task...");
        taskEntity.setUuid();
        taskEntity.setUsername(userEntity.getUsername());
        toDoService.saveTask(taskEntity);

        return new ResponseEntity<>(taskEntity.getUuid().toString(), HttpStatus.OK);
    }

    private UserEntity getUserEntity(String header, String s) {
        UserEntity userEntity;
        try {
            UserDecoder userDecoder = new UserDecoder();
            userEntity = userDecoder.decodeUser(header);
        } catch (AuthorizationException exception) {
            LOGGER.warning(s + "::userDecoder: Authorization exception");
            throw new BadRequestException();
        } catch (BadRequestException exception) {
            LOGGER.warning(s + "::userDecoder: BadRequest exception");
            throw new BadRequestException();
        }

        try {
            validateUser(userEntity);
        } catch (AuthorizationException exception) {
            LOGGER.warning(s + "::validateUser: Unauthorized exception");
            throw new NoUserException();
        }
        return userEntity;
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
        if(tempUser == null){
            throw new AuthorizationException();
        }
        if(!(tempUser.getPassword().equals(userEntity.getPassword()))){
            throw new AuthorizationException();
        }
    }

    @ResponseBody
    @GetMapping("todo/task")
    public ResponseEntity<String> getUserTaskList(@RequestHeader("auth") String header){
        UserEntity userEntity = getUserEntity(header, "TaskGetTaskList");

        LOGGER.info("TaskGetTasks::getUserTaskList: Returning tasks list");
        return new ResponseEntity<>(getTasks(userEntity).toString(), HttpStatus.OK);
    }

    private List<String> getTasks(UserEntity userEntity){
        List<String> taskList = new LinkedList<>();
        List<TaskEntity> taskEntityList = toDoService.getTaskRepository().query(userEntity.getUsername());
        if(taskEntityList == null){
            return taskList;
        }
        for(TaskEntity taskEntity : taskEntityList){
            taskList.add(taskEntity.toString());
        }
        return taskList;
    }

    @ResponseBody
    @GetMapping("todo/task/{id}")
    public ResponseEntity<String> getUserTask(@RequestHeader("auth") String header, @PathVariable UUID id){
        UserEntity userEntity = getUserEntity(header, "getUserTask");
        TaskEntity taskEntity;
        try {
            taskEntity = checkTaskRequest(userEntity, id);
        } catch(BadRequestException exception){
            LOGGER.warning("TaskGetTaskDetails::checkTaskRequest: Task not found");
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        } catch(taskBelongToOtherUserException exception){
            LOGGER.warning("TaskGetTaskDetails::checkTaskRequest: Task belong to another user");
            return new ResponseEntity<>("Task belong to another user", HttpStatus.FORBIDDEN);
        }
        LOGGER.warning("TaskGetTaskDetails::getUserTask: Returning task details");
        return new ResponseEntity<>(taskEntity.toString(), HttpStatus.OK);
    }

    private TaskEntity checkTaskRequest(UserEntity userEntity, UUID id) {
        TaskEntity taskEntity;
        try {
            taskEntity = toDoService.getTaskRepository().query(id);
        } catch (NullPointerException exception) {
            throw new BadRequestException();
        }
        if(taskEntity == null){
            throw new BadRequestException();
        }
        if (!taskEntity.getUsername().equals(userEntity.getUsername())) {
            throw new taskBelongToOtherUserException();
        }

        return taskEntity;
    }

}
