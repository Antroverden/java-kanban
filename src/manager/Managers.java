package manager;

import java.io.File;

public class Managers {
    public static final String ROOT = "resources";

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File(ROOT + File.separator + "task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}