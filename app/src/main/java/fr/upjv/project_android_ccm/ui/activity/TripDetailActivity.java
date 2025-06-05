package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

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


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.UserLocation;
import fr.upjv.project_android_ccm.data.repository.LocationRepository;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.utils.TravelExporter;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationRepository locationRepository;
    private String tripId, tripName, dateStart, dateEnd, tripStatus;

    private TextView  distanceText, dateStartText2, dateStartText3;
    private android.view.View ongoingSection, finishedSection;

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
        boolean isFriendTrip = getIntent().getBooleanExtra("isFriendTrip", false);

        tripId = getIntent().getStringExtra("tripId");
        tripName = getIntent().getStringExtra("tripName");
        dateEnd = getIntent().getStringExtra("dateEnd");
        dateStart = getIntent().getStringExtra("dateStart");
        tripStatus = getIntent().getStringExtra("tripStatus");
        //frind

        locationRepository = new LocationRepository();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        TextView textViewTripName = findViewById(R.id.TripName );
        if (tripName != null && !tripName.isEmpty()) {
            if (isFriendTrip) {
                String friendPseudo = getIntent().getStringExtra("friendName");
                textViewTripName.setText("Voyage de "+ friendPseudo +" : " + tripName);
            } else {
                textViewTripName.setText("Voyage " + tripName);
            }
        } else {
            textViewTripName.setText("Voyage sans nom");
        }

        ongoingSection = findViewById(R.id.ongoingSection);

        finishedSection = findViewById(R.id.finishedSection);
        distanceText = findViewById(R.id.dateStartText4);
        dateStartText2 = findViewById(R.id.dateStartText2);
        dateStartText3 = findViewById(R.id.dateStartText3);

        if ("ongoing".equals(tripStatus)) {
            if (isFriendTrip) {
                ongoingSection.setVisibility(android.view.View.GONE);
            }
            else {
                ongoingSection.setVisibility(android.view.View.VISIBLE);
                Button endTripButton = findViewById(R.id.endTripButton);
                endTripButton.setOnClickListener(v -> {
                    LocalDateTime now = LocalDateTime.now();
                    String nowFormatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));

                    TravelRepository travelRepository = new TravelRepository();
                    travelRepository.endTrip(tripId, nowFormatted, success -> {
                        if (success) {
                            Toast.makeText(this, "Voyage terminé !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TripDetailActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Erreur lors de la fin du voyage.", Toast.LENGTH_SHORT).show();
                        }
                    });
                });


                finishedSection.setVisibility(android.view.View.GONE);
            }
        } else {
            ongoingSection.setVisibility(android.view.View.GONE);
            finishedSection.setVisibility(android.view.View.VISIBLE);
            //DELETE BUTTON
            Button deleteButton = findViewById(R.id.deleteButton);
            if (isFriendTrip) {
                deleteButton.setVisibility(android.view.View.GONE);
            }else {
            deleteButton.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Voulez-vous vraiment supprimer ce voyage et toutes ses localisations ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            LocationRepository locationRepository = new LocationRepository();
                            TravelRepository travelRepository = new TravelRepository();

                            locationRepository.deleteLocationsByTripId(tripId, locSuccess -> {
                                if (locSuccess) {
                                    travelRepository.deleteTrip(tripId, tripSuccess -> {
                                        if (tripSuccess) {
                                            Toast.makeText(this, "Voyage supprimé", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(TripDetailActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Erreur lors de la suppression du voyage", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Erreur lors de la suppression des localisations", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            });
            }


            //EXPORT BUTTON
            Button exportButton = findViewById(R.id.exportButton);

            exportButton.setOnClickListener(v -> {
                if (tripId == null || tripId.isEmpty()) {
                    Toast.makeText(this, "Aucun voyage sélectionné", Toast.LENGTH_SHORT).show();
                    return;
                }

                locationRepository.getLocationsByTripId(tripId, locations -> {
                    if (locations == null || locations.isEmpty()) {
                        Toast.makeText(this, "Aucune localisation à exporter", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] formats = {"GPX", "KML"};
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Choisir le format d'exportation")
                            .setItems(formats, (dialog, whichFormat) -> {
                                String extension = (whichFormat == 0) ? ".gpx" : ".kml";
                                String fileName = "travel_" + tripId + extension;
                                TravelExporter exporter = new TravelExporter(locations);

                                // ⚙️ Deuxième popup : Partager ou Télécharger
                                String[] actions = {"Partager", "Télécharger"};
                                new androidx.appcompat.app.AlertDialog.Builder(this)
                                        .setTitle("Action à effectuer")
                                        .setItems(actions, (dialog2, whichAction) -> {
                                            try {
                                                // Générer le fichier
                                                if (extension.equals(".gpx")) {
                                                    exporter.exportAsGpx(this, fileName);
                                                } else {
                                                    exporter.exportAsKml(this, fileName);
                                                }

                                                if (whichAction == 0) {
                                                    // 📤 Partager
                                                    exporter.sendFileByEmail(this, fileName, "");
                                                } else {
                                                    // 💾 Télécharger
                                                    Toast.makeText(this, "Fichier enregistré dans :\n" + getExternalFilesDir(null) + "/" + fileName, Toast.LENGTH_LONG).show();
                                                }

                                            } catch (IOException e) {
                                                Toast.makeText(this, "Erreur export : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        })
                                        .setNegativeButton("Annuler", null)
                                        .show();
                            })
                            .setNegativeButton("Annuler", null)
                            .show();
                });
            });



            dateStartText2.setText(formatDate(dateStart));
            dateStartText3.setText(formatDate(dateEnd));
        }

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
                            color = BitmapDescriptorFactory.HUE_AZURE;
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
                    distanceText.setText(String.format(Locale.FRANCE, "%.0f Km", totalDistanceKm));

                    mMap.addPolyline(polylineOptions);

                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (UserLocation loc : locations) {
                        boundsBuilder.include(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                    LatLngBounds bounds = boundsBuilder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                } else {
                    Log.w("MAP", "Aucune localisation à afficher");
                }
            });
        } else {
            Log.e("MAP", "tripId invalide !");
        }
    }


    private String formatDate(String rawDate) {
        try {
            DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            DateTimeFormatter output = DateTimeFormatter.ofPattern("dd/MM/yy HH'h'mm", Locale.FRANCE);
            return LocalDateTime.parse(rawDate, input).format(output);
        } catch (Exception e) {
            return rawDate;
        }
    }
}