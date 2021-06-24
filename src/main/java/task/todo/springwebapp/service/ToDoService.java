package task.todo.springwebapp.service;

import org.springframework.stereotype.Component;
import task.todo.springwebapp.entities.TaskEntity;
import task.todo.springwebapp.entities.UserEntity;
import task.todo.springwebapp.repositories.TaskRepository;
import task.todo.springwebapp.repositories.UserRepository;

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
        userRepository.save(userEntity);
    }

    public void saveTask(TaskEntity taskEntity){
        taskRepository.save(taskEntity);
    }

}
