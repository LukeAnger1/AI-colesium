package fuckingRush;

import aic2024.user.UnitController;

import java.util.Arrays;

public class Buffer<T> {
    private final int[] buffer;
    private int changing_size = 0;
    private int final_size;

    @SuppressWarnings("unchecked")
    public Buffer(int capacity) {
        this.buffer = new int[capacity];
        this.final_size = capacity;
    }

    // only add if there is enough room
    public void add(int item) {
        if (changing_size < final_size) {
            buffer[changing_size] = item;
            changing_size++;
        }
    }

    public boolean isEmpty() {
        return changing_size == 0;
    }

    public boolean isFull() {
        return changing_size != 0;
    }

    public int size() {
        return changing_size;
    }

    public int capacity() {
        return final_size;
    }

    public void clear() {
        // Just change the changing size
        changing_size = 0;
    }

    // Get at the given index
    public int get(int index) {
        if (index >= changing_size) {
            return -1;
        }

        return buffer[index];
    }

    @Override
    public String toString() {
        return "CircularBuffer{" +
                "buffer=" + Arrays.toString(buffer);
    }
}
