package com.example.videocall.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videocall.R;
import com.example.videocall.adapters.UsersAdapter;
import com.example.videocall.listeners.UsersListener;
import com.example.videocall.models.User;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference, imageSettings;

    String userMedicalCenter;
    String personalDoctorString;
    boolean personalIsDoctor = false;

    final Handler handler = new Handler();
    Runnable runnable;
    final int delay = 10*1000;

    // Rating system for colour interpretation of health status
    // green - Healthy values across all three data points
    // yellow - Data of concern with one point
    // red - Data of concern with two points

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());

        // finding the medical center user belongs
        userMedicalCenter = preferenceManager.getString(Constants.KEY_MEDICAL_CENTER);

        // Log.d("MedicalCenter", userMedicalCenter);

        // checking if user is a doctor
        if (preferenceManager.getString(Constants.KEY_DOCTOR).equals("true")) {
            personalIsDoctor = true;
            personalDoctorString = "Dr ";
        }
        else {
            personalIsDoctor = false;
            personalDoctorString = "";
        }

        imageConference = findViewById(R.id.imageConference);
        imageSettings = findViewById(R.id.imageSettings);

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(personalDoctorString +
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });

        imageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personalIsDoctor) {
                    Intent settingIntent = new Intent(getApplicationContext(), SettingsDoctorActivity.class);
                    startActivity(settingIntent);
                }
                else {
                    Intent settingIntent = new Intent(getApplicationContext(), SettingsPatientActivity.class);
                    startActivity(settingIntent);
                }
            }
        });

        RecyclerView usersRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users, this);
        usersRecyclerView.setAdapter(usersAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        getUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(personalDoctorString +
                        "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        getUsers();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void getUsers() {
        Log.d("MainActivity", "iteration of runnable/handler");
        swipeRefreshLayout.setRefreshing(true);
        textErrorMessage.setVisibility(View.GONE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        // finding the medical center user belongs
                        userMedicalCenter = preferenceManager.getString(Constants.KEY_MEDICAL_CENTER);
                        if (task.isSuccessful() && task.getResult() != null) {
                            users.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (myUserId.equals(documentSnapshot.getId())) {
                                    continue;
                                }
                                User user = new User();
                                user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                                user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                                user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                                user.medicalCenter = documentSnapshot.getString(Constants.KEY_MEDICAL_CENTER);
                                user.isDoctor = documentSnapshot.getString(Constants.KEY_DOCTOR);
                                user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);

                                if (personalIsDoctor) {
                                    if (user.medicalCenter.equals(userMedicalCenter) && user.isDoctor.equals("false")) {
                                        user.overallHealthStatus = documentSnapshot.getString(Constants.KEY_OVERALL_HEALTH_STATUS);
                                        user.bloodOxygenConcentration = documentSnapshot.getString(Constants.KEY_BLOOD_OXYGEN_CONCENTRATION);
                                        user.heartRate = documentSnapshot.getString(Constants.KEY_HEART_RATE);
                                        user.temperature = documentSnapshot.getString(Constants.KEY_TEMPERATURE);
                                        users.add(user);
                                    }
                                } else {
                                    if (user.medicalCenter.equals(userMedicalCenter) && user.isDoctor.equals("true")) {
                                        users.add(user);
                                    }
                                }
                            }
                            if (users.size() > 0) {
                                usersAdapter.notifyDataSetChanged();
                            } else {
                                users.clear();
                                usersAdapter.notifyDataSetChanged();
                                textErrorMessage.setText(String.format("%s", "No users available" ));
                                textErrorMessage.setVisibility(View.VISIBLE);
                            }

                        } else {
                            users.clear();
                            usersAdapter.notifyDataSetChanged();
                            textErrorMessage.setText(String.format("%s", "No users available" ));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOut() {
        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clearPreferences();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void initiateVideoCall(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);

        }
    }

    @Override
    public void initiateAudioCall(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void initiateViewData(User user) {
        Intent intent = new Intent(getApplicationContext(), PersonalDataActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("type", "data");
        startActivity(intent);
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                    intent.putExtra("selectedUsers", new Gson().toJson(usersAdapter.getSelectedUsers()));
                    intent.putExtra("type", "video");
                    intent.putExtra("isMultiple", true);
                    startActivity(intent);
                }
            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }

}