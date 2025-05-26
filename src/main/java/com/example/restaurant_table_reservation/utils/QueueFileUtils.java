package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletContext;

/**
 * File utilities for managing the reservation queue data
 */
public class QueueFileUtils {
    private static final String QUEUE_FILE_NAME = "reservation_queue.json";
    private static String queueFilePath;
    private static boolean initialized = false;

    public static void initialize(ServletContext context) {
        if (initialized) {
            System.out.println("QueueFileUtils already initialized, skipping.");
            return;
        }

        System.out.println("Initializing QueueFileUtils...");

        if (context == null) {
            System.err.println("ServletContext is null, cannot initialize file path. Falling back to default path.");
            queueFilePath = "WEB-INF/data/" + QUEUE_FILE_NAME;
            System.out.println("Using default queue file path: " + queueFilePath);
        } else {
            String realPath = context.getRealPath("/WEB-INF/data/");
            if (realPath == null) {
                System.err.println("Unable to resolve real path for /WEB-INF/data/. Check deployment settings.");
                queueFilePath = "WEB-INF/data/" + QUEUE_FILE_NAME;
                System.err.println("Falling back to relative path: " + queueFilePath);
            } else {
                File directory = new File(realPath);
                if (!directory.exists()) {
                    System.out.println("Creating directory: " + realPath);
                    if (!directory.mkdirs()) {
                        System.err.println("Failed to create directory: " + realPath);
                        queueFilePath = "WEB-INF/data/" + QUEUE_FILE_NAME;
                        System.err.println("Falling back to relative path after mkdirs failure: " + queueFilePath);
                    } else {
                        System.out.println("Directory created successfully: " + realPath);
                        queueFilePath = realPath + File.separator + QUEUE_FILE_NAME;
                        System.out.println("Resolved queue file path: " + queueFilePath);
                    }
                } else {
                    queueFilePath = realPath + File.separator + QUEUE_FILE_NAME;
                    System.out.println("Resolved queue file path: " + queueFilePath);
                }
            }
        }

        // Ensure file exists
        try {
            File queueFile = new File(queueFilePath);
            if (!queueFile.exists()) {
                System.out.println(QUEUE_FILE_NAME + " does not exist at: " + queueFile.getAbsolutePath() + ". Attempting to create it.");
                if (queueFile.createNewFile()) {
                    writeQueueFile("[]");
                    System.out.println("Created and initialized " + QUEUE_FILE_NAME + " at: " + queueFilePath);
                } else {
                    System.err.println("Failed to create " + QUEUE_FILE_NAME + " at: " + queueFilePath + ". Check permissions.");
                }
            } else {
                System.out.println(QUEUE_FILE_NAME + " already exists at: " + queueFilePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize " + QUEUE_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
        }
        initialized = true;
        System.out.println("QueueFileUtils initialization finished.");
    }

    public static String readQueueFile() {
        System.out.println("Attempting to read from " + queueFilePath);
        try {
            File file = new File(queueFilePath);
            if (!file.exists()) {
                System.out.println(QUEUE_FILE_NAME + " does not exist at: " + file.getAbsolutePath() + ". Returning empty array string.");
                return "[]";
            }
            if (!file.canRead()) {
                System.err.println("Cannot read from " + QUEUE_FILE_NAME + " at: " + file.getAbsolutePath() + ". Check permissions.");
                return "[]";
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("Read from " + QUEUE_FILE_NAME + " (" + file.getAbsolutePath() + "): " + content);
            return content;
        } catch (IOException e) {
            System.err.println("Failed to read " + QUEUE_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    public static void writeQueueFile(String content) {
        System.out.println("Attempting to write to " + queueFilePath);
        try {
            File file = new File(queueFilePath);
            if (!file.exists()) {
                System.out.println(QUEUE_FILE_NAME + " does not exist at: " + file.getAbsolutePath() + ". Attempting to create it for writing.");
                if (!file.createNewFile()) {
                    System.err.println("Failed to create " + QUEUE_FILE_NAME + " for writing at: " + file.getAbsolutePath() + ". Check permissions.");
                    return;
                }
            }
            if (!file.canWrite()) {
                System.err.println("Cannot write to " + QUEUE_FILE_NAME + " at: " + file.getAbsolutePath() + ". Check permissions.");
                return;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                writer.flush();
                System.out.println("Successfully wrote to " + QUEUE_FILE_NAME + " (" + file.getAbsolutePath() + "): " + content);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to " + QUEUE_FILE_NAME + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get current file path (for debugging)
    public static String getQueueFilePath() {
        return queueFilePath;
    }

    // Method to check if file utils is initialized
    public static boolean isInitialized() {
        return initialized;
    }
}