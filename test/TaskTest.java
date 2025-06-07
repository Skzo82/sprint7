package test;

import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    // Проверяем геттеры задачи
    @Test
    void taskFields() {
        Task t = new Task("Задача", "Описание", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.of(2024, 1, 1, 12, 0));
        assertEquals("Задача", t.getName());
        assertEquals("Описание", t.getDescription());
        assertEquals(TaskStatus.NEW, t.getStatus());
        assertEquals(Duration.ofMinutes(20), t.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), t.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 20), t.getEndTime());
    }

    // Проверяем, что Subtask хранит правильный epicId
    @Test
    void subtaskEpicId() {
        Subtask sub = new Subtask("Subtask", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 10, 0), 5);
        assertEquals(5, sub.getEpicId());
    }

    // Проверяем логику подсчёта времени и длительности эпика
    @Test
    void epicSubtasksAndDuration() {
        Epic epic = new Epic("Переезд", "Собрать вещи");
        // Подзадача 1: 1 января 12:00 - 1 января 12:30 (30 минут)
        Subtask sub1 = new Subtask(
                "Упаковать вещи",
                "Всё собрать по списку",
                TaskStatus.DONE,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 12, 0),
                epic.getId()
        );
        // Подзадача 2: 1 января 12:30 - 1 января 13:20 (50 минут)
        Subtask sub2 = new Subtask(
                "Найти грузчиков",
                "Позвонить в компанию",
                TaskStatus.DONE,
                Duration.ofMinutes(50),
                LocalDateTime.of(2024, 1, 1, 12, 30),
                epic.getId()
        );

        // Добавляем подзадачи к эпику
        epic.addSubtask(sub1);
        epic.addSubtask(sub2);

        // Проверяем расчет суммы durations
        assertEquals(Duration.ofMinutes(80), epic.getDuration(),
                "Длительность эпика должна быть суммой длительностей подзадач");

        // Проверяем расчет самого раннего старта
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), epic.getStartTime(),
                "Время старта эпика должно совпадать с самой ранней подзадачей");

        // Проверяем расчет самого позднего конца
        assertEquals(LocalDateTime.of(2024, 1, 1, 13, 20), epic.getEndTime(),
                "Время окончания эпика должно совпадать с самой поздней подзадачей");
    }
}
