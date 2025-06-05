package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;

public class FriendTravelActivity extends AppCompatActivity {

    private LinearLayout tripListContainer;
    private LayoutInflater inflater;
    private String requiredIdFriend;
    private String pseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        Intent ActualIntent = getIntent();
        requiredIdFriend = ActualIntent.getStringExtra("friendId");

        if (requiredIdFriend == null) {
            throw new IllegalArgumentException("Missing required argument: friendId");
        }
        setContentView(R.layout.activity_friend_travel);

        //Importation Menu
        ImageButton homeBtn = findViewById(R.id.Homebutton);
        ImageButton friendsButton = findViewById(R.id.Friendsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FriendTravelActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        friendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendTravelActivity.this, FriendListActivity.class);
            startActivity(intent);
        });
//Fin importation Menu

        TextView titleFriendTravel = findViewById(R.id.titleFriendTravel);

        UserRepository userRepository = new UserRepository();
        LiveData<User> userLiveData = userRepository.getUserById(requiredIdFriend);

        userLiveData.observe(this, user -> {
            if (user != null && user.getPseudo() != null) {
                pseudo = user.getPseudo().trim();
                System.out.println("bjr");
                System.out.println("Pseudo : [" + pseudo + "]");
                System.out.println("Longueur : " + pseudo.length());
                String titleFriendTravelText = "Les voyages de " + pseudo;
                titleFriendTravel.setText(titleFriendTravelText);
            }
        });

        tripListContainer = findViewById(R.id.tripListContainer);
        inflater = LayoutInflater.from(this);

        TravelRepository travelRepository = new TravelRepository();
        travelRepository.getTravelsByUser(requiredIdFriend, travels -> {
            if (travels != null) {
                for (Travel travel : travels) {
                    addTrip(travel.getName(), travel.getId(), travel.getDateEnd(), travel.getDateStart());
                }
            } else {
                Toast.makeText(this, "Aucun voyage trouvé", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addTrip(String tripName, String tripId, String dateEnd, String dateStart) {
        ConstraintLayout parentLayout = new ConstraintLayout(this);
        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(81)
        ));

        View tripButton = inflater.inflate(R.layout.tripbutton, parentLayout, false);
        parentLayout.addView(tripButton);

        ConstraintLayout tripButtonLayout = tripButton.findViewById(R.id.constraintLayout3);
        String tripStatus = "ongoing";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        try {
            LocalDateTime now = LocalDateTime.now();

            if (dateStart != null && !dateStart.isEmpty()) {
                LocalDateTime startDate = LocalDateTime.parse(dateStart.substring(0, 19), formatter);

                if (dateEnd != null && !dateEnd.isEmpty()) {
                    LocalDateTime endDate = LocalDateTime.parse(dateEnd.substring(0, 19), formatter);

                    if (now.isBefore(startDate)) {
                        tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyageboutonfutur));
                        tripStatus = "future";
                    } else if (now.isAfter(endDate)) {
                        tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyageboutonfin));
                        tripStatus = "finished";
                    } else {
                        tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyagebutton));
                        tripStatus = "ongoing";
                    }
                } else {
                    if (now.isBefore(startDate)) {
                        tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyageboutonfutur));
                        tripStatus = "future";
                    } else {
                        tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyagebutton));
                        tripStatus = "ongoing";
                    }
                }
            } else {
                tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyageboutonfin));
                tripStatus = "finished";
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            tripButtonLayout.setBackgroundColor(getResources().getColor(R.color.voyagebutton));
            tripStatus = "ongoing";
        }

        TextView tripText = tripButton.findViewById(R.id.TripNameText);
        tripText.setText(tripName);

        if (!tripStatus.equals("future")) {
            String finalTripStatus = tripStatus;
            ConstraintLayout clickableTrip = tripButton.findViewById(R.id.constraintLayout3);
            clickableTrip.setOnClickListener(v -> {
                Intent intent = new Intent(FriendTravelActivity.this, TripDetailActivity.class);
                intent.putExtra("tripId", tripId);
                intent.putExtra("isFriendTrip", true);
                intent.putExtra("tripName", tripName);
                intent.putExtra("dateEnd", dateEnd);
                intent.putExtra("dateStart", dateStart);
                intent.putExtra("tripStatus", finalTripStatus);
                intent.putExtra("friendName", pseudo);
                startActivity(intent);
            });
        }

        tripListContainer.addView(parentLayout);
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}