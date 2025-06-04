package fr.upjv.project_android_ccm.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
            Intent intent = new Intent(HomeActivity.this, FriendListActivity.class);
            startActivity(intent);
        });
//Fin importation Menu


        ConstraintLayout addTravelButton = findViewById(R.id.constraintLayout2);

        addTravelButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddNewTravelActivity.class);
            startActivity(intent);
        });

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
                        addTrip(travel.getName(), travel.getId() , travel.getDateEnd() , travel.getDateStart());
                    }
                } else {
                    Toast.makeText(this, "Aucun voyage trouvé", Toast.LENGTH_SHORT).show();
                }});

        });

    }

    private void addTrip(String tripName, String tripId , String dateEnd , String dateStart) {
        ConstraintLayout parentLayout = new ConstraintLayout(this);
        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(81)
        ));

        View tripButton = inflater.inflate(R.layout.tripbutton, parentLayout, false);

        parentLayout.addView(tripButton);
        ConstraintLayout tripButtonLayout = tripButton.findViewById(R.id.constraintLayout3);
        //change backgroundcolor if trip is finish if date end is not null ou que la date end est passé

        TextView tripText = tripButton.findViewById(R.id.TripNameText);
        tripText.setText(tripName);

        ConstraintLayout clickableTrip = tripButton.findViewById(R.id.constraintLayout3);
        clickableTrip.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TripDetailActivity.class);
            intent.putExtra("tripId", tripId);
            intent.putExtra("isFriendTrip", false);
            startActivity(intent);
        });

        tripListContainer.addView(parentLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
