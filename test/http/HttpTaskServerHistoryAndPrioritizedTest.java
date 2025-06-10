import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;
import http.HttpTaskServer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerHistoryAndPrioritizedTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private final Gson gson = Managers.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу и добавляем в историю
        Task task = new Task("Test Task", "History test",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        int id = manager.addNewTask(task);
        manager.getTask(id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа от /history");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskListType);

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(id, history.get(0).getId(), "ID задачи в истории не совпадает");
    }

    @Test
    public void testEmptyHistory() throws IOException, InterruptedException {
        // проверяем поведение при пустой истории
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа от /history (пустая история)");
        assertEquals("[]", response.body(), "Ожидался пустой список истории");
    }

    @Test
    public void testHistoryAfterDeletion() throws IOException, InterruptedException {
        // добавляем задачу, затем удаляем и проверяем, что история пуста
        Task task = new Task("To delete", "Test",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        int id = manager.addNewTask(task);
        manager.getTask(id);
        manager.deleteTaskById(id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body(), "История должна быть пустой после удаления задачи");
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Добавляем задачи с различными временем начала, избегая пересечений
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 10, 0));
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2022, 1, 1, 10, 16));

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Неверный код ответа от /prioritized");

        // Проверяем, что задачи вернулись в правильном порядке
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskListType);

        assertEquals(2, tasks.size(), "Неверное количество задач в ответе");
        assertEquals(task1.getName(), tasks.get(0).getName(), "Первая задача в списке неправильная");
        assertEquals(task2.getName(), tasks.get(1).getName(), "Вторая задача в списке неправильная");
    }
}
