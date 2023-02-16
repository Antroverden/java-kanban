package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    protected HistoryManager historyManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        LocalDateTime qq = LocalDateTime.of(2022, 2, 7, 21, 46);
        task1 = new Task("one", "task one", Status.NEW);
        task2 = new Task("two", "task two", Status.NEW, 50, qq.plusHours(5));
        task3 = new Task("three", "task three", Status.NEW, 20, qq.plusHours(10));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addTask() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTwoTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В историю корректно добавляются задачи");
    }

    @Test
    void addTwoIdenticalTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории не дублируются задачи");
    }

    @Test
    void removeTask() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "История пустая.");
    }

    @Test
    void removeTaskWhenHistoryIsEmpty() {
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "История пустая.");
    }

    @Test
    void removeFirstTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из истории: начало.");
        assertEquals(task2, historyManager.getHistory().get(0), "Удаление из истории: начало.");
    }

    @Test
    void removeMiddleTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из истории: середина");
        assertEquals(task3, historyManager.getHistory().get(1), "Удаление из истории: середина");
    }

    @Test
    void removeLastTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из истории: конец.");
        assertEquals(task2, historyManager.getHistory().get(1), "Удаление из истории: конец");
    }

    @Test
    void getZeroHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История пустая.");
    }
}