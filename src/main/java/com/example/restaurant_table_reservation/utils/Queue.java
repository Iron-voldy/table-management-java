package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Queue {
    private static Queue instance = null;
    private int maxSize;
    private Reservation[] queArray;
    private int front;
    private int rear;
    private int nItems;
    private Gson gson;

    private Queue(int s) {
        maxSize = s;
        queArray = new Reservation[maxSize];
        front = 0;
        rear = -1;
        nItems = 0;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        // Note: Queue persistence might need a separate file if it's not meant
        // to share the main reservations.json with ReservationService.
        // For now, we'll align the format.
        loadFromFile();
    }

    public static synchronized Queue getInstance(int size) {
        if (instance == null) {
            instance = new Queue(size);
        }
        return instance;
    }

    public void insert(Reservation reservation) {
        if (rear == maxSize - 1) {
            System.out.println("Queue is full");
            return;
        }
        queArray[++rear] = reservation;
        nItems++;
        mergeSort(queArray, front, rear);
        saveToFile();
    }

    public Reservation remove() {
        if (nItems == 0) {
            System.out.println("Queue is empty");
            return null;
        }
        Reservation value = queArray[front];
        queArray[front] = null;
        front++;
        nItems--;
        saveToFile();
        return value;
    }

    public Reservation peekFront() {
        if (nItems == 0) {
            System.out.println("Queue is empty");
            return null;
        }
        return queArray[front];
    }

    public void display() {
        if (nItems == 0) {
            System.out.println("Queue is empty");
        } else {
            for (int i = front; i <= rear; i++) {
                if (queArray[i] != null) {
                    System.out.println(queArray[i]);
                }
            }
        }
    }

    private void saveToFile() {
        List<Reservation> nonNullReservationsList = new ArrayList<>();
        for (int i = front; i <= rear; i++) {
            if (queArray[i] != null) {
                nonNullReservationsList.add(queArray[i]);
            }
        }
        System.out.println("Saving " + nonNullReservationsList.size() + " reservations to file.");
        String json = gson.toJson(nonNullReservationsList);
        ReservationFileUtils.writeReservationsFile(json);
    }

    private void loadFromFile() {
        String json = ReservationFileUtils.readReservationsFile();
        Type listType = new TypeToken<List<Reservation>>() {}.getType();
        List<Reservation> reservationsList = gson.fromJson(json, listType);
        System.out.println("Loaded reservations from file: " + (reservationsList != null ? reservationsList.size() : 0) + " entries");
        if (reservationsList != null) {
            queArray = reservationsList.toArray(new Reservation[maxSize]);
            nItems = reservationsList.size();
            rear = nItems - 1;
            front = 0;
            if (nItems > 0) {
                 // Need to handle potential nulls if loaded data is less than maxSize
                 // and sort correctly based on time.
                 // For simplicity, re-inserting might be safer or resizing array.
                 // Let's assume data fits and resort.
                mergeSort(queArray, front, rear);
            }
        }
    }

    private void mergeSort(Reservation[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(Reservation[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Reservation[] leftArray = new Reservation[n1];
        Reservation[] rightArray = new Reservation[n2];
        for (int i = 0; i < n1; i++) {
            leftArray[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j] = arr[mid + 1 + j];
        }
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i] == null || rightArray[j] == null) {
                if (leftArray[i] == null) {
                    arr[k++] = rightArray[j++];
                } else {
                    arr[k++] = leftArray[i++];
                }
            } else if (leftArray[i].getReservationTime().compareTo(rightArray[j].getReservationTime()) <= 0) {
                arr[k++] = leftArray[i++];
            } else {
                arr[k++] = rightArray[j++];
            }
        }
        while (i < n1) {
            arr[k++] = leftArray[i++];
        }
        while (j < n2) {
            arr[k++] = rightArray[j++];
        }
    }

    // Getter for queArray (for AdminServlet to access)
    // This getter still returns an array, which is fine if the AdminDashboardServlet
    // is updated to handle it, or if we convert it to a List here.
    // Given the previous error, let's return a List to be consistent with the JSP expectation.
    public List<Reservation> getReservations() {
        List<Reservation> reservationsList = new ArrayList<>();
        for (int i = front; i <= rear; i++) {
            if (queArray[i] != null) {
                reservationsList.add(queArray[i]);
            }
        }
        return reservationsList;
    }

    public static void main(String[] args) {
        ReservationFileUtils.initialize(null); // Use ReservationFileUtils
        Queue theQueue = Queue.getInstance(5);
        theQueue.insert(new Reservation(1, 101, 1, "2025-05-26T14:00:00"));
        theQueue.insert(new Reservation(2, 102, 2, "2025-05-26T12:00:00"));
        theQueue.insert(new Reservation(3, 103, 3, "2025-05-26T15:00:00"));
        System.out.println("Queue Elements (sorted by time):");
        theQueue.display();
    }
}