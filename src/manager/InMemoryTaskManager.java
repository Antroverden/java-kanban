package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int id = 0;

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
        for (Task task : getTasks()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return id == that.id && Objects.equals(tasks, that.tasks) && Objects.equals(subtasks, that.subtasks)
                && Objects.equals(epicTasks, that.epicTasks) && Objects.equals(historyManager, that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, subtasks, epicTasks, historyManager, id);
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : getEpics()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
        for (Subtask subtask : getSubtasks()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : getEpics()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : getSubtasks()) {
            historyManager.remove(subtask.getId());
        }
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
        Epic epic = epicTasks.get(task.getEpicId());
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
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtask(int taskId) {
        Subtask task = subtasks.get(taskId);
        subtasks.remove(taskId);
        Epic epic = epicTasks.get(task.getEpicId());
        epic.deleteSubtask(taskId);
        updateEpicStatus(epic);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int taskId) {
        for (Integer id : epicTasks.get(taskId).getSubtasksId()) {
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epicTasks.remove(taskId);
        historyManager.remove(taskId);
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