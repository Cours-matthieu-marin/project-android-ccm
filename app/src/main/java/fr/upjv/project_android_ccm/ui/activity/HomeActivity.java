package fr.upjv.project_android_ccm.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import intent
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.upjv.project_android_ccm.R;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout tripListContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.layout_activity_home);

//Importation Menu
        ImageButton homeBtn = findViewById(R.id.Homebutton);
        ImageButton settingsBtn = findViewById(R.id.Settingsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        settingsBtn.setOnClickListener(v -> {
            //layout settings
        });
//Fin importation Menu

        tripListContainer = findViewById(R.id.tripListContainer);
        inflater = LayoutInflater.from(this);

        // Ajoute des voyages en mode test
        addTrip("Voyage Maadriiiiiid", "1");
        addTrip("Voyage Londres", "2");
    }

    private void addTrip(String tripName, String tripId) {
        // Créer dynamiquement un parent ConstraintLayout
        ConstraintLayout parentLayout = new ConstraintLayout(this);
        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(81)
        ));

        // Gonfler le layout tripbutton
        View tripButton = inflater.inflate(R.layout.tripbutton, parentLayout, false);

        // Ajouter la vue gonflée au parent
        parentLayout.addView(tripButton);

        // Modifier les éléments du layout
        TextView tripText = tripButton.findViewById(R.id.TripNameText);
        tripText.setText(tripName);

        ConstraintLayout clickableTrip = tripButton.findViewById(R.id.constraintLayout3);
        clickableTrip.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TripDetailActivity.class);
            intent.putExtra("tripId", tripId);
            startActivity(intent);
        });


        // Ajouter au conteneur final
        tripListContainer.addView(parentLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
