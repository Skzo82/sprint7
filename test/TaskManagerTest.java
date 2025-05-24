package test;

import managers.TaskManager;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void shouldAddAndGetTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 21, 10, 0));
        int id = manager.addNewTask(task);
        Task loaded = manager.getTask(id);

        assertNotNull(loaded, "Task should be found");
        assertEquals("Задача", loaded.getName());
        assertEquals("Описание", loaded.getDescription());
        assertEquals(TaskStatus.NEW, loaded.getStatus());
    }

    @Test
    void shouldAddAndGetEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int id = manager.addNewEpic(epic);
        Epic loaded = manager.getEpic(id);

        assertNotNull(loaded, "Epic should be found");
        assertEquals("Эпик", loaded.getName());
        assertEquals("Описание эпика", loaded.getDescription());
    }

    @Test
    void shouldAddAndGetSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Сабтаск", "Описание", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 5, 21, 12, 0), epicId);
        int subId = manager.addNewSubtask(subtask);

        Subtask loaded = manager.getSubtask(subId);
        assertNotNull(loaded, "Subtask should be found");
        assertEquals(epicId, loaded.getEpicId());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("A", "B", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025, 5, 21, 14, 0));
        int id = manager.addNewTask(task);
        Task update = new Task(id, "C", "D", TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.of(2025, 5, 21, 15, 0));
        manager.updateTask(update);

        Task loaded = manager.getTask(id);
        assertEquals("C", loaded.getName());
        assertEquals(TaskStatus.DONE, loaded.getStatus());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("A", "B");
        int id = manager.addNewEpic(epic);
        Epic update = new Epic(id, "C", "D");
        manager.updateEpic(update);

        Epic loaded = manager.getEpic(id);
        assertEquals("C", loaded.getName());
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic("A", "B");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 21, 16, 0), epicId);
        int subId = manager.addNewSubtask(subtask);

        Subtask update = new Subtask(subId, "Updated", "Changed", TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.of(2025, 5, 21, 17, 0), epicId);
        manager.updateSubtask(update);

        Subtask loaded = manager.getSubtask(subId);
        assertEquals("Updated", loaded.getName());
        assertEquals(TaskStatus.DONE, loaded.getStatus());
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("A", "B", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 21, 18, 0));
        int id = manager.addNewTask(task);
        manager.removeTask(id);

        assertNull(manager.getTask(id));
    }

    @Test
    void shouldRemoveEpicAndAllSubtasks() {
        Epic epic = new Epic("Epic", "Epic desc");
        int epicId = manager.addNewEpic(epic);
        Subtask sub1 = new Subtask("S1", "D1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025, 5, 21, 19, 0), epicId);
        int subId1 = manager.addNewSubtask(sub1);
        manager.removeEpic(epicId);

        assertNull(manager.getEpic(epicId));
        assertNull(manager.getSubtask(subId1));
    }

    @Test
    void shouldRemoveSubtask() {
        Epic epic = new Epic("Epic", "Epic desc");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("S1", "D1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 21, 20, 0), epicId);
        int subId = manager.addNewSubtask(subtask);

        manager.removeSubtask(subId);
        assertNull(manager.getSubtask(subId));
    }

    @Test
    void shouldGetAllTasksEpicsSubtasks() {
        Task task = new Task("T", "Desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025, 5, 21, 21, 0));
        Epic epic = new Epic("Epic", "Desc");
        int taskId = manager.addNewTask(task);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("S", "Desc", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 21, 22, 0), epicId);
        int subId = manager.addNewSubtask(subtask);

        List<Task> tasks = manager.getTasks();
        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();

        assertTrue(tasks.stream().anyMatch(t -> t.getId() == taskId));
        assertTrue(epics.stream().anyMatch(e -> e.getId() == epicId));
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subId));
    }

    @Test
    void shouldClearAllTasksEpicsSubtasks() {
        Task task = new Task("T", "Desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025, 5, 21, 23, 0));
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("S", "Desc", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 22, 0, 0), epicId);
        manager.addNewTask(task);
        manager.addNewSubtask(subtask);

        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();

        assertEquals(0, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldReturnEpicSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.addNewEpic(epic);
        Subtask sub1 = new Subtask("S1", "D1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 22, 1, 0), epicId);
        Subtask sub2 = new Subtask("S2", "D2", TaskStatus.DONE, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 22, 2, 0), epicId);
        int subId1 = manager.addNewSubtask(sub1);
        int subId2 = manager.addNewSubtask(sub2);

        List<Subtask> subtasks = manager.getEpicSubtasks(epicId);

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subId1));
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subId2));
    }

    @Test
    void shouldSaveAndGetHistory() {
        Task task = new Task("A", "B", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025, 5, 22, 3, 0));
        int taskId = manager.addNewTask(task);
        manager.getTask(taskId);

        List<Task> history = manager.getHistory();
        assertTrue(history.stream().anyMatch(t -> t.getId() == taskId));
    }
}
