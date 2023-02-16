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
    private static final String KEY_TASKS = "tasks";
    private static final String KEY_SUBTASKS = "subtasks";
    private static final String KEY_EPICS = "epics";
    private static final String KEY_HISTORY = "history";
    private static final Type ARRAY_LIST_TASK_TYPE = new TypeToken<ArrayList<Task>>() {
    }.getType();
    private static final Type ARRAY_LIST_SUBTASK_TYPE = new TypeToken<ArrayList<Subtask>>() {
    }.getType();
    private static final Type ARRAY_LIST_EPIC_TYPE = new TypeToken<ArrayList<Epic>>() {
    }.getType();
    private static final Type ARRAY_LIST_HISTORY_TYPE = new TypeToken<ArrayList<Integer>>() {
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
        client.put(KEY_TASKS, jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put(KEY_SUBTASKS, jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epicTasks.values()));
        client.put(KEY_EPICS, jsonEpics);
        String jsonHistory = gson.toJson(historyManager.getHistory().stream()
                .map(Task::getId).collect(Collectors.toList()));
        client.put(KEY_HISTORY, jsonHistory);
    }

    private void addTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            int taskId = task.getId();
            TaskType type = task.getType();
            if (taskId > id) {
                id = taskId;
            }
            if (type.equals(TaskType.TASK)) {
                this.tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            } else if (type.equals(TaskType.EPIC)) {
                Epic epic = (Epic) task;
                epicTasks.put(epic.getId(), epic);
                prioritizedTasks.add(epic);
            } else if (type.equals(TaskType.SUBTASK)) {
                Subtask subtask = (Subtask) task;
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epicTasks.get(subtask.getEpicId());
                epic.addSubTask(subtask.getId());
                prioritizedTasks.add(subtask);
            }
        }
    }

    private void loadFromServer() {
        ArrayList<Task> tasks = gson.fromJson(client.load(KEY_TASKS), ARRAY_LIST_TASK_TYPE);
        addTasks(tasks);
        ArrayList<Epic> epics = gson.fromJson(client.load(KEY_EPICS), ARRAY_LIST_EPIC_TYPE);
        addTasks(epics);
        ArrayList<Subtask> subtasks = gson.fromJson(client.load(KEY_SUBTASKS), ARRAY_LIST_SUBTASK_TYPE);
        addTasks(subtasks);
        List<Integer> history = gson.fromJson(client.load(KEY_HISTORY), ARRAY_LIST_HISTORY_TYPE);
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