package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.location.Location;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.UserLocation;
import fr.upjv.project_android_ccm.data.repository.LocationRepository;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationRepository locationRepository;
    private String tripId;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_trip_detail);
//Importation Menu
        ImageButton homeBtn = findViewById(R.id.Homebutton);
        ImageButton friendsButton = findViewById(R.id.Friendsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TripDetailActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        friendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(TripDetailActivity.this, FriendListActivity.class);
            startActivity(intent);
        });
//Fin importation Menu

        tripId = getIntent().getStringExtra("tripId");
        locationRepository = new LocationRepository();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        textView = findViewById(R.id.tripIdTextView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (tripId != null && !tripId.isEmpty()) {
            locationRepository.getLocationsByTripId(tripId, locations -> {
                if (locations != null && !locations.isEmpty()) {
                    locations.sort((l1, l2) -> l1.getDate().compareTo(l2.getDate()));

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .width(8f)
                            .color(Color.BLUE)
                            .geodesic(true);

                    for (int i = 0; i < locations.size(); i++) {
                        UserLocation loc = locations.get(i);
                        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                        polylineOptions.add(latLng);

                        // 🕒 Formater la date
                        String rawDate = loc.getDate();
                        String formattedDate = rawDate;
                        try {
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm");
                            LocalDateTime dateTime = LocalDateTime.parse(rawDate, inputFormatter);
                            formattedDate = dateTime.format(outputFormatter);
                        } catch (Exception e) {
                            Log.e("MAP", "Erreur de parsing de la date : " + rawDate, e);
                        }

                        String title;
                        float color;

                        if (i == 0) {
                            title = "🚩 Départ : " + formattedDate;
                            color = BitmapDescriptorFactory.HUE_GREEN;
                        } else if (i == locations.size() - 1) {
                            title = "🏁 Arrivée : " + formattedDate;
                            color = BitmapDescriptorFactory.HUE_RED;
                        } else {
                            title = "Arrêt : " + formattedDate;
                            color = BitmapDescriptorFactory.HUE_AZURE; // ou YELLOW / ORANGE
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(color)));
                    }

                    float totalDistanceMeters = 0;

                    for (int i = 0; i < locations.size() - 1; i++) {
                        UserLocation loc1 = locations.get(i);
                        UserLocation loc2 = locations.get(i + 1);

                        float[] result = new float[1];
                        Location.distanceBetween(
                                loc1.getLatitude(), loc1.getLongitude(),
                                loc2.getLatitude(), loc2.getLongitude(),
                                result
                        );
                        totalDistanceMeters += result[0];
                    }
                    float totalDistanceKm = totalDistanceMeters / 1000f;
                    Log.d("DISTANCE", "Distance totale : " + totalDistanceKm + " km");
                    textView.setText("Distance totale : " + String.format("%.2f", totalDistanceKm) + " km");

                    mMap.addPolyline(polylineOptions);

                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (UserLocation loc : locations) {
                        boundsBuilder.include(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                    LatLngBounds bounds = boundsBuilder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100 = padding (px)

                } else {
                    Log.w("MAP", "Aucune localisation à afficher");
                }
            });
        } else {
            Log.e("MAP", "tripId invalide !");
        }
    }



}