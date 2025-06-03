package managers;

import tasks.*;
import Utils.CsvUtil;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Сохраняет все задачи в файл
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CsvUtil.HEADER);
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(CsvUtil.toCsv(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    // Получить все задачи в одном списке (tasks + epics + subtasks)
    public List<Task> getAllTasks() {
        List<Task> result = new ArrayList<>();
        result.addAll(tasks.values());
        result.addAll(epics.values());
        result.addAll(subtasks.values());
        return result;
    }

    // Загружает все задачи из файла
    protected void loadFromFile() {
        if (!Files.exists(file.toPath())) return;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) return; // Нет задач
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                TaskType type = TaskType.valueOf(fields[1]);
                int id = Integer.parseInt(fields[0]);
                if (id >= currentId) currentId = id + 1;

                switch (type) {
                    case TASK:
                        Task task = CsvUtil.fromCsvTask(line);
                        tasks.put(task.getId(), task);
                        addToPrioritized(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) CsvUtil.fromCsvTask(line);
                        epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) CsvUtil.fromCsvTask(line);
                        subtasks.put(subtask.getId(), subtask);
                        addToPrioritized(subtask);
                        Epic parentEpic = epics.get(subtask.getEpicId());
                        if (parentEpic != null) {
                            parentEpic.addSubtask(subtask);
                        }
                        break;
                }
            }
            // После загрузки пересчитать время и статус эпиков
            for (Epic epic : epics.values()) {
                updateEpicStatus(epic);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки задач из файла", e);
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
