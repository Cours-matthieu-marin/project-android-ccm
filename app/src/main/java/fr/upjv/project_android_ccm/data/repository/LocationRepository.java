package fr.upjv.project_android_ccm.data.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.upjv.project_android_ccm.data.model.Location;

public class LocationRepository {

    private FirebaseFirestore db;
    private String locationsCollection = "locations";

    public LocationRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Location> getLocation(String locationId) {
        final MutableLiveData<Location> locationData = new MutableLiveData<>();

        db.collection(locationsCollection).document(locationId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Location location = documentSnapshot.toObject(Location.class);
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

    public void saveLocation(Location location, final OnCompleteListener onComplete) {
        db.collection(locationsCollection).document(location.getId())
                .set(location, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onComplete.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        onComplete.onComplete(false);
                    }
                });
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
