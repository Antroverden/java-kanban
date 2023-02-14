package manager;

import manager.http.HttpTaskManager;
import manager.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    static HttpTaskServer server;
    HttpTaskManager httpTaskManager1;

    static KVServer kvServer;

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        taskManager = server.taskManager;
    }

    @AfterEach
    void AfterEach() {
        kvServer.stop();
        server.stop();
    }

    @Test
    void getPrioritizedTasks() {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        initializeAndAddTasks();
        assertFalse(taskManager.getPrioritizedTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getPrioritizedTasks(), httpTaskManager1.getPrioritizedTasks());
    }

    @Test
    void getHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getHistory().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getHistory(), httpTaskManager1.getHistory());
    }

    @Test
    void getTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTasks(), httpTaskManager1.getTasks());
    }

    @Test
    void getSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtasks(), httpTaskManager1.getSubtasks());
    }

    @Test
    void getEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpics(), httpTaskManager1.getEpics());
    }

    @Test
    void deleteTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTasks(), httpTaskManager1.getTasks());
    }

    @Test
    void deleteSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getSubtasks().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtasks(), httpTaskManager1.getSubtasks());
    }

    @Test
    void deleteEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getEpics().isEmpty());
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpics(), httpTaskManager1.getEpics());
    }

    @Test
    void getTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void getSubtask() {
        assertNull(taskManager.getSubtask(5));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(5));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void getEpicSubtasks() {
        assertNull(taskManager.getEpicSubtasks(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertFalse(taskManager.getEpicSubtasks(4).isEmpty());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpicSubtasks(4), httpTaskManager1.getEpicSubtasks(4));
    }

    @Test
    void getEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void addTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        assertEquals(3, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void addSubtask() {
        assertNull(taskManager.getSubtask(5));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(5));
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void addEpicTask() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void updateTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        Task task = new Task("one", "task one", Status.NEW);
        task.setId(1);
        taskManager.updateTask(task);
        assertEquals(3, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task, task2, task3), httpTaskManager1.getTasks());
        assertEquals(taskManager.getTask(1), httpTaskManager1.getTask(1));
    }

    @Test
    void updateSubtask() {
        assertNull(taskManager.getSubtask(6));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(6));
        Subtask task = new Subtask("one", "task one", Status.NEW, 4);
        task.setId(6);
        taskManager.updateSubtask(task);
        assertEquals(3, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(subtask5, task, subtask7), httpTaskManager1.getSubtasks());
        assertEquals(taskManager.getSubtask(5), httpTaskManager1.getSubtask(5));
    }

    @Test
    void updateEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        Epic task = new Epic("one", "task one", Status.NEW);
        task.setId(4);
        taskManager.updateEpic(task);
        assertEquals(2, taskManager.getEpics().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task, epic2), httpTaskManager1.getEpics());
        assertEquals(taskManager.getEpic(4), httpTaskManager1.getEpic(4));
    }

    @Test
    void deleteTask() {
        assertNull(taskManager.getTask(1));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getTask(1));
        taskManager.deleteTask(1);
        assertEquals(2, taskManager.getTasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(task2, task3), httpTaskManager1.getTasks());
        assertNull(httpTaskManager1.getTask(1));
    }

    @Test
    void deleteSubtask() {
        assertNull(taskManager.getSubtask(6));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getSubtask(6));
        taskManager.deleteSubtask(6);
        assertEquals(2, taskManager.getSubtasks().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(subtask5, subtask7), httpTaskManager1.getSubtasks());
        assertNull(httpTaskManager1.getSubtask(6));
    }

    @Test
    void deleteEpic() {
        assertNull(taskManager.getEpic(4));
        initializeAndAddTasks();
        getSomeTasks();
        assertNotNull(taskManager.getEpic(4));
        taskManager.deleteEpic(4);
        assertEquals(1, taskManager.getEpics().size());
        httpTaskManager1 = new HttpTaskManager("http://localhost:8078/", true);
        assertEquals(List.of(epic2), httpTaskManager1.getEpics());
        assertNull(httpTaskManager1.getEpic(4));
    }
}