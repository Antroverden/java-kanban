import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
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