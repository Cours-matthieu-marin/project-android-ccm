package fr.upjv.project_android_ccm.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.model.UserLocation;
import fr.upjv.project_android_ccm.data.repository.LocationRepository;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;

public class LocationService extends Service {

    private ScheduledExecutorService executorService;
    private LocationManager locationManager;
    private boolean isRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadScheduledExecutor();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!checkRequirement()) {
            return START_STICKY;
        }

        isRunning = true;
        executorService.scheduleWithFixedDelay(() -> {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    UserLocation locationObject = new UserLocation(latitude, longitude);
                    saveLocation(locationObject);
                } else {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new android.location.LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            UserLocation locationObject = new UserLocation(latitude, longitude);
                            saveLocation(locationObject);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {}

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {}
                    }, null);
                }

            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }, 0, 5, TimeUnit.MINUTES);

        createNotification();
        return START_STICKY;
    }

    private void createNotification() {
        String channelId = "456";
        int notificationId = 123;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "marcopologo";
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build();

        startForeground(notificationId, notification);
    }

    private void saveLocation(UserLocation locationData){
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        UserRepository userRepository = new UserRepository();
        TravelRepository travelRepository = new TravelRepository();
        LocationRepository locationRepository = new LocationRepository();
        LiveData<User> user = userRepository.getUser(Objects.requireNonNull(sharedPreferences.getString("email", null)));
        LiveData<List<Travel>>  travels = travelRepository.getUnfinishedTravelsByUser(Objects.requireNonNull(user.getValue()).getId());

        for (Travel travel : Objects.requireNonNull(travels.getValue())){
            locationRepository.addLocation(
                    new UserLocation(
                            locationData.getLatitude(),
                            locationData.getLongitude(),
                            LocalDateTime.now(),
                            travel.getId()
                    )
            );
        }
    }

    private boolean checkRequirement() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        boolean fineLocationPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (fineLocationPermission) {
            Log.d("CheckRequirement", "Fine location permission is granted");
        }

        boolean coarseLocationPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (coarseLocationPermission) {
            Log.d("CheckRequirement", "Coarse location permission is granted");
        }

        boolean internetPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        if (internetPermission) {
            Log.d("CheckRequirement", "Internet permission is granted");
        }

        String email = sharedPreferences.getString("email", null);
        boolean emailNotExist = (email == null) || email.isBlank() || email.isEmpty();
        if (!emailNotExist) {
            Log.d("CheckRequirement", "Email exists and is valid: " + email);
        }

        if (!isRunning) {
            Log.d("CheckRequirement", "Service is not running");
        }

        return internetPermission && fineLocationPermission && coarseLocationPermission && !emailNotExist && !isRunning;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
