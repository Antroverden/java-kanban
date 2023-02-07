package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subtasks = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, long duration, LocalDateTime startTime,
                LocalDateTime endTime) {
        super(name, description, status, duration, startTime);
        type = TaskType.EPIC;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endTime);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        type = TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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