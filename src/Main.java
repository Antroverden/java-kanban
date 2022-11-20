public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("one", "task one", "NEW");
        Task task2 = new Task("two", "task two", "NEW");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("three", "task three", "NEW");
        int epicId1 = manager.addEpicTask(epic);

        Subtask subtask1 = new Subtask("four", "task four", "NEW", epicId1);
        Subtask subtask12 = new Subtask("five", "task five", "NEW", epicId1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask12);

        Epic epic2 = new Epic("six", "task six", "NEW");
        int epicId2 = manager.addEpicTask(epic2);

        Subtask subtask21 = new Subtask("seven", "task seven", "DONE", epicId2);
        manager.addSubtask(subtask21);

        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getTasks());
        manager.deleteSubtask(7);
        manager.deleteEpic(3);
        manager.deleteTask(1);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}