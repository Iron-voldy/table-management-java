package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.utils.UserFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static UserService instance = null;
    private List<User> userList;
    private Gson gson;

    private UserService() {
        gson = new Gson();
        loadUsers();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadUsers() {
        try {
            String json = UserFileUtils.readFile();
            TypeToken<List<User>> typeToken = new TypeToken<List<User>>(){};
            userList = gson.fromJson(json, typeToken.getType());
            if (userList == null) {
                userList = new ArrayList<>();
            }
            System.out.println("Loaded Users: " + userList);
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            userList = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            UserFileUtils.writeFile(gson.toJson(userList));
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            throw new RuntimeException("Failed to save users due to I/O error", e);
        }
    }

    public List<User> getAllUsers() {
        loadUsers(); // Reload users from file to get latest data
        return userList;
    }

    public boolean registerUser(String username, String password, String email) {
        try {
            // Check if username already exists
            for (User user : userList) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    System.out.println("Registration failed: Username " + username + " is already taken.");
                    return false;
                }
            }

            int newId = userList.isEmpty() ? 1 : userList.get(userList.size() - 1).getId() + 1;
            User newUser = new User(newId, username, email, password, "", "", false);
            userList.add(newUser);
            saveUsers();
            System.out.println("Registered new user with ID: " + newId);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public User authenticateUser(String username, String password) {
        try {
            for (User user : userList) {
                if (user.getUsername().equalsIgnoreCase(username) && 
                    user.getPassword().equals(password)) {
                    return user;
                }
            }
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            throw new RuntimeException("Failed to authenticate user", e);
        }
        return null;
    }

    public boolean updateUserProfile(User updatedUser) {
        try {
            // Check for username conflict, excluding the current user
            for (User user : userList) {
                if (user.getId() != updatedUser.getId() && 
                    user.getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                    System.out.println("Username conflict: " + updatedUser.getUsername() + " is already taken");
                    return false;
                }
            }

            // Update the user in the list
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId() == updatedUser.getId()) {
                    userList.set(i, updatedUser);
                    saveUsers();
                    System.out.println("Updated user with ID: " + updatedUser.getId());
                    return true;
                }
            }
            System.out.println("User with ID " + updatedUser.getId() + " not found");
            return false;
        } catch (Exception e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    public boolean deleteUser(int userId) {
        try {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId() == userId) {
                    userList.remove(i);
                    saveUsers();
                    System.out.println("Deleted user with ID: " + userId);
                    return true;
                }
            }
            System.out.println("User with ID " + userId + " not found");
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}