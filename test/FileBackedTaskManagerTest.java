package test;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private Path testFile;
    private TaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = Files.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Test
    public void тестСохраненияИЗагрузки() {
        // Создаем задачи, эпик и подзадачу
        Task задача = new Task("Задача", "Описание", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        int taskId = manager.addNewTask(задача);

        Epic эпик = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(эпик);

        Subtask подзадача = new Subtask("Подзадача", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now(), epicId);
        int subtaskId = manager.addNewSubtask(подзадача);

        // Пересоздаем менеджер и проверяем, что всё загрузилось из файла
        TaskManager loaded = new FileBackedTaskManager(testFile);

        Task taskFromFile = loaded.getTask(taskId);
        Epic epicFromFile = loaded.getEpic(epicId);
        Subtask subtaskFromFile = loaded.getSubtask(subtaskId);

        assertNotNull(taskFromFile, "Задача должна быть загружена из файла");
        assertNotNull(epicFromFile, "Эпик должен быть загружен из файла");
        assertNotNull(subtaskFromFile, "Подзадача должна быть загружена из файла");
        assertEquals("Задача", taskFromFile.getName());
        assertEquals("Эпик", epicFromFile.getName());
        assertEquals("Подзадача", subtaskFromFile.getName());
    }
}
