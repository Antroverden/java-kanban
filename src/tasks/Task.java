package tasks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected TaskType type;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        type = TaskType.TASK;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        type = TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status && type == task.type
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, type, duration, startTime);
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration, ChronoUnit.MINUTES);
        } else {
            return null;
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public TaskType getType() {
        return type;
    }
}