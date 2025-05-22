package utils

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvUtil {

    public static String taskToString(Task task) {
        String base = task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," +
                (task.getStartTime() != null ? task.getStartTime() : "") + "," +
                (task.getDuration() != null ? task.getDuration().toMinutes() : "");
        if (task instanceof Subtask subtask) {
            return base + "," + subtask.getEpicId();
        }
        return base;
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isBlank() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isBlank() ? null : Duration.ofMinutes(Long.parseLong(fields[6]));

        Task task;
        switch (type) {
            case TASK -> task = new Task(name, description, status, duration, startTime);
            case EPIC -> task = new Epic(name, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[7]);
                task = new Subtask(name, description, status, duration, startTime, epicId);
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }

        task.setId(id);
        return task;
    }
}