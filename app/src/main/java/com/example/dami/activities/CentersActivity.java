package com.example.dami.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dami.R;
import com.example.dami.models.BloodCenter;
import com.example.dami.retrofit.CenterApi;
import com.example.dami.retrofit.RetrofitClient;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CentersActivity extends AppCompatActivity {
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private CompassOverlay compassOverlay;
    private List<BloodCenter> bloodCenters;
    private CenterApi centerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_centers);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        mapView = findViewById(R.id.mapView);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize OSMDroid
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Set up location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Set up compass overlay
        compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Set initial map position (Morocco)
        GeoPoint startPoint = new GeoPoint(31.7917, -7.0926);
        mapView.getController().setZoom(6.0);
        mapView.getController().setCenter(startPoint);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        centerApi = retrofit.create(CenterApi.class);

        // Fetch blood centers
        fetchBloodCenters();

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchBloodCenters() {
        Call<List<BloodCenter>> call = centerApi.getAllCenters();
        call.enqueue(new Callback<List<BloodCenter>>() {
            @Override
            public void onResponse(Call<List<BloodCenter>> call, Response<List<BloodCenter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bloodCenters = response.body();
                    addBloodCenterMarkers();
                }
            }

            @Override
            public void onFailure(Call<List<BloodCenter>> call, Throwable t) {
                Toast.makeText(CentersActivity.this, 
                    "Failed to fetch blood centers: " + t.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addBloodCenterMarkers() {
        for (BloodCenter center : bloodCenters) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(center.getLatitude(), center.getLongitude()));
            marker.setTitle(center.getNamecenter());
            marker.setSnippet("Admin: " + center.getNameadmin());
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
