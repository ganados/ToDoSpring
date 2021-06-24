package task.todo.springwebapp.web.utils;

public enum HttpPaths {
    APP_PATH("http://localhost:8080"),
    TODO("/todo"),
    USER("/user"),
    TASK("/task");

    private final String path;

    HttpPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
