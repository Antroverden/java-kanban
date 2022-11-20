public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    String getStatus() {
        return status;
    }

    Integer getEpicId() {
        return epicId;
    }
}