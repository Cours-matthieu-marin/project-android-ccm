package fr.upjv.project_android_ccm.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.upjv.project_android_ccm.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
//        UserRepository userRepo = new UserRepository();
//        TravelRepository travelRepository = new TravelRepository();
//        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//        LiveData<User> userLiveData = userRepo.getUser(sharedPreferences.getString("email", null));
//
//        userLiveData.observe(this, user -> {
//            assert user.getId() != null;
//            Travel travel = new Travel("test travel", user.getId(), LocalDateTime.now().toString(), null);
//            travelRepository.addTravel(travel);
//
//            Intent serviceIntent = new Intent(this, LocationService.class);
//            ContextCompat.startForegroundService(this, serviceIntent);
//        });


//Fin importation Menu
    }
}