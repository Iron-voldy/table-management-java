package com.example.restaurant_table_reservation.model;

public class Reservation {
    private int id;
    private int userId;
    private int tableId;
    private String reservationTime;

    public Reservation(int id, int userId, int tableId, String reservationTime) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
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

    // Setters (optional, depending on whether reservations will be edited)
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", userId=" + userId + ", tableId=" + tableId + ", time=" + reservationTime + "}";
    }
}