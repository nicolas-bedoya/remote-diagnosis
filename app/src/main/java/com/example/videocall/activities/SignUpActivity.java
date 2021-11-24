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

public class SignUpActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword,
    inputMedicalCenter;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;
    private CheckBox checkDoctor, checkPatient;
    boolean isDoctor = false, isPatient = false;
    int initialMedicalValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.textSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);
        checkDoctor = findViewById(R.id.doctorCheckBox);
        checkPatient = findViewById(R.id.patientCheckBox);
        inputMedicalCenter = findViewById(R.id.inputMedicalCenter);

        checkDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    checkPatient.setChecked(false);
                    isDoctor = true;
                    isPatient = false;
                    Log.d("signUp", "checkDoctor " + isDoctor);
                } else {
                    isDoctor = false;
                    isPatient = false;
                }
            }
        });

        checkPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    checkDoctor.setChecked(false);
                    isPatient = true;
                    isDoctor = false;
                    Log.d("signUp", "checkPatient " + isPatient);
                } else {
                    isPatient = false;
                    isDoctor = false;
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputFirstName.toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                } else if (inputLastName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
                } else if (inputEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else if (inputMedicalCenter.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter medical center you attend", Toast.LENGTH_SHORT).show();
                }else if (!isDoctor && !isPatient) {
                    Toast.makeText(SignUpActivity.this, "Please select from Patient or Doctor", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                    Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                } else if (inputPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Confirm your password", Toast.LENGTH_SHORT).show();
                } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Password & confirm password must be the same", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }
            }
        });
    }

    private void signUp() {

        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());
        user.put(Constants.KEY_DOCTOR, String.valueOf(isDoctor));
        user.put(Constants.KEY_PATIENT, String.valueOf(isPatient));
        user.put(Constants.KEY_MEDICAL_CENTER, inputMedicalCenter.getText().toString());

        if (isPatient) {
            user.put(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION, String.valueOf(initialMedicalValue));
            user.put(Constants.KEY_HEART_RATE, String.valueOf(initialMedicalValue));
            user.put(Constants.KEY_TEMPERATURE, String.valueOf(initialMedicalValue));
            user.put(Constants.KEY_OVERALL_HEALTH_STATUS, String.valueOf(initialMedicalValue));
        }


        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                        preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                        preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());
                        preferenceManager.putString(Constants.KEY_DOCTOR, String.valueOf(isDoctor));
                        preferenceManager.putString(Constants.KEY_PATIENT, String.valueOf(isPatient));
                        preferenceManager.putString(Constants.KEY_MEDICAL_CENTER, inputMedicalCenter.getText().toString());

                        if (isPatient) {
                            preferenceManager.putString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION, String.valueOf(initialMedicalValue));
                            preferenceManager.putString(Constants.KEY_HEART_RATE, String.valueOf(initialMedicalValue));
                            preferenceManager.putString(Constants.KEY_TEMPERATURE, String.valueOf(initialMedicalValue));
                            preferenceManager.putString(Constants.KEY_OVERALL_HEALTH_STATUS, String.valueOf(initialMedicalValue));
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                        buttonSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
