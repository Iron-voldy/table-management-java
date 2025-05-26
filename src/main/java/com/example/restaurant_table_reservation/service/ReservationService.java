package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private static ReservationService instance = null;
    private List<Reservation> reservationList;
    private Gson gson;

    private ReservationService() {
        gson = new Gson();
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
        String json = ReservationFileUtils.readReservationsFile();
        Type listType = new TypeToken<List<Reservation>>(){}.getType();
        reservationList = gson.fromJson(json, listType);
        if (reservationList == null) {
            reservationList = new ArrayList<>();
        }
        System.out.println("ReservationService: Loaded " + reservationList.size() + " reservations.");
    }

    private void saveReservations() {
        System.out.println("ReservationService: Saving reservations...");
        ReservationFileUtils.writeReservationsFile(gson.toJson(reservationList));
        System.out.println("ReservationService: Finished saving reservations.");
    }

    public List<Reservation> getAllReservations() {
        // We might not need to load every time, but for robustness with file storage, keeping it for now.
        loadReservations(); 
        System.out.println("ReservationService: Returning all reservations (count: " + reservationList.size() + ").");
        return reservationList;
    }

    public void addReservation(Reservation reservation) {
        System.out.println("ReservationService: Attempting to add reservation: " + reservation);
        // Assign a new ID
        int newId = reservationList.isEmpty() ? 1 : reservationList.get(reservationList.size() - 1).getId() + 1;
        reservation.setId(newId);

        reservationList.add(reservation);
        System.out.println("ReservationService: Added reservation to list. New size: " + reservationList.size());
        saveReservations();
        System.out.println("ReservationService: Successfully added and saved reservation with ID: " + newId);
    }

    // Optional: Methods to get reservations by user ID or table ID if needed
} 