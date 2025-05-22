package taskmanager.test;

import org.junit.jupiter.api.Test;
import managers.FileBackedTaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private final File file = new File("test.csv");
    private FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

    @Test
    public void testSaveAndLoad() {

        Task task = new Task("Test Task", "This is a test task.");
        taskManager.addNewTask(task);

        Epic epic = new Epic("Test Epic", "Epic description");
        int epicId = taskManager.addNewEpic(epic); // Otteniamo l'ID dell'epic

        Subtask subtask = new Subtask("Test Subtask", "Subtask description", epicId);
        taskManager.addNewSubtask(subtask);

        taskManager = FileBackedTaskManager.loadFromFile(file);


        assertNotNull(taskManager.getTask(task.getId()), "Task should be loaded.");
        assertNotNull(taskManager.getEpic(epicId), "Epic should be loaded."); // Usa epicId
        assertNotNull(taskManager.getSubtask(subtask.getId()), "Subtask should be loaded.");
    }
}
