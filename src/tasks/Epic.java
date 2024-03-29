package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {

    protected Set<Integer> subtasks = new HashSet<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        type = TaskType.EPIC;
        this.startTime = startTime;
    }

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
        return Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ArrayList<Integer> getSubtasksId() {
        return new ArrayList<>(subtasks);
    }

    public void addSubTask(Integer subtaskId) {
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