import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;

// Тесты для файлового менеджера задач
public class FileBackedTaskManagerTest extends test.TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }

    // Тест проверки сохранения и загрузки из файла
    @Test
    public void shouldSaveAndLoadTasksFromFile() {
        // Реализация теста специфична для FileBackedTaskManager
    }
}
