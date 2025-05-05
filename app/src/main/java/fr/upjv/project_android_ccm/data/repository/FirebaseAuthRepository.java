package fr.upjv.project_android_ccm.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthRepository {

    private FirebaseAuth auth;

    public FirebaseAuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registerUser(String email, String password, OnAuthCompleteListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
    }

    public void loginUser(String email, String password, OnAuthCompleteListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
    }

    public void logout() {
        auth.signOut();
    }

    public interface OnAuthCompleteListener {
        void onComplete(boolean success);
    }
}
