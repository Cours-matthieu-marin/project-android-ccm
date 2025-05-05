package fr.upjv.project_android_ccm.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.viewmodel.AuthViewModel;

public class ConnectionActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        EditText pseudoEditText = findViewById(R.id.pseudonymeEditText);
        EditText passwordEditText = findViewById(R.id.motDePasseEditText);
        Button loginButton = findViewById(R.id.connectionButton);

        loginButton.setOnClickListener(view -> {
            String email = pseudoEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            authViewModel.loginUser(email, password, success -> {
                if (success) {
                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
}
