package com.example.restaurant_table_reservation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jakarta.servlet.ServletContext;

public class UserFileUtils {
    private static final String FILE_NAME = "users.json";
    private static String filePath;
    private static String sourceFilePath;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void initialize(ServletContext context) {
        String realPath = context.getRealPath("/WEB-INF/data/");
        File directory = new File(realPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create directory: " + realPath);
            }
        }
        filePath = realPath + File.separator + FILE_NAME;
        // Define the source path (adjust based on your project structure)
        sourceFilePath = "src/main/webapp/WEB-INF/data/" + FILE_NAME;
        System.out.println("Users file path (runtime): " + filePath);
        System.out.println("Users file path (source): " + sourceFilePath);

        // Ensure the file exists
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                writeFile("[]");
                System.out.println("Created empty users.json at: " + filePath);
            } catch (IOException e) {
                System.err.println("Failed to create users.json: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static String readFile() throws IOException {
        lock.readLock().lock();
        try {
            if (filePath == null) {
                throw new IllegalStateException("UserFileUtils not initialized. Call initialize() first.");
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                writeFile("[]");
            }
            return new String(Files.readAllBytes(file.toPath()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void writeFile(String content) throws IOException {
        lock.writeLock().lock();
        try {
            if (filePath == null) {
                throw new IllegalStateException("UserFileUtils not initialized. Call initialize() first.");
            }
            // Write to the runtime file (target directory)
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(content);
            }
            // Copy the updated file back to the source directory
            File runtimeFile = new File(filePath);
            File sourceFile = new File(sourceFilePath);
            Files.copy(runtimeFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            lock.writeLock().unlock();
        }
    }
}