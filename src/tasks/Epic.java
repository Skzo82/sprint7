package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime startTime = null;
    private Duration duration = Duration.ZERO;
    private LocalDateTime endTime = null;

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
        return new ArrayList<>(subtasks);
    }

    // Пересчитать время начала и длительность эпика на основе подзадач
    public void recalculateTimeAndDuration() {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
            return;
        }
        LocalDateTime minStart = LocalDateTime.MAX;
        LocalDateTime maxEnd = LocalDateTime.MIN;
        Duration totalDuration = Duration.ZERO;
        boolean hasValid = false;

        for (Subtask sub : subtasks) {
            if (sub.getStartTime() != null && sub.getDuration() != null) {
                hasValid = true;
                LocalDateTime subStart = sub.getStartTime();
                LocalDateTime subEnd = sub.getEndTime();
                if (subStart.isBefore(minStart)) minStart = subStart;
                if (subEnd.isAfter(maxEnd)) maxEnd = subEnd;
                totalDuration = totalDuration.plus(sub.getDuration());
            }
        }

        if (hasValid) {
            this.startTime = minStart;
            this.endTime = maxEnd;
            this.duration = totalDuration;
        } else {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
        }
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
