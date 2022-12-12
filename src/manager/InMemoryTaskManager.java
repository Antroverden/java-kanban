package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public Collection<Epic> getEpics() {
        return epicTasks.values();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epicTasks.values()) {
            epic.clearSubtasks();
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epicTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> subtask = new ArrayList<>();
        for (int subtaskId : epic.getSubtasksId()) {
            subtask.add(subtasks.get(subtaskId));
        }
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addTask(Task task) {
        final int id = ++this.id;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addSubtask(Subtask task) {
        Epic epic = getEpic(task.getEpicId());
        final int id = ++this.id;
        task.setId(id);
        subtasks.put(id, task);
        epic.addSubTask(task.getId());
        updateEpicStatus(epic);
    }

    @Override
    public int addEpicTask(Epic task) {
        final int id = ++this.id;
        task.setId(id);
        epicTasks.put(id, task);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask task) {
        if (subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), task);
            Epic epic = getEpic(task.getEpicId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateEpic(Epic task) {
        if (epicTasks.containsKey(task.getId())) {
            epicTasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteSubtask(int taskId) {
        Subtask task = getSubtask(taskId);
        subtasks.remove(taskId);
        Epic epic = getEpic(task.getEpicId());
        epic.deleteSubtask(taskId);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteEpic(int taskId) {
        for (Integer id : epicTasks.get(taskId).getSubtasksId()) {
            subtasks.remove(id);
        }
        epicTasks.remove(taskId);
    }

    @Override
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