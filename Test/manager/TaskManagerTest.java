package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected Task task1;

    protected Task task2;

    protected Task task3;

    protected Epic epic1;
    protected Epic epic2;

    protected Subtask subtask5;
    protected Subtask subtask6;
    protected Subtask subtask7;

    File file;

    LocalDateTime qq = LocalDateTime.of(2022, 2, 7, 21, 46);


    void initializeTasks() {
        task1 = new Task("one", "task one", Status.NEW);
        task2 = new Task("two", "task two", Status.NEW, 50, qq.plusHours(5));
        task3 = new Task("three", "task three", Status.NEW, 20, qq.plusHours(10));
        epic1 = new Epic("three", "task three", Status.NEW);
    }

    void initializeAndAddTasks() {
        task1 = new Task("one", "task one", Status.NEW);
        task2 = new Task("two", "task two", Status.NEW, 50, qq.plusHours(5));
        task3 = new Task("three", "task three", Status.NEW, 20, qq.plusHours(10));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        epic1 = new Epic("four", "task four", Status.NEW);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("five", "task five", Status.DONE, epicId1);
        subtask6 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1, 10, qq.plusHours(3));
        subtask7 = new Subtask("seven", "task seven", Status.NEW, epicId1, 10, qq.plusHours(2));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        epic2 = new Epic("eight", "task eight", Status.NEW);
        taskManager.addEpicTask(epic2);
    }

    @Test
    void getTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        assertEquals(new ArrayList<>(List.of(task1, task2, task3)), taskManager.getTasks());
    }

    @Test
    void getSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        assertEquals(new ArrayList<>(List.of(subtask5, subtask6, subtask7)), taskManager.getSubtasks());
    }

    @Test
    void getEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
        initializeAndAddTasks();
        assertEquals(new ArrayList<>(List.of(epic1, epic2)), taskManager.getEpics());
    }

    @Test
    void deleteTasks() {
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteSubtasks() {
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteEpics() {
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        initializeAndAddTasks();
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void getTask() {
        assertTrue(taskManager.getTasks().isEmpty());
        assertNull(taskManager.getTask(200));
        initializeAndAddTasks();
        assertEquals(task2, taskManager.getTask(2));
    }

    @Test
    void getSubtask() {
        assertTrue(taskManager.getTasks().isEmpty());
        assertNull(taskManager.getSubtask(200));
        initializeAndAddTasks();
        assertEquals(subtask5, taskManager.getSubtask(5));
    }

    @Test
    void getEpicSubtasks() {
        assertTrue(taskManager.getTasks().isEmpty());
        assertNull(taskManager.getEpicSubtasks(epic1));
        initializeAndAddTasks();
        assertEquals(new ArrayList<>(List.of(subtask5, subtask6, subtask7)), taskManager.getEpicSubtasks(epic1));
        assertEquals(3, taskManager.getEpicSubtasks(epic1).size());
    }

    @Test
    void getEpic() {
        assertTrue(taskManager.getTasks().isEmpty());
        assertNull(taskManager.getEpic(200));
        initializeAndAddTasks();
        assertEquals(epic1, taskManager.getEpic(4));
    }

    @Test
    void addTask() {
        taskManager.addTask(task1);
        assertTrue(taskManager.getTasks().isEmpty());

        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        initializeTasks();
        taskManager.addTask(task1);
        assertNotNull(taskManager.getTask(task1.getId()));
        assertEquals(task1, taskManager.getTask(task1.getId()));

        assertNotNull(taskManager.getTasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(task1, taskManager.getTasks().get(1), "Задачи не совпадают.");
    }

    @Test
    void addSubtask() {
        taskManager.addSubtask(subtask5);

        assertTrue(taskManager.getSubtasks().isEmpty());

        subtask5 = new Subtask("five", "task five", Status.DONE, 1000);
        taskManager.addSubtask(subtask5);

        assertNull(taskManager.getSubtask(subtask5.getId()));

        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(0, taskManager.getSubtasks().size());

        epic1 = new Epic("four", "task four", Status.NEW);

        int epicId1 = taskManager.addEpicTask(epic1);
        taskManager.addSubtask(subtask5);

        assertTrue(epic1.getSubtasksId().isEmpty());

        subtask6 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask6);

        assertEquals(IN_PROGRESS, epic1.getStatus());
        assertNotNull(taskManager.getSubtask(subtask6.getId()));
        assertEquals(subtask6, taskManager.getSubtask(subtask6.getId()));

        assertNotNull(taskManager.getSubtasks());
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(2, subtask6.getId());
    }

    @Test
    void addEpicTask() {
        taskManager.addEpicTask(epic1);

        assertTrue(taskManager.getEpics().isEmpty());

        epic1 = new Epic("four", "task four", Status.NEW);

        int epicId1 = taskManager.addEpicTask(epic1);
        taskManager.addSubtask(subtask5);

        assertNotNull(taskManager.getEpic(epic1.getId()));

        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(1, taskManager.getEpics().size());

        subtask5 = new Subtask("five", "task five", Status.DONE, epicId1);
        taskManager.addSubtask(subtask5);

        assertEquals(DONE, epic1.getStatus());
        assertNotNull(epic1.getSubtasksId());
        assertEquals(subtask5.getEpicId(), epic1.getId());
        assertNotNull(taskManager.getEpic(epic1.getId()));
        assertEquals(epic1, taskManager.getEpic(epic1.getId()));

        assertNotNull(taskManager.getEpics());
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, epicId1);
    }

    @Test
    void updateTask() {
        taskManager.updateTask(task1);
        assertTrue(taskManager.getTasks().isEmpty());

        initializeAndAddTasks();
        Task task = new Task("one", "task one", Status.NEW);
        taskManager.updateTask(task);
        assertEquals(List.of(task1, task2, task3), taskManager.getTasks());

        task.setId(2000);
        taskManager.updateTask(task);
        assertEquals(List.of(task1, task2, task3), taskManager.getTasks());

        task.setId(1);
        taskManager.updateTask(task);
        assertEquals(3, taskManager.getTasks().size());
        assertEquals(List.of(task, task2, task3), taskManager.getTasks());
    }

    @Test
    void updateSubtask() {
        taskManager.updateSubtask(subtask5);
        assertTrue(taskManager.getSubtasks().isEmpty());

        initializeAndAddTasks();
        Subtask task = new Subtask("one", "task one", Status.NEW, 2000);
        taskManager.updateSubtask(task);
        assertEquals(List.of(subtask5, subtask6, subtask7), taskManager.getSubtasks());

        task.setId(2000);
        taskManager.updateSubtask(task);
        assertEquals(List.of(subtask5, subtask6, subtask7), taskManager.getSubtasks());

        task.setId(5);
        taskManager.updateSubtask(task);
        assertEquals(List.of(subtask5, subtask6, subtask7), taskManager.getSubtasks());

        task = new Subtask("one", "task one", Status.DONE, 4);
        task.setId(6);
        Subtask subtask2 = new Subtask("one", "task two", Status.DONE, 4);
        subtask2.setId(7);

        taskManager.updateSubtask(task);
        taskManager.updateSubtask(subtask2);
        assertEquals(3, taskManager.getSubtasks().size());
        assertEquals(3, epic1.getSubtasksId().size());
        assertEquals(List.of(subtask5, task, subtask2), taskManager.getSubtasks());
        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    void updateEpic() {
        taskManager.updateEpic(epic1);
        assertTrue(taskManager.getEpics().isEmpty());

        initializeAndAddTasks();
        Epic task = new Epic("one", "task one", Status.NEW);
        taskManager.updateEpic(task);
        assertEquals(List.of(epic1, epic2), taskManager.getEpics());

        task.setId(2000);
        taskManager.updateEpic(task);
        assertEquals(List.of(epic1, epic2), taskManager.getEpics());

        task.setId(4);
        taskManager.updateEpic(task);
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(List.of(task, epic2), taskManager.getEpics());
        assertTrue(task.getSubtasksId().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());

        subtask5 = new Subtask("five", "task five", Status.DONE, task.getId());
        taskManager.addSubtask(subtask5);
        epic2 = new Epic("eight", "task eight", Status.NEW);
        int epicId2 = taskManager.addEpicTask(epic2);
        subtask6 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId2, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask6);

        Epic task2 = new Epic("two", "task two", Status.NEW);
        task2.setId(4);
        taskManager.updateEpic(task2);
        assertTrue(taskManager.getSubtasks().contains(subtask6));
    }

    @Test
    void deleteTask() {
        taskManager.deleteTask(200);
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        taskManager.deleteTask(1);
        assertNull(taskManager.getTask(1));
        assertEquals(2, taskManager.getTasks().size());
        assertEquals(List.of(task2, task3), taskManager.getTasks());
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(200);
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        Epic epic = taskManager.getEpic(subtask5.getEpicId());
        taskManager.deleteSubtask(6);
        assertNull(taskManager.getSubtask(6));
        assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteSubtask(7);
        assertEquals(Status.DONE, epic.getStatus());
        assertEquals(List.of(5), epic.getSubtasksId());
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(200);
        assertTrue(taskManager.getTasks().isEmpty());
        initializeAndAddTasks();
        taskManager.deleteEpic(4);
        assertNull(taskManager.getEpic(4));
        assertEquals(1, taskManager.getEpics().size());
        assertNull(taskManager.getEpicSubtasks(epic1));
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void updateEpicStatusWhenEmpty() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        epic1 = new Epic("three", "task three", Status.IN_PROGRESS);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("four", "task four", Status.NEW, epicId1, 10, qq.plusHours(2));
        subtask6 = new Subtask("five", "task five", Status.NEW, epicId1);
        subtask7 = new Subtask("six", "task six", Status.NEW, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        taskManager.deleteEpic(epicId1);
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список пуст");
    }

    @Test
    void updateEpicStatusWhenAllNew() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        epic1 = new Epic("three", "task three", Status.IN_PROGRESS);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("four", "task four", Status.NEW, epicId1, 10, qq.plusHours(2));
        subtask6 = new Subtask("five", "task five", Status.NEW, epicId1);
        subtask7 = new Subtask("six", "task six", Status.NEW, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        Epic epic1 = new Epic("three", "task three", Status.NEW);
        epic1.setId(1);
        assertEquals(epic1.getStatus(), this.epic1.getStatus(), "Статус эпика new");
    }

    @Test
    void updateEpicStatusWhenAllDone() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        epic1 = new Epic("three", "task three", Status.NEW);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("four", "task four", Status.DONE, epicId1, 10, qq.plusHours(2));
        subtask6 = new Subtask("five", "task five", Status.DONE, epicId1);
        subtask7 = new Subtask("six", "task six", Status.DONE, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        Epic epic1 = new Epic("three", "task three", Status.DONE);
        epic1.setId(1);
        assertEquals(epic1.getStatus(), this.epic1.getStatus(), "Статус эпика done");
    }

    @Test
    void updateEpicStatusWhenNewAndDone() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        epic1 = new Epic("three", "task three", Status.NEW);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("four", "task four", Status.NEW, epicId1, 10, qq.plusHours(2));
        subtask6 = new Subtask("five", "task five", Status.DONE, epicId1);
        subtask7 = new Subtask("six", "task six", Status.DONE, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        Epic epic1 = new Epic("three", "task three", Status.IN_PROGRESS);
        epic1.setId(1);
        assertEquals(epic1.getStatus(), this.epic1.getStatus(), "Статус эпика IN_PROGRESS");
    }

    @Test
    void updateEpicStatusWhenAllInProgress() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список пуст");
        epic1 = new Epic("three", "task three", Status.NEW);
        int epicId1 = taskManager.addEpicTask(epic1);
        subtask5 = new Subtask("four", "task four", Status.IN_PROGRESS, epicId1, 10, qq.plusHours(2));
        subtask6 = new Subtask("five", "task five", Status.IN_PROGRESS, epicId1);
        subtask7 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1, 10, qq.plusHours(3));
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        taskManager.addSubtask(subtask7);
        Epic epic1 = new Epic("three", "task three", Status.IN_PROGRESS);
        epic1.setId(1);
        assertEquals(epic1.getStatus(), this.epic1.getStatus(), "Статус эпика IN_PROGRESS");
    }

    @Test
    void getHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
        initializeAndAddTasks();
        taskManager.getTask(1);
        taskManager.deleteSubtask(5);
        for (int i = 0; i < 20; i++) {
            taskManager.getSubtask(6);
            taskManager.getSubtask(7);
            taskManager.getEpic(8);
        }
        assertEquals(4, taskManager.getHistory().size());
        assertEquals((List.of(task1, subtask6, subtask7, epic2)), taskManager.getHistory());
    }
}