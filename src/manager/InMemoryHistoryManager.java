package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    int idTail = 0;
    int idHead = 0;

    private final HashMap<Integer, Node> history = new HashMap<>();

    public static class Node {
        Task task;
        int nextNode;
        int prevNode;

        Node(int prevNode, Task task, int nextNode) {
            this.task = task;
            this.nextNode = nextNode;
            this.prevNode = prevNode;
        }

        public Task getTask() {
            return task;
        }

        public int getPrevNode() {
            return prevNode;
        }

        public int getNextNode() {
            return nextNode;
        }

        public void setNextNode(int nextNode) {
            this.nextNode = nextNode;
        }

        public void setPrevNode(int prevNode) {
            this.prevNode = prevNode;
        }
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    public void linkLast(Task task) {
        if (idHead == 0) {
            idHead = task.getId();
            history.put(task.getId(), new Node(idTail, task, 0));
            idTail = task.getId();
        } else {
            int prevId = idHead;
            history.get(idHead).setNextNode(task.getId());
            idHead = task.getId();
            history.put(task.getId(), new Node(prevId, task, 0));
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>(history.size());
        result.add(getTask(idTail));
        int idNextNode = history.get(idTail).getNextNode();
        for (int i = 1; i < history.size(); i++) {
            result.add(getTask(idNextNode));
            idNextNode = history.get(idNextNode).getNextNode();
        }
        return result;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public void removeNode(Node node) {
        if (node != null) {
            if (history.containsKey(node.getTask().getId())) {
                if ((node.getPrevNode() != 0) && (node.getNextNode() != 0)) {
                    history.get(node.getPrevNode()).setNextNode(node.getNextNode());
                    history.get(node.getNextNode()).setPrevNode(node.getPrevNode());
                }
                if (node.getTask().getId() == idHead) {
                    idHead = node.getPrevNode();
                    history.get(node.getPrevNode()).setNextNode(0);
                }
                if (node.getTask().getId() == idTail) {
                    idTail = node.getNextNode();
                    history.get(node.getNextNode()).setPrevNode(0);
                }
                history.remove(node.getTask().getId());
            }
        }
    }

    public Task getTask(int id) {
        return history.get(id).getTask();
    }
}