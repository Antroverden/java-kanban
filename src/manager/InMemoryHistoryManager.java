package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;

    private final HashMap<Integer, Node> history = new HashMap<>();

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    public void linkLast(Task task) {
        Node node = last;
        last = new Node(last, task, null);
        if (node == null) {
            first = last;
        } else {
            last.prev = node;
            node.next = last;
        }
        history.put(task.getId(), last);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        Node node = first;
        for (int i = 0; i < history.size(); i++) {
            result.add(node.task);
            node = node.next;
        }
        return result;
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        removeNode(node);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public void removeNode(Node node) {
        if (node != null) {
            if (node.prev == null) {
                first = node.next;
                node.next.prev = null;
            } else if (node.next == null) {
                last = node.prev;
                node.prev.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }
    }
}