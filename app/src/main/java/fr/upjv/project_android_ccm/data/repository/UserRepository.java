package fr.upjv.project_android_ccm.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.function.Consumer;

import fr.upjv.project_android_ccm.data.model.User;

public class UserRepository {

    private FirebaseFirestore db;
    private String usersCollection = "users";

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<User> getUser(String userEmail, Consumer<User> callback) {
        final MutableLiveData<User> userData = new MutableLiveData<>();

        db.collection(usersCollection)
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            callback.accept(user);
                        }
                    } else {
                        Log.e("service", "Utilisateur non trouvé");
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Erreur lors de la récupération de l'utilisateur", e);
                    userData.setValue(null);
                });

        return userData;
    }
public LiveData<User> getUser(String userEmail) {
        final MutableLiveData<User> userData = new MutableLiveData<>();

        db.collection(usersCollection)
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        User user = document.toObject(User.class);
                        assert user != null;
                        user.setId(document.getId());
                        userData.setValue(user);
                    } else {
                        userData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    userData.setValue(null);
                });

        return userData;
    }



    public void addUser(User user) {
        db.collection(usersCollection).add(user);
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
