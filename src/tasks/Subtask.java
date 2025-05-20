package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

// Класс "Подзадача"
public class Subtask extends Task {
    private int epicId; // ID родительского эпика

    public Subtask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    // Переопределяем toCsv (для сохранения в файл)
    @Override
    public String toCsv() {
        return String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d",
                id, name, status, description, startTime, duration, epicId);
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", epicId=" + epicId +
                '}';
    }
    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
