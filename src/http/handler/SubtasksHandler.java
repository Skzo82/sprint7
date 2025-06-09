package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.HttpTaskServer;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtasksHandler(TaskManager manager) {
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
                        Subtask subtask = manager.getSubtask(id);
                        if (subtask != null) {
                            sendText(exchange, gson.toJson(subtask));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        List<Subtask> subtasks = manager.getSubtasks();
                        sendText(exchange, gson.toJson(subtasks));
                    }
                    break;
                case "POST":
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    try {
                        if (manager.getSubtask(subtask.getId()) != null) {
                            manager.updateSubtask(subtask);
                        } else {
                            manager.addNewSubtask(subtask);
                        }
                        sendCreated(exchange);
                    } catch (IllegalArgumentException e) {
                        sendHasInteractions(exchange);
                    }
                    break;
                case "DELETE":
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.removeSubtask(id);
                    } else {
                        manager.removeAllSubtasks();
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