package fr.upjv.project_android_ccm.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public LiveData<List<User>> getUsersByFriendCodes(List<String> friendCodes) {
        final MutableLiveData<List<User>> usersData = new MutableLiveData<>();

        if (friendCodes == null || friendCodes.isEmpty()) {
            usersData.setValue(Collections.emptyList());
            return usersData;
        }

        db.collection(usersCollection)
                .whereIn("codeAmi", new ArrayList<>(friendCodes))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            users.add(user);
                        }
                    }
                    usersData.setValue(users);
                })
                .addOnFailureListener(e -> usersData.setValue(null));

        return usersData;
    }




    public LiveData<User> getUserById(String userId, Consumer<User> callback) {
        final MutableLiveData<User> userData = new MutableLiveData<>();

        db.collection(usersCollection)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setId(documentSnapshot.getId());
                            callback.accept(user);
                            userData.setValue(user);
                        }
                    } else {
                        Log.e("service", "User not found");
                        callback.accept(null);
                        userData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving user", e);
                    callback.accept(null);
                    userData.setValue(null);
                });

        return userData;
    }



    public void addUser(User user) {
        db.collection(usersCollection).add(user);
    }

    public void updateUser(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            Log.e("Firestore", "Impossible de mettre à jour : ID utilisateur manquant");
            return;
        }

        db.collection(usersCollection)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Utilisateur mis à jour"))
                .addOnFailureListener(e -> Log.e("Firestore", "Erreur lors de la mise à jour", e));
    }

    public interface OnCompleteListener {
        void onComplete(boolean success);
    }
}
