package tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        type = TaskType.SUBTASK;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getEpicId() {
        return epicId;
    }
}