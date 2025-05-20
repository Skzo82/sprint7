package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    // Карты для хранения задач, эпиков и подзадач
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));
    // Переменная для генерации id
    protected int nextId = 1;

    // Генерация нового id
    protected int generateId() {
        return nextId++;
    }

    // Получить задачу по id
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    // Получить эпик по id
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    // Получить подзадачу по id
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    // Получить список всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получить список всех эпиков
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получить список всех подзадач
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получить историю просмотров
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Добавить новую задачу
    @Override
    public int addNewTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    // Добавить новый эпик
    @Override
    public int addNewEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    // Добавить новую подзадачу
    @Override
    public int addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        epic.recalculateTimeAndDuration();
        return id;
    }

    // Обновить задачу
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с id " + task.getId() + " не найдена.");
        }
        tasks.put(task.getId(), task);
    }

    // Обновить эпик
    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Эпик с id " + epic.getId() + " не найден.");
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        epic.recalculateTimeAndDuration();
    }

    // Обновить подзадачу
    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с id " + subtask.getId() + " не найдена.");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            epic.recalculateTimeAndDuration();
        }
    }

    // Удалить задачу по id
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    // Удалить эпик по id
    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(epic.getId());
        }
    }

    // Удалить подзадачу по id
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
            historyManager.remove(subtask.getId());
        }
    }

    // Получить подзадачи эпика
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks();
        }
        return Collections.emptyList();
    }

    // Удалить все задачи
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    // Удалить все эпики
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Удалить все подзадачи
    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            epic.recalculateTimeAndDuration();
        }
    }

    // Пересчитать статус эпика
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
}
