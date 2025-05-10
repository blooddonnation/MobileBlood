package com.example.dami.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.dami.ApiConfig;
import com.example.dami.MainActivity;
import com.example.dami.R;
import com.example.dami.api.PositionApi;
import com.example.dami.model.GpsPosition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private PositionApi positionApi;
    private String macAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Get MAC address
        macAddress = getMacAddress();
        
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        positionApi = retrofit.create(PositionApi.class);
        
        // Create location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    sendLocationToServer(location);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(900); // 15 minutes

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void sendLocationToServer(Location location) {
        GpsPosition position = new GpsPosition(macAddress, location.getLatitude(), location.getLongitude());
        
        positionApi.updatePosition(position).enqueue(new Callback<GpsPosition>() {
            @Override
            public void onResponse(Call<GpsPosition> call, Response<GpsPosition> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("LocationService", "Position updated successfully");
                } else {
                    android.util.Log.e("LocationService", "Failed to update position: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GpsPosition> call, Throwable t) {
                android.util.Log.e("LocationService", "Error updating position: " + t.getMessage());
            }
        });
    }

    private String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            android.util.Log.d("LocationService", "Available network interfaces: " + all.size());
            
            for (NetworkInterface nif : all) {
                android.util.Log.d("LocationService", "Checking interface: " + nif.getName());
                
                // Skip loopback and inactive interfaces
                if (nif.isLoopback() || !nif.isUp()) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    android.util.Log.d("LocationService", "No MAC address for interface: " + nif.getName());
                    continue;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                    String macAddress = res1.toString();
                    android.util.Log.d("LocationService", "Found MAC address: " + macAddress + " for interface: " + nif.getName());
                    return macAddress;
                }
            }
        } catch (Exception ex) {
            android.util.Log.e("LocationService", "Error getting MAC address: " + ex.getMessage());
        }
        
        // If we couldn't get MAC address, use device info
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                deviceId = Build.getSerial();
            } catch (SecurityException e) {
                android.util.Log.w("LocationService", "No permission to get serial number: " + e.getMessage());
                deviceId = null;
            }
        } else {
            deviceId = Build.SERIAL;
        }

        if (deviceId == null || deviceId.isEmpty() || deviceId.equals("unknown")) {
            deviceId = Build.MANUFACTURER + Build.MODEL + Build.DEVICE;
        }
        android.util.Log.w("LocationService", "Using fallback device ID: " + deviceId);
        return deviceId;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking")
                .setContentText("Tracking your location")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 