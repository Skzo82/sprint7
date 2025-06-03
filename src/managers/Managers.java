package managers;

// Импортируем нужные классы

import tasks.*;

import java.nio.file.Path;

public class Managers {
    // Возвращает стандартный менеджер задач
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Возвращает стандартный менеджер истории
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    // Пример: можно добавить фабрику для менеджера, работающего с файлами
    public static TaskManager getFileBacked(Path filePath) {
        return new FileBackedTaskManager(filePath.toFile());
    }
}
