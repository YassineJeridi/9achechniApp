package com.kach.kachechni.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kach.kachechni.R;
import com.kach.kachechni.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        displayUserData();
        //el edit
        binding.edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent = new Intent(Profile.this, EditProfile.class);
        startActivity(intent);

    }
});

        binding.Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish();
            }
        });
    }

    private void displayUserData() {
        String name = sharedPreferences.getString("name", "");
        String lastName = sharedPreferences.getString("lastName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phoneNumber", "");
        String birthDate = sharedPreferences.getString("birthDate", "");
        String gender = sharedPreferences.getString("gender", "");
        String state = sharedPreferences.getString("state", "");
        String adresse1 = sharedPreferences.getString("addr1", "");
        String adresse2 = sharedPreferences.getString("addr2", "");

        binding.username.setText(name +" "+lastName);
        binding.nameContent.setText(name);
        binding.mailContent.setText(email);
        binding.phoneContent.setText(phone);
        binding.birthdateContent.setText(birthDate);
        binding.genderContent.setText(gender);
        binding.stateContent.setText(state);
        binding.addr1Content.setText(adresse1);
        binding.addr2Content.setText(adresse2);

    }

    private void logout() {
        // Signing out the user from Firebase authentication
        FirebaseAuth.getInstance().signOut();

        // Clearing user data from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


        Toast.makeText(this,"you logout ",Toast.LENGTH_SHORT);
        Intent intent = new Intent(Profile.this, LoginAccount.class);
        startActivity(intent);
        finish();
    }
}
