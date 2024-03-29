package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    Task getTask(int id);

    Subtask getSubtask(int id);

    ArrayList<Subtask> getEpicSubtasks(int id);

    Epic getEpic(int id);

    void addTask(Task task);

    void addSubtask(Subtask task);

    int addEpicTask(Epic task);

    void updateTask(Task task);

    void updateSubtask(Subtask task);

    void updateEpic(Epic task);

    void deleteTask(int taskId);

    void deleteSubtask(int taskId);

    void deleteEpic(int taskId);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}