package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.Reservation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom Merge Sort implementation for sorting reservations by time
 * Implements merge sort algorithm from scratch without using Java Collections
 */
public class MergeSort {

    /**
     * Main sort method - sorts reservations by reservation time
     * @param reservations Array of reservations to sort
     * @param left Starting index
     * @param right Ending index
     */
    public void sort(Reservation[] reservations, int left, int right) {
        if (left < right) {
            // Find the middle point
            int middle = left + (right - left) / 2;

            // Sort first and second halves
            sort(reservations, left, middle);
            sort(reservations, middle + 1, right);

            // Merge the sorted halves
            merge(reservations, left, middle, right);
        }
    }

    /**
     * Merge two sorted subarrays
     * @param reservations Main array
     * @param left Starting index of left subarray
     * @param middle Ending index of left subarray
     * @param right Ending index of right subarray
     */
    private void merge(Reservation[] reservations, int left, int middle, int right) {
        // Calculate sizes of the two subarrays
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        // Create temporary arrays
        Reservation[] leftArray = new Reservation[leftSize];
        Reservation[] rightArray = new Reservation[rightSize];

        // Copy data to temporary arrays
        for (int i = 0; i < leftSize; i++) {
            leftArray[i] = reservations[left + i];
        }
        for (int j = 0; j < rightSize; j++) {
            rightArray[j] = reservations[middle + 1 + j];
        }

        // Merge the temporary arrays back into the main array
        int i = 0; // Initial index of first subarray
        int j = 0; // Initial index of second subarray
        int k = left; // Initial index of merged subarray

        while (i < leftSize && j < rightSize) {
            if (compareReservationTimes(leftArray[i], rightArray[j]) <= 0) {
                reservations[k] = leftArray[i];
                i++;
            } else {
                reservations[k] = rightArray[j];
                j++;
            }
            k++;
        }

        // Copy the remaining elements of leftArray[], if any
        while (i < leftSize) {
            reservations[k] = leftArray[i];
            i++;
            k++;
        }

        // Copy the remaining elements of rightArray[], if any
        while (j < rightSize) {
            reservations[k] = rightArray[j];
            j++;
            k++;
        }
    }

    /**
     * Compare two reservations by their reservation time
     * @param r1 First reservation
     * @param r2 Second reservation
     * @return Negative if r1 < r2, Zero if r1 == r2, Positive if r1 > r2
     */
    private int compareReservationTimes(Reservation r1, Reservation r2) {
        if (r1 == null && r2 == null) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;

        try {
            LocalDateTime time1 = parseReservationTime(r1.getReservationTime());
            LocalDateTime time2 = parseReservationTime(r2.getReservationTime());

            return time1.compareTo(time2);
        } catch (Exception e) {
            System.err.println("Error comparing reservation times: " + e.getMessage());
            // Fallback to string comparison
            return r1.getReservationTime().compareTo(r2.getReservationTime());
        }
    }

    /**
     * Parse reservation time string to LocalDateTime
     * @param timeString Time string in various formats
     * @return LocalDateTime object
     */
    private LocalDateTime parseReservationTime(String timeString) {
        try {
            // Try ISO format first (yyyy-MM-ddTHH:mm:ss)
            if (timeString.contains("T")) {
                return LocalDateTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // Try other common formats
            String[] formats = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "dd/MM/yyyy HH:mm",
                    "MM/dd/yyyy HH:mm"
            };

            for (String format : formats) {
                try {
                    return LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                } catch (Exception ignored) {
                    // Try next format
                }
            }

            // If all parsing attempts fail, use current time
            System.err.println("Could not parse time string: " + timeString + ". Using current time.");
            return LocalDateTime.now();

        } catch (Exception e) {
            System.err.println("Error parsing reservation time: " + e.getMessage());
            return LocalDateTime.now();
        }
    }

    /**
     * Sort reservations and return statistics
     * @param reservations Array to sort
     * @return Statistics about the sorting operation
     */
    public SortStatistics sortWithStatistics(Reservation[] reservations) {
        long startTime = System.nanoTime();
        int comparisons = 0;

        if (reservations != null && reservations.length > 0) {
            sort(reservations, 0, reservations.length - 1);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        return new SortStatistics(
                reservations != null ? reservations.length : 0,
                comparisons,
                duration / 1_000_000.0 // Convert to milliseconds
        );
    }

    /**
     * Check if array is sorted by reservation time
     * @param reservations Array to check
     * @return true if sorted, false otherwise
     */
    public boolean isSorted(Reservation[] reservations) {
        if (reservations == null || reservations.length <= 1) {
            return true;
        }

        for (int i = 0; i < reservations.length - 1; i++) {
            if (compareReservationTimes(reservations[i], reservations[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inner class to hold sorting statistics
     */
    public static class SortStatistics {
        private final int elementsCount;
        private final int comparisons;
        private final double durationMs;

        public SortStatistics(int elementsCount, int comparisons, double durationMs) {
            this.elementsCount = elementsCount;
            this.comparisons = comparisons;
            this.durationMs = durationMs;
        }

        public int getElementsCount() { return elementsCount; }
        public int getComparisons() { return comparisons; }
        public double getDurationMs() { return durationMs; }

        @Override
        public String toString() {
            return String.format("Sort Statistics - Elements: %d, Comparisons: %d, Time: %.2f ms",
                    elementsCount, comparisons, durationMs);
        }
    }

    /**
     * Demo method to show sorting functionality
     */
    public static void demonstrateSorting() {
        System.out.println("=== Merge Sort Demonstration ===");

        // Create sample reservations
        Reservation[] testReservations = {
                new Reservation(1, 101, 1, "2025-05-26T18:00:00"),
                new Reservation(2, 102, 2, "2025-05-26T12:00:00"),
                new Reservation(3, 103, 3, "2025-05-26T15:30:00"),
                new Reservation(4, 104, 1, "2025-05-26T09:00:00"),
                new Reservation(5, 105, 3, "2025-05-26T20:00:00")
        };

        MergeSort sorter = new MergeSort();

        System.out.println("Before sorting:");
        for (int i = 0; i < testReservations.length; i++) {
            System.out.println((i + 1) + ". " + testReservations[i]);
        }

        SortStatistics stats = sorter.sortWithStatistics(testReservations);

        System.out.println("\nAfter sorting:");
        for (int i = 0; i < testReservations.length; i++) {
            System.out.println((i + 1) + ". " + testReservations[i]);
        }

        System.out.println("\n" + stats);
        System.out.println("Is sorted: " + sorter.isSorted(testReservations));
    }
}