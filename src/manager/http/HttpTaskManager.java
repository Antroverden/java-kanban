package manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(String url, boolean load) {
        super(null);
        gson = Managers.getGson();
        client = new KVTaskClient(url);
        if (load) {
            loadFromServer();
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubtascs = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtascs);
        String jsonEpics = gson.toJson(new ArrayList<>(epicTasks.values()));
        client.put("epics", jsonEpics);
        String jsonHistory = gson.toJson(historyManager.getHistory().stream()
                .map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
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
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);
        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);
        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);
        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
        }.getType());
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