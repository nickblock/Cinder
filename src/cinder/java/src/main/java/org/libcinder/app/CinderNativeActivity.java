package org.libcinder.app;

import android.Manifest;
import android.app.Activity;
import android.app.NativeActivity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

import org.libcinder.Cinder;
import org.libcinder.hardware.Camera;
import org.libcinder.hardware.CinderLocationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.spec.ECField;

public class CinderNativeActivity extends NativeActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "CinderNativeActivity";

    private static CinderNativeActivity sInstance = null;

    private Handler mHandler = null;

    private boolean mKeepScreenOn = false;
    private boolean mFullScreen = false;

    public static CinderNativeActivity getInstance() {
        return sInstance;
    }

    private class CameraConfig {
        public boolean init = false;
        public boolean start = false;
        public int width = 0;
        public int height = 0;
        public String deviceId;
    } 
    CameraConfig mCameraConfig = new CameraConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sInstance = this;

        mHandler = new Handler(Looper.getMainLooper());

        if(mUseLocationManager) {

            mLocationManager = new CinderLocationManager(this);
        }

        Log.i(TAG, "onCreate | -------------- ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i(TAG, "onRestart | -------------- ");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "onStart | -------------- ");
        

//        if( mKeepScreenOn ) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            Log.i(TAG, "KEEPING SCREEN ON | -------------- ");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mUseLocationManager && mLocationManager != null) {
            mLocationManager.startLocationUpdates();
        }

        Log.i(TAG, "onResume | -------------- ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.i(TAG, "onWindowFocusChanged | -------------- ");

        if( mFullScreen && hasFocus ) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );

            Log.i(TAG, "GOING IMMERSIVE | -------------- ");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // =============================================================================================
    // Permission
    // =============================================================================================

    /** \class Permissions
     *
     */
    public class Permissions {

        ArrayList<String> mDesiredPermissions = new ArrayList<String>();

        CinderNativeActivity mActivity = null;
        int mRequestCode = 0;
        
        public Permissions(CinderNativeActivity a) {

            mActivity = a;
        }


        public boolean get(String permission) {

            if (checkCallingOrSelfPermission(
                        permission) != PackageManager.PERMISSION_GRANTED) {

                mDesiredPermissions.add(permission);

                return false;
            }

            return true;
        }

        public void doRequestPermissions() {

            if(mDesiredPermissions.size() >  0) {

                ActivityCompat.requestPermissions(mActivity, mDesiredPermissions.toArray(new String[0]), mRequestCode);

                mDesiredPermissions.clear();
            }
        }

        public class Missing {

            private String msg(String permission) {
                return "Permission denied (maybe missing " + permission + " permission)";
            }

            public String CAMERA()                  { return msg(Manifest.permission.CAMERA); }
            public String INTERNET()                { return msg(Manifest.permission.INTERNET); }
            public String WRITE_EXTERNAL_STORAGE()  { return msg(Manifest.permission.WRITE_EXTERNAL_STORAGE); }
            public String LOCATION()                { return msg(Manifest.permission.ACCESS_FINE_LOCATION); }
        }

        private Missing mMissing = new Missing();

        public Missing missing() {
            return mMissing;
        }

        private boolean check(String ident) {
            return (checkCallingOrSelfPermission(ident) == PackageManager.PERMISSION_GRANTED);
        }

        public boolean CAMERA()                 { return check(Manifest.permission.CAMERA); }
        public boolean INTERNET()               { return check(Manifest.permission.INTERNET); }
        public boolean WRITE_EXTERNAL_STORAGE() { return check(Manifest.permission.WRITE_EXTERNAL_STORAGE); }
        public boolean LOCATION()               { return check(Manifest.permission.ACCESS_FINE_LOCATION); }

    }

    protected Permissions mPermissions = new Permissions(this);

    static public Permissions permissions() {
        return sInstance.mPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        for(int i=0; i<grantResults.length; i++) {

            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                if(permissions[i].equals(Manifest.permission.CAMERA)) {

                    // do_hardware_camera_initialize(Build.VERSION.SDK_INT);
                }
                else if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    mLocationManager = new CinderLocationManager(this);
                }
            }
        }
    }

    public boolean havePermission(String permission) {
        
        return mPermissions.get(permission);
    }

    public void getPermissions() {
        
        new Thread(new Runnable() {
            public void run() {
                mPermissions.doRequestPermissions();
            }
        }).start();
    }

    // =============================================================================================
    // Misc
    // =============================================================================================

    public String getCacheDirectory() {
        String result = this.getExternalCacheDir().toString();
        return result;
    }

    public String getPicturesDirectory() {
        String result = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        return result;
    }

    public String getDocumentsDirectory() {
        String result = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        return result;
    }

    public void setWallpaper(String path) {
        File file = new File(path);
        if(file.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
                wm.setBitmap(bitmap);
            }
            catch(Exception e) {
                Log.e(TAG, "setWallpaper failed: " + e.getMessage());
            }
        }
    }

    // =============================================================================================
    // Display
    // =============================================================================================

    public Display getDefaultDisplay() {
        Display result = null;
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        result = wm.getDefaultDisplay();
        return result;
    }

    public Point getDefaultDisplaySize() {
        Display defaultDisplay = this.getDefaultDisplay();
        Point result = new Point();
        defaultDisplay.getRealSize(result);
        return result;
    }

    public int getDisplayRotation() {
        int result = getDefaultDisplay().getRotation();
        return result;
    }

    public void setKeepScreenOn( boolean keepScreenOn ) {
        mKeepScreenOn = keepScreenOn;

        Log.i(TAG, "setKeepScreenOn : keepScreenOn=" + keepScreenOn + " | -------------- ");
    }

    public void setFullScreen( boolean fullScreen ) {
        mFullScreen = fullScreen;

        Log.i(TAG, "setFullscreen : fullScreen=" + fullScreen + " | -------------- ");
    }

    // =============================================================================================
    // Actions
    // =============================================================================================

    public void launchWebBrowser(String url) {
        try {
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(decodedUrl));
            startActivity(intent);
        }
        catch(Exception e) {
            Log.e(TAG, "launchWebBrowser failed: " + e.getMessage());
        }
    }

    // NOTE: tempImagePath should be a png
    public void launchTwitter(String text, String tempImagePath) {
        Log.i(TAG, "launchTwitter: text=" + text + ", tempImagePath=" + tempImagePath);

        try {
            File tempImageFile = null;
            try {
                if(!tempImagePath.isEmpty()) {
                    tempImageFile = new File(tempImagePath);
                }
            }
            catch(Exception e) {
                tempImageFile = null;
                Log.w(TAG, "launchTwitter: couldn't load requested image");
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("/*");
            intent.setClassName("com.twitter.android", "com.twitter.android.composer.ComposerActivity");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            if(null != tempImageFile) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempImageFile));
                intent.setType("image/png");
            }
            startActivity(intent);
        }
        catch(final ActivityNotFoundException e) {
            Log.e(TAG, "Couldn't launch Twitter app (Twitter may have changed ComposerActivity...): " + e.getMessage());
        }
        catch(Exception e) {
            Log.e(TAG, "shareOnTwitterMsg failed: " + e.getMessage());
        }
    }

    // =============================================================================================
    // Camera
    // =============================================================================================

    private Camera mCamera;

    /**
     * hardware_camera_initialize
     *
     */
    public void hardware_camera_initialize(final int apiLevel) {
        Log.i(TAG, "hardware_camera_initialize");

        if(permissions().get(Manifest.permission.CAMERA)) {

            do_hardware_camera_initialize(apiLevel);
        }

    }

    private void do_hardware_camera_initialize(final int apiLevel) {

        if(null != mCamera) {
            return;
        }

        if(1 != Thread.currentThread().getId()) { 
            final ConditionVariable condition = new ConditionVariable(false); 
            final CinderNativeActivity activity = this; 
            mHandler.post(new Runnable() { 
                @Override 
                public void run() { 
                    //mCamera = Camera.create(Build.VERSION_CODES.KITKAT, activity); 
                    mCamera = Camera.create(apiLevel, activity); 
                    mCamera.initialize(); 
                    condition.open(); 
                } 
            }); 
            condition.block(); 
        } 
        else { 
            
            mCamera = Camera.create(apiLevel, this);

            if(mCameraConfig.start) {
                do_hardware_camera_startCapture(mCameraConfig.deviceId, mCameraConfig.width, mCameraConfig.height);
            }
        } 
        
    }


    /**
     * hardware_camera_enumerateDevices
     *
     */
    public Camera.DeviceInfo[] hardware_camera_enumerateDevices() {
        Log.i(TAG, "hardware_camera_enumerateDevices");

        Camera.DeviceInfo[] result = null;

        if(null != mCamera) {
            result = mCamera.enumerateDevices();
            Log.i(TAG, "hardware_camera_enumerateDevices: Found " + result.length + " devices");
        }

        return result;
    }

    /**
     * hardware_camera_startCapture
     *
     */
    public void do_hardware_camera_startCapture(final String deviceId, final int width, final int height) {
    //public void hardware_camera_startCapture() {
        Log.i(TAG, "hardware_camera_startCapture");

        //if(null == mCamera) {
        //    return;
        //}

        if(1 != Thread.currentThread().getId()) {
            final ConditionVariable condition = new ConditionVariable(false);
            final CinderNativeActivity activity = this;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //mCamera = Camera.create(Build.VERSION_CODES.KITKAT, activity);
                    mCamera.startCapture(deviceId, width, height);
                    condition.open();
                }
            });
            condition.block();
        }
        else {
            mCamera.startCapture(deviceId, width, height);
        }
    }

    private void hardware_camera_startCapture(final String deviceId, final int width, final int height) {

        if(mPermissions.get(Manifest.permission.CAMERA)) {
            do_hardware_camera_startCapture(deviceId, width, height);
        }
        else {
            mCameraConfig.deviceId = deviceId;
            mCameraConfig.width = width;
            mCameraConfig.height = height;
            mCameraConfig.start = true;
        }
    }


    /**
     * hardware_camera_stopCapture
     *
     */
    public void hardware_camera_stopCapture() {
        Log.i(TAG, "hardware_camera_stopCapture: mHandler=" + mHandler);

        if(null == mCamera) {
            return;
        }

        mCamera.stopCapture();
        mCamera = null;

        /*
        if(1 != Thread.currentThread().getId()) {
            try {
                Log.i(TAG, "hardware_camera_stopCapture: Mark 0.0");
                final ConditionVariable condition = new ConditionVariable(false);
                Log.i(TAG, "hardware_camera_stopCapture: Mark 0.1");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "hardware_camera_stopCapture: Mark 0.5");
                        mCamera.stopCapture();
                        condition.open();
                    }
                });
                Log.i(TAG, "hardware_camera_stopCapture: Mark 0.6");
                condition.block();
                Log.i(TAG, "hardware_camera_stopCapture: Mark 0.7");
            }
            catch(Exception e) {
                Log.e(TAG, "hardware_camera_stopCapture failed: " + e.getMessage());
            }
        }
        else {
            Log.i(TAG, "hardware_camera_stopCapture: Mark 1.0");
            mCamera.stopCapture();
        }

        Log.i(TAG, "hardware_camera_stopCapture: Mark 2.0");
        mCamera = null;
        */
    }

    /**
     * hardware_camera_lockPixels
     *
     */
    public byte[] hardware_camera_lockPixels() {
        //Log.i(TAG, "hardware_camera_lockPixels");

        byte[] result = null;

        if(null != mCamera) {
            result = mCamera.lockPixels();
        }

        return result;
    }

    /**
     * hardware_camera_unlockPixels
     *
     */
    public void hardware_camera_unlockPixels() {
        //Log.i(TAG, "hardware_camera_unlockPixels");

        if(null != mCamera) {
            mCamera.unlockPixels();
        }
    }

    /**
     * hardware_camera_isNewFrameAvailable
     *
     */
    public boolean hardware_camera_isNewFrameAvailable() {
        //Log.i(TAG, "hardware_camera_isNewFrameAvailable");

        return (null != mCamera) && (mCamera.isNewFrameAvailable());
    }

    /**
     * hardware_camera_clearNewFrameAvailable
     *
     */
    public void hardware_camera_clearNewFrameAvailable() {
        //Log.i(TAG, "hardware_camera_clearNewFrameAvailable");

        if(null != mCamera) {
            mCamera.clearNewFrameAvailable();
        }
    }

    /**
     * hardware_camera_initPreviewTexture
     *
     */
    public void hardware_camera_initPreviewTexture(int texName) {
        Log.i(TAG, "hardware_camera_initPreviewTexture: " + texName);

        if(null != mCamera) {
            try {
                SurfaceTexture previewTexture = new SurfaceTexture(texName);
                mCamera.setPreviewTexture(previewTexture);
            }
            catch(Exception e) {
                Log.e(TAG, "hardware_camera_initPreviewTexture error: " + e.getMessage());
            }
        }
    }

    /**
     * hardware_camera_updateTexImage
     *
     */
    public void hardware_camera_updateTexImage() {
        //Log.i(TAG, "hardware_camera_updateTexImage");

        if ((null != mCamera) && (null != mCamera.getPreviewTexture())) {
            mCamera.getPreviewTexture().updateTexImage();
        }

        /*
        try {
            if ((null != mCamera) && (null != mCamera.getPreviewTexture())) {
                mCamera.getPreviewTexture().updateTexImage();
            }
        }
        catch(Exception e) {
            Log.e(TAG, "hardware_camera_updateTexImage error:" + e.getMessage());
        }
        */
    }

    CinderLocationManager mLocationManager;
    boolean mUseLocationManager = false;

    public void enableLocationManager() {

        if(mPermissions.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mUseLocationManager = true;
        }

    }
}