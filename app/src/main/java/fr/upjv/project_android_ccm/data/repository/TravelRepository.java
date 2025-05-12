package fr.upjv.project_android_ccm.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

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

    public LiveData<List<Travel>> getUnfinishedTravelsByUser(String userId) {
        MutableLiveData<List<Travel>> travelListData = new MutableLiveData<>();
        db.collection(travelsCollection)
                .whereEqualTo("userId", userId)
                .whereEqualTo("endDate", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Travel> travels = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Travel travel = doc.toObject(Travel.class);
                        if (travel != null) {
                            travels.add(travel);
                        }
                    }
                    travelListData.setValue(travels);
                })
                .addOnFailureListener(e -> travelListData.setValue(null));

        return travelListData;
    }


    public void addTravel(Travel travel) {
        db.collection(travelsCollection).add(travel);
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
