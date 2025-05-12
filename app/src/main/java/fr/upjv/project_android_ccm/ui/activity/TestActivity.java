package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import java.time.LocalDateTime;
import java.util.Date;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;
import fr.upjv.project_android_ccm.service.LocationService;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        UserRepository userRepo = new UserRepository();
        TravelRepository travelRepository = new TravelRepository();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        LiveData<User> userLiveData = userRepo.getUser(sharedPreferences.getString("email", null));

        userLiveData.observe(this, user -> {
            assert user.getId() != null;
            Travel travel = new Travel("test travel", user.getId(), LocalDateTime.now().toString(), null);
            travelRepository.addTravel(travel);

            Intent serviceIntent = new Intent(this, LocationService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        });

    }
}