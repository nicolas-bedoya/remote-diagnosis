package com.example.videocall.listeners;

import com.example.videocall.models.User;

public interface UsersListener {

    void initiateVideoCall(User user);
    void initiateAudioCall(User user);
    void initiateViewData(User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);


}
