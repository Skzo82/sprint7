package managers;

import tasks.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация менеджера задач, хранящая все данные в памяти.
 */
public class InMemoryTaskManager implements TaskManager {
    // Защищённые поля, доступны в наследниках (например, FileBackedTaskManager)
    protected int currentId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    // История просмотров (реализация не показана)
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    // Получить следующий уникальный id задачи
    protected int generateId() {
        return currentId++;
    }

    // Проверка пересечения по времени между задачами
    protected boolean isTimeOverlaps(Task a, Task b) {
        if (a.getStartTime() == null || a.getDuration() == null
                || b.getStartTime() == null || b.getDuration() == null) {
            return false;
        }
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();
        return !(aEnd.isBefore(bStart) || aStart.isAfter(bEnd) || aEnd.equals(bStart) || aStart.equals(bEnd));
    }

    // Добавить задачу в отсортированный список и проверить пересечение по времени
    protected void addToPrioritized(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            for (Task t : prioritizedTasks) {
                if (t.getStartTime() != null && t.getDuration() != null && isTimeOverlaps(task, t)) {
                    throw new IllegalArgumentException("Пересечение задач по времени!");
                }
            }
        }
        prioritizedTasks.add(task);
    }

    // Обновить статус эпика по статусам подзадач
    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasks();
        if (subtaskList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public int addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден: " + epicId);
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        addToPrioritized(subtask);
        updateEpicStatus(epic);
        epic.recalculateTimeAndDuration();
        return subtask.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? new ArrayList<>(epic.getSubtasks()) : Collections.emptyList();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NoSuchElementException("Задача не найдена!");
        }
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        addToPrioritized(task);
    }


    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NoSuchElementException("Эпик не найден!");
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        epic.recalculateTimeAndDuration();
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NoSuchElementException("Подзадача не найдена!");
        }
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            epic.recalculateTimeAndDuration();
        }
        addToPrioritized(subtask);
    }


    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                updateEpicStatus(epic);
                epic.recalculateTimeAndDuration();
            }
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeAllTasks() {
        for (Task t : tasks.values()) {
            prioritizedTasks.remove(t);
            historyManager.remove(t.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic e : epics.values()) {
            for (Subtask subtask : e.getSubtasks()) {
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            historyManager.remove(e.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask sub : subtasks.values()) {
            prioritizedTasks.remove(sub);
            historyManager.remove(sub.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            epic.recalculateTimeAndDuration();
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

}
