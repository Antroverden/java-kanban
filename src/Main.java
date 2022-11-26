public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("one", "task one", Status.NEW);
        Task task2 = new Task("two", "task two", Status.NEW);
        task1.getId();
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("three", "task three", Status.NEW);
        int epicId1 = manager.addEpicTask(epic);

        Subtask subtask1 = new Subtask("four", "task four", Status.NEW, epicId1);
        Subtask subtask12 = new Subtask("five", "task five", Status.DONE, epicId1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask12);

        Epic epic2 = new Epic("six", "task six", Status.NEW);
        int epicId2 = manager.addEpicTask(epic2);

        Subtask subtask21 = new Subtask("seven", "task seven", Status.DONE, epicId2);
        manager.addSubtask(subtask21);

        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getTasks());
        manager.deleteSubtask(7);
        manager.deleteTask(1);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}