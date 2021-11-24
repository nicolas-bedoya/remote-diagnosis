package com.example.videocall.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videocall.R;
import com.example.videocall.activities.PersonalDataActivity;
import com.example.videocall.listeners.UsersListener;
import com.example.videocall.models.User;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private List<User> users;
    private UsersListener usersListener;
    private List<User> selectedUsers;

    public UsersAdapter(List<User> users, UsersListener usersListener){
        this.users = users;
        this.usersListener = usersListener;
        selectedUsers = new ArrayList<>();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textFirstChar, textUsername, textEmail;
        ImageView imageAudioCall, imageVideoCall, imageViewData;
        ConstraintLayout userContainer;
        ImageView imageSelected;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioCall = itemView.findViewById(R.id.imageAudioCall);
            imageVideoCall = itemView.findViewById(R.id.imageVideoCall);
            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);
            imageViewData = itemView.findViewById(R.id.imageViewData);
        }

        void setUserData(User user) {
            textFirstChar.setText(user.firstName.substring(0,1));
            Timber.d("doctor status: %s", user.isDoctor);
            if (user.isDoctor.equals("false")) {
                textUsername.setText(String.format("%s %s", user.firstName, user.lastName));

                if (user.overallHealthStatus.equals("0")) {
                    textFirstChar.setBackgroundColor(Color.parseColor("#3ddc84"));
                } else if (user.overallHealthStatus.equals("1")) {
                    textFirstChar.setBackgroundColor(Color.parseColor("#ffd700"));
                } else if (user.overallHealthStatus.equals("2")) {
                    textFirstChar.setBackgroundColor(Color.parseColor("#b22222"));
                }

            } else {
                textUsername.setText(String.format("Dr " + "%s %s", user.firstName, user.lastName));
                imageViewData.setVisibility(View.GONE);
            }
            textEmail.setText(user.email);
            imageAudioCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateAudioCall(user);
                }
            });

            imageVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateVideoCall(user);
                }
            });

            imageViewData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateViewData(user);
                }
            });

            userContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (imageSelected.getVisibility() != View.VISIBLE) {
                        selectedUsers.add(user);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageVideoCall.setVisibility(View.GONE);
                        imageAudioCall.setVisibility(View.GONE);
                        imageViewData.setVisibility(View.GONE);
                        textFirstChar.setBackgroundColor(Color.parseColor("#FF5722"));
                        usersListener.onMultipleUsersAction(true);
                    }
                    return true;
                }
            });

            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageSelected.getVisibility() == View.VISIBLE) {
                        selectedUsers.remove(user);
                        imageSelected.setVisibility(View.GONE);
                        imageVideoCall.setVisibility(View.VISIBLE);
                        imageAudioCall.setVisibility(View.VISIBLE);
                        imageViewData.setVisibility(View.VISIBLE);
                        if (selectedUsers.size() == 0) {
                            usersListener.onMultipleUsersAction(false);
                        } else {
                            if (selectedUsers.size() > 0) {
                                selectedUsers.add(user);
                                imageSelected.setVisibility(View.VISIBLE);
                                imageVideoCall.setVisibility(View.GONE);
                                imageAudioCall.setVisibility(View.GONE);
                                imageViewData.setVisibility(View.GONE);
                                textFirstChar.setBackgroundColor(Color.parseColor("#FF5722"));
                            }
                        }
                    }
                }
            });
        }
    }
}
