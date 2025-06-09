package managers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    // Получить задачу по id
    Task getTask(int id);

    // Получить эпик по id
    Epic getEpic(int id);

    // Получить подзадачу по id
    Subtask getSubtask(int id);

    // Получить список всех задач
    List<Task> getTasks();

    // Получить список всех эпиков
    List<Epic> getEpics();

    // Получить список всех подзадач
    List<Subtask> getSubtasks();

    // Получить историю просмотров
    List<Task> getHistory();

    // Добавить новую задачу
    int addNewTask(Task task);

    // Добавить новый эпик
    int addNewEpic(Epic epic);

    // Добавить новую подзадачу
    int addNewSubtask(Subtask subtask);

    // Обновить задачу
    void updateTask(Task task);

    // Обновить эпик
    void updateEpic(Epic epic);

    // Обновить подзадачу
    void updateSubtask(Subtask subtask);

    // Удалить задачу по id
    void removeTask(int id);

    // Удалить эпик по id
    void removeEpic(int id);

    // Удалить подзадачу по id
    void removeSubtask(int id);

    // Получить подзадачи эпика
    List<Subtask> getEpicSubtasks(int epicId);

    // Удалить все задачи
    void removeAllTasks();

    // Удалить все эпики
    void removeAllEpics();

    // Удалить все подзадачи
    void removeAllSubtasks();

    // Получить задачи в порядке приоритета
    List<Task> getPrioritizedTasks();

    void deleteTaskById(int id);
}
