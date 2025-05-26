package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.MenuItem;
import com.example.restaurant_table_reservation.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private List<MenuItem> menuList;
    private Gson gson = new Gson();

    public MenuService() {
        loadMenu();
    }

    private void loadMenu() {
        try {
            String json = FileUtils.readFile();
            Type listType = new TypeToken<List<MenuItem>>(){}.getType();
            menuList = gson.fromJson(json, listType);
            if (menuList == null || menuList.isEmpty()) {
                menuList = new ArrayList<>();
                // Add default menu items
                menuList.add(new MenuItem(1, "Classic Burger", 12.99, "Juicy beef patty with fresh vegetables", true, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", "main dishes"));
                menuList.add(new MenuItem(2, "Caesar Salad", 8.99, "Fresh romaine lettuce with Caesar dressing", true, "https://images.unsplash.com/photo-1550304943-4f24f54ddde9", "appetizers"));
                menuList.add(new MenuItem(3, "Chocolate Cake", 6.99, "Rich chocolate cake with ganache", true, "https://images.unsplash.com/photo-1578985545062-69928b1d9587", "desserts"));
                menuList.add(new MenuItem(4, "Cola", 2.99, "Refreshing carbonated drink", true, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97", "soft drink"));
                menuList.add(new MenuItem(5, "Whiskey", 8.99, "Premium aged whiskey", true, "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b", "hard drink"));
                saveMenu();
            }
            System.out.println("Loaded Menu: " + menuList);
        } catch (Exception e) {
            System.err.println("Failed to load menu: " + e.getMessage());
            menuList = new ArrayList<>();
        }
    }

    private void saveMenu() {
        try {
            FileUtils.writeFile(gson.toJson(menuList));
        } catch (Exception e) {
            System.err.println("Failed to save menu: " + e.getMessage());
        }
    }

    public List<MenuItem> getAllItems() {
        loadMenu();
        return menuList;
    }

    public void addItem(String name, double price, String description, boolean available, String imageUrl, String category) {
        int id = menuList.isEmpty() ? 1 : menuList.get(menuList.size() - 1).getId() + 1;
        menuList.add(new MenuItem(id, name, price, description, available, imageUrl, category));
        saveMenu();
        loadMenu();
        loadMenu();


    }

    public void deleteItem(int id) {
        menuList.removeIf(item -> item.getId() == id);
        saveMenu();
    }

    public void updateItem(int id, String name, double price, String description, boolean available, String imageUrl, String category) {
        for (MenuItem item : menuList) {
            if (item.getId() == id) {
                item.setName(name);
                item.setPrice(price);
                item.setDescription(description);
                item.setAvailable(available);
                item.setImageUrl(imageUrl);
                item.setCategory(category);
                saveMenu();
                break;
            }
        }
    }

    public MenuItem getItemById(int id) {
        for (MenuItem item : menuList) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;  // Return null if item with given ID is not found
    }
}

