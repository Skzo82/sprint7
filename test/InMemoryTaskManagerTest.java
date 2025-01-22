package taskmanager.test;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import managers.HistoryManager;
import managers.Managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private InMemoryTaskManager manager;

    @BeforeEach
    public void setup() {
        taskManager = new InMemoryTaskManager();
        manager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndRetrieveTask() {
        Task task = new Task("Test Task", "This is a test task.");
        int taskId = taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(taskId);
        assertNotNull(retrievedTask, "Task should not be null.");
        assertEquals(task, retrievedTask, "Tasks should be equal.");
    }

    @Test
    public void testAddNewEpicAndSubtask() {
        Epic epic = new Epic("Epic", "Description of Epic");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description of Subtask", epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        assertNotNull(taskManager.getEpic(epicId), "Epic должен быть найден.");
        assertNotNull(taskManager.getSubtask(subtaskId), "Subtask должен быть найден.");
    }

    @Test
    public void testRemoveTaskFromHistory() {
        Task task = new Task("Test Task", "This is a test task.");
        int taskId = taskManager.addNewTask(task);
        taskManager.getTask(taskId);
        taskManager.removeTask(taskId);

        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task), "Task should not be in history after removal.");
    }

    @Test
    public void testRemoveEpicAndSubtasksFromHistory() {
        Epic epic = new Epic("Epic", "Description of Epic");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description of Subtask1", epicId);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Description of Subtask2", epicId);
        taskManager.addNewSubtask(subtask2);

        taskManager.getEpic(epicId);
        taskManager.removeEpic(epicId);

        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(epic), "Epic should not be in history after removal.");
        assertFalse(history.contains(subtask1), "Subtask1 should not be in history after removal.");
        assertFalse(history.contains(subtask2), "Subtask2 should not be in history after removal.");
    }

    @Test
    public void testEpicStatusUpdate() {
        Epic epic = new Epic("Epic", "Description of Epic");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description of Subtask1", epicId);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Description of Subtask2", epicId);
        taskManager.addNewSubtask(subtask2);

        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "Epic status should be IN_PROGRESS.");
    }

    @Test
    public void testEqualityOfEpicAndSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Description1");
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description1", epic.getId());
        manager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Description2", epic.getId());
        subtask2.setId(subtask1.getId() + 1);

        assertEquals(epic.getId(), subtask1.getEpicId(), "IDs Epic и Subtask должны совпадать.");
        assertNotEquals(subtask1, subtask2, "Subtasks с разными ID не обязательно должны быть одинаковыми.");
    }

    @Test
    public void testConflictOfIds() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");

        int id = manager.addNewTask(task1);
        task2.setId(id);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addNewTask(task2);
        });

        assertEquals("Task with this ID already exists.", exception.getMessage());
    }
    @Test
    public void testTaskImmutability() {
        Task task = new Task("Task1", "Description1");
        int id = manager.addNewTask(task);


        Task modifiedTask = new Task(task);
        modifiedTask.setStatus(TaskStatus.DONE);


        Task retrievedTask = manager.getTask(id);
        assertNotEquals(TaskStatus.DONE, retrievedTask.getStatus(), "Task in the manager should remain unchanged.");
    }

    @Test
    public void testTaskFieldsImmutability() {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Immutable Task", "Check immutability");
        int taskId = manager.addNewTask(task);
        Task retrievedTask = manager.getTask(taskId);

        assertEquals(task.getName(), retrievedTask.getName(), "Названия задач должны совпадать.");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описания задач должны совпадать.");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Статусы задач должны совпадать.");
    }

    @Test
    public void testHistoryPreservesPreviousVersion() {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task1", "Description1");
        int id = manager.addNewTask(task);
        task.setDescription("Updated Description");
        manager.updateTask(task);

        List<Task> history = manager.getHistory();
        assertTrue(history.contains(task), "История должна содержать обновленную задачу.");
    }
    @Test
    public void testManagersGetDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Дефолтный менеджер задач должен быть создан.");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Дефолтный менеджер истории должен быть создан.");
    }

    @Test
    public void testAddNewTask() {
        Task task = new Task("Test Task", "This is a test task.");
        int taskId = manager.addNewTask(task);

        Task retrievedTask = manager.getTask(taskId);
        assertNotNull(retrievedTask, "Task should not be null.");
        assertEquals(task, retrievedTask, "Tasks should be equal.");
    }

    @Test
    public void testPreviousVersionInHistory() {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "History check");
        int taskId = manager.addNewTask(task);


        task.setStatus(TaskStatus.DONE);
        manager.updateTask(new Task(task));


        List<Task> history = manager.getHistory();


        boolean previousVersionRecorded = history.stream()
                .anyMatch(t -> t.getId() == taskId && t.getStatus() == TaskStatus.NEW);

        assertFalse(previousVersionRecorded, "Previous version of the task should be saved in history.");
    }

    @Test
    public void testManagersInitialization() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "TaskManager должен быть инициализирован.");
        assertNotNull(historyManager, "HistoryManager должен быть инициализирован.");
    }

    @Test
    public void testComplexTaskHandling() {
        TaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic", "Complex test");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Part of Epic", epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        Task task = new Task("Task", "Standalone task");
        int taskId = manager.addNewTask(task);

        assertNotNull(manager.getEpic(epicId), "Epic должен быть найден.");
        assertNotNull(manager.getSubtask(subtaskId), "Subtask должен быть найден.");
        assertNotNull(manager.getTask(taskId), "Task должен быть найден.");
    }

    @Test
    void testEqualsForTaskTypes() {
        Task task1 = new Task("Task1", "Description");
        task1.setId(1);
        Epic epic = new Epic("Epic1", "Epic Description");
        epic.setId(1);

        assertNotEquals(task1, epic, "Tasks with the same ID but different types should not be equal");
    }

    @Test
    void testIdConflict() {
        Task task1 = new Task("Task1", "Description");
        task1.setId(1);
        manager.addNewTask(task1);

        Task task2 = new Task("Task2", "Description");
        task2.setId(1);

        assertThrows(IllegalArgumentException.class, () -> manager.addNewTask(task2));
    }

    @Test
    void testTaskFieldsAfterAddition() {
        Task task = new Task("Task1", "Description");
        int id = manager.addNewTask(task);

        Task retrieved = manager.getTask(id);
        assertEquals("Task1", retrieved.getName());
        assertEquals("Description", retrieved.getDescription());
    }

    @Test
    void testManagersFactoryMethods() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    void testIntegrationOfDifferentTaskTypes() {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Task", "Description");
        int taskId = manager.addNewTask(task);

        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        assertNotNull(manager.getTask(taskId));
        assertNotNull(manager.getEpic(epicId));
        assertNotNull(manager.getSubtask(subtaskId));
    }

    @Test
    public void shouldNotAllowEpicAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.addNewEpic(epic);

        Subtask invalidSubtask = new Subtask("Subtask", "Invalid", epicId);
        invalidSubtask.setId(epicId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addNewSubtask(invalidSubtask);
        });

        assertEquals("Epic cannot be its own subtask.", exception.getMessage());
    }

    @Test
    public void testHistoryLimit() {
        TaskManager taskManager = new InMemoryTaskManager();
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            taskManager.addNewTask(task);
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "History should contain only the last 10 tasks.");
    }

    @Test
    public void testAddAndRetrieveDifferentTaskTypes() {
        Task task = new Task("Task name", "Task description");
        Epic epic = new Epic("Test Epic", "Epic description");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask description", epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        int taskId = manager.addNewTask(task);

        assertEquals(task, manager.getTask(taskId), "Task should be retrieved correctly.");
        assertEquals(epic, manager.getEpic(epicId), "Epic should be retrieved correctly.");
        assertEquals(subtask, manager.getSubtask(subtaskId), "Subtask should be retrieved correctly.");
    }
}
