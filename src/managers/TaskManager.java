package managers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    Task getTask(int id);
    Task getSubtask(int id);
    Task getEpic(int id);
    List<Task> getTasks();
    List<Task> getEpics();
    List<Task> getSubtasks();
    List<Subtask> getSubtasksByEpic(int epicId);
    List<Task> getHistory();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void removeAllTasks();

    void removeAllEpics();
    
    void removeAllSubtasks();
}
