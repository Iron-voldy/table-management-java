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
        System.out.println("ReservationQueue initialized with maxSize: " + maxSize);
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
        System.out.println("ReservationQueue: Attempting to enqueue reservation: " + reservation);

        if (isFull()) {
            System.out.println("Queue is full! Cannot add more reservations.");
            return false;
        }

        // Assign a unique ID if not already set
        if (reservation.getId() == 0) {
            reservation.setId(generateNextId());
            System.out.println("ReservationQueue: Assigned new ID: " + reservation.getId());
        }

        rear = (rear + 1) % maxSize;
        queue[rear] = reservation;
        size++;

        System.out.println("ReservationQueue: Added reservation. New size: " + size);

        // Sort the queue by reservation time after adding
        sortQueueByTime();
        saveQueueToFile();

        System.out.println("Reservation added to queue successfully: " + reservation);
        return true;
    }

    /**
     * Remove a reservation from the queue (dequeue operation)
     */
    public Reservation dequeue() {
        System.out.println("ReservationQueue: Attempting to dequeue");

        if (isEmpty()) {
            System.out.println("Queue is empty! No reservations to remove.");
            return null;
        }

        Reservation reservation = queue[front];
        queue[front] = null;
        front = (front + 1) % maxSize;
        size--;

        System.out.println("ReservationQueue: Dequeued reservation. New size: " + size);

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
            if (queue[current] != null) {
                result[index++] = queue[current];
            }
            current = (current + 1) % maxSize;
        }

        // Trim array if needed
        if (index < result.length) {
            Reservation[] trimmed = new Reservation[index];
            System.arraycopy(result, 0, trimmed, 0, index);
            return trimmed;
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
            if (queue[current] != null) {
                result.add(queue[current]);
            }
            current = (current + 1) % maxSize;
        }

        System.out.println("ReservationQueue: Returning " + result.size() + " reservations as list");
        return result;
    }

    /**
     * Sort the queue by reservation time using custom merge sort
     */
    private void sortQueueByTime() {
        System.out.println("ReservationQueue: Sorting queue by time...");

        if (size <= 1) {
            System.out.println("ReservationQueue: No sorting needed, size: " + size);
            return;
        }

        // Create array of current reservations
        Reservation[] reservations = getAllReservations();

        if (reservations.length == 0) {
            System.out.println("ReservationQueue: No reservations to sort");
            return;
        }

        System.out.println("ReservationQueue: Sorting " + reservations.length + " reservations");

        // Use custom merge sort
        MergeSort.SortStatistics stats = mergeSort.sortWithStatistics(reservations);

        // Clear current queue and re-add sorted reservations
        clearQueue();
        for (Reservation reservation : reservations) {
            if (reservation != null) {
                addToQueueWithoutSort(reservation);
            }
        }

        System.out.println("ReservationQueue: Sorting completed. " + stats.toString());
    }

    /**
     * Add reservation without triggering sort (used internally)
     */
    private void addToQueueWithoutSort(Reservation reservation) {
        if (!isFull()) {
            rear = (rear + 1) % maxSize;
            queue[rear] = reservation;
            size++;
        }
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
        System.out.println("ReservationQueue: Removing reservation by ID: " + reservationId);

        int current = front;
        boolean found = false;

        // Find the reservation
        for (int i = 0; i < size; i++) {
            if (queue[current] != null && queue[current].getId() == reservationId) {
                found = true;
                break;
            }
            current = (current + 1) % maxSize;
        }

        if (!found) {
            System.out.println("ReservationQueue: Reservation with ID " + reservationId + " not found");
            return false;
        }

        // Create new array without the removed reservation
        List<Reservation> remainingReservations = new ArrayList<>();
        current = front;

        for (int i = 0; i < size; i++) {
            if (queue[current] != null && queue[current].getId() != reservationId) {
                remainingReservations.add(queue[current]);
            }
            current = (current + 1) % maxSize;
        }

        // Clear queue and re-add remaining reservations
        clearQueue();
        for (Reservation reservation : remainingReservations) {
            addToQueueWithoutSort(reservation);
        }

        saveQueueToFile();
        System.out.println("ReservationQueue: Successfully removed reservation with ID: " + reservationId);
        return true;
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
            System.out.println("ReservationQueue: Queue saved to file successfully. Size: " + reservationsList.size());
        } catch (Exception e) {
            System.err.println("Error saving queue to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load queue from file
     */
    private void loadQueueFromFile() {
        try {
            String json = QueueFileUtils.readQueueFile();
            System.out.println("ReservationQueue: Loading from file. JSON: " + json);

            if (json != null && !json.trim().equals("[]") && !json.trim().isEmpty()) {
                Type listType = new TypeToken<List<Reservation>>() {}.getType();
                List<Reservation> reservationsList = gson.fromJson(json, listType);

                if (reservationsList != null && !reservationsList.isEmpty()) {
                    clearQueue();
                    for (Reservation reservation : reservationsList) {
                        if (reservation != null) {
                            addToQueueWithoutSort(reservation);
                        }
                    }
                    // Sort after loading
                    sortQueueByTime();
                    System.out.println("ReservationQueue: Loaded " + size + " reservations from file");
                } else {
                    System.out.println("ReservationQueue: No valid reservations found in file");
                }
            } else {
                System.out.println("ReservationQueue: File is empty or contains empty array");
            }
        } catch (Exception e) {
            System.err.println("Error loading queue from file: " + e.getMessage());
            e.printStackTrace();
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

    /**
     * Force sort the queue (public method for admin use)
     */
    public void forceSortQueue() {
        System.out.println("ReservationQueue: Force sorting queue...");
        sortQueueByTime();
        saveQueueToFile();
    }

    /**
     * Get detailed queue information
     */
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== Reservation Queue Details ===\n");
        info.append("Current Size: ").append(size).append("/").append(maxSize).append("\n");
        info.append("Front Index: ").append(front).append("\n");
        info.append("Rear Index: ").append(rear).append("\n");
        info.append("Is Empty: ").append(isEmpty()).append("\n");
        info.append("Is Full: ").append(isFull()).append("\n");

        if (!isEmpty()) {
            Reservation[] reservations = getAllReservations();
            boolean sorted = mergeSort.isSorted(reservations);
            info.append("Is Sorted: ").append(sorted).append("\n");

            if (reservations.length > 0) {
                info.append("First Reservation Time: ").append(reservations[0].getReservationTime()).append("\n");
                if (reservations.length > 1) {
                    info.append("Last Reservation Time: ").append(reservations[reservations.length - 1].getReservationTime()).append("\n");
                }
            }
        }

        return info.toString();
    }
}