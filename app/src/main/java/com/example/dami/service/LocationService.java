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
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.dami.R;
import com.example.dami.activities.MainActivity;
import com.example.dami.api.ApiConfig;
import com.example.dami.api.PositionApi;
import com.example.dami.models.GpsPosition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private PositionApi positionApi;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
        setupLocationCallback();
        setupRetrofit();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL_Position_Tracking)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        positionApi = retrofit.create(PositionApi.class);
    }

    private void setupLocationCallback() {
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

    private void sendLocationToServer(Location location) {
        String deviceId = generateDeviceId();
        GpsPosition position = new GpsPosition(deviceId, location.getLatitude(), location.getLongitude());
        
        android.util.Log.d(TAG, "Attempting to send location - DeviceId: " + deviceId + 
            ", Lat: " + location.getLatitude() + 
            ", Lng: " + location.getLongitude());

        positionApi.updatePosition(position).enqueue(new Callback<GpsPosition>() {
            @Override
            public void onResponse(Call<GpsPosition> call, retrofit2.Response<GpsPosition> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d(TAG, "Position updated successfully");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        android.util.Log.e(TAG, "Error updating position: " + response.code() + 
                            " - " + response.message() + 
                            "\nError body: " + errorBody);
                    } catch (IOException e) {
                        android.util.Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GpsPosition> call, Throwable t) {
                android.util.Log.e(TAG, "Error updating position: " + t.getMessage() + 
                    "\nCause: " + (t.getCause() != null ? t.getCause().getMessage() : "No cause") +
                    "\nStack trace: " + android.util.Log.getStackTraceString(t));
            }
        });
    }

    private String generateDeviceId() {
        String deviceId = null;

        // Try to get MAC address first
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : interfaces) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (byte b : macBytes) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                deviceId = sb.toString();
                break;
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error getting MAC address: " + e.getMessage());
        }

        // If MAC address is not available, use Android ID
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        // If Android ID is not available, use device info
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = Build.MANUFACTURER + "_" + Build.MODEL + "_" + Build.DEVICE;
        }

        return deviceId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        startLocationUpdates();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking location...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(30000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(15000)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
            android.util.Log.d(TAG, "Location updates requested successfully");
        } catch (SecurityException e) {
            android.util.Log.e(TAG, "Error requesting location updates: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 