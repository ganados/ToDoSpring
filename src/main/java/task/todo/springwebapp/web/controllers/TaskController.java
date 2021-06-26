package task.todo.springwebapp.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import task.todo.springwebapp.SpringwebappApplication;
import task.todo.springwebapp.entities.TaskEntity;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.exceptions.AuthorizationException;
import task.todo.springwebapp.web.exceptions.BadRequestException;
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
    public String createTask(@RequestBody TaskEntity taskEntity, @RequestHeader("auth") String header){
        UserEntity userEntity;

        userEntity = getUserEntity(header,"TaskPost");

        try{
            validateTask(taskEntity);
        } catch(BadRequestException exception){
            LOGGER.warning("TaskPost::validateTask: BadRequest exception");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing task data", exception);
        }
        LOGGER.info("TaskPost::saveTask: Saving task...");
        taskEntity.setUuid();
        taskEntity.setUsername(userEntity.getUsername());
        toDoService.saveTask(taskEntity);

        return taskEntity.getUuid().toString();
    }

    private UserEntity getUserEntity(@RequestHeader("auth") String header, String s) {
        UserEntity userEntity;
        try {
            UserDecoder userDecoder = new UserDecoder();
            userEntity = userDecoder.decodeUser(header);
        } catch (AuthenticationException exception) {
            LOGGER.warning(s + "::userDecoder: Authorization exception");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing headers or data", exception);
        } catch (BadRequestException exception) {
            LOGGER.warning(s + "::userDecoder: BadRequest exception");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid headers", exception);
        }

        try {
            validateUser(userEntity);
        } catch (AuthorizationException exception) {
            LOGGER.warning(s + "::validateUser: Unauthorized exception");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user or invalid password", exception);
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
        if(!(tempUser.getPassword().equals(userEntity.getPassword()))){
            throw new AuthorizationException();
        }
    }

    @ResponseBody
    @GetMapping("todo/task")
    public List<String> getUserTaskList(@RequestHeader("auth") String header){
        UserEntity userEntity = getUserEntity(header, "TaskGetTaskList");

        LOGGER.info("TaskGetTasks::getUserTaskList: Returning tasks list");
        return getTasks(userEntity);
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
    public String getUserTask(@RequestHeader("auth") String header, @PathVariable UUID id){
        UserEntity userEntity = getUserEntity(header, "getUserTask");
        TaskEntity taskEntity;
        try {
            taskEntity = checkTaskRequest(userEntity, id);
        } catch(BadRequestException exception){
            LOGGER.warning("TaskGetTaskDetails::checkTaskRequest: Task not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found", exception);
        } catch(taskBelongToOtherUserException exception){
            LOGGER.warning("TaskGetTaskDetails::checkTaskRequest: Task belong to another user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Task belong to another user", exception);
        }
        LOGGER.warning("TaskGetTaskDetails::getUserTask: Returning task details");
        return taskEntity.toString();
    }

    TaskEntity checkTaskRequest(UserEntity userEntity, UUID id) {
        TaskEntity taskEntity;
        try {
            taskEntity = toDoService.getTaskRepository().query(id);
        } catch (NullPointerException exception) {
            throw new BadRequestException();
        }
        if (!taskEntity.getUsername().equals(userEntity.getUsername())) {
            throw new taskBelongToOtherUserException();
        }

        return taskEntity;
    }

}
