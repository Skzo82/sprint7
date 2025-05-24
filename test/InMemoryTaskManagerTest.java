package test;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import managers.Managers;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void addAndGetTaskTest() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 5, 1, 9, 0));
        int id = manager.addNewTask(task);
        Task received = manager.getTask(id);

        assertNotNull(received, "Задача должна быть найдена по id");
        assertEquals(task.getName(), received.getName(), "Имена задач должны совпадать");
        assertEquals(task.getDescription(), received.getDescription(), "Описания задач должны совпадать");
    }

    @Test
    public void addEpicAndSubtaskTest() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 5, 1, 10, 0), epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        Epic receivedEpic = manager.getEpic(epicId);
        Subtask receivedSubtask = manager.getSubtask(subtaskId);

        assertNotNull(receivedEpic, "Эпик должен быть найден по id");
        assertNotNull(receivedSubtask, "Подзадача должна быть найдена по id");
        assertEquals(epicId, receivedSubtask.getEpicId(), "ID эпика должен совпадать");
    }

    @Test
    public void removeTaskTest() {
        Task task = new Task("Тест", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 11, 0));
        int id = manager.addNewTask(task);
        manager.removeTask(id);
        assertNull(manager.getTask(id), "Задача должна быть удалена");
    }

    @Test
    public void epicStatusTest() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.addNewEpic(epic);

        // ВНИМАНИЕ: разные времена!
        Subtask sub1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 12, 0), epicId);
        Subtask sub2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, Duration.ofMinutes(15), LocalDateTime.of(2024, 5, 1, 13, 0), epicId);

        manager.addNewSubtask(sub1);
        manager.addNewSubtask(sub2);

        Epic receivedEpic = manager.getEpic(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, receivedEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void viewHistoryTest() {
        Task t1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 14, 0));
        int id1 = manager.addNewTask(t1);

        Task t2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 15, 0));
        int id2 = manager.addNewTask(t2);

        manager.getTask(id1);
        manager.getTask(id2);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "В истории должно быть 2 задачи");
        assertEquals(id2, history.get(history.size() - 1).getId(), "Последней должна быть вторая задача");
    }

    @Test
    public void getAllTasksTest() {
        manager.addNewTask(new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 16, 0)));
        manager.addNewTask(new Task("Задача 2", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 17, 0)));
        List<Task> list = manager.getTasks();
        assertEquals(2, list.size(), "Должно быть 2 задачи");
    }

    @Test
    public void getAllEpicsTest() {
        manager.addNewEpic(new Epic("Эпик 1", "Описание"));
        manager.addNewEpic(new Epic("Эпик 2", "Описание"));
        List<Epic> list = manager.getEpics();
        assertEquals(2, list.size(), "Должно быть 2 эпика");
    }

    @Test
    public void getAllSubtasksTest() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.addNewEpic(epic);
        manager.addNewSubtask(new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 18, 0), epicId));
        manager.addNewSubtask(new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 1, 19, 0), epicId));
        List<Subtask> list = manager.getSubtasks();
        assertEquals(2, list.size(), "Должно быть 2 подзадачи");
    }
}
