package task.todo.springwebapp.entities;

import java.util.UUID;

public class TaskEntity {
    private String username;

    private String description;
    private String due;
    private UUID uuid;

    public TaskEntity(String username, String description, String due) {
        this.username = username;
        this.description = description;
        this.due = due;
    }

    public TaskEntity(String due, String description) {
        this.due = due;
        this.description = description;
    }

    public TaskEntity(String description){
        this.description = description;
        this.due = "";
    }

    public TaskEntity(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUuid() {
        this.uuid = UUID.randomUUID();
    }
}
