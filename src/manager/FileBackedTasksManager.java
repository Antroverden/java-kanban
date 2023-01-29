package manager;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static final String ROOT = "resources";
    private final File file;

    private FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {

        File file = new File(ROOT + File.separator + "task.csv");
        Files.createDirectories(Path.of("resources"));
        if (!file.exists()) {
            Files.createFile(Path.of(file.getPath()));
        }
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        Task task1 = new Task("one", "task one", Status.NEW);
        Task task2 = new Task("two", "task two", Status.NEW);
        task1.getId();
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTask(1);
        manager.getTask(2);
        manager.deleteTask(2);

        Epic epic = new Epic("three", "task three", Status.NEW);
        int epicId1 = manager.addEpicTask(epic);

        Subtask subtask1 = new Subtask("four", "task four", Status.NEW, epicId1);
        Subtask subtask12 = new Subtask("five", "task five", Status.DONE, epicId1);
        Subtask subtask13 = new Subtask("six", "task six", Status.IN_PROGRESS, epicId1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask12);
        manager.addSubtask(subtask13);

        Epic epic2 = new Epic("seven", "task seven", Status.NEW);
        int epicId2 = manager.addEpicTask(epic2);
        Subtask subtask14 = new Subtask("eight", "task eight", Status.IN_PROGRESS, epicId2);
        manager.addSubtask(subtask14);

        for (int i = 0; i < 20; i++) {
            manager.getSubtask(5);
            manager.getSubtask(4);
            manager.getEpic(7);
        }
        manager.getEpic(7);
        manager.getEpic(3);
        manager.getEpic(7);

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(file);

        manager.getHistory().forEach(System.out::println);
        fileBackedTasksManager.getHistory().forEach(System.out::println);
        assert manager.equals(fileBackedTasksManager);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FileBackedTasksManager that = (FileBackedTasksManager) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file);
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
                Task task = taskManager.fromString(lines.get(i));
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

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + ","
                + task.getStatus() + "," + "Description " + task.getDescription() + "," + getEpicId(task);
    }

    private Task fromString(String value) {
        Task task;
        String[] values = value.split(",");
        final int taskId = Integer.parseInt(values[0]);
        if (taskId > id) {
            id = taskId;
        }
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4].substring(12);
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

    private static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(task -> task.getId().toString())
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return ((Subtask) task).getEpicId().toString();
        } else {
            return "";
        }
    }

    private void save() {
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