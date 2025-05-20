package test;

import managers.TaskManager;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    // Перед каждым тестом создаём новый экземпляр менеджера
    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    // Метод для создания конкретной реализации менеджера (реализовать в наследнике)
    protected abstract T createManager();

    @Test
    void тестДобавленияЭпикаИПодзадач() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.addNewEpic(epic);

        Subtask подзадача1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now(), epicId);
        Subtask подзадача2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(20), epicId);

        int subId1 = manager.addNewSubtask(подзадача1);
        int subId2 = manager.addNewSubtask(подзадача2);

        assertNotNull(manager.getEpic(epicId), "Эпик должен быть найден");
        assertNotNull(manager.getSubtask(subId1), "Первая подзадача должна быть найдена");
        assertNotNull(manager.getSubtask(subId2), "Вторая подзадача должна быть найдена");
    }
}
