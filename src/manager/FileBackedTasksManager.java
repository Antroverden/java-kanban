package manager;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                break;
            } else {
                Task task = taskManager.FromString(lines.get(i));
                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    taskManager.epicTasks.put(epic.getId(), epic);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    taskManager.subtasks.put(subtask.getId(), subtask);
                    Epic epic = taskManager.epicTasks.get(subtask.getEpicId());
                    epic.addSubTask(subtask.getId());
                } else {
                    taskManager.tasks.put(task.getId(), task);
                }
            }
        }
        List<Integer> history = historyFromString(lines.get(lines.size() - 1));
        for (Integer id : history) {
            if (taskManager.tasks.containsKey(id)) {
                taskManager.historyManager.add(taskManager.tasks.get(id));
            } else if (taskManager.subtasks.containsKey(id)) {
                taskManager.historyManager.add(taskManager.subtasks.get(id));
            } else {
                taskManager.historyManager.add(taskManager.epicTasks.get(id));
            }
        }
        return taskManager;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getStatus() + "," + "Description " + task.getDescription() + "," + getEpicId(task);
    }

    public Task FromString(String line) {
        Task task;
        String[] values = line.split(",");
        final int taskId = Integer.parseInt(values[0]);
        if (taskId > id) {
            id = taskId;
        }
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        if (type.equals(TaskType.TASK)) {
            task = new Task(name, description, status);
        } else if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(values[5]);
            task = new Subtask(name, description, status, epicId);
        } else if (type.equals(TaskType.EPIC)) {
            task = new Epic(name, description, status);
        } else {
            throw new RuntimeException("Задача неизвестного типа");
        }
        task.setId(taskId);
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(task -> task.getId().toString())
                .collect(Collectors.joining(","));
    }

    public static List<Integer> historyFromString(String line) {
        return Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return ((Subtask) task).getEpicId().toString();
        } else {
            return "";
        }
    }

    protected void save() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(subtasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.sort(Comparator.comparingInt(Task::getId));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : allTasks) {
                writer.write(toString(task));
                writer.newLine();
            }
            writer.newLine();
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(Exception e) {
            super(e);
        }
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public int addEpicTask(Epic task) {
        int id = super.addEpicTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask task) {
        super.updateSubtask(task);
        save();
    }

    @Override
    public void updateEpic(Epic task) {
        super.updateEpic(task);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubtask(int taskId) {
        super.deleteSubtask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int taskId) {
        super.deleteEpic(taskId);
        save();
    }
}