package com.example.mywebapp;

public class CustomPriorityQueue<T extends Comparable<T>> {
    private T[] heap;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public CustomPriorityQueue() {
        this.heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public void add(T element) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = element;
        siftUp(size);
        size++;
    }

    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T result = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newHeap = (T[]) new Comparable[heap.length * 2];
        for (int i = 0; i < heap.length; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    private void siftUp(int index) {
        T element = heap[index];
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            T parent = heap[parentIndex];
            if (element.compareTo(parent) <= 0) { // Max-heap: parent should be >= child
                break;
            }
            heap[index] = parent;
            index = parentIndex;
        }
        heap[index] = element;
    }

    private void siftDown(int index) {
        T element = heap[index];
        int half = size / 2;
        while (index < half) {
            int leftChild = 2 * index + 1;
            int rightChild = leftChild + 1;
            int maxIndex = leftChild;

            if (rightChild < size && heap[rightChild].compareTo(heap[leftChild]) > 0) {
                maxIndex = rightChild;
            }

            if (element.compareTo(heap[maxIndex]) >= 0) { // Max-heap: parent should be >= child
                break;
            }

            heap[index] = heap[maxIndex];
            index = maxIndex;
        }
        heap[index] = element;
    }
}