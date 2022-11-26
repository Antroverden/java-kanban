public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, Statuses status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    Statuses getStatus() {
        return status;
    }

    Integer getEpicId() {
        return epicId;
    }
}