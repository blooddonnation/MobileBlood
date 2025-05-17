package com.example.dami.utils;

import android.util.Base64;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtDecoder {
    private static final String TAG = "JwtDecoder";

    public static Long getUserIdFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                Log.e(TAG, "Token is null or empty");
                return null;
            }

            // Split the token into parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid token format");
                return null;
            }

            // Decode the payload (second part)
            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
            Log.d(TAG, "Decoded payload: " + payload);

            // Parse the JSON payload
            JSONObject jsonPayload = new JSONObject(payload);
            
            // Extract the user ID from the "id" claim
            if (jsonPayload.has("id")) {
                Long userId = jsonPayload.getLong("id");
                Log.d(TAG, "Found user ID in token: " + userId);
                return userId;
            } else {
                Log.e(TAG, "No 'id' claim found in token");
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JWT payload: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing user ID from token: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error decoding token: " + e.getMessage());
            return null;
        }
    }
} 