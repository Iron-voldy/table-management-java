package com.example.restaurant_table_reservation.utils;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jakarta.servlet.ServletContext;

public class FileUtils {
    private static final String FILE_NAME = "menu.json";
    private static String filePath;
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
        System.out.println("Menu file path: " + filePath);
    }

    public static String readFile() throws IOException {
        lock.readLock().lock();
        try {
            if (filePath == null) {
                throw new IllegalStateException("FileUtils not initialized. Call initialize() first.");
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
                throw new IllegalStateException("FileUtils not initialized. Call initialize() first.");
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(content);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}