package com.example.videocall.activities;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videocall.R;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class EditAccountActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword,
            inputMedicalCenter;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;

    boolean firstNameChanged = false, lastNameChanged = false, emailChanged = false,
    passwordChanged = false, medicalCenterChanged = false, rejected = false;

    String firstName, lastName, email, password, confirmPassword, medicalCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);


        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditAccountActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to leave this page? Make sure your changes are saved!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        findViewById(R.id.buttonConfirmEditAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePersonalDetails();
            }
        });

        inputFirstName = findViewById(R.id.inputFirstName);
        inputFirstName.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME));

        inputLastName = findViewById(R.id.inputLastName);
        inputLastName.setText(preferenceManager.getString(Constants.KEY_LAST_NAME));

        inputEmail = findViewById(R.id.inputEmail);
        inputEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));

        inputPassword = findViewById(R.id.inputPassword);
        inputPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));

        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputConfirmPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));

        inputMedicalCenter = findViewById(R.id.inputMedicalCenter);
        inputMedicalCenter.setText(preferenceManager.getString(Constants.KEY_MEDICAL_CENTER));

        signUpProgressBar = findViewById(R.id.signUpProgressBar);

    }

    private void ChangePersonalDetails() {
        rejected = false; firstNameChanged = false; lastNameChanged = false; emailChanged = false; passwordChanged = false;
        medicalCenterChanged = false;

        firstName = inputFirstName.getText().toString();
        Log.d("EditAccount", "edit input text " + firstName + " " +
                "database name " + preferenceManager.getString(Constants.KEY_FIRST_NAME) +
                " " + !Objects.equals(preferenceManager.getString(Constants.KEY_FIRST_NAME), firstName));

        lastName = inputLastName.getText().toString();
        medicalCenter = inputMedicalCenter.getText().toString();
        password = inputPassword.getText().toString();
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();
        confirmPassword = inputConfirmPassword.getText().toString();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();

        // ADD IF CONDITION FOR EACH OF THE EDIT FIELDS
        // if the first name has changed, update database
        if (!Objects.equals(preferenceManager.getString(Constants.KEY_FIRST_NAME), firstName)) {
            updates.put(Constants.KEY_FIRST_NAME, FieldValue.delete());
            Log.d("EditAccount", "firstNameChangedConfirmed");
            firstNameChanged = true;
        }

        if (!lastName.equals(preferenceManager.getString(Constants.KEY_LAST_NAME))) {
            updates.put(Constants.KEY_LAST_NAME, FieldValue.delete());
            lastNameChanged = true;
        }

        if (!email.equals(preferenceManager.getString(Constants.KEY_EMAIL))) {
            updates.put(Constants.KEY_EMAIL, FieldValue.delete());
            emailChanged = true;
        }

        if (!medicalCenter.equals(preferenceManager.getString(Constants.KEY_FIRST_NAME))) {
            updates.put(Constants.KEY_MEDICAL_CENTER, FieldValue.delete());
            medicalCenterChanged = true;
        }

        if (!password.equals(preferenceManager.getString(Constants.KEY_PASSWORD))) {
            if (Objects.equals(password, confirmPassword)) {
                updates.put(Constants.KEY_PASSWORD, FieldValue.delete());
                passwordChanged = true;
            }
            else {
                Toast.makeText(EditAccountActivity.this, "Ensure password is written correctly both occasions!", Toast.LENGTH_SHORT).show();
                rejected = true;
            }
        }

        if (!rejected) {
            documentReference.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (firstNameChanged) {
                                preferenceManager.putString(Constants.KEY_FIRST_NAME, firstName);
                                Log.d("EditAccount", "firstNameChanged is true");
                                documentReference.update(Constants.KEY_FIRST_NAME, firstName)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditAccountActivity.this, "Unable to change first name", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (lastNameChanged) {
                                preferenceManager.putString(Constants.KEY_LAST_NAME, lastName);
                                documentReference.update(Constants.KEY_LAST_NAME, lastName)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditAccountActivity.this, "Unable to change last name", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (medicalCenterChanged) {
                                preferenceManager.putString(Constants.KEY_MEDICAL_CENTER, medicalCenter);
                                documentReference.update(Constants.KEY_MEDICAL_CENTER, medicalCenter)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditAccountActivity.this, "Unable to change medicalCenter", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (passwordChanged) {
                                preferenceManager.putString(Constants.KEY_PASSWORD, password);
                                documentReference.update(Constants.KEY_PASSWORD, password)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditAccountActivity.this, "Unable to change password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (emailChanged) {
                                preferenceManager.putString(Constants.KEY_EMAIL, email);
                                documentReference.update(Constants.KEY_EMAIL, email)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditAccountActivity.this, "Unable to change email", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            Toast.makeText(EditAccountActivity.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
