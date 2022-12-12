package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int LIMIT = 10;
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= LIMIT) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}