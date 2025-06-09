package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.HttpTaskServer;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Task task = manager.getTask(id);
                        if (task != null) {
                            sendText(exchange, gson.toJson(task));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        List<Task> tasks = manager.getTasks();
                        sendText(exchange, gson.toJson(tasks));
                    }
                    break;
                case "POST":
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    try {
                        if (manager.getTask(task.getId()) != null) {
                            manager.updateTask(task);
                        } else {
                            manager.addNewTask(task);
                        }
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendHasInteractions(exchange);
                    }
                    break;
                case "DELETE":
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.removeTask(id);
                    } else {
                        manager.removeAllTasks();
                    }
                    sendCreated(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}