package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private final DoublyLinkedList historyList = new DoublyLinkedList();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        historyList.addLast(task);
        nodeMap.put(task.getId(), historyList.getTail());

        if (historyList.size() > MAX_HISTORY_SIZE) {
            Node oldestNode = historyList.removeFirst();
            nodeMap.remove(oldestNode.task.getId());
        }
    }

    @Override
    public void remove(int id) {
        if (!nodeMap.containsKey(id)) {
            return;
        }
        Node nodeToRemove = nodeMap.get(id);
        historyList.removeNode(nodeToRemove);
        nodeMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private static class DoublyLinkedList {
        private Node head;
        private Node tail;
        private int size;

        void addLast(Task task) {
            Node newNode = new Node(task);
            if (tail != null) {
                tail.next = newNode;
                newNode.prev = tail;
            }
            tail = newNode;
            if (head == null) {
                head = newNode;
            }
            size++;
        }

        void removeNode(Node node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
            size--;
        }

        Node removeFirst() {
            if (head == null) {
                return null;
            }
            Node firstNode = head;
            removeNode(firstNode);
            return firstNode;
        }

        List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node current = head;
            while (current != null) {
                tasks.add(current.task);
                current = current.next;
            }
            return tasks;
        }

        int size() {
            return size;
        }

        Node getTail() {
            return tail;
        }
    }
}
