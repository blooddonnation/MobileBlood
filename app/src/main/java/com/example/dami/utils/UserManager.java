package com.example.dami.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_CURRENT_USER = "current_user";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public UserManager(Context context) {
        try {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            gson = new Gson();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UserManager: " + e.getMessage());
            throw e;
        }
    }

    public static class User {
        private String username;
        private String password;
        private String email;
        private String bloodType;
        private String dateOfBirth;
        private String role;

        public User(String username, String password, String email, String bloodType, String dateOfBirth, String role) {
            if (username == null || password == null || email == null || 
                bloodType == null || dateOfBirth == null || role == null) {
                throw new IllegalArgumentException("All fields must be non-null");
            }
            this.username = username;
            this.password = password;
            this.email = email;
            this.bloodType = bloodType;
            this.dateOfBirth = dateOfBirth;
            this.role = role;
        }

        // Getters
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getBloodType() { return bloodType; }
        public String getDateOfBirth() { return dateOfBirth; }
        public String getRole() { return role; }
    }

    public boolean registerUser(User user) {
        try {
            if (user == null) {
                Log.e(TAG, "Cannot register null user");
                return false;
            }

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                Log.e(TAG, "Username cannot be empty");
                return false;
            }

            List<User> users = getUsers();
            
            // Check if username already exists
            for (User existingUser : users) {
                if (existingUser.getUsername().equals(user.getUsername())) {
                    Log.d(TAG, "Username already exists: " + user.getUsername());
                    return false;
                }
            }
            
            users.add(user);
            String usersJson = gson.toJson(users);
            editor.putString(KEY_USERS, usersJson);
            boolean success = editor.commit();
            
            if (!success) {
                Log.e(TAG, "Failed to save user data");
                return false;
            }
            
            Log.d(TAG, "Successfully registered user: " + user.getUsername());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error registering user: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        try {
            if (username == null || password == null) {
                Log.e(TAG, "Username or password cannot be null");
                return null;
            }

            List<User> users = getUsers();
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    // Save current user
                    String userJson = gson.toJson(user);
                    editor.putString(KEY_CURRENT_USER, userJson);
                    boolean success = editor.commit();
                    
                    if (!success) {
                        Log.e(TAG, "Failed to save current user");
                        return null;
                    }
                    
                    Log.d(TAG, "Successfully logged in user: " + username);
                    return user;
                }
            }
            Log.d(TAG, "Login failed for user: " + username);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error during login: " + e.getMessage());
            return null;
        }
    }

    public User getCurrentUser() {
        try {
            String userJson = prefs.getString(KEY_CURRENT_USER, null);
            if (userJson == null) {
                return null;
            }
            return gson.fromJson(userJson, User.class);
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user: " + e.getMessage());
            return null;
        }
    }

    public void logout() {
        try {
            editor.remove(KEY_CURRENT_USER);
            boolean success = editor.commit();
            if (!success) {
                Log.e(TAG, "Failed to logout user");
            } else {
                Log.d(TAG, "Successfully logged out user");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
        }
    }

    private List<User> getUsers() {
        try {
            String usersJson = prefs.getString(KEY_USERS, null);
            if (usersJson == null) {
                return new ArrayList<>();
            }
            Type type = new TypeToken<ArrayList<User>>(){}.getType();
            return gson.fromJson(usersJson, type);
        } catch (Exception e) {
            Log.e(TAG, "Error getting users list: " + e.getMessage());
            return new ArrayList<>();
        }
    }
} 