package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.Reservation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Merge Sort implementation for sorting reservations by time
 * Implements merge sort algorithm from scratch without using Java Collections
 */
public class MergeSort {
    private int comparisons = 0;

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
            comparisons++;
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

        String time1 = r1.getReservationTime();
        String time2 = r2.getReservationTime();

        if (time1 == null && time2 == null) return 0;
        if (time1 == null) return 1;
        if (time2 == null) return -1;

        try {
            LocalDateTime dateTime1 = parseReservationTime(time1);
            LocalDateTime dateTime2 = parseReservationTime(time2);

            int result = dateTime1.compareTo(dateTime2);
            System.out.println("Comparing: " + time1 + " vs " + time2 + " = " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error comparing reservation times: " + e.getMessage());
            // Fallback to string comparison
            return time1.compareTo(time2);
        }
    }

    /**
     * Parse reservation time string to LocalDateTime
     * @param timeString Time string in various formats
     * @return LocalDateTime object
     */
    private LocalDateTime parseReservationTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            // Clean the time string
            timeString = timeString.trim();

            // Handle ISO format with T separator (yyyy-MM-ddTHH:mm or yyyy-MM-ddTHH:mm:ss)
            if (timeString.contains("T")) {
                if (timeString.length() == 16) { // yyyy-MM-ddTHH:mm
                    return LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                } else if (timeString.length() == 19) { // yyyy-MM-ddTHH:mm:ss
                    return LocalDateTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    // Try standard ISO format
                    return LocalDateTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }

            // Try other common formats
            String[] formats = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "dd/MM/yyyy HH:mm",
                    "MM/dd/yyyy HH:mm",
                    "yyyy/MM/dd HH:mm",
                    "dd-MM-yyyy HH:mm",
                    "MM-dd-yyyy HH:mm"
            };

            for (String format : formats) {
                try {
                    return LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                } catch (Exception ignored) {
                    // Try next format
                }
            }

            // If all parsing attempts fail, use current time
            System.err.println("Could not parse time string: '" + timeString + "'. Using current time.");
            return LocalDateTime.now();

        } catch (Exception e) {
            System.err.println("Error parsing reservation time '" + timeString + "': " + e.getMessage());
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
        comparisons = 0; // Reset comparison counter

        System.out.println("Starting merge sort with " + (reservations != null ? reservations.length : 0) + " reservations");

        if (reservations != null && reservations.length > 0) {
            // Print before sorting
            System.out.println("Before sorting:");
            for (int i = 0; i < Math.min(reservations.length, 5); i++) {
                if (reservations[i] != null) {
                    System.out.println("  [" + i + "] " + reservations[i].getReservationTime());
                }
            }

            sort(reservations, 0, reservations.length - 1);

            // Print after sorting
            System.out.println("After sorting:");
            for (int i = 0; i < Math.min(reservations.length, 5); i++) {
                if (reservations[i] != null) {
                    System.out.println("  [" + i + "] " + reservations[i].getReservationTime());
                }
            }
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        SortStatistics stats = new SortStatistics(
                reservations != null ? reservations.length : 0,
                comparisons,
                duration / 1_000_000.0 // Convert to milliseconds
        );

        System.out.println("Sorting completed: " + stats.toString());
        return stats;
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
            if (reservations[i] != null && reservations[i + 1] != null) {
                if (compareReservationTimes(reservations[i], reservations[i + 1]) > 0) {
                    System.out.println("Array is NOT sorted at index " + i + ": " +
                            reservations[i].getReservationTime() + " > " + reservations[i + 1].getReservationTime());
                    return false;
                }
            }
        }
        System.out.println("Array is properly sorted");
        return true;
    }

    /**
     * Get detailed sorting analysis
     * @param reservations Array to analyze
     * @return Analysis string
     */
    public String analyzeSorting(Reservation[] reservations) {
        if (reservations == null || reservations.length == 0) {
            return "No reservations to analyze";
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("=== Merge Sort Analysis ===\n");
        analysis.append("Total Reservations: ").append(reservations.length).append("\n");
        analysis.append("Is Sorted: ").append(isSorted(reservations)).append("\n");

        if (reservations.length > 1) {
            // Create a copy for analysis to avoid modifying original
            Reservation[] copy = new Reservation[reservations.length];
            System.arraycopy(reservations, 0, copy, 0, reservations.length);

            SortStatistics stats = sortWithStatistics(copy);
            analysis.append("Sort Performance: ").append(stats.toString()).append("\n");
        }

        analysis.append("Time Range: ");
        if (reservations.length > 0) {
            Reservation first = reservations[0];
            Reservation last = reservations[reservations.length - 1];
            if (first != null && last != null) {
                analysis.append(first.getFormattedReservationTime())
                        .append(" to ")
                        .append(last.getFormattedReservationTime());
            }
        }

        return analysis.toString();
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

        public String getDetailedString() {
            return String.format(
                    "Merge Sort Results:\n" +
                            "- Elements Sorted: %d\n" +
                            "- Comparisons Made: %d\n" +
                            "- Time Taken: %.2f ms\n" +
                            "- Time Complexity: O(n log n)\n" +
                            "- Space Complexity: O(n)",
                    elementsCount, comparisons, durationMs
            );
        }
    }

    /**
     * Demo method to show sorting functionality with sample data
     */
    public static void demonstrateSorting() {
        System.out.println("=== Merge Sort Demonstration ===");

        // Create sample reservations with different times
        Reservation[] testReservations = {
                createSampleReservation(1, "2025-05-26T18:00"),
                createSampleReservation(2, "2025-05-26T12:00"),
                createSampleReservation(3, "2025-05-26T15:30"),
                createSampleReservation(4, "2025-05-26T09:00"),
                createSampleReservation(5, "2025-05-26T20:00")
        };

        MergeSort sorter = new MergeSort();

        System.out.println("Before sorting:");
        for (int i = 0; i < testReservations.length; i++) {
            System.out.println((i + 1) + ". " + testReservations[i].toShortString());
        }

        SortStatistics stats = sorter.sortWithStatistics(testReservations);

        System.out.println("\nAfter sorting:");
        for (int i = 0; i < testReservations.length; i++) {
            System.out.println((i + 1) + ". " + testReservations[i].toShortString());
        }

        System.out.println("\n" + stats.getDetailedString());
        System.out.println("Is sorted: " + sorter.isSorted(testReservations));
    }

    private static Reservation createSampleReservation(int id, String time) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setUserId(100 + id);
        reservation.setTableId(id);
        reservation.setReservationTime(time);
        reservation.setCustomerName("Customer " + id);
        reservation.setStatus("pending");
        return reservation;
    }
}