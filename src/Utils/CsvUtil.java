package utils;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvUtil {
    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic";

    // Преобразовать задачу в CSV-строку
    public static String toCsv(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        sb.append(task.getStartTime() != null ? task.getStartTime() : "").append(",");
        sb.append(task.getDuration() != null ? task.getDuration().toMinutes() : "");
        if (task instanceof Subtask) {
            sb.append(",").append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    // Восстановить тип задачи из строки
    public static TaskType parseType(String value) {
        return TaskType.valueOf(value);
    }

    // Восстановить задачу из CSV-строки
    public static Task fromCsvTask(String csv) {
        String[] fields = csv.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[6]));
        return new Task(id, name, description, status, duration, startTime);
    }

    public static Epic fromCsvEpic(String csv) {
        String[] fields = csv.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(id, name, description);
        epic.setStatus(status);
        return epic;
    }

    public static Subtask fromCsvSubtask(String csv) {
        String[] fields = csv.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[6]));
        int epicId = Integer.parseInt(fields[7]);
        return new Subtask(id, name, description, status, duration, startTime, epicId);
    }
}
