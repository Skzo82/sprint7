package tasks;

import utils.CsvUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс "Задача" (Task)
 */
public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    // Конструктор задачи без id
    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Конструктор задачи с id
    public Task(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this(name, description, status, duration, startTime);
        this.id = id;
    }

    // Получить id задачи
    public int getId() {
        return id;
    }

    // Установить id задачи
    public void setId(int id) {
        this.id = id;
    }

    // Получить название задачи
    public String getName() {
        return name;
    }

    // Получить описание задачи
    public String getDescription() {
        return description;
    }

    // Получить статус задачи
    public TaskStatus getStatus() {
        return status;
    }

    // Установить статус задачи
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    // Получить тип задачи
    public TaskType getType() {
        return TaskType.TASK;
    }

    // Получить продолжительность задачи
    public Duration getDuration() {
        return duration;
    }

    // Получить время начала задачи
    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Получить время окончания задачи
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    // Преобразовать задачу в CSV-строку
    public String toCsv() {
        return CsvUtil.toCsv(this);
    }

    // Создать задачу из CSV-строки (только для Task)
    public static Task fromCsvTask(String csv) {
        return CsvUtil.fromCsvTask(csv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", статус=" + status +
                ", длительность=" + duration +
                ", начало=" + startTime +
                '}';
    }
}
