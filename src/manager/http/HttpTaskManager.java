package manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    private final KVTaskClient client;

    public static final String keyTasks = "tasks";
    public static final String keySubtasks = "subtasks";
    public static final String keyEpics = "epics";
    public static final String keyHistory = "history";
    public static final Type arrayListTaskType = new TypeToken<ArrayList<Task>>() {
    }.getType();
    public static final Type arrayListSubtaskType = new TypeToken<ArrayList<Subtask>>() {
    }.getType();
    public static final Type arrayListEpicType = new TypeToken<ArrayList<Epic>>() {
    }.getType();
    public static final Type arrayListHistoryType = new TypeToken<ArrayList<Integer>>() {
    }.getType();

    public HttpTaskManager(String url, boolean load) {
        super(null);
        gson = new Gson();
        client = new KVTaskClient(url);
        if (load) {
            loadFromServer();
        }
    }

    public static class httpTaskManagerException extends RuntimeException {

        public httpTaskManagerException(Exception e) {
            super(e);
        }

        public httpTaskManagerException(String message) {
            super(message);
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put(keyTasks, jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put(keySubtasks, jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epicTasks.values()));
        client.put(keyEpics, jsonEpics);
        String jsonHistory = gson.toJson(historyManager.getHistory().stream()
                .map(Task::getId).collect(Collectors.toList()));
        client.put(keyHistory, jsonHistory);
    }

    private void addTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            int taskId = task.getId();
            TaskType type = task.getType();
            if (taskId > id) {
                id = taskId;
            }
            if (type.equals(TaskType.EPIC)) {
                Epic epic = (Epic) task;
                epicTasks.put(epic.getId(), epic);
                prioritizedTasks.add(epic);
            } else if (type.equals(TaskType.SUBTASK)) {
                Subtask subtask = (Subtask) task;
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epicTasks.get(subtask.getEpicId());
                epic.addSubTask(subtask.getId());
                prioritizedTasks.add(subtask);
            } else {
                this.tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }
    }

    private void loadFromServer() {
        ArrayList<Task> tasks = gson.fromJson(client.load(keyTasks), arrayListTaskType);
        addTasks(tasks);
        ArrayList<Epic> epics = gson.fromJson(client.load(keyEpics), arrayListEpicType);
        addTasks(epics);
        ArrayList<Subtask> subtasks = gson.fromJson(client.load(keySubtasks), arrayListSubtaskType);
        addTasks(subtasks);
        List<Integer> history = gson.fromJson(client.load(keyHistory), arrayListHistoryType);
        for (Integer id : history) {
            if (this.tasks.containsKey(id)) {
                historyManager.add(this.tasks.get(id));
            } else if (this.epicTasks.containsKey(id)) {
                historyManager.add(this.epicTasks.get(id));
            } else if (this.subtasks.containsKey(id))
                historyManager.add(this.subtasks.get(id));
        }
    }
}