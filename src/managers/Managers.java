package managers;

// Импортируем нужные классы

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utils.DurationAdapter;

import java.time.Duration;

import utils.LocalDateTimeAdapter;


import java.time.LocalDateTime;

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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
