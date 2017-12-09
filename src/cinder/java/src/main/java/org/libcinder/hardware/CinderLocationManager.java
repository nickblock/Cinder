package org.libcinder.hardware;

import android.app.Activity;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import org.libcinder.Cinder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class CinderLocationManager {

  static final String TAG = "CinderLocationManager";

  private FusedLocationProviderClient mFusedLocationClient;
  private Location mLastLocation;
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;


  public CinderLocationManager(Activity activity) {
      
    Log.i(TAG, "Construct");
    
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    mFusedLocationClient.getLastLocation()
      .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            
              if (location != null) {
                updateLocation(location);

              }
          }
      });
    
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                
                updateLocation(location);
            }
        };
    };

    startLocationUpdates();
  }

  public void startLocationUpdates() {
    
    Log.i(TAG, "startLocationUpdates");

    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
            mLocationCallback,
            null /* Looper */);
  }

  private void updateLocation(Location location) {
    
    Log.i(TAG, "updateLocation");

    mLastLocation = location;
    
    updateLocation(
      location.getLongitude(),
      location.getLatitude(),
      location.getBearing(),
      location.getAltitude()
    );
  }

  private native void updateLocation(double lon, double lat, double bearing, double altitude);
}