package fr.upjv.project_android_ccm.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.upjv.project_android_ccm.data.model.UserLocation;

public class LocationRepository {

    private FirebaseFirestore db;
    private String locationsCollection = "locations";

    public LocationRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<UserLocation> getLocation(String locationId) {
        final MutableLiveData<UserLocation> locationData = new MutableLiveData<>();

        db.collection(locationsCollection).document(locationId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserLocation location = documentSnapshot.toObject(UserLocation.class);
                            locationData.setValue(location);
                        } else {
                            locationData.setValue(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        locationData.setValue(null);
                    }
                });

        return locationData;
    }


    public void addLocation(UserLocation location) {
        Log.d("location", "ça enregistre ici");
        db.collection(locationsCollection).add(location);
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
