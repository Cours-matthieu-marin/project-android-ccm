package fr.upjv.project_android_ccm.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                            location.setId(documentSnapshot.getId());
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
        db.collection(locationsCollection).add(location);
    }
    public LiveData<List<UserLocation>> getLocationsByTripId(String tripId, Consumer<List<UserLocation>> callback) {
        MutableLiveData<List<UserLocation>> locationListData = new MutableLiveData<>();

        if (tripId == null || tripId.isEmpty()) {
            locationListData.setValue(null);
            callback.accept(null);
            return locationListData;
        }

        db.collection(locationsCollection)
                .whereEqualTo("idVoyage", tripId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        locationListData.setValue(new ArrayList<>());
                        callback.accept(new ArrayList<>());
                        return;
                    }

                    List<UserLocation> locations = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserLocation location = doc.toObject(UserLocation.class);
                        if (location != null) {
                            location.setId(doc.getId());
                            locations.add(location);
                        }
                    }

                    locations.sort((loc1, loc2) -> loc1.getDate().compareTo(loc2.getDate()));

                    locationListData.setValue(locations);
                    callback.accept(locations);
                })
                .addOnFailureListener(e -> {
                    Log.e("LocationRepository", "Erreur lors de la récupération des localisations pour le voyage : " + tripId, e);
                    locationListData.setValue(null);
                    callback.accept(null);
                });

        return locationListData;
    }
    public void deleteLocationsByTripId(String tripId, Consumer<Boolean> callback) {
        db.collection("locations")
                .whereEqualTo("tripId", tripId)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        doc.getReference().delete();
                    }
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("location", "Erreur lors de la suppression des localisations", e);
                    callback.accept(false);
                });
    }


    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
