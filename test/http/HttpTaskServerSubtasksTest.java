import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
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

public class HttpTaskServerSubtasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;
    private Epic epic;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Container");
        manager.addNewEpic(epic);
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now(), epic.getId());
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "GetAll", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> list = gson.fromJson(response.body(), listType);
        assertEquals(1, list.size());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("ById", "Test", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask result = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), result.getId());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Update", "Before", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(subtask);
        subtask.setName("Updated");

        String json = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", manager.getSubtask(subtask.getId()).getName());
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Delete", "Me", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNull(manager.getSubtask(subtask.getId()));
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        manager.addNewSubtask(new Subtask("S1", "Desc", TaskStatus.NEW, Duration.ofMinutes(5), now, epic.getId()));
        manager.addNewSubtask(new Subtask("S2", "Desc", TaskStatus.NEW, Duration.ofMinutes(5), now.plusMinutes(6), epic.getId())); // ✅ senza sovrapposizione

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void testAddConflictingSubtask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Subtask s1 = new Subtask("S1", "Time", TaskStatus.NEW, Duration.ofMinutes(60), now, epic.getId());
        Subtask s2 = new Subtask("S2", "Overlap", TaskStatus.NEW, Duration.ofMinutes(30), now.plusMinutes(30), epic.getId());
        manager.addNewSubtask(s1);

        String json = gson.toJson(s2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testGetNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
