package managers;

import tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    // Сохранение всех задач, эпиков и подзадач в файл
    private void save() {
        StringBuilder sb = new StringBuilder("id,type,name,status,description,startTime,duration,epic\n");
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) continue;
            if (task instanceof Epic) continue;
            sb.append(task.toCsv()).append("\n");
        }
        for (Epic epic : epics.values()) {
            sb.append(epic.toCsv()).append("\n");
        }
        for (Subtask subtask : subtasks.values()) {
            sb.append(subtask.toCsv()).append("\n");
        }
        try {
            Files.writeString(filePath, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении задач в файл", e);
        }
    }

    // Загрузка задач, эпиков и подзадач из файла
    private void loadFromFile() {
        try {
            if (!Files.exists(filePath)) return;
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 1; i < lines.size(); i++) { // пропустить первую строку-заголовок
                String line = lines.get(i);
                if (line.isEmpty()) continue;
                // Преобразуем строку CSV в задачу любого типа
                Task task = Task.fromCsv(line);

                // В зависимости от типа задачи сохраняем в соответствующую мапу
                switch (task.getType()) {
                    case TASK:
                        tasks.put(task.getId(), task);
                        prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        subtasks.put(subtask.getId(), subtask);
                        prioritizedTasks.add(subtask);
                        // Добавляем подзадачу в соответствующий эпик
                        Epic parentEpic = epics.get(subtask.getEpicId());
                        if (parentEpic != null) {
                            parentEpic.addSubtask(subtask);
                        }
                        break;
                }
            }
            // После загрузки всех подзадач пересчитать время и статус для каждого эпика
            for (Epic epic : epics.values()) {
                epic.recalculateTimeAndDuration();
                updateEpicStatus(epic);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке задач из файла", e);
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
