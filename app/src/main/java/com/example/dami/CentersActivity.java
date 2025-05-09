package com.example.dami;

import android.os.Bundle;
<<<<<<< HEAD
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
=======
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dami.retrofit.CenterApi;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
>>>>>>> origin/rajae

public class CentersActivity extends AppCompatActivity {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private CompassOverlay compassOverlay;
<<<<<<< HEAD
    private List<HospitalMarker> hospitalMarkers;
=======
    private List<BloodCenter> hospitalMarkers;
>>>>>>> origin/rajae

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centers);

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
<<<<<<< HEAD
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize hospital markers list
        initializeHospitalMarkers();
=======
        backButton.setOnClickListener(v -> finish());
>>>>>>> origin/rajae

        // Set up the map
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Add location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);

        // Add compass overlay
        compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // Set default view to Morocco
        map.getController().setZoom(6.0);
        map.getController().setCenter(new GeoPoint(31.7917, -7.0926));

<<<<<<< HEAD
        // Add hospital markers
        addHospitalMarkers();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeHospitalMarkers() {
        hospitalMarkers = new ArrayList<>();
        // Add some sample hospitals in Morocco
        hospitalMarkers.add(new HospitalMarker("Hôpital Provincial de Casablanca", 33.5731, -7.5898));
        hospitalMarkers.add(new HospitalMarker("Hôpital Provincial de Rabat", 34.0209, -6.8416));
        hospitalMarkers.add(new HospitalMarker("Hôpital Provincial de Marrakech", 31.6295, -7.9811));
        hospitalMarkers.add(new HospitalMarker("Hôpital Provincial de Fès", 34.0333, -5.0000));
        hospitalMarkers.add(new HospitalMarker("Hôpital Provincial de Tanger", 35.7595, -5.8330));
    }

    private void addHospitalMarkers() {
        for (HospitalMarker marker : hospitalMarkers) {
            Marker mapMarker = new Marker(map);
            mapMarker.setPosition(new GeoPoint(marker.getLatitude(), marker.getLongitude()));
            mapMarker.setTitle(marker.getName());
            map.getOverlays().add(mapMarker);
=======
        // Fetch hospitals from backend
        fetchHospitals();

        // Set up map event listener
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Display the coordinates as a toast message
                double latitude = p.getLatitude();
                double longitude = p.getLongitude();
                Toast.makeText(CentersActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // Handle long press to add a marker or show info
                Marker longPressMarker = new Marker(map);
                longPressMarker.setPosition(p);
                longPressMarker.setTitle("Long Pressed Location");
                map.getOverlays().add(longPressMarker);
                // Optionally, display additional info in a Toast or custom overlay
                Toast.makeText(CentersActivity.this, "Long Pressed Location: " + p.getLatitude() + ", " + p.getLongitude(), Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(receiver);
        map.getOverlays().add(mapEventsOverlay);
    }

    private void fetchHospitals() {
        // Use the correct IP for accessing local server on the emulator
        String baseUrl = "http://10.0.2.2:8082/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)  // Use 10.0.2.2 for Android emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CenterApi apiService = retrofit.create(CenterApi.class);
        Call<List<BloodCenter>> call = apiService.getCenters();
        call.enqueue(new Callback<List<BloodCenter>>() {
            @Override
            public void onResponse(Call<List<BloodCenter>> call, Response<List<BloodCenter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hospitalMarkers = response.body();
                    addHospitalMarkers();
                } else {
                    Log.e("CentersActivity", "Failed response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BloodCenter>> call, Throwable t) {
                Log.e("CentersActivity", "Error fetching data", t);
                Toast.makeText(CentersActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addHospitalMarkers() {
        if (hospitalMarkers != null) {
            for (BloodCenter hospital : hospitalMarkers) {
                Log.d("CentersActivity", "Adding marker at: " + hospital.getLatitude() + ", " + hospital.getLongitude());
                Marker mapMarker = new Marker(map);
                mapMarker.setPosition(new GeoPoint(hospital.getLatitude(), hospital.getLongitude()));
                mapMarker.setTitle(hospital.getNamecenter());
                map.getOverlays().add(mapMarker);
                Log.d("CentersActivity", "Title = " + hospital.getNamecenter());
                mapMarker.showInfoWindow();


            }
>>>>>>> origin/rajae
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
<<<<<<< HEAD

    // Inner class to represent hospital markers
    private static class HospitalMarker {
        private String name;
        private double latitude;
        private double longitude;

        public HospitalMarker(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
} 
=======
}
>>>>>>> origin/rajae
