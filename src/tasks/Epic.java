package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Класс "Эпик" — объединяет подзадачи и автоматически рассчитывает время и длительность
public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime startTime = LocalDateTime.MAX;
    private Duration duration = Duration.ZERO;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.MAX);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.MAX);
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

    // Очистить все подзадачи у эпика
    public void clearSubtasks() {
        subtasks.clear();
        recalculateTimeAndDuration();
    }

    // Получить список подзадач
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    // Пересчитать время начала и длительность эпика на основе подзадач
    public void recalculateTimeAndDuration() {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            return;
        }
        LocalDateTime minStart = LocalDateTime.MAX;
        LocalDateTime maxEnd = LocalDateTime.MIN;
        long totalMinutes = 0;

        for (Subtask sub : subtasks) {
            if (sub.getStartTime() != null && sub.getStartTime().isBefore(minStart)) {
                minStart = sub.getStartTime();
            }
            if (sub.getEndTime() != null && sub.getEndTime().isAfter(maxEnd)) {
                maxEnd = sub.getEndTime();
            }
            if (sub.getDuration() != null) {
                totalMinutes += sub.getDuration().toMinutes();
            }
        }

        this.startTime = (minStart == LocalDateTime.MAX) ? null : minStart;
        this.duration = Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime == LocalDateTime.MAX ? null : startTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    // Установить время начала эпика (для совместимости с менеджером)
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Установить длительность эпика (для совместимости с менеджером)
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    // Для сериализации эпика в CSV (может быть полезно для FileBackedTaskManager)
    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," +
                getDescription() + "," +
                (getStartTime() != null ? getStartTime() : "") + "," +
                (getDuration() != null ? getDuration().toMinutes() : "");
    }
}
