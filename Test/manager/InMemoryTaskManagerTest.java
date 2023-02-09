package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void getTasksEmpty() {
        assertTrue(taskManager.getTasks().isEmpty(), "Пустой список");
    }

    @Test
    void getPrioritizedTasks() {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        initializeTasks();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().last());
        assertEquals(task2, taskManager.getPrioritizedTasks().first());

        taskManager.deleteTask(2);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().last());
        assertEquals(task3, taskManager.getPrioritizedTasks().first());

        taskManager.deleteTask(3);
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().last());
        assertEquals(task1, taskManager.getPrioritizedTasks().first());
    }
}