package com.example.videocall.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MainService extends Service {

    private final IBinder MainUserBinder = new LocalBinder();

    public MainService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class LocalBinder extends Binder {
        MainService getService () {
            return MainService.this;
        }
    }
}
