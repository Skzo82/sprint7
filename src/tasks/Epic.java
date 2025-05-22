package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Класс "Эпик" (группирует подзадачи)
public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    // Получить список подзадач эпика
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    // Добавить подзадачу к эпику
    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        recalculateTimeAndDuration();
    }

    // Удалить подзадачу из эпика
    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        recalculateTimeAndDuration();
    }

    // Очистить все подзадачи эпика
    public void clearSubtasks() {
        subtasks.clear();
        recalculateTimeAndDuration();
    }

    // Пересчитать время начала, окончания и длительность эпика
    public void recalculateTimeAndDuration() {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            return;
        }
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        Duration total = Duration.ZERO;

        for (Subtask sub : subtasks) {
            if (sub.getStartTime() != null) {
                if (minStart == null || sub.getStartTime().isBefore(minStart)) {
                    minStart = sub.getStartTime();
                }
                LocalDateTime end = sub.getEndTime();
                if (end != null && (maxEnd == null || end.isAfter(maxEnd))) {
                    maxEnd = end;
                }
            }
            if (sub.getDuration() != null) {
                total = total.plus(sub.getDuration());
            }
        }
        this.startTime = minStart;
        this.duration = total;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty()) return null;
        LocalDateTime maxEnd = null;
        for (Subtask subtask : subtasks) {
            LocalDateTime subEnd = subtask.getEndTime();
            if (subEnd != null && (maxEnd == null || subEnd.isAfter(maxEnd))) {
                maxEnd = subEnd;
            }
        }
        return maxEnd;
    }

    // Переопределяем toCsv (для сохранения в файл)
    @Override
    public String toCsv() {
        return String.format("%d,EPIC,%s,%s,%s,%s,%s,",
                id, name, status, description, startTime, duration);
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", subtasks=" + subtasks.size() +
                '}';
    }
    
    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
