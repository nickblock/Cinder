package org.libcinder.sample.locationmanagerapp;

import android.os.Bundle;

import org.libcinder.app.CinderNativeActivity;

public class LocationManagerAppActivity extends CinderNativeActivity {
    static final String TAG = "LocationManagerAppActivity";

    static {
        System.loadLibrary("LocationManagerApp");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.enableLocationManager();

        super.onCreate(savedInstanceState);
    }
}
