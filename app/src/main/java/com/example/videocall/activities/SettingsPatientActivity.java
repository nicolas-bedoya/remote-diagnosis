package com.example.videocall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videocall.R;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SettingsPatientActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_patient);

        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.EditPatientProfileCardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPatientIntent = new Intent(getApplicationContext(), EditAccountActivity.class);
                startActivity(editPatientIntent);
            }
        });

        findViewById(R.id.ViewPersonalDataCardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPersonalDataIntent = new Intent(getApplicationContext(), PersonalDataActivity.class);
                startActivity(viewPersonalDataIntent);
            }
        });

    }

}
