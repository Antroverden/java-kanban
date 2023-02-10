package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static manager.FileBackedTasksManager.ROOT;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    FileBackedTasksManager fileBackedTasksManager1;

    @BeforeEach
    void setUp() throws IOException {
        file = new File(ROOT + File.separator + "task.csv");
        Files.createDirectories(Path.of("resources"));
        if (!file.exists()) {
            Files.createFile(Path.of(file.getPath()));
        }
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    void tearDown() {
        assertTrue(file.delete());
    }

    @Test
    void loadFromFileWithEmptyList() {
        assertTrue(taskManager.getTasks().isEmpty(), "Пустой список");
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        assertTrue(fileBackedTasksManager1.getTasks().isEmpty(), "Пустой список");
        assertEquals(taskManager.getTasks(), fileBackedTasksManager1.getTasks(), "Правильное восстановление");
    }

    @Test
    void loadFromFileWithEmptyListAfterDeleteTask() {
        initializeTasks();
        taskManager.addTask(task1);
        taskManager.deleteTask(task1.getId());
        fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        assertTrue(fileBackedTasksManager1.getTasks().isEmpty(), "Пустой список");
        assertEquals(taskManager.getTasks(), fileBackedTasksManager1.getTasks(), "Правильное восстановление");
    }

    @Test
    void loadFromFileWhenEpicWithoutSubtasks() {
        initializeAndAddTasks();
        assertNotNull(taskManager.getTasks());
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTasks(), fileBackedTasksManager1.getTasks(), "Правильное восстановление");

        taskManager.deleteEpic(epic1.getId());
        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTasks(), fileBackedTasksManager2.getTasks(), "Правильное восстановление");
    }

    @Test
    void loadFromFileWhenEmptyHistory() {
        initializeTasks();
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        assertTrue(fileBackedTasksManager1.getTasks().isEmpty(), "Пустой список");
        assertEquals(taskManager.getTasks(), fileBackedTasksManager1.getTasks(), "Правильное восстановление");

        taskManager.addTask(task1);
        taskManager.deleteTask(task1.getId());
        fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        assertTrue(fileBackedTasksManager1.getTasks().isEmpty(), "Пустой список");
        assertEquals(taskManager.getTasks(), fileBackedTasksManager1.getTasks(), "Правильное восстановление");
    }
}