package test;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Тест для проверки сохранения и загрузки задач в файл
public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path testFile;

    @BeforeEach
    void setUpFile() throws IOException {
        // Создаем временный файл для теста
        testFile = Files.createTempFile("tasks_test", ".csv");
        manager = createManager();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаляем временный файл после теста
        if (testFile != null && Files.exists(testFile)) {
            Files.delete(testFile);
        }
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(testFile);
    }
}
