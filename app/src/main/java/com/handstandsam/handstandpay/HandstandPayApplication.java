package com.handstandsam.handstandpay;

import android.app.Application;

import timber.log.Timber;

public class HandstandPayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Logging
        Timber.plant(new Timber.DebugTree());

    }


}
