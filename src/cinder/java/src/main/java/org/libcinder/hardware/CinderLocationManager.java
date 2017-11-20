package org.libcinder.hardware;

import android.app.Activity;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import org.libcinder.Cinder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class CinderLocationManager {

  static final String TAG = "CinderLocationManager";

  private FusedLocationProviderClient mFusedLocationClient;

  public CinderLocationManager(Activity activity) {
      
      Log.i(TAG, "Construct");
      
      mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
      mFusedLocationClient.getLastLocation()
        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                  updateLocation(location);
                }
            }
        });
  }

  private void updateLocation(Location location) {
    
    updateLocation(
      location.getLongitude(),
      location.getLatitude(),
      location.getBearing(),
      location.getAltitude()
    );
  }

  private native void updateLocation(double lon, double lat, double bearing, double altitude);
}