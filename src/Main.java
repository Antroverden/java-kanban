import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static manager.Managers.ROOT;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        File file = new File(ROOT + File.separator + "task.csv");
        Files.createDirectories(Path.of("resources"));
        if (!file.exists()) {
            Files.createFile(Path.of(file.getPath()));
        }

        Task task1 = new Task("one", "task one", Status.NEW);
        Task task2 = new Task("two", "task two", Status.NEW);
        task1.getId();
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTask(1);
        manager.getTask(2);
        manager.deleteTask(2);

        Epic epic = new Epic("three", "task three", Status.NEW);
        int epicId1 = manager.addEpicTask(epic);

        Subtask subtask1 = new Subtask("four", "task four", Status.NEW, epicId1);
        Subtask subtask12 = new Subtask("five", "task five", Status.DONE, epicId1);
        Subtask subtask13 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask12);
        manager.addSubtask(subtask13);

        Epic epic2 = new Epic("seven", "task seven", Status.NEW);
        int epicId2 = manager.addEpicTask(epic2);
        Subtask subtask14 = new Subtask("eight", "task eight", Status.IN_PROGRESS, epicId2);
        manager.addSubtask(subtask14);

        for (int i = 0; i < 20; i++) {
            manager.getSubtask(5);
            manager.getSubtask(4);
            manager.getEpic(7);
        }
        manager.getEpic(7);
        manager.getEpic(3);
        manager.getEpic(7);

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        manager.getHistory().forEach(System.out::println);

    }
}