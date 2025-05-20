import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    void taskFields() {
        Task t = new Task("Nome", "Desc", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        assertEquals("Nome", t.getName());
        assertEquals(TaskStatus.NEW, t.getStatus());
    }

    @Test
    void subtaskEpicId() {
        Subtask s = new Subtask("Sub", "Desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 42);
        assertEquals(42, s.getEpicId());
        assertEquals(TaskType.SUBTASK, s.getType());
    }

    @Test
    void epicSubtasksAndDuration() {
        Epic e = new Epic("Epic", "Desc");
        assertEquals(0, e.getSubtasks().size());
        Subtask s1 = new Subtask("A", "B", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now(), 1);
        Subtask s2 = new Subtask("B", "C", TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.now().plusHours(1), 1);
        e.addSubtask(s1);
        e.addSubtask(s2);
        assertEquals(2, e.getSubtasks().size());
        assertEquals(Duration.ofMinutes(30), e.getDuration());
    }
}