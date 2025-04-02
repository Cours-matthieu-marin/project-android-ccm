package fr.upjv.project_android_ccm.data.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.upjv.project_android_ccm.data.model.Travel;

public class TravelRepository {

    private FirebaseFirestore db;
    private String travelsCollection = "travels";

    public TravelRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Travel> getTravel(String travelId) {
        final MutableLiveData<Travel> travelData = new MutableLiveData<>();

        db.collection(travelsCollection).document(travelId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Travel travel = documentSnapshot.toObject(Travel.class);
                            travelData.setValue(travel);
                        } else {
                            travelData.setValue(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        travelData.setValue(null);
                    }
                });

        return travelData;
    }

    public void saveTravel(Travel travel, final OnCompleteListener onComplete) {
        db.collection(travelsCollection).document(travel.getId())
                .set(travel, SetOptions.merge())
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
