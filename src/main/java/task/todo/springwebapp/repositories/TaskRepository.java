package task.todo.springwebapp.repositories;

import task.todo.springwebapp.entities.TaskEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
public class TaskRepository implements Repository<UUID, TaskEntity> {
    private Map<UUID, TaskEntity> taskEntityMap = new HashMap<>();

    @Override
    public UUID save(TaskEntity taskEntity){
        taskEntityMap.put(taskEntity.getUuid(), taskEntity);

        return taskEntity.getUuid();
    }

    @Override
    public TaskEntity query(UUID uuid){
        return taskEntityMap.get(uuid);
    }

    @Override
    public List<TaskEntity> query(Predicate<TaskEntity> condition) {
        return taskEntityMap
                .values()
                .stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public TaskEntity update(UUID uuid, TaskEntity taskEntity) {
        return taskEntityMap.replace(uuid, taskEntity);
    }

    @Override
    public boolean delete(UUID uuid) {
        return taskEntityMap.remove(uuid) != null;
    }

    public List<TaskEntity> query(String username) {
        return taskEntityMap
                .values()
                .stream()
                .filter(taskEntity -> taskEntity.getUsername().equals(username))
                .collect(Collectors.toList());
    }
}
