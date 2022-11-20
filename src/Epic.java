import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    ArrayList<Integer> getSubtasksId() {
        return subtasks;
    }

    void addSubTask(int subtaskId) {
        subtasks.add(subtaskId);
    }
}