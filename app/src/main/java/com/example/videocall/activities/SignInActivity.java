package com.example.videocall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videocall.R;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.textSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                    Toast.makeText(SignInActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                } else if (inputPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    signIn();
                }
            }
        });

    }

    private void signIn() {
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                            preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            preferenceManager.putString(Constants.KEY_DOCTOR, documentSnapshot.getString(Constants.KEY_DOCTOR));
                            preferenceManager.putString(Constants.KEY_PATIENT, documentSnapshot.getString(Constants.KEY_PATIENT));
                            preferenceManager.putString(Constants.KEY_MEDICAL_CENTER, documentSnapshot.getString(Constants.KEY_MEDICAL_CENTER));
                            preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));

                            if (preferenceManager.getString(Constants.KEY_DOCTOR).equals("false")) {
                                preferenceManager.putString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION, documentSnapshot.getString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION));
                                preferenceManager.putString(Constants.KEY_HEART_RATE, documentSnapshot.getString(Constants.KEY_HEART_RATE));
                                preferenceManager.putString(Constants.KEY_TEMPERATURE, documentSnapshot.getString(Constants.KEY_TEMPERATURE));
                                preferenceManager.putString(Constants.KEY_OVERALL_HEALTH_STATUS, documentSnapshot.getString(Constants.KEY_OVERALL_HEALTH_STATUS));
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            signInProgressBar.setVisibility(View.INVISIBLE);
                            buttonSignIn.setVisibility(View.VISIBLE);
                            Toast.makeText(SignInActivity.this, "Unable to sign in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

