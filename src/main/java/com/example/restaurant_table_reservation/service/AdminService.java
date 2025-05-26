package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.utils.UserFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;

public class AdminService {
    public User authenticateAdmin(String username, String password) {
        try {
            String userData = UserFileUtils.readFile();
            System.out.println("Reading users.json for admin auth: " + userData);
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new TypeToken<List<User>>(){}.getType());
            if (users != null) {
                for (User user : users) {
                    System.out.println("Checking user: " + user.getUsername() + "/" + user.getPassword() + ", isAdmin: " + user.isAdmin());
                    if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isAdmin()) {
                        return user;
                    }
                }
            } else {
                System.out.println("No users found in users.json");
            }
        } catch (IOException e) {
            System.err.println("Error reading users.json for admin auth: " + e.getMessage());
            throw new RuntimeException("Failed to authenticate admin due to I/O error", e);
        }
        return null;
    }

    public boolean updateUser(User updatedUser) {
        try {
            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new TypeToken<List<User>>(){}.getType());
            if (users != null) {
                for (User user : users) {
                    if (user.getId() != updatedUser.getId() && user.getUsername().equals(updatedUser.getUsername())) {
                        return false;
                    }
                }
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId() == updatedUser.getId()) {
                        users.set(i, updatedUser);
                        UserFileUtils.writeFile(gson.toJson(users));
                        System.out.println("Updated user with id: " + updatedUser.getId());
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error updating user with id " + updatedUser.getId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to update user due to I/O error", e);
        }
        return false;
    }

    public void editUser(int id, String username, String password, String email, String firstName, String lastName) {
        try {
            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new TypeToken<List<User>>(){}.getType());
            if (users != null) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId() == id) {
                        users.get(i).setUsername(username);
                        users.get(i).setPassword(password);
                        users.get(i).setEmail(email);
                        users.get(i).setFirstName(firstName);
                        users.get(i).setLastName(lastName);
                        UserFileUtils.writeFile(gson.toJson(users));
                        System.out.println("Edited user with id: " + id);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error editing user with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to edit user due to I/O error", e);
        }
    }

    public void deleteUser(int id) {
        try {
            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new TypeToken<List<User>>(){}.getType());
            if (users != null) {
                users.removeIf(user -> user.getId() == id);
                UserFileUtils.writeFile(gson.toJson(users));
                System.out.println("Deleted user with id: " + id);
            }
        } catch (IOException e) {
            System.err.println("Error deleting user with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete user due to I/O error", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            return gson.fromJson(userData, new TypeToken<List<User>>(){}.getType());
        } catch (IOException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            throw new RuntimeException("Failed to get all users due to I/O error", e);
        }
    }
}