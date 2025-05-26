package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;
import com.example.restaurant_table_reservation.utils.MergeSort;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private static ReservationService instance = null;
    private List<Reservation> reservationList;
    private Gson gson;
    private MergeSort mergeSort;

    private ReservationService() {
        gson = new Gson();
        mergeSort = new MergeSort();
        loadReservations();
    }

    public static synchronized ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }

    private void loadReservations() {
        System.out.println("ReservationService: Loading reservations...");
        try {
            String json = ReservationFileUtils.readReservationsFile();
            System.out.println("ReservationService: JSON content: " + json);

            Type listType = new TypeToken<List<Reservation>>(){}.getType();
            reservationList = gson.fromJson(json, listType);

            if (reservationList == null) {
                reservationList = new ArrayList<>();
                System.out.println("ReservationService: Created new empty list");
            }
            System.out.println("ReservationService: Loaded " + reservationList.size() + " reservations.");
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
            reservationList = new ArrayList<>();
        }
    }

    private void saveReservations() {
        System.out.println("ReservationService: Saving reservations...");
        try {
            String json = gson.toJson(reservationList);
            System.out.println("ReservationService: Saving JSON: " + json);
            ReservationFileUtils.writeReservationsFile(json);
            System.out.println("ReservationService: Successfully saved " + reservationList.size() + " reservations.");
        } catch (Exception e) {
            System.err.println("Error saving reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Reservation> getAllReservations() {
        loadReservations(); // Reload to get latest data
        System.out.println("ReservationService: Returning all reservations (count: " + reservationList.size() + ").");
        return new ArrayList<>(reservationList); // Return copy to prevent external modification
    }

    public void addReservation(Reservation reservation) {
        System.out.println("ReservationService: Adding reservation: " + reservation);
        try {
            // Assign a new ID if not set
            if (reservation.getId() == 0) {
                int newId = getNextId();
                reservation.setId(newId);
                System.out.println("ReservationService: Assigned ID: " + newId);
            }

            // Add to list
            reservationList.add(reservation);
            System.out.println("ReservationService: Added to list. New size: " + reservationList.size());

            // Sort reservations by time using merge sort
            sortReservationsByTime();

            // Save to file
            saveReservations();
            System.out.println("ReservationService: Successfully added reservation with ID: " + reservation.getId());
        } catch (Exception e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateReservation(Reservation updatedReservation) {
        System.out.println("ReservationService: Updating reservation with ID: " + updatedReservation.getId());
        try {
            for (int i = 0; i < reservationList.size(); i++) {
                if (reservationList.get(i).getId() == updatedReservation.getId()) {
                    reservationList.set(i, updatedReservation);
                    sortReservationsByTime(); // Re-sort after update
                    saveReservations();
                    System.out.println("ReservationService: Successfully updated reservation with ID: " + updatedReservation.getId());
                    return;
                }
            }
            System.out.println("ReservationService: Reservation with ID " + updatedReservation.getId() + " not found for update");
        } catch (Exception e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteReservation(int reservationId) {
        System.out.println("ReservationService: Attempting to delete reservation with ID: " + reservationId);
        try {
            boolean removed = reservationList.removeIf(r -> r.getId() == reservationId);
            if (removed) {
                saveReservations();
                System.out.println("ReservationService: Successfully deleted reservation with ID: " + reservationId);
                return true;
            } else {
                System.out.println("ReservationService: Reservation with ID " + reservationId + " not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Reservation getReservationById(int id) {
        for (Reservation reservation : reservationList) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }
        return null;
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> userReservations = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if (reservation.getUserId() == userId) {
                userReservations.add(reservation);
            }
        }
        return userReservations;
    }

    public List<Reservation> getReservationsByTableId(int tableId) {
        List<Reservation> tableReservations = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if (reservation.getTableId() == tableId) {
                tableReservations.add(reservation);
            }
        }
        return tableReservations;
    }

    /**
     * Sort reservations by time using custom merge sort algorithm
     */
    public void sortReservationsByTime() {
        System.out.println("ReservationService: Sorting reservations by time using merge sort...");
        try {
            if (reservationList.size() <= 1) {
                System.out.println("ReservationService: No sorting needed, list size: " + reservationList.size());
                return;
            }

            // Convert list to array for sorting
            Reservation[] reservationArray = reservationList.toArray(new Reservation[0]);

            System.out.println("Before sorting:");
            for (int i = 0; i < Math.min(reservationArray.length, 5); i++) {
                System.out.println("  " + reservationArray[i].getReservationTime());
            }

            // Use custom merge sort
            MergeSort.SortStatistics stats = mergeSort.sortWithStatistics(reservationArray);

            // Convert back to list
            reservationList.clear();
            for (Reservation reservation : reservationArray) {
                if (reservation != null) {
                    reservationList.add(reservation);
                }
            }

            System.out.println("After sorting:");
            for (int i = 0; i < Math.min(reservationList.size(), 5); i++) {
                System.out.println("  " + reservationList.get(i).getReservationTime());
            }

            System.out.println("ReservationService: Sorting completed. " + stats.toString());
        } catch (Exception e) {
            System.err.println("Error sorting reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get sorted reservations by time (uses merge sort)
     */
    public List<Reservation> getSortedReservations() {
        System.out.println("ReservationService: Getting sorted reservations...");
        loadReservations();
        sortReservationsByTime();
        return new ArrayList<>(reservationList);
    }

    /**
     * Check if reservations are sorted by time
     */
    public boolean isSorted() {
        if (reservationList.size() <= 1) {
            return true;
        }

        Reservation[] array = reservationList.toArray(new Reservation[0]);
        return mergeSort.isSorted(array);
    }

    /**
     * Get merge sort statistics for the last sort operation
     */
    public String getSortStatistics() {
        if (reservationList.isEmpty()) {
            return "No reservations to sort";
        }

        Reservation[] array = reservationList.toArray(new Reservation[0]);
        MergeSort.SortStatistics stats = mergeSort.sortWithStatistics(array);
        return stats.toString();
    }

    private int getNextId() {
        int maxId = 0;
        for (Reservation reservation : reservationList) {
            if (reservation.getId() > maxId) {
                maxId = reservation.getId();
            }
        }
        return maxId + 1;
    }

    /**
     * Demonstrate merge sort functionality
     */
    public void demonstrateMergeSort() {
        System.out.println("=== Reservation Merge Sort Demonstration ===");

        if (reservationList.isEmpty()) {
            System.out.println("No reservations available for demonstration");
            return;
        }

        Reservation[] array = reservationList.toArray(new Reservation[0]);

        System.out.println("Before sorting:");
        for (int i = 0; i < Math.min(array.length, 5); i++) {
            System.out.println((i + 1) + ". " + array[i].toShortString());
        }

        MergeSort.SortStatistics stats = mergeSort.sortWithStatistics(array);

        System.out.println("\nAfter sorting:");
        for (int i = 0; i < Math.min(array.length, 5); i++) {
            System.out.println((i + 1) + ". " + array[i].toShortString());
        }

        System.out.println("\n" + stats);
        System.out.println("Is sorted: " + mergeSort.isSorted(array));
    }
}