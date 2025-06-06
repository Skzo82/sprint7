package utils;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CsvUtil {
    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Преобразовать задачу в CSV-строку
    public static String toCsv(Task task) {
        String[] fields = new String[8];
        fields[0] = String.valueOf(task.getId());
        fields[1] = task.getType().toString();
        fields[2] = task.getName();
        fields[3] = task.getStatus().toString();
        fields[4] = task.getDescription();
        fields[5] = (task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "");
        fields[6] = (task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "");

        if (task.getType() == TaskType.SUBTASK) {
            fields[7] = String.valueOf(((Subtask) task).getEpicId());
        } else {
            fields[7] = "";
        }

        return String.join(",", fields);
    }

    // Восстановить тип задачи из строки
    public static TaskType parseType(String value) {
        return TaskType.valueOf(value);
    }

    // Восстановить задачу из CSV-строки
    public static Task fromCsvTask(String csv) {
        String[] fields = csv.split(",", -1);
        if (fields.length < 5) {
            throw new IllegalArgumentException(
                    "В строке CSV недостаточно полей для задачи: " + Arrays.toString(fields)
            );
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = fields.length > 1 ? TaskType.valueOf(fields[1]) : TaskType.TASK;
        String name = fields.length > 2 ? fields[2] : "";
        TaskStatus status = fields.length > 3 ? TaskStatus.valueOf(fields[3]) : TaskStatus.NEW;
        String description = fields.length > 4 ? fields[4] : "";
        LocalDateTime startTime = fields.length > 5 && !fields[5].isEmpty()
                ? LocalDateTime.parse(fields[5], DATE_TIME_FORMATTER)
                : null;
        Duration duration = fields.length > 6 && !fields[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[6])) : Duration.ZERO;

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = fields.length > 7 && !fields[7].isEmpty() ? Integer.parseInt(fields[7]) : 0;
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static Epic fromCsvEpic(String csv) {
        String[] fields = csv.split(",", -1);
        if (fields.length < 5) {
            throw new IllegalArgumentException(
                    "В строке CSV недостаточно полей для эпика: " + Arrays.toString(fields)
            );
        }
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
        if (fields.length < 8) {
            throw new IllegalArgumentException(
                    "В строке CSV недостаточно полей для подзадачи: " + Arrays.toString(fields)
            );
        }
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isEmpty()
                ? null
                : LocalDateTime.parse(fields[5], DATE_TIME_FORMATTER);
        Duration duration = fields[6].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[6]));
        int epicId = Integer.parseInt(fields[7]);
        return new Subtask(id, name, description, status, duration, startTime, epicId);
    }
}