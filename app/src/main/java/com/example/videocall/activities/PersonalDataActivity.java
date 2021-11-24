package com.example.videocall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videocall.R;
import com.example.videocall.models.User;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class PersonalDataActivity extends AppCompatActivity {

    User user;

    private PreferenceManager preferenceManager;
    TextView txtBloodOxygen;
    TextView txtHeartRate;
    TextView txtTemperature;
    TextView txtOverallHealth;
    TextView txtTitle;

    int heartRate = 0;
    int bloodOxygen = 0;
    int temperature = 0;
    String overallHealth;

    Button butStartDemo;
    boolean sendMedicalData = false;
    boolean getMedicalData = false;

    Handler getDataHandler = new Handler();
    Runnable getDataRunnable;
    int delay = 5*1000; // Delay for 5 seconds

    Handler sendDataHandler = new Handler();
    Runnable sendDataRunnable;

    final boolean[] startDemo = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        preferenceManager = new PreferenceManager(getApplicationContext());

        butStartDemo = (Button)findViewById(R.id.buttonStartDemo);

        txtTitle = (TextView)findViewById(R.id.PersonalDataTitle);
        txtBloodOxygen = (TextView) findViewById(R.id.txtBloodOxygen);
        txtHeartRate = (TextView) findViewById(R.id.txtHeartRate);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        txtOverallHealth = (TextView) findViewById(R.id.txtHealthStatus);

        user = (User) getIntent().getSerializableExtra("user");

        if (preferenceManager.getString(Constants.KEY_DOCTOR).equals("false")) {
            txtTitle.setText("View My Data");
            butStartDemo.setVisibility(View.VISIBLE);

        } else {
            txtTitle.setText(String.format("%s %s", user.firstName, user.lastName));
            getMedicalData();
            //getMedicalData = true;
        }

        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        butStartDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startDemo[0]) {
                    startDemo[0] = true;
                    butStartDemo.setText("Stop Demo");
                    //TODO: CALL THREAD TO INPUT DECREASING VALUES INTO FIREBASE FOR
                    // MEDICAL VALUES
                    sendMedicalData();
                    //sendMedicalData = true;

                } else {
                    butStartDemo.setText("Start Demo");
                    startDemo[0] = false;
                    sendDataHandler.removeCallbacks(sendDataRunnable);
                    //sendMedicalData = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendMedicalData() {
        // sends data to firebase and updates preference manager with the current details
        sendDataHandler.postDelayed(sendDataRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        database.collection(Constants.KEY_COLLECTION_USERS).document(
                                preferenceManager.getString(Constants.KEY_USER_ID)
                        );
                HashMap<String, Object> updates = new HashMap<>();
                heartRate ++;
                bloodOxygen ++;
                temperature ++;

                txtBloodOxygen.setText(String.valueOf(bloodOxygen));
                txtHeartRate.setText(String.valueOf(heartRate));
                txtTemperature.setText(String.valueOf(temperature));

                documentReference.update(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION, String.valueOf(bloodOxygen));
                documentReference.update(Constants.KEY_TEMPERATURE, String.valueOf(temperature));
                documentReference.update(Constants.KEY_HEART_RATE, String.valueOf(heartRate));

                preferenceManager.putString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION, String.valueOf(bloodOxygen));
                preferenceManager.putString(Constants.KEY_TEMPERATURE, String.valueOf(temperature));
                preferenceManager.putString(Constants.KEY_HEART_RATE, String.valueOf(heartRate));

                sendDataHandler.postDelayed(sendDataRunnable, delay);
            }
        }, delay);
    }

    private void getMedicalData() {
        getDataHandler.postDelayed(getDataRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        if (myUserId.equals(documentSnapshot.getId())) {
                                            continue;
                                        }
                                        Log.d("PersonalData", documentSnapshot.getString(Constants.KEY_LAST_NAME));
                                        if (user.firstName.equals(documentSnapshot.getString(Constants.KEY_FIRST_NAME))
                                                        && user.lastName.equals(documentSnapshot.getString(Constants.KEY_LAST_NAME))
                                                        && user.email.equals(documentSnapshot.getString(Constants.KEY_EMAIL))
                                                        && user.medicalCenter.equals(documentSnapshot.getString(Constants.KEY_MEDICAL_CENTER))) {
                                            txtTemperature.setText(documentSnapshot.getString(Constants.KEY_TEMPERATURE ));
                                            txtHeartRate.setText(documentSnapshot.getString(Constants.KEY_HEART_RATE));
                                            txtBloodOxygen.setText(documentSnapshot.getString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION));
                                        }
                                    }
                                }
                            }
                        });
                getDataHandler.postDelayed(getDataRunnable, delay);
            }
        }, delay);
    }

    @Override
    protected void onPause() {
        if (startDemo[0]) {
            sendDataHandler.removeCallbacks(sendDataRunnable);
        } else if (preferenceManager.getString(Constants.KEY_DOCTOR).equals("true")) {
            getDataHandler.removeCallbacks(getDataRunnable);
        }

        Log.d("PersonalData", "onPause called!");
        super.onPause();
    }
}
