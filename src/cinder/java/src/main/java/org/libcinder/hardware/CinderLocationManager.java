package org.libcinder.hardware;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import org.libcinder.Cinder;


public class CinderLocationManager {

  static final String TAG = "CinderLocationManager";
  private static final int LOCATION_INTERVAL = 0;
  private static final float LOCATION_DISTANCE = 0.0f;
  private LocationManager mLocationManager = null;
  private Location mLastLocation = null;
  private long mNativePtr = 0;

  public CinderLocationManager(Context activityContext) {
      
      Log.i(TAG, "Construct");
      
      mLocationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
      mLastLocation = new Location(Context.LOCATION_SERVICE);

      mLastLocation = mLocationManager.getLastKnownLocation(Context.LOCATION_SERVICE);

      LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

          Log.i(TAG, "onLocationChanged: " + location);
          mLastLocation.set(location);
          updateLocation(mLastLocation);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

          Log.i(TAG, "onStatusChanged: " + provider);
        }

        public void onProviderEnabled(String provider) {

          Log.i(TAG, "onProviderEnabled: " + provider);
        }

        public void onProviderDisabled(String provider) {
          
          Log.i(TAG, "onProviderDisabled: " + provider);
        }
      };

      mLocationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 
        LOCATION_INTERVAL, 
        LOCATION_DISTANCE,
        locationListener
      );

      if(mLastLocation != null) {

        updateLocation(mLastLocation);
      }
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