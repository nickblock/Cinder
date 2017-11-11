package org.libcinder.locationmanager;

import android.location.LocationManager;

import org.libcinder.Cinder;


public class CinderLocationManager {

  private LocationManager mLocationManager = null;

  public CinderLocationManager() {

    Log.i(TAG, "enable_location_services");

    mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

  }
}