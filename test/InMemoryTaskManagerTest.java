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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void добавитьИПолучитьЗадачу() {
        Task задача = new Task("Задача", "Описание задачи", TaskStatus.NEW, java.time.Duration.ofMinutes(60), java.time.LocalDateTime.now());
        int id = manager.addNewTask(задача);
        Task полученная = manager.getTask(id);

        assertNotNull(полученная, "Задача должна быть найдена по id");
        assertEquals(задача.getName(), полученная.getName(), "Имена задач должны совпадать");
        assertEquals(задача.getDescription(), полученная.getDescription(), "Описания задач должны совпадать");
    }

    @Test
    public void добавитьEpicИПодзадачу() {
        Epic эпик = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(эпик);

        Subtask подзадача = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, java.time.Duration.ofMinutes(30), java.time.LocalDateTime.now(), epicId);
        int subtaskId = manager.addNewSubtask(подзадача);

        Epic полученныйЭпик = manager.getEpic(epicId);
        Subtask полученнаяПодзадача = manager.getSubtask(subtaskId);

        assertNotNull(полученныйЭпик, "Эпик должен быть найден по id");
        assertNotNull(полученнаяПодзадача, "Подзадача должна быть найдена по id");
        assertEquals(epicId, полученнаяПодзадача.getEpicId(), "ID эпика должен совпадать");
    }

    @Test
    public void тестУдаленияЗадачи() {
        Task задача = new Task("Тест", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now());
        int id = manager.addNewTask(задача);
        manager.removeTask(id);
        assertNull(manager.getTask(id), "Задача должна быть удалена");
    }

    @Test
    public void тестСтатусаЭпика() {
        Epic эпик = new Epic("Эпик", "Описание");
        int epicId = manager.addNewEpic(эпик);

        Subtask sub1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now(), epicId);
        Subtask sub2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, java.time.Duration.ofMinutes(15), java.time.LocalDateTime.now(), epicId);

        manager.addNewSubtask(sub1);
        manager.addNewSubtask(sub2);

        Epic полученныйЭпик = manager.getEpic(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, полученныйЭпик.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void тестИсторииПросмотров() {
        Task t1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, java.time.Duration.ofMinutes(5), java.time.LocalDateTime.now());
        int id1 = manager.addNewTask(t1);

        Task t2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, java.time.Duration.ofMinutes(5), java.time.LocalDateTime.now());
        int id2 = manager.addNewTask(t2);

        manager.getTask(id1);
        manager.getTask(id2);

        List<Task> история = manager.getHistory();
        assertEquals(2, история.size(), "В истории должно быть 2 задачи");
        assertEquals(id2, история.get(история.size() - 1).getId(), "Последней должна быть вторая задача");
    }

    @Test
    public void тестПолученияВсехЗадач() {
        manager.addNewTask(new Task("Задача 1", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now()));
        manager.addNewTask(new Task("Задача 2", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now()));
        List<Task> список = manager.getTasks();
        assertEquals(2, список.size(), "Должно быть 2 задачи");
    }

    @Test
    public void тестПолученияВсехЭпиков() {
        manager.addNewEpic(new Epic("Эпик 1", "Описание"));
        manager.addNewEpic(new Epic("Эпик 2", "Описание"));
        List<Epic> список = manager.getEpics();
        assertEquals(2, список.size(), "Должно быть 2 эпика");
    }

    @Test
    public void тестПолученияВсехПодзадач() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.addNewEpic(epic);
        manager.addNewSubtask(new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now(), epicId));
        manager.addNewSubtask(new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, java.time.Duration.ofMinutes(10), java.time.LocalDateTime.now(), epicId));
        List<Subtask> список = manager.getSubtasks();
        assertEquals(2, список.size(), "Должно быть 2 подзадачи");
    }

}
