package managers;

import tasks.*;
import utils.CsvUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    // Сохраняет все задачи в файл
    protected void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(CsvUtil.HEADER);
            for (Task task : tasks.values()) {
                if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                    lines.add(CsvUtil.toCsv(task));
                }
            }
            for (Epic epic : epics.values()) {
                lines.add(CsvUtil.toCsv(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                lines.add(CsvUtil.toCsv(subtask));
            }
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения задач в файл", e);
        }
    }

    // Загружает все задачи из файла
    protected void loadFromFile() {
        if (!Files.exists(filePath)) return;
        try {
            List<String> lines = Files.readAllLines(filePath);
            if (lines.size() < 2) return; // Нет задач
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                TaskType type = CsvUtil.parseType(fields[1]);
                int id = Integer.parseInt(fields[0]);
                if (id >= currentId) currentId = id + 1;

                switch (type) {
                    case TASK:
                        Task task = CsvUtil.fromCsvTask(line);
                        tasks.put(task.getId(), task);
                        addToPrioritized(task);
                        break;
                    case EPIC:
                        Epic epic = CsvUtil.fromCsvEpic(line);
                        epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = CsvUtil.fromCsvSubtask(line);
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
