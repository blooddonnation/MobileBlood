package com.example.dami;

import android.os.Bundle;
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

public class CentersActivity extends AppCompatActivity {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private CompassOverlay compassOverlay;
    private List<BloodCenter> hospitalMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centers);

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

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
}
