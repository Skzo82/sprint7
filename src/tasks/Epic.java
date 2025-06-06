package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        recalculateTimeAndDuration();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        recalculateTimeAndDuration();
    }

    public void clearSubtasks() {
        subtasks.clear();
        recalculateTimeAndDuration();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    // Пересчитать время начала и длительность эпика на основе подзадач
    public void recalculateTimeAndDuration() {
        if (subtasks.isEmpty()) {
            setStartTime(null);
            setDuration(Duration.ZERO);
            endTime = null;
            return;
        }
        LocalDateTime minStart = LocalDateTime.MAX;
        LocalDateTime maxEnd = LocalDateTime.MIN;
        Duration totalDuration = Duration.ZERO;

        for (Subtask s : subtasks) {
            if (s.getStartTime() != null) {
                if (s.getStartTime().isBefore(minStart)) {
                    minStart = s.getStartTime();
                }
                LocalDateTime subEnd = s.getEndTime();
                if (subEnd != null && subEnd.isAfter(maxEnd)) {
                    maxEnd = subEnd;
                }
            }
            if (s.getDuration() != null) {
                totalDuration = totalDuration.plus(s.getDuration());
            }
        }

        // Если minStart не изменился, значит не было ни одной подзадачи с датой старта
        setStartTime(minStart.equals(LocalDateTime.MAX) ? null : minStart);
        // Если maxEnd не изменился, значит не было ни одной подзадачи с датой окончания
        endTime = maxEnd.equals(LocalDateTime.MIN) ? null : maxEnd;
        setDuration(totalDuration);
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," +
                getDescription() + "," +
                (getStartTime() != null ? getStartTime() : "") + "," +
                (getDuration() != null ? getDuration().toMinutes() : "");
    }
}
