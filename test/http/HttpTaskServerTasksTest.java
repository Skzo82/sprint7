import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
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

public class HttpTaskServerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидался код 201 при добавлении задачи");

        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size(), "Должна быть одна задача в менеджере");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.addNewTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskListType);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Named Task", "With ID", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        int id = manager.addNewTask(task);

        URI uri = URI.create("http://localhost:8080/tasks?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task returned = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), returned.getId());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("To update", "Before", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.addNewTask(task);
        task.setName("Updated");
        task.setDescription("After");

        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", manager.getTask(task.getId()).getName());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("To Delete", "Bye", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        int id = manager.addNewTask(task);

        URI uri = URI.create("http://localhost:8080/tasks?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNull(manager.getTask(id));
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        manager.addNewTask(new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofMinutes(5), now));
        manager.addNewTask(new Task("Task2", "Desc2", TaskStatus.NEW, Duration.ofMinutes(5), now.plusMinutes(6))); // ⏱ no overlap

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());
    }


    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddConflictingTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("T1", "Overlap", TaskStatus.NEW, Duration.ofMinutes(60), now);
        Task task2 = new Task("T2", "Conflict", TaskStatus.NEW, Duration.ofMinutes(30), now.plusMinutes(30));
        manager.addNewTask(task1);

        String json = gson.toJson(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Задача пересекается и должна быть отклонена");
    }
}