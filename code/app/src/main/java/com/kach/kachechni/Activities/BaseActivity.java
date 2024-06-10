package com.kach.kachechni.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.kach.kachechni.R;
import com.kach.kachechni.databinding.ActivityDetailBinding;

public class BaseActivity extends AppCompatActivity {

    // Firebase Database instance
    FirebaseDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of Firebase Database
        database = FirebaseDatabase.getInstance();

    }
}
