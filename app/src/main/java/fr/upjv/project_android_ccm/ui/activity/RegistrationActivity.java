package fr.upjv.project_android_ccm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.repository.UserRepository;
import fr.upjv.project_android_ccm.viewmodel.AuthViewModel;

public class RegistrationActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        EditText pseudoEditText = findViewById(R.id.pseudonymeEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.motDePasseEditText);
        Button registerButton = findViewById(R.id.registerButton);

        Button switchButton = findViewById(R.id.toConnectionButton);

        switchButton.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationActivity.this, ConnectionActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(view -> {
            String pseudo = pseudoEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            authViewModel.registerUser(email, password, success -> {
                if (success) {
                    UserRepository userRepository = new UserRepository();
                    User newUser = new User(email, pseudo, UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());

                    userRepository.addUser(newUser);
                    Toast.makeText(this, "Inscription réussie. Veuillez vous connectez.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
