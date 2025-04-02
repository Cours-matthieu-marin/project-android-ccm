package fr.upjv.project_android_ccm.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import fr.upjv.project_android_ccm.data.model.User;

public class UserRepository {

    private FirebaseFirestore db;
    private String usersCollection = "users";

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<User> getUser(String userId) {
        final MutableLiveData<User> userData = new MutableLiveData<>();

        db.collection(usersCollection).document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            userData.setValue(user);
                        } else {
                            userData.setValue(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        userData.setValue(null);
                    }
                });

        return userData;
    }

    public void saveUser(User user, final OnCompleteListener onComplete) {
        db.collection(usersCollection).document(user.getId())
                .set(user, SetOptions.merge())
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
