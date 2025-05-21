package fr.upjv.project_android_ccm.ui.activity;

import android.content.SharedPreferences;
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
import androidx.lifecycle.LiveData;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;

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
        ImageButton friendsButton = findViewById(R.id.Friendsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        friendsButton.setOnClickListener(v -> {
            //layout settings
        });
//Fin importation Menu

        tripListContainer = findViewById(R.id.tripListContainer);
        inflater = LayoutInflater.from(this);

        UserRepository userRepo = new UserRepository();
        TravelRepository travelRepository = new TravelRepository();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        LiveData<User> userLiveData = userRepo.getUser(sharedPreferences.getString("email", null));

        userLiveData.observe(this, user -> {
            assert user.getId() != null;
             travelRepository.getTravelsByUser(user.getId() , travels -> {
                if (travels != null) {
                    for (Travel travel : travels) {
                        addTrip(travel.getName(), travel.getId());
                    }
                } else {
                    Toast.makeText(this, "Aucun voyage trouvé", Toast.LENGTH_SHORT).show();
                }});

        });

    }

    private void addTrip(String tripName, String tripId) {
        ConstraintLayout parentLayout = new ConstraintLayout(this);
        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(81)
        ));

        View tripButton = inflater.inflate(R.layout.tripbutton, parentLayout, false);

        parentLayout.addView(tripButton);

        TextView tripText = tripButton.findViewById(R.id.TripNameText);
        tripText.setText(tripName);

        ConstraintLayout clickableTrip = tripButton.findViewById(R.id.constraintLayout3);
        clickableTrip.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TripDetailActivity.class);
            intent.putExtra("tripId", tripId);
            startActivity(intent);
        });

        tripListContainer.addView(parentLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
