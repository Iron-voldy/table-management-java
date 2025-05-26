package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletContext;

public class ReservationFileUtils {
    private static final String RESERVATIONS_FILE_NAME = "reservations.json";
    private static String reservationsFilePath;
    private static boolean initialized = false;

    public static void initialize(ServletContext context) {
        if (initialized) {
            System.out.println("ReservationFileUtils already initialized, skipping.");
            return;
        }

        System.out.println("Initializing ReservationFileUtils...");

        if (context == null) {
            System.err.println("ServletContext is null, cannot initialize file path. Falling back to default path.");
            reservationsFilePath = "WEB-INF/data/" + RESERVATIONS_FILE_NAME;
             System.out.println("Using default reservations file path: " + reservationsFilePath);
        } else {
            String realPath = context.getRealPath("/WEB-INF/data/");
            if (realPath == null) {
                System.err.println("Unable to resolve real path for /WEB-INF/data/. Check deployment settings.");
                // Fallback to a relative path within the likely webapp structure if realPath is null
                 reservationsFilePath = "WEB-INF/data/" + RESERVATIONS_FILE_NAME;
                 System.err.println("Falling back to relative path: " + reservationsFilePath);
            } else {
                File directory = new File(realPath);
                if (!directory.exists()) {
                    System.out.println("Creating directory: " + realPath);
                    if (!directory.mkdirs()) {
                        System.err.println("Failed to create directory: " + realPath);
                        // Fallback to relative path if directory creation fails
                         reservationsFilePath = "WEB-INF/data/" + RESERVATIONS_FILE_NAME;
                         System.err.println("Falling back to relative path after mkdirs failure: " + reservationsFilePath);
                    } else {
                        System.out.println("Directory created successfully: " + realPath);
                        reservationsFilePath = realPath + File.separator + RESERVATIONS_FILE_NAME;
                         System.out.println("Resolved reservations file path: " + reservationsFilePath);
                    }
                } else {
                    reservationsFilePath = realPath + File.separator + RESERVATIONS_FILE_NAME;
                     System.out.println("Resolved reservations file path: " + reservationsFilePath);
                }
            }
        }

        // Ensure file exists
        try {
            File reservationsFile = new File(reservationsFilePath);
            if (!reservationsFile.exists()) {
                System.out.println(RESERVATIONS_FILE_NAME + " does not exist at: " + reservationsFile.getAbsolutePath() + ". Attempting to create it.");
                if (reservationsFile.createNewFile()) {
                    writeReservationsFile("[]");
                    System.out.println("Created and initialized " + RESERVATIONS_FILE_NAME + " at: " + reservationsFilePath);
                } else {
                    System.err.println("Failed to create " + RESERVATIONS_FILE_NAME + " at: " + reservationsFilePath + ". Check permissions.");
                }
            } else {
                 System.out.println(RESERVATIONS_FILE_NAME + " already exists at: " + reservationsFilePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize " + RESERVATIONS_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
        }
        initialized = true;
        System.out.println("ReservationFileUtils initialization finished.");
    }

    public static String readReservationsFile() {
         System.out.println("Attempting to read from " + reservationsFilePath);
        try {
            File file = new File(reservationsFilePath);
            if (!file.exists()) {
                System.out.println(RESERVATIONS_FILE_NAME + " does not exist at: " + file.getAbsolutePath() + ". Returning empty array string.");
                return "[]";
            }
             if (!file.canRead()) {
                System.err.println("Cannot read from " + RESERVATIONS_FILE_NAME + " at: " + file.getAbsolutePath() + ". Check permissions.");
                return "[]";
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("Read from " + RESERVATIONS_FILE_NAME + " (" + file.getAbsolutePath() + "): " + content);
            return content;
        } catch (IOException e) {
            System.err.println("Failed to read " + RESERVATIONS_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    public static void writeReservationsFile(String content) {
         System.out.println("Attempting to write to " + reservationsFilePath);
        try {
            File file = new File(reservationsFilePath);
             if (!file.exists()) {
                 System.out.println(RESERVATIONS_FILE_NAME + " does not exist at: " + file.getAbsolutePath() + ". Attempting to create it for writing.");
                  if (!file.createNewFile()) {
                     System.err.println("Failed to create " + RESERVATIONS_FILE_NAME + " for writing at: " + file.getAbsolutePath() + ". Check permissions.");
                     return;
                  }
             }
             if (!file.canWrite()) {
                 System.err.println("Cannot write to " + RESERVATIONS_FILE_NAME + " at: " + file.getAbsolutePath() + ". Check permissions.");
                 return;
             }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                writer.flush();
                System.out.println("Successfully wrote to " + RESERVATIONS_FILE_NAME + " (" + file.getAbsolutePath() + "): " + content);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to " + RESERVATIONS_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ensure TableFileUtils also has an initialize method that takes ServletContext
    // so it can be called from AppInitializer or a Servlet's init method.
} 