package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletContext;

public class ReviewFileUtils {
    private static final String FILE_NAME = "review.json";
    private static String filePath;

    public static void initialize(ServletContext context) {
        if (context != null) {
            String realPath = context.getRealPath("/WEB-INF/data/");
            File directory = new File(realPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            filePath = realPath + File.separator + FILE_NAME;
            System.out.println("Review file path initialized: " + filePath);
            // Ensure file exists
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                    writeFile("[]"); // Initialize with empty array
                }
            } catch (IOException e) {
                System.err.println("Failed to initialize review.json: " + e.getMessage());
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
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.err.println("Failed to read review.json: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    public static void writeFile(String content) {
        try (FileWriter writer = new FileWriter(filePath != null ? filePath : FILE_NAME)) {
            writer.write(content);
            System.out.println("Successfully wrote to review.json: " + content);
        } catch (IOException e) {
            System.err.println("Failed to write to review.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}