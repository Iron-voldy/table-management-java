package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Queue implementation for managing table reservation requests
 * Implements queue operations from scratch without using Java Collections
 */
public class ReservationQueue {
    private static ReservationQueue instance = null;
    private Reservation[] queue;
    private int front;
    private int rear;
    private int size;
    private int maxSize;
    private Gson gson;
    private MergeSort mergeSort;

    private ReservationQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new Reservation[maxSize];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        this.mergeSort = new MergeSort();
        loadQueueFromFile();
    }

    public static synchronized ReservationQueue getInstance(int maxSize) {
        if (instance == null) {
            instance = new ReservationQueue(maxSize);
        }
        return instance;
    }

    public static synchronized ReservationQueue getInstance() {
        if (instance == null) {
            instance = new ReservationQueue(100); // Default size
        }
        return instance;
    }

    /**
     * Add a reservation to the queue (enqueue operation)
     */
    public boolean enqueue(Reservation reservation) {
        if (isFull()) {
            System.out.println("Queue is full! Cannot add more reservations.");
            return false;
        }

        // Assign a unique ID if not already set
        if (reservation.getId() == 0) {
            reservation.setId(generateNextId());
        }

        rear = (rear + 1) % maxSize;
        queue[rear] = reservation;
        size++;

        // Sort the queue by reservation time after adding
        sortQueueByTime();
        saveQueueToFile();

        System.out.println("Reservation added to queue: " + reservation);
        return true;
    }

    /**
     * Remove a reservation from the queue (dequeue operation)
     */
    public Reservation dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty! No reservations to remove.");
            return null;
        }

        Reservation reservation = queue[front];
        queue[front] = null;
        front = (front + 1) % maxSize;
        size--;

        saveQueueToFile();
        System.out.println("Reservation removed from queue: " + reservation);
        return reservation;
    }

    /**
     * Peek at the front reservation without removing it
     */
    public Reservation peek() {
        if (isEmpty()) {
            return null;
        }
        return queue[front];
    }

    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Check if queue is full
     */
    public boolean isFull() {
        return size == maxSize;
    }

    /**
     * Get current size of queue
     */
    public int getSize() {
        return size;
    }

    /**
     * Get all reservations in queue as array (for display purposes)
     */
    public Reservation[] getAllReservations() {
        Reservation[] result = new Reservation[size];
        int index = 0;
        int current = front;

        for (int i = 0; i < size; i++) {
            result[index++] = queue[current];
            current = (current + 1) % maxSize;
        }

        return result;
    }

    /**
     * Get all reservations in queue as list (for JSP compatibility)
     */
    public List<Reservation> getAllReservationsList() {
        List<Reservation> result = new ArrayList<>();
        int current = front;

        for (int i = 0; i < size; i++) {
            result.add(queue[current]);
            current = (current + 1) % maxSize;
        }

        return result;
    }

    /**
     * Sort the queue by reservation time using custom merge sort
     */
    private void sortQueueByTime() {
        if (size <= 1) return;

        // Create array of current reservations
        Reservation[] reservations = getAllReservations();

        // Sort using custom merge sort
        mergeSort.sort(reservations, 0, reservations.length - 1);

        // Clear current queue and re-add sorted reservations
        clearQueue();
        for (Reservation reservation : reservations) {
            addToQueueWithoutSort(reservation);
        }
    }

    /**
     * Add reservation without triggering sort (used internally)
     */
    private void addToQueueWithoutSort(Reservation reservation) {
        rear = (rear + 1) % maxSize;
        queue[rear] = reservation;
        size++;
    }

    /**
     * Clear the queue
     */
    private void clearQueue() {
        for (int i = 0; i < maxSize; i++) {
            queue[i] = null;
        }
        front = 0;
        rear = -1;
        size = 0;
    }

    /**
     * Remove specific reservation by ID
     */
    public boolean removeReservationById(int reservationId) {
        int current = front;
        boolean found = false;

        for (int i = 0; i < size; i++) {
            if (queue[current].getId() == reservationId) {
                // Found the reservation, remove it
                found = true;

                // Shift all elements after this position
                int shiftIndex = current;
                for (int j = i; j < size - 1; j++) {
                    int nextIndex = (shiftIndex + 1) % maxSize;
                    queue[shiftIndex] = queue[nextIndex];
                    shiftIndex = nextIndex;
                }

                // Clear the last position and adjust rear
                queue[rear] = null;
                rear = (rear - 1 + maxSize) % maxSize;
                size--;

                saveQueueToFile();
                break;
            }
            current = (current + 1) % maxSize;
        }

        return found;
    }

    /**
     * Generate next unique ID
     */
    private int generateNextId() {
        int maxId = 0;
        int current = front;

        for (int i = 0; i < size; i++) {
            if (queue[current] != null && queue[current].getId() > maxId) {
                maxId = queue[current].getId();
            }
            current = (current + 1) % maxSize;
        }

        return maxId + 1;
    }

    /**
     * Save queue to file
     */
    private void saveQueueToFile() {
        try {
            List<Reservation> reservationsList = getAllReservationsList();
            String json = gson.toJson(reservationsList);
            QueueFileUtils.writeQueueFile(json);
            System.out.println("Queue saved to file successfully.");
        } catch (Exception e) {
            System.err.println("Error saving queue to file: " + e.getMessage());
        }
    }

    /**
     * Load queue from file
     */
    private void loadQueueFromFile() {
        try {
            String json = QueueFileUtils.readQueueFile();
            if (json != null && !json.trim().equals("[]")) {
                Type listType = new TypeToken<List<Reservation>>() {}.getType();
                List<Reservation> reservationsList = gson.fromJson(json, listType);

                if (reservationsList != null) {
                    clearQueue();
                    for (Reservation reservation : reservationsList) {
                        if (reservation != null) {
                            addToQueueWithoutSort(reservation);
                        }
                    }
                    sortQueueByTime(); // Sort after loading
                    System.out.println("Queue loaded from file. Size: " + size);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading queue from file: " + e.getMessage());
        }
    }

    /**
     * Display queue contents (for debugging)
     */
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        System.out.println("Queue contents (ordered by time):");
        Reservation[] reservations = getAllReservations();
        for (int i = 0; i < reservations.length; i++) {
            System.out.println((i + 1) + ". " + reservations[i]);
        }
    }

    /**
     * Get queue statistics
     */
    public String getQueueStatistics() {
        return String.format("Queue Status - Size: %d/%d, Empty: %s, Full: %s",
                size, maxSize, isEmpty(), isFull());
    }
}