import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Тесты для файлового менеджера задач
public class FileBackedTaskManagerTest extends test.TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }

    // Тест проверки сохранения и загрузки из файла
    @Test
    public void shouldSaveAndLoadTasksFromFile() {
        File file = new File("test-tasks.csv");
        if (file.exists()) file.delete();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Test", "Desc", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 6, 3, 12, 0));
        int taskId = manager.addNewTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        loadedManager.loadFromFile();

        Task loadedTask = loadedManager.getTask(taskId);

        assertNotNull(loadedTask);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());

        file.delete();
    }
}
