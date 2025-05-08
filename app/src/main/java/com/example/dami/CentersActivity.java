package com.example.dami;

import android.os.Bundle;
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

public class CentersActivity extends AppCompatActivity {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private CompassOverlay compassOverlay;
    private List<HospitalMarker> hospitalMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centers);

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize hospital markers list
        initializeHospitalMarkers();

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