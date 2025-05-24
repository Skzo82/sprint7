package test;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest extends test.TaskManagerTest<FileBackedTaskManager> {

    private Path testFile;

    @BeforeEach
    void setUp() {
        try {
            // Создаем временный файл перед каждым тестом
            testFile = Files.createTempFile("tasks_test", ".csv");
        } catch (IOException e) {
            throw new AssertionError("Не удалось создать временный файл", e);
        }
        manager = createManager();
    }

    @AfterEach
    void tearDown() {
        try {
            // Удаляем временный файл после каждого теста
            if (testFile != null && Files.exists(testFile)) {
                Files.delete(testFile);
            }
        } catch (IOException e) {
            throw new AssertionError("Не удалось удалить временный файл", e);
        }
    }

    @Override
    protected FileBackedTaskManager createManager() {
        // Возвращаем новый экземпляр менеджера, работающего с временным файлом
        return new FileBackedTaskManager(testFile);
    }
}
