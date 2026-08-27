package turtley.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import turtley.exception.TurtleyException;

/**
 * Owns Turtley's ordered collection of tasks and its basic list operations.
 */
public class TaskList {

    private static final int MAX_TASK_NUM = 100;
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage
     */
    public TaskList(List<Task> initialTasks) {
        addAll(initialTasks);
    }

    /**
     * Returns whether this list has reached its supported capacity.
     *
     * @return {@code true} when the list contains 100 tasks
     */
    public boolean isFull() {
        return tasks.size() >= MAX_TASK_NUM;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list has no tasks.
     *
     * @return {@code true} when the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the supplied zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     * @throws TurtleyException if the list is full or the task is null
     */
    public void add(Task task) {
        if (task == null) {
            throw new TurtleyException("Cannot add a null task.");
        }
        if (isFull()) {
            throw new TurtleyException("Task list exceeds 100 tasks.");
        }
        tasks.add(task);
    }

    /**
     * Adds all supplied tasks to the end of the list.
     *
     * @param newTasks the tasks to add
     * @throws TurtleyException if the tasks are null, contain null, or exceed capacity
     */
    public void addAll(List<Task> newTasks) {
        if (newTasks == null || newTasks.stream().anyMatch(task -> task == null)) {
            throw new TurtleyException("Cannot add null tasks.");
        }
        if (tasks.size() + newTasks.size() > MAX_TASK_NUM) {
            throw new TurtleyException("Task list exceeds 100 tasks.");
        }
        tasks.addAll(newTasks);
    }

    /**
     * Removes and returns the task at the supplied zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Inserts a task at the supplied zero-based index.
     *
     * @param index the insertion index
     * @param task the task to insert
     */
    public void add(int index, Task task) {
        if (task == null) {
            throw new TurtleyException("Cannot add a null task.");
        }
        if (isFull()) {
            throw new TurtleyException("Task list exceeds 100 tasks.");
        }
        tasks.add(index, task);
    }

    /**
     * Provides a read-only view for collaborators such as Storage.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
