package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

// Класс "Задача"
public class Task {
    protected int id; // Идентификатор задачи
    protected String name; // Название
    protected String description; // Описание
    protected TaskStatus status; // Статус (NEW, IN_PROGRESS, DONE)
    protected Duration duration; // Длительность
    protected LocalDateTime startTime; // Время начала

    // Конструктор без id (для новых задач)
    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Конструктор с id (например, для загрузки из файла)
    public Task(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this(name, description, status, duration, startTime);
        this.id = id;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Получить время окончания задачи
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    // Для сериализации в CSV (пример)
    public String toCsv() {
        return String.format("%d,TASK,%s,%s,%s,%s,%s,",
                id, name, status, description, startTime, duration);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public static Task fromCsv(String csvLine) {
        // Формат: id,type,name,status,description,startTime,duration,epic
        String[] fields = csvLine.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isEmpty() ? Duration.ZERO : Duration.parse(fields[6]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
