package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletContext;

public class OrderFileUtils {
    private static final String FILE_NAME = "orders.json";
    private static String filePath;

    public static void initialize(ServletContext context) {
        if (context != null) {
            String realPath = context.getRealPath("/WEB-INF/data/");
            File directory = new File(realPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            filePath = realPath + File.separator + FILE_NAME;
            System.out.println("Orders file path initialized: " + filePath);
            // Ensure file exists
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                    writeFile("[]"); // Initialize with empty array
                    System.out.println("Created new orders.json file and initialized with empty array");
                } else if (!file.canWrite()) {
                    System.err.println("orders.json is not writable at: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("Failed to initialize orders.json: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("ServletContext is null, cannot initialize file path");
        }
    }

    public static String readFile() {
        try {
            File file = new File(filePath != null ? filePath : FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
                writeFile("[]");
                System.out.println("Created new orders.json file and initialized with empty array");
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.err.println("Failed to read orders.json: " + e.getMessage());
            e.printStackTrace();
            return "[]"; // Fallback to empty array on failure
        }
    }

    public static void writeFile(String content) {
        try (FileWriter writer = new FileWriter(filePath != null ? filePath : FILE_NAME)) {
            writer.write(content);
            System.out.println("Successfully wrote to orders.json: " + content.substring(0, Math.min(content.length(), 50)) + (content.length() > 50 ? "..." : ""));
        } catch (IOException e) {
            System.err.println("Failed to write to orders.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}