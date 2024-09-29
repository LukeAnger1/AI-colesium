package V5;

import aic2024.user.UnitController;

import java.util.Arrays;

public class CircularBuffer<T> {
    private final T[] buffer;
    private int head = 0;
    private int tail = 0;
    private boolean isFull = false;
    private boolean overlapNotified = false;


    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.buffer = (T[]) new Object[capacity];
    }

    public void add(T item) {
        buffer[head] = item;

        if (isFull) {
            tail = (tail + 1) % buffer.length; // Advance tail to avoid collision
            if (!overlapNotified) {
//                uc.println("Buffer overlap occurred!");
                overlapNotified = true; // Ensure notification is sent only once per overlap
            }
        }

        head = (head + 1) % buffer.length;
        isFull = head == tail;
    }

    public T remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Buffer is empty");
        }

        T item = buffer[tail];
        buffer[tail] = null; // Clear the removed element
        tail = (tail + 1) % buffer.length;
        isFull = false; // Once an element is removed, buffer is no longer full
        overlapNotified = false; // Reset overlap notification flag

        return item;
    }

    public boolean isEmpty() {
        return head == tail && !isFull;
    }

    public boolean isFull() {
        return isFull;
    }

    public int size() {
        if (isFull) {
            return buffer.length;
        }
        if (head >= tail) {
            return head - tail;
        }
        return buffer.length - tail + head;
    }

    public int capacity() {
        return buffer.length;
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Buffer is empty");
        }

        T item = buffer[tail]; // Get the first element
        buffer[tail] = null;   // Remove the element from the buffer
        tail = (tail + 1) % buffer.length; // Move the tail pointer
        isFull = false; // Since we removed an element, the buffer can't be full
        overlapNotified = false; // Reset overlap notification flag

        return item;
    }

    @Override
    public String toString() {
        return "CircularBuffer{" +
                "buffer=" + Arrays.toString(buffer) +
                ", head=" + head +
                ", tail=" + tail +
                ", isFull=" + isFull +
                '}';
    }
}
