package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.utils.TableFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TableService {
    private static TableService instance = null;
    private Table[] tables;
    private Gson gson;

    private TableService() {
        gson = new GsonBuilder().create();
        loadTables();
    }

    public static synchronized TableService getInstance() {
        if (instance == null) {
            instance = new TableService();
        }
        return instance;
    }

    private void loadTables() {
        String json = TableFileUtils.readTablesFile();
        tables = gson.fromJson(json, Table[].class);
        if (tables == null) {
            tables = new Table[0];
        }
    }

    private void saveTables() {
        String json = gson.toJson(tables);
        TableFileUtils.writeTablesFile(json);
    }

    public Table[] getAllTables() {
        loadTables(); // Reload tables from file to get latest data
        return tables;
    }

    public Table getTableById(int id) {
        for (Table table : tables) {
            if (table.getId() == id) {
                return table;
            }
        }
        return null;
    }

    public void updateTable(int id, int number, int capacity, boolean available, String category) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].getId() == id) {
                tables[i] = new Table(id, number, capacity, available, category);
                break;
            }
        }
        saveTables();
    }

    public void addTable(int id, int number, int capacity, boolean available, String category) {
        Table newTable = new Table(id, number, capacity, available, category);
        Table[] newTables = new Table[tables.length + 1];
        System.arraycopy(tables, 0, newTables, 0, tables.length);
        newTables[tables.length] = newTable;
        tables = newTables;
        saveTables();
    }

    public long getAvailableTablesCount() {
        long count = 0;
        for (Table table : tables) {
            if (table.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalTablesCount() {
        return tables.length;
    }
}