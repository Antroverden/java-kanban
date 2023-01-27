package manager;

import tasks.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskFormat {

    static int maxId = 0;

    private CSVTaskFormat() {
    }

    public static String toString(Task task) {
        return task.getId() + "," + task.getClass().getSimpleName().toUpperCase() + "," + task.getName() + ","
                + task.getStatus() + "," + "Description " + task.getDescription() + "," + getEpicId(task);
    }

    private static String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return ((Subtask) task).getEpicId().toString();
        } else {
            return "";
        }
    }

    public static Task taskFromString(String line) {
        Task task;
        String[] values = line.split(",");
        final int id = Integer.parseInt(values[0]);
        if (id > maxId) {
            maxId = id;
        }
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(values[5]);
            task = new Subtask(name, description, status, epicId);
            task.setId(id);
            task.setType(type);
            return task;
        } else if (type.equals(TaskType.EPIC)) {
            task = new Epic(name, description, status);
            task.setId(id);
            task.setType(type);
            return task;
        }
        task = new Task(name, description, status);
        task.setId(id);
        task.setType(type);
        return task;
    }

    public static String toString(List<Task> history) {
        return history.stream().map(task -> task.getId().toString())
                .collect(Collectors.joining(","));
    }

    public static List<Integer> historyFromString(String line) {
        return Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }
}