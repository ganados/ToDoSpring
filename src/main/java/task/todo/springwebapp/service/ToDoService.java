package task.todo.springwebapp.service;

import org.springframework.stereotype.Component;
import task.todo.springwebapp.entities.TaskEntity;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.repositories.TaskRepository;
import task.todo.springwebapp.repositories.UserRepository;
import task.todo.springwebapp.web.exceptions.BadRequestException;
import task.todo.springwebapp.web.exceptions.UserAlreadyExistsException;

@Component
public class ToDoService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ToDoService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void saveUser(UserEntity userEntity){
        validateUser(userEntity);

        userRepository.save(userEntity);
    }

    private void validateUser(UserEntity userEntity) {
        if (userEntity == null)
            throw new BadRequestException();

        String username = userEntity.getUsername();
        String password = userEntity.getPassword();

        if(username == null
                || password == null
                || username.isBlank()
                || password.isBlank())
            throw new BadRequestException();

        if (userRepository.query(username) != null) {
            throw new UserAlreadyExistsException();
        }
    }

    public void saveTask(TaskEntity taskEntity){
        taskRepository.save(taskEntity);
    }

}
