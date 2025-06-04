package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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
        String tripId = getIntent().getStringExtra("tripId");
        String isFriendTrip = getIntent().getStringExtra("isFriendTrip");


        TextView textView = findViewById(R.id.tripIdTextView);
        textView.setText("ID du voyage reçu : " + tripId);

    }
}