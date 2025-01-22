import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks;

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }
}