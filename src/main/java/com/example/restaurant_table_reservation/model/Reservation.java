package com.example.restaurant_table_reservation.model;

/**
 * Complete Reservation model class for restaurant table reservation system
 * Updated to support the custom Queue implementation and merge sort functionality
 */
public class Reservation {
    private int id;
    private int userId;
    private int tableId;
    private String reservationTime;
    private String status; // pending, confirmed, cancelled
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String specialRequests;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Reservation() {
        this.status = "pending";
        this.createdAt = java.time.LocalDateTime.now().toString();
        this.updatedAt = java.time.LocalDateTime.now().toString();
    }

    // Original constructor for backward compatibility
    public Reservation(int id, int userId, int tableId, String reservationTime) {
        this();
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
    }

    // Extended constructor with basic fields
    public Reservation(int id, int userId, int tableId, String reservationTime, String customerName) {
        this(id, userId, tableId, reservationTime);
        this.customerName = customerName;
    }

    // Complete constructor with all fields
    public Reservation(int id, int userId, int tableId, String reservationTime, String status,
                       String customerName, String customerPhone, String customerEmail, String specialRequests) {
        this();
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
        this.status = status;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.specialRequests = specialRequests;
    }

    // Constructor for queue operations (commonly used)
    public Reservation(int userId, int tableId, String reservationTime, String customerName, String customerPhone) {
        this();
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getTableId() {
        return tableId;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
        updateTimestamp();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        updateTimestamp();
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
        updateTimestamp();
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
        updateTimestamp();
    }

    public void setStatus(String status) {
        this.status = status;
        updateTimestamp();
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
        updateTimestamp();
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        updateTimestamp();
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        updateTimestamp();
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
        updateTimestamp();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods for status management
    public boolean isPending() {
        return "pending".equalsIgnoreCase(this.status);
    }

    public boolean isConfirmed() {
        return "confirmed".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(this.status);
    }

    public void confirm() {
        this.status = "confirmed";
        updateTimestamp();
    }

    public void cancel() {
        this.status = "cancelled";
        updateTimestamp();
    }

    public void setPending() {
        this.status = "pending";
        updateTimestamp();
    }

    // Helper method to update timestamp
    private void updateTimestamp() {
        this.updatedAt = java.time.LocalDateTime.now().toString();
    }

    // Validation methods
    public boolean isValid() {
        return userId > 0 && tableId > 0 &&
                reservationTime != null && !reservationTime.trim().isEmpty() &&
                customerName != null && !customerName.trim().isEmpty();
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (userId <= 0) {
            errors.append("User ID must be positive. ");
        }
        if (tableId <= 0) {
            errors.append("Table ID must be positive. ");
        }
        if (reservationTime == null || reservationTime.trim().isEmpty()) {
            errors.append("Reservation time is required. ");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            errors.append("Customer name is required. ");
        }

        return errors.toString().trim();
    }

    // Comparison method for sorting (used by merge sort)
    public int compareByTime(Reservation other) {
        if (other == null) return -1;
        if (this.reservationTime == null && other.reservationTime == null) return 0;
        if (this.reservationTime == null) return 1;
        if (other.reservationTime == null) return -1;

        try {
            // Parse and compare as LocalDateTime
            java.time.LocalDateTime thisTime = java.time.LocalDateTime.parse(
                    this.reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            java.time.LocalDateTime otherTime = java.time.LocalDateTime.parse(
                    other.reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return thisTime.compareTo(otherTime);
        } catch (Exception e) {
            // Fallback to string comparison
            return this.reservationTime.compareTo(other.reservationTime);
        }
    }

    // Formatted time display
    public String getFormattedReservationTime() {
        if (reservationTime == null) return "N/A";

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                    reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        } catch (Exception e) {
            return reservationTime; // Return original if parsing fails
        }
    }

    // Get date only
    public String getReservationDate() {
        if (reservationTime == null) return "N/A";

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                    reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return reservationTime.split("T")[0]; // Basic fallback
        }
    }

    // Get time only
    public String getReservationTimeOnly() {
        if (reservationTime == null) return "N/A";

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                    reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return reservationTime.contains("T") ? reservationTime.split("T")[1] : reservationTime;
        }
    }

    // Check if reservation is in the future
    public boolean isFutureReservation() {
        if (reservationTime == null) return false;

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                    reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.isAfter(java.time.LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }

    // Check if reservation is today
    public boolean isTodayReservation() {
        if (reservationTime == null) return false;

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                    reservationTime, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            java.time.LocalDate reservationDate = dateTime.toLocalDate();
            java.time.LocalDate today = java.time.LocalDate.now();
            return reservationDate.equals(today);
        } catch (Exception e) {
            return false;
        }
    }

    // Get status with icon for display
    public String getStatusWithIcon() {
        switch (status.toLowerCase()) {
            case "pending":
                return "⏳ Pending";
            case "confirmed":
                return "✅ Confirmed";
            case "cancelled":
                return "❌ Cancelled";
            default:
                return "❓ " + status;
        }
    }

    // Clone method for queue operations
    public Reservation clone() {
        Reservation cloned = new Reservation();
        cloned.id = this.id;
        cloned.userId = this.userId;
        cloned.tableId = this.tableId;
        cloned.reservationTime = this.reservationTime;
        cloned.status = this.status;
        cloned.customerName = this.customerName;
        cloned.customerPhone = this.customerPhone;
        cloned.customerEmail = this.customerEmail;
        cloned.specialRequests = this.specialRequests;
        cloned.createdAt = this.createdAt;
        cloned.updatedAt = this.updatedAt;
        return cloned;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", tableId=" + tableId +
                ", reservationTime='" + reservationTime + '\'' +
                ", status='" + status + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", specialRequests='" + specialRequests + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    // Compact toString for logging
    public String toShortString() {
        return String.format("Reservation[#%d: %s - Table %d at %s (%s)]",
                id, customerName, tableId, getFormattedReservationTime(), status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation that = (Reservation) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Static factory methods for common use cases
    public static Reservation createPendingReservation(int userId, int tableId, String reservationTime,
                                                       String customerName, String customerPhone) {
        return new Reservation(userId, tableId, reservationTime, customerName, customerPhone);
    }

    public static Reservation createQuickReservation(String customerName, int tableId, String reservationTime) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setTableId(tableId);
        reservation.setReservationTime(reservationTime);
        reservation.setUserId(0); // Guest reservation
        return reservation;
    }

    // Method to convert to JSON-like string (for debugging)
    public String toJsonString() {
        return String.format(
                "{\"id\":%d,\"userId\":%d,\"tableId\":%d,\"reservationTime\":\"%s\"," +
                        "\"status\":\"%s\",\"customerName\":\"%s\",\"customerPhone\":\"%s\"," +
                        "\"customerEmail\":\"%s\",\"specialRequests\":\"%s\"}",
                id, userId, tableId, reservationTime, status,
                customerName != null ? customerName : "",
                customerPhone != null ? customerPhone : "",
                customerEmail != null ? customerEmail : "",
                specialRequests != null ? specialRequests : ""
        );
    }
}