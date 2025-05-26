package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletContext;

public class TableFileUtils {
    private static final String TABLES_FILE_NAME = "tables.json";
    private static final String RESERVATIONS_FILE_NAME = "reservations.json";
    private static String tablesFilePath;
    private static String reservationsFilePath;
    private static boolean initialized = false;

    public static void initialize(ServletContext context) {
        if (initialized) {
            System.out.println("TableFileUtils already initialized, skipping.");
            return;
        }

        if (context == null) {
            System.err.println("ServletContext is null, cannot initialize file paths. Falling back to default paths.");
            tablesFilePath = "WEB-INF/data/" + TABLES_FILE_NAME;
            reservationsFilePath = "WEB-INF/data/" + RESERVATIONS_FILE_NAME;
        } else {
            String realPath = context.getRealPath("/WEB-INF/data/");
            if (realPath == null) {
                System.err.println("Unable to resolve real path for /WEB-INF/data/. Check deployment settings.");
                return;
            }
            File directory = new File(realPath);
            if (!directory.exists()) {
                System.out.println("Creating directory: " + realPath);
                if (!directory.mkdirs()) {
                    System.err.println("Failed to create directory: " + realPath);
                    return;
                }
            }
            tablesFilePath = realPath + File.separator + TABLES_FILE_NAME;
            reservationsFilePath = realPath + File.separator + RESERVATIONS_FILE_NAME;
            System.out.println("Tables file path: " + tablesFilePath);
            System.out.println("Reservations file path: " + reservationsFilePath);
        }

        // Ensure files exist
        try {
            File tablesFile = new File(tablesFilePath);
            if (!tablesFile.exists()) {
                if (tablesFile.createNewFile()) {
                    writeTablesFile("[]");
                    System.out.println("Created and initialized tables.json at: " + tablesFilePath);
                } else {
                    System.err.println("Failed to create tables.json at: " + tablesFilePath);
                }
            }
            File reservationsFile = new File(reservationsFilePath);
            if (!reservationsFile.exists()) {
                if (reservationsFile.createNewFile()) {
                    writeReservationsFile("[]");
                    System.out.println("Created and initialized reservations.json at: " + reservationsFilePath);
                } else {
                    System.err.println("Failed to create reservations.json at: " + reservationsFilePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize files: " + e.getMessage());
            e.printStackTrace();
        }
        initialized = true;
    }

    public static String readTablesFile() {
        try {
            File file = new File(tablesFilePath != null ? tablesFilePath : "WEB-INF/data/" + TABLES_FILE_NAME);
            if (!file.exists()) {
                System.out.println("tables.json does not exist at: " + file.getAbsolutePath() + ". Creating it.");
                file.createNewFile();
                writeTablesFile("[]");
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("Read from tables.json (" + file.getAbsolutePath() + "): " + content);
            return content;
        } catch (IOException e) {
            System.err.println("Failed to read tables.json: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    public static void writeTablesFile(String content) {
        try {
            File file = new File(tablesFilePath != null ? tablesFilePath : "WEB-INF/data/" + TABLES_FILE_NAME);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                writer.flush();
                System.out.println("Successfully wrote to tables.json (" + file.getAbsolutePath() + "): " + content);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to tables.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String readReservationsFile() {
        try {
            File file = new File(reservationsFilePath != null ? reservationsFilePath : "WEB-INF/data/" + RESERVATIONS_FILE_NAME);
            if (!file.exists()) {
                System.out.println("reservations.json does not exist at: " + file.getAbsolutePath() + ". Creating it.");
                file.createNewFile();
                writeReservationsFile("[]");
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("Read from reservations.json (" + file.getAbsolutePath() + "): " + content);
            return content;
        } catch (IOException e) {
            System.err.println("Failed to read reservations.json: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    public static void writeReservationsFile(String content) {
        try {
            File file = new File(reservationsFilePath != null ? reservationsFilePath : "WEB-INF/data/" + RESERVATIONS_FILE_NAME);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                writer.flush();
                System.out.println("Successfully wrote to reservations.json (" + file.getAbsolutePath() + "): " + content);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to reservations.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}