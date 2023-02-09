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

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int id = 0;

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addToPrioritizedTasks(Task task) {
        if (isNotCrossed(task)) {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }

    private void updateEpicTime(Epic epic) {
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

    private boolean isNotCrossed(Task task) {
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
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicTasks.values());
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
        if (tasks.containsKey(id)) {
            final Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            final Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        } else return null;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        if (epic != null && epicTasks.containsKey(epic.getId())) {
            ArrayList<Subtask> subtask = new ArrayList<>();
            for (int subtaskId : epic.getSubtasksId()) {
                subtask.add(subtasks.get(subtaskId));
            }
            return subtask;
        } else return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epicTasks.containsKey(id)) {
            final Epic epic = epicTasks.get(id);
            historyManager.add(epic);
            return epic;
        } else return null;
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            final int id = ++this.id;
            task.setId(id);
            tasks.put(id, task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void addSubtask(Subtask task) {
        if (task != null && epicTasks.containsKey(task.getEpicId())) {
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
        if (task != null) {
            final int id = ++this.id;
            task.setId(id);
            epicTasks.put(id, task);
            return id;
        } else return 0;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateSubtask(Subtask task) {
        if (task != null && subtasks.containsKey(task.getId()) && epicTasks.containsKey(task.getEpicId())) {
            subtasks.put(task.getId(), task);
            Epic epic = getEpic(task.getEpicId());
            epic.addSubTask(task.getId());
            updateEpicStatus(epic);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateEpic(Epic task) {
        if (task != null && epicTasks.containsKey(task.getId())) {
            if (!epicTasks.get(task.getId()).getSubtasksId().isEmpty()) {
                for (int i : epicTasks.get(task.getId()).getSubtasksId()) {
                    subtasks.remove(i);
                }
            }
            epicTasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteSubtask(int taskId) {
        if (subtasks.containsKey(taskId)) {
            Subtask task = subtasks.get(taskId);
            prioritizedTasks.remove(task);
            subtasks.remove(taskId);
            Epic epic = epicTasks.get(task.getEpicId());
            epic.deleteSubtask(taskId);
            updateEpicStatus(epic);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteEpic(int taskId) {
        if (epicTasks.containsKey(taskId)) {
            Epic epic = epicTasks.get(taskId);
            for (Integer id : epic.getSubtasksId()) {
                prioritizedTasks.remove(subtasks.get(id));
                subtasks.remove(id);
                historyManager.remove(id);
            }
            epicTasks.remove(taskId);
            historyManager.remove(taskId);
        }
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