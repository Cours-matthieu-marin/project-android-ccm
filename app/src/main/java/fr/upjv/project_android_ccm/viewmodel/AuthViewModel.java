package fr.upjv.project_android_ccm.viewmodel;

import androidx.lifecycle.ViewModel;
import fr.upjv.project_android_ccm.data.repository.FirebaseAuthRepository;

public class AuthViewModel extends ViewModel {
    private FirebaseAuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new FirebaseAuthRepository();
    }

    public void registerUser(String email, String password, FirebaseAuthRepository.OnAuthCompleteListener listener) {
        authRepository.registerUser(email, password, listener);
    }

    public void loginUser(String email, String password, FirebaseAuthRepository.OnAuthCompleteListener listener) {
        authRepository.loginUser(email, password, listener);
    }

    public void logout() {
        authRepository.logout();
    }
}
