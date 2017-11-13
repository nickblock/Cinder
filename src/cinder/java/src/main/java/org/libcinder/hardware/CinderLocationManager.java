package org.libcinder.hardware;

import android.location.LocationManager;
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

  private class LocationListener implements android.location.LocationListener
  {
      Location mLastLocation;

      public LocationListener(String provider)
      {
          Log.i(TAG, "LocationListener " + provider);
          mLastLocation = new Location(provider);
      }

      @Override
      public void onLocationChanged(Location location)
      {
          Log.i(TAG, "onLocationChanged: " + location);
          mLastLocation.set(location);
      }

      @Override
      public void onProviderDisabled(String provider)
      {
          Log.i(TAG, "onProviderDisabled: " + provider);
      }

      @Override
      public void onProviderEnabled(String provider)
      {
          Log.i(TAG, "onProviderEnabled: " + provider);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras)
      {
          Log.i(TAG, "onStatusChanged: " + provider);
      }
  }

  LocationListener[] mLocationListeners;

  public CinderLocationManager(Context activityContext) {
      
      mLocationListeners = new LocationListener[] {
              new LocationListener(LocationManager.GPS_PROVIDER),
              new LocationListener(LocationManager.NETWORK_PROVIDER)
      };
      
      mLocationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
      
//      try {
//        mLocationManager.requestLocationUpdates(
//          LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//          mLocationListeners[1]
//        );
//      } catch (java.lang.SecurityException ex) {
//        Log.i(TAG, "fail to request location update, ignore", ex);
//      } catch (IllegalArgumentException ex) {
//        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//      }
      try {
        mLocationManager.requestLocationUpdates(
          LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
          mLocationListeners[0]
        );
      } catch (java.lang.SecurityException ex) {
        Log.i(TAG, "fail to request location update, ignore", ex);
      } catch (IllegalArgumentException ex) {
        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
      }

      Log.i(TAG, "Construct");

  }
}