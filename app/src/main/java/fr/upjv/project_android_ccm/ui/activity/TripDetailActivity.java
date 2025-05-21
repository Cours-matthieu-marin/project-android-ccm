package fr.upjv.project_android_ccm.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fr.upjv.project_android_ccm.R;

public class TripDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        // Récupérer l'ID passé via l'intent
        String tripId = getIntent().getStringExtra("tripId");

        // TEST
        TextView textView = findViewById(R.id.tripIdTextView);
        textView.setText("ID du voyage reçu : " + tripId);
    }
}