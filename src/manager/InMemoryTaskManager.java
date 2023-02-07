package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int id = 0;

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void addToPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    public void updateEpicTime(Epic epic) {
        long sumDuration = 0;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (!epic.getSubtasksId().isEmpty()) {
            for (Subtask subtask : getEpicSubtasks(epic)) {
                if (subtask.getStartTime() == null) {
                    return;
                }
                sumDuration += subtask.getDuration();
                if ((startTime == null) || subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if ((endTime == null) || subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
        }
        epic.setDuration(sumDuration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    public boolean isNotCrossed(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start == null) {
            return true;
        }
        for (Task t : prioritizedTasks) {
            if (t.getStartTime() == null) {
                break;
            }
            if ((start.isAfter(t.getStartTime()) && start.isBefore(t.getEndTime()))
                    || (end.isAfter(t.getStartTime()) && end.isBefore(t.getEndTime()))) {
                return false;
            }
        }
        return true;
    }

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
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : getEpics()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
        for (Subtask subtask : getSubtasks()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : getEpics()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        for (Subtask subtask : getSubtasks()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
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
        if (isNotCrossed(task)) {
            final int id = ++this.id;
            task.setId(id);
            tasks.put(id, task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void addSubtask(Subtask task) {
        if (isNotCrossed(task)) {
            Epic epic = epicTasks.get(task.getEpicId());
            final int id = ++this.id;
            task.setId(id);
            subtasks.put(id, task);
            epic.addSubTask(task.getId());
            updateEpicStatus(epic);
            addToPrioritizedTasks(task);
        }
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
        addToPrioritizedTasks(task);
    }

    @Override
    public void updateSubtask(Subtask task) {
        if (subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), task);
            Epic epic = getEpic(task.getEpicId());
            updateEpicStatus(epic);
        }
        addToPrioritizedTasks(task);
    }

    @Override
    public void updateEpic(Epic task) {
        if (epicTasks.containsKey(task.getId())) {
            epicTasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtask(int taskId) {
        Subtask task = subtasks.get(taskId);
        prioritizedTasks.remove(task);
        subtasks.remove(taskId);
        Epic epic = epicTasks.get(task.getEpicId());
        epic.deleteSubtask(taskId);
        updateEpicStatus(epic);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int taskId) {
        for (Integer id : epicTasks.get(taskId).getSubtasksId()) {
            prioritizedTasks.remove(subtasks.get(id));
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
        updateEpicTime(epic);
    }
}