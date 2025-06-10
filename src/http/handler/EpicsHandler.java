package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = Managers.getGson();

    public EpicsHandler(TaskManager manager) {
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
                        Epic epic = manager.getEpic(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        List<Epic> epics = manager.getEpics();
                        sendText(exchange, gson.toJson(epics));
                    }
                    break;
                case "POST":
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    try {
                        manager.updateEpic(epic);
                    } catch (NoSuchElementException e) {
                        manager.addNewEpic(epic);
                    }
                    sendCreated(exchange);

                    break;
                case "DELETE":
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.removeEpic(id);
                    } else {
                        manager.removeAllEpics();
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
