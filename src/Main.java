import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("one", "task one", Status.NEW);
        Task task2 = new Task("two", "task two", Status.NEW);
        task1.getId();
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("three", "task three", Status.NEW);
        int epicId1 = manager.addEpicTask(epic);

        Subtask subtask1 = new Subtask("four", "task four", Status.NEW, epicId1);
        Subtask subtask12 = new Subtask("five", "task five", Status.DONE, epicId1);
        Subtask subtask13 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask12);
        manager.addSubtask(subtask13);

        Epic epic2 = new Epic("seven", "task seven", Status.NEW);
        manager.addEpicTask(epic2);

        for (int i = 0; i < 20; i++) {
            manager.getTask(1);
            manager.getTask(2);
            manager.getSubtask(5);
            manager.getSubtask(4);
            manager.getEpic(7);
        }
        manager.getEpic(7);
        manager.getEpic(3);
        for (int i = 0; i < 20; i++) {
            manager.getTask(2);
        }

        manager.deleteTask(2);
        manager.deleteEpic(3);

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}