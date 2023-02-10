package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

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

        epic1 = new Epic("four", "task four", Status.NEW);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("five", "task five", Status.DONE, epicId1);
        subtask6 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1, 10,
                qq.plusHours(10));
        subtask7 = new Subtask("seven", "task seven", Status.NEW, epicId1, 10, qq.plusHours(1));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);

        taskManager.addTask(task2);
        assertEquals(5, taskManager.getPrioritizedTasks().size());
    }
}