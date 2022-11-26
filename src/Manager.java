import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();

    private int id = 0;

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getEpics() {
        return epicTasks.values();
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epicTasks.values()) {
            epic.clearSubtasks();
        }
        subtasks.clear();
    }

    public void deleteEpics() {
        subtasks.clear();
        epicTasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> subtask = new ArrayList<>();
        for (int subtaskId : epic.getSubtasksId()) {
            subtask.add(subtasks.get(subtaskId));
        }
        return subtask;
    }

    public Epic getEpic(int id) {
        return epicTasks.get(id);
    }

    public void addTask(Task task) {
        final int id = ++this.id;
        task.setId(id);
        tasks.put(id, task);
    }

    public void addSubtask(Subtask task) {
        Epic epic = getEpic(task.getEpicId());
        final int id = ++this.id;
        task.setId(id);
        subtasks.put(id, task);
        epic.addSubTask(task.getId());
        updateEpicStatus(epic);
    }

    public int addEpicTask(Epic task) {
        final int id = ++this.id;
        task.setId(id);
        epicTasks.put(id, task);
        return id;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask task) {
        if (subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), task);
            Epic epic = getEpic(task.getEpicId());
            updateEpicStatus(epic);
        }
    }

    public void updateEpic(Epic task) {
        if (epicTasks.containsKey(task.getId())) {
            epicTasks.put(task.getId(), task);
        }
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteSubtask(int taskId) {
        Subtask task = getSubtask(taskId);
        subtasks.remove(taskId);
        Epic epic = getEpic(task.getEpicId());
        epic.deleteSubtask(taskId);
        updateEpicStatus(epic);
    }

    public void deleteEpic(int taskId) {
        for (Integer id : epicTasks.get(taskId).getSubtasksId()) {
            subtasks.remove(id);
        }
        epicTasks.remove(taskId);
    }

    public void updateEpicStatus(Epic epic) {
        int newCount = 0;
        int doneCount = 0;
        for (Integer s : epic.getSubtasksId()) {
            if (subtasks.get(s).getStatus() == Status.NEW) {
                newCount++;
            } else if (subtasks.get(s).getStatus() == Status.DONE) {
                doneCount++;
            }
        }
        if (newCount == epic.getNumberSubtasks()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epic.getNumberSubtasks()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}