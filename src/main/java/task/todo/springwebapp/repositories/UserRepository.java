package task.todo.springwebapp.repositories;

import task.todo.springwebapp.entities.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
public class UserRepository implements Repository<String, UserEntity> {

    private final Map<String, UserEntity> userEntityMap = new HashMap<>();

    @Override
    public String save(UserEntity userEntity) {
        userEntityMap.put(userEntity.getUsername(), userEntity);

        return userEntity.getUsername();
    }

    @Override
    public UserEntity query(String s) {
        return userEntityMap.get(s);
    }

    @Override
    public List<UserEntity> query(Predicate<UserEntity> condition) {
        return userEntityMap
                .values()
                .stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity update(String s, UserEntity userEntity) {
        return userEntityMap.replace(s, userEntity);
    }

    @Override
    public boolean delete(String s) {
        return userEntityMap.remove(s) != null;
    }

}
