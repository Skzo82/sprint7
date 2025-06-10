package http.handler;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicSubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicSubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                String[] parts = path.split("/");
                if (parts.length == 4 && "epics".equals(parts[1]) && "subtasks".equals(parts[3])) {
                    int epicId = Integer.parseInt(parts[2]);
                    List<Subtask> subtasks = manager.getEpicSubtasks(epicId);
                    sendText(exchange, gson.toJson(subtasks));
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}
