package com.kach.kachechni.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.kach.kachechni.R;
import com.kach.kachechni.databinding.ActivityEditProfileBinding;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        // Set the content view to the root view of the inflated layout
        setContentView(binding.getRoot());

        // Get SharedPreferences with name "userData"
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        // Get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Get the currently signed-in user
        currentUser = mAuth.getCurrentUser();

        // affichage ( utilisateur data )
        displayUserData();

        // Set click listener for the back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Profile activity
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        // birth date EditText
        binding.editTextBirthDate.addTextChangedListener(birthDateTextWatcher);

        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verif input ( el data el jdida )
                 if (register() == false){
                     return;
                 }

                if (currentUser != null ) {
                    reauthenticateUser();
                }
                else {

                    Toast.makeText(EditProfile.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // Method to reauthenticate the user using their email and password
    private void reauthenticateUser() {
        // Retrieve email from SharedPreferences
        String email = sharedPreferences.getString("email", "");
        // Retrieve password from the password EditText
        String password = binding.editTextPassword.getText().toString();

        //Create AuthCredential object using email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Reauthenticate user with the provided credentials
        currentUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // If reauthentication is successful, update profile data
                        updateProfileData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If reauthentication fails, display an error message and navigate back to Profile activity
                        Toast.makeText(EditProfile.this, "Failed to reauthenticate user", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfile.this, Profile.class);
                        startActivity(intent);
                    }
                });
    }
    // Method to update user profile data in Firebase Realtime Database and SharedPreferences
    private void updateProfileData() {

        // Retrieve updated profile data from EditText fields
        String updatedName = binding.editTextName.getText().toString().trim();
        String updatedLastName = binding.editTextLastName.getText().toString().trim();
        String updatedGender = getSelectedGender().trim();
        String updatedBirth = binding.editTextBirthDate.getText().toString().trim();
        String updatedState = (String) binding.spinnerState.getSelectedItem();
        String updatedAdresse1 = binding.editTextAddress1.getText().toString().trim();
        String updatedAdresse2 =  binding.editTextAddress2.getText().toString().trim();
        String updatedPhone = binding.editTextPhoneNumber.getText().toString().trim();

        // Retrieve stored profile data from SharedPreferences
        String storedName = sharedPreferences.getString("name", "");
        String storedLastName = sharedPreferences.getString("lastName", "");
        String storedGender = sharedPreferences.getString("gender", "");
        String storedBirth = sharedPreferences.getString("birthDate", "");
        String storedState = sharedPreferences.getString("state", "");
        String storedPhone = sharedPreferences.getString("phoneNumber", "");
        String storedAdresse1 = sharedPreferences.getString("addr1", "");
        String storedAdresse2 = sharedPreferences.getString("addr2", "");

        // Create SharedPreferences editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // mise a jour ta3 el user data
        if (!updatedPhone.equals(storedPhone)) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("phoneNumber").setValue(updatedPhone);
            editor.putString("phoneNumber", updatedPhone);
        }

        if (!updatedName.equals(storedName)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("name").setValue(updatedName);
            editor.putString("name", updatedName);
        }
        if (!updatedLastName.equals(storedLastName)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("lastName").setValue(updatedLastName);
            editor.putString("lastName", updatedLastName);
        }
        if (!updatedGender.equals(storedGender)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("gender").setValue(updatedGender);
        }
        if (!updatedBirth.equals(storedBirth)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("birthDate").setValue(updatedBirth);
            editor.putString("birthDate", convertToDashesFormat(updatedBirth));
        }
        if (!updatedState.equals(storedState)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("state").setValue(updatedState);
            editor.putString("state", updatedState);
        }

        if (!updatedAdresse1.equals(storedAdresse1)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("addresse1").setValue(updatedAdresse1);
            editor.putString("addr1", updatedAdresse1);
        }
        if (!updatedAdresse2.equals(storedAdresse2)) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("addresse2").setValue(updatedAdresse2);
            editor.putString("addr2", updatedAdresse2);
        }

        editor.apply();

        // Update update time in Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userRef.child("updateTime").setValue(ServerValue.TIMESTAMP);

        // Display success message and navigate back to Profile activity
        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditProfile.this, Profile.class);
        startActivity(intent);
    }


    private String getSelectedGender() {
        int selectedId = binding.radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonMale) {
            return "Male";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "Female";
        }
        return "";
    }

    // Method to display user data retrieved from SharedPreferences
    private void displayUserData() {
        String name = sharedPreferences.getString("name", "");
        binding.editTextName.setText(name);

        String lastName = sharedPreferences.getString("lastName", "");
        binding.editTextLastName.setText(lastName);

        String phone = sharedPreferences.getString("phoneNumber", "");
        binding.editTextPhoneNumber.setText(phone);

        String birthDate = sharedPreferences.getString("birthDate", "");
        binding.editTextBirthDate.setText(birthDate);

        String gender = sharedPreferences.getString("gender", "");
        if (gender.equals("Male")) {
            binding.radioButtonMale.setChecked(true);
        } else if (gender.equals("Female")) {
            binding.radioButtonFemale.setChecked(true);
        }

        String state = sharedPreferences.getString("state", "");
        // ( 5ater moujouda fi lista )
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spinnerState.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(state);
            binding.spinnerState.setSelection(position);
        }

        String adresse1 = sharedPreferences.getString("addr1", "");
        binding.editTextAddress1.setText(adresse1);

        String adresse2 = sharedPreferences.getString("addr2", "");
        binding.editTextAddress2.setText(adresse2);


    }
    // Method to convert a birth date
    private String convertToSlashesFormat(String dashedBirthDate) {
        // 9assem f tableau date
        String[] parts = dashedBirthDate.split("-");
        if (parts.length != 3) {

            return null;
        }
        int day, month, year;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {

            return null;
        }

        // format (DD/MM/YYYY)
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    private TextWatcher birthDateTextWatcher = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();
        private int currentYear = cal.get(Calendar.YEAR);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", "");
                String cleanC = current.replaceAll("[^\\d.]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }

                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    int day  = Integer.parseInt(clean.substring(0, 2));
                    int month  = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    if (month < 1 || month > 12) {
                        Toast.makeText(EditProfile.this, "Invalid month", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate day based on month and year
                    // Set calendar to the first day of the given month and year
                    cal.set(year, month - 1, 1);
                    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                    if (day < 1 || day > maxDay)
                    {
                        Toast.makeText(EditProfile.this, "Invalid day", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate year
                    if (clean.substring(4, 8).length() < 4)
                    {
                        Toast.makeText(EditProfile.this, "Invalid year", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Limit year
                    if (year < 1900 || year > currentYear)
                    {
                        Toast.makeText(EditProfile.this, "Year must be between 1900 and " + currentYear, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clean = String.format("%02d%02d%04d", day, month, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2), clean.substring(2, 4), clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;


                current = clean;

                binding.editTextBirthDate.setText(current);
                binding.editTextBirthDate.setSelection(sel < current.length() ? sel : current.length());

            }
        }
    };

    public boolean register() {
        // Get the text from all input fields
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
        String birthDate = binding.editTextBirthDate.getText().toString().trim();
        String name = binding.editTextName.getText().toString().trim();
        String lastName = binding.editTextLastName.getText().toString().trim();
        String phoneNumber = binding.editTextPhoneNumber.getText().toString().trim();

        String selectedState = (String) binding.spinnerState.getSelectedItem();
        String gender = getSelectedGender();

        String addr1 = binding.editTextAddress1.getText().toString().trim();
        String addr2 = binding.editTextAddress2.getText().toString().trim();


        if (gender.isEmpty()) {
            Toast.makeText(EditProfile.this, "Choose gender", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!isValidBirthDate(birthDate)) {
            Toast.makeText(EditProfile.this, "Invalid birth date", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            birthDate = convertToDashesFormat(birthDate);
        }




        if (password.length() < 6) {
            Toast.makeText(EditProfile.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!password.equals(confirmPassword)) {
            Toast.makeText(EditProfile.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (name.isEmpty()) {
            Toast.makeText(EditProfile.this, "Enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(EditProfile.this, "Enter your last name", Toast.LENGTH_SHORT).show();
            return false;
        }




        if (phoneNumber.isEmpty() || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            Toast.makeText(EditProfile.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (selectedState.isEmpty() || selectedState.equals("Select State")) {
            Toast.makeText(EditProfile.this, "Select your state", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    private String convertToDashesFormat(String birthDate) {

        String[] parts = birthDate.split("/");
        if (parts.length != 3) {

            return null;
        }
        int day, month, year;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {

            return null;
        }


        return String.format("%02d-%02d-%04d", day, month, year);
    }
    private boolean isValidBirthDate(String birthDate) {

        String[] parts = birthDate.split("/");
        if (parts.length != 3) {

            return false;
        }
        int day, month, year;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {

            return false;
        }


        if (month < 1 || month > 12) {
            return false;
        }


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day < 1 || day > maxDay) {
            return false;
        }


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1900 || year > currentYear) {
            return false;
        }


        return true;
    }

}