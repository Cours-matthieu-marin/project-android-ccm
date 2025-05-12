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
import java.util.function.Consumer;

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

    public LiveData<List<Travel>> getUnfinishedTravelsByUser(String userId, Consumer<List<Travel>> callback) {
        MutableLiveData<List<Travel>> travelListData = new MutableLiveData<>();
        Log.d("repo", "1 - Récupération des voyages pour l'utilisateur : " + userId);

        if (userId == null || userId.isEmpty()) {
            Log.e("repo", "L'ID utilisateur est invalide !");
            travelListData.setValue(null);  // Retourne null si l'ID utilisateur est invalide
            return travelListData;
        }

        db.collection(travelsCollection)
                .whereEqualTo("idUser", userId)
                .whereEqualTo("dateEnd", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("repo", "2 - Récupération réussie des voyages. Nombre de documents : " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("repo", "Aucun voyage trouvé pour l'utilisateur avec ID : " + userId);
                        travelListData.setValue(new ArrayList<>());  // Retourne une liste vide si aucun voyage n'est trouvé
                        return;
                    }

                    List<Travel> travels = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Travel travel = doc.toObject(Travel.class);
                        if (travel != null) {
                            travels.add(travel);
                        } else {
                            Log.w("repo", "Voyage null détecté dans les résultats pour l'utilisateur avec ID : " + userId);
                        }
                    }
                    Log.d("repo", "3 - Voyages récupérés : " + travels.size());
                    travelListData.setValue(travels);

                    // Appelle le callback avec les voyages récupérés
                    callback.accept(travels);
                })
                .addOnFailureListener(e -> {
                    Log.e("repo", "Erreur lors de la récupération des voyages pour l'utilisateur : " + userId, e);
                    travelListData.setValue(null);  // Retourne null en cas d'erreur
                    callback.accept(null);  // Appelle le callback avec null en cas d'échec
                });

        return travelListData;
    }


    public void addTravel(Travel travel) {
        db.collection(travelsCollection).add(travel);
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
