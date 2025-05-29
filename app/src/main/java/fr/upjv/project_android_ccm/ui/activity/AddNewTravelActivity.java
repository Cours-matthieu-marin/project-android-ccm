package fr.upjv.project_android_ccm.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.model.UserLocation;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;

public class AddNewTravelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_new_travel);

        EditText travelName = findViewById(R.id.editTextText_nom_du_voya);
        EditText startDate = findViewById(R.id.editTextDate_date_de_d_b);
        CheckBox startNow = findViewById(R.id.checkBox_date_de_d_b);
        EditText endDate = findViewById(R.id.editTextDate_date_de_fin);
        Button createTravel = findViewById(R.id.createTravelButton);

        //Importation Menu
        ImageButton homeBtn = findViewById(R.id.Homebutton);
        ImageButton friendsButton = findViewById(R.id.Friendsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AddNewTravelActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        friendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddNewTravelActivity.this, FriendListActivity.class);
            startActivity(intent);
        });
//Fin importation Menu

        createTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                UserRepository userRepository = new UserRepository();
                TravelRepository travelRepository = new TravelRepository();

                String name = travelName.getText().toString().trim();
                String start = startDate.getText().toString().trim();
                String end = endDate.getText().toString().trim();

                if (name.isEmpty() || start.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Veuillez remplir au moins le nom et la date de début.", Toast.LENGTH_LONG).show();
                    return;
                }

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                String startIso, endIso = null;
                try {
                    LocalDate startDateParsed = LocalDate.parse(start, inputFormatter);
                    LocalDateTime startDateTime = startDateParsed.atStartOfDay();
                    startIso = startDateTime.format(isoFormatter);

                    if (!end.isEmpty()) {
                        LocalDate endDateParsed = LocalDate.parse(end, inputFormatter);
                        LocalDateTime endDateTime = endDateParsed.atStartOfDay();
                        endIso = endDateTime.format(isoFormatter);

                        if (startDateParsed.isAfter(endDateParsed)) {
                            Toast.makeText(getApplicationContext(), "La date de début doit être avant la date de fin.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } catch (DateTimeParseException e) {
                    Toast.makeText(getApplicationContext(), "Format de date invalide.", Toast.LENGTH_LONG).show();
                    return;
                }

                String finalEndIso = endIso;
                userRepository.getUser(sharedPreferences.getString("email", null), (User userDb) -> {
                    if (userDb.getId() == null) {
                        Log.e("service", "User is null!");
                        return;
                    }

                    Travel newTravel = new Travel(name, userDb.getId(), startIso, finalEndIso);

                    travelRepository.addTravel(newTravel, (idAdd) -> {
                        if (idAdd) {
                            Toast.makeText(getApplicationContext(), "Voyage créé !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Erreur lors de la création du voyage.", Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }
        });



        startDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        startDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });


        endDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        endDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

        startNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                LocalDate today = LocalDate.now();
                String formattedToday = today.format(inputFormatter);
                startDate.setText(formattedToday);
                startDate.setFocusable(false);
                startDate.setClickable(false);
            } else {
                startDate.setText("");
                startDate.setFocusable(true);
                startDate.setFocusableInTouchMode(true);
                startDate.setClickable(true);
            }
        });
    }
}