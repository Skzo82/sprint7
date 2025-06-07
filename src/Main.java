package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Создаём менеджер задач
        TaskManager manager = Managers.getDefault();

        // Создаём новую задачу
        Task task1 = new Task("Сделать домашку", "Сделать задачи по Java", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 5, 20, 18, 0));
        int taskId = manager.addNewTask(task1);

        // Получаем задачу по id и выводим на экран
        Task taskLoaded = manager.getTask(taskId);
        System.out.println("Задача: " + taskLoaded.getName() + ", конец: " + taskLoaded.getEndTime());

        // Создаём эпик и подзадачу
        Epic epic = new Epic("Купить продукты", "Купить молоко, хлеб и яйца");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Купить молоко", "В магазине у дома", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 5, 20, 19, 0), epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        // Получаем эпик и его подзадачи
        Epic epicLoaded = manager.getEpic(epicId);
        System.out.println("Эпик: " + epicLoaded.getName() + ", длительность: " + epicLoaded.getDuration() + ", начало: " + epicLoaded.getStartTime());
        System.out.println("Подзадачи эпика: ");
        for (Subtask s : epicLoaded.getSubtasks()) {
            System.out.println(" - " + s.getName());
        }
    }
}
