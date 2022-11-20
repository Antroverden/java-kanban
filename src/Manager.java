import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Manager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();

    public int id = 0;

    Collection<Task> getTasks() {
        return tasks.values();
    }

    Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    Collection<Epic> getEpics() {
        return epicTasks.values();
    }

    Collection<Subtask> getEpicSubtasks() {
        return null;
    }

    void deleteTasks() {
        tasks.clear();
    }

    void deleteSubtasks() {
        subtasks.clear();
    }

    void deleteEpics() {
        subtasks.clear();
        epicTasks.clear();
    }

    Task getTask(int id) {
        return tasks.get(id);
    }

    Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> subtask = new ArrayList<>();
        for (int subtaskId : epic.getSubtasksId()) {
            subtask.add(subtasks.get(subtaskId));
        }
        return subtask;
    }

    Epic getEpic(int id) {
        return epicTasks.get(id);
    }

    void addTask(Task task) {
        final int id = ++this.id;
        task.setId(id);
        tasks.put(id, task);
    }

    void addSubtask(Subtask task) {
        Epic epic = getEpic(task.getEpicId());
        final int id = ++this.id;
        task.setId(id);
        subtasks.put(id, task);
        epic.addSubTask(task.getId());
        updateEpicStatus(epic);
    }

    int addEpicTask(Epic task) {
        final int id = ++this.id;
        task.setId(id);
        epicTasks.put(id, task);
        return id;
    }

    void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    void updateSubtask(Subtask task) {
        subtasks.put(task.getId(), task);
        Epic epic = getEpic(task.getEpicId());
        updateEpicStatus(epic);
    }

    void updateEpic(Epic task) {
        epicTasks.put(task.getId(), task);
    }

    void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    void deleteSubtask(int taskId) {
        Subtask task = getSubtask(taskId);
        subtasks.remove(taskId);
        Epic epic = getEpic(task.getEpicId());
        updateEpicStatus(epic);
    }

    void deleteEpic(int taskId) {
        for (Integer id : epicTasks.get(taskId).getSubtasksId()) {
            subtasks.remove(id);
        }
        epicTasks.remove(taskId);
    }

    void updateEpicStatus(Epic epic) {
        HashSet<String> statuses = new HashSet<>();
        for (Integer s : epic.getSubtasksId()) {
            if (!(subtasks.get(s) == null)) {
                statuses.add(subtasks.get(s).getStatus());
            }
        }
        if (statuses.isEmpty() || (!statuses.contains("IN_PROGRESS") && !statuses.contains("DONE"))) {
            epic.setStatus("NEW");
        } else if (!statuses.contains("IN_PROGRESS") && !statuses.contains("NEW")) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }
}