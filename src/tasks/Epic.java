package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        type = TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasks;
    }

    public void addSubTask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void deleteSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public int getNumberSubtasks() {
        return subtasks.size();
    }
}