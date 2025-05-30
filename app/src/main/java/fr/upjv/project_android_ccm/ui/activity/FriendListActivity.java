package fr.upjv.project_android_ccm.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.Collections;

import fr.upjv.project_android_ccm.R;
import fr.upjv.project_android_ccm.data.model.Travel;
import fr.upjv.project_android_ccm.data.model.User;
import fr.upjv.project_android_ccm.data.repository.TravelRepository;
import fr.upjv.project_android_ccm.data.repository.UserRepository;

public class FriendListActivity extends AppCompatActivity {

    private LinearLayout friendListContainer;
    private LayoutInflater inflater;
    private UserRepository userRepo = new UserRepository();
    private User userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_friend_list);
        friendListContainer = findViewById(R.id.friendListContainer);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        LiveData<User> userLiveData = userRepo.getUser(sharedPreferences.getString("email", null));
        userLiveData.observe(this, user -> {
            userObject = user;
            generateFriendList();

            Button copyButton = findViewById(R.id.copyButton);

            copyButton.setOnClickListener(v -> {
                String textToCopy = userObject.getFriendCode();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "Texte copié", Toast.LENGTH_SHORT).show();
            });

            EditText editTextCodeAmi = findViewById(R.id.editTextText_nom_du_voya);
            ConstraintLayout submitButton = findViewById(R.id.constraintLayout2);

            submitButton.setOnClickListener(v -> {
                String codeAmi = editTextCodeAmi.getText().toString().trim();

                if (!codeAmi.isEmpty()) {
                    userRepo.getUsersByFriendCodes(Collections.singletonList(codeAmi)).observe(this, users -> {
                        if(!users.isEmpty()&& !userObject.getFriendsList().contains(codeAmi)){
                            userObject.addFriendCode(codeAmi);
                            userRepo.updateUser(userObject);
                            generateFriendList();
                        }
                    });
                    Toast.makeText(this, "Code saisi : " + codeAmi, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Veuillez entrer un code d'ami", Toast.LENGTH_SHORT).show();
                }
            });


        });



//Importation Menu
        ImageButton homeBtn = findViewById(R.id.Homebutton);
        ImageButton friendsButton = findViewById(R.id.Friendsbutton);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        friendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, FriendListActivity.class);
            startActivity(intent);
        });
//Fin importation Menu




    }

    private void generateFriendList(){
        reset();
        inflater = LayoutInflater.from(this);
        assert userObject.getFriendsList() != null;
        userRepo.getUsersByFriendCodes(userObject.getFriendsList()).observe(this, users -> {

            for(User friend : users){
                addFriend(friend.getId());
            }
        });
    }

    private void reset() {
        friendListContainer.removeAllViews();
    }


    private void addFriend(String friendId) {
        LiveData<User> userLiveData = userRepo.getUserById(friendId);

        userLiveData.observe(this, user -> {
            ConstraintLayout parentLayout = new ConstraintLayout(this);
            parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(81)
            ));

            View friendButton = inflater.inflate(R.layout.friendbutton, parentLayout, false);

            parentLayout.addView(friendButton);

            TextView friendText = friendButton.findViewById(R.id.FriendNameText);
            friendText.setText(user.getPseudo());

            ConstraintLayout clickableFriend = friendButton.findViewById(R.id.constraintLayout3);
            clickableFriend.setOnClickListener(v -> {
                Intent intent = new Intent(FriendListActivity.this, FriendTravelActivity.class);
                intent.putExtra("friendId", friendId);
                startActivity(intent);
            });

            friendListContainer.addView(parentLayout);
        });


    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}