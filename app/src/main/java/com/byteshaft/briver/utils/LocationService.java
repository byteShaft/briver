package com.byteshaft.briver.utils;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.byteshaft.briver.fragments.PreferencesFragment;
import com.byteshaft.briver.fragments.RegisterFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fi8er1 on 16/05/2016.
 */
public class LocationService extends ContextWrapper implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static int onLocationChangedCounter = 0;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public static LatLng driverCurrentLocation = null;
    public static LatLng driverLastKnownLocation = null;
    private static int recursionCounter = 0;

    public LocationService(Context base) {
        super(base);
//        if (!AppGlobals.locationPermissionsAllowedForMarshmallow() &&
//                (MainActivity.isMainActivityRunning || WelcomeActivity.isWelcomeActivityRunning)) {
//            Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(), "Permission Denied",
//                    "You need to grant permissions to use Location Services for Briver", "Settings",
//                    "Exit App", Helpers.openPermissionsSettingsForMarshmallow, Helpers.exitApp);
//            return;
//        }
        connectGoogleApiClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(), "Permission Denied",
                    "You need to grant permissions to use Location Services for Briver", "Settings",
                    "Exit App", Helpers.openPermissionsSettingsForMarshmallow, Helpers.exitApp);
            return;
        }
        recursionCounter();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        onLocationChangedCounter++;
        if (RegisterFragment.isRegistrationFragmentOpen && onLocationChangedCounter == 3) {
            RegisterFragment.latLngDriverLocationForRegistration = driverCurrentLocation;
            Helpers.showSnackBar(RegisterFragment.baseViewRegisterFragment, "Location Acquired", Snackbar.LENGTH_SHORT, "#A4C639");
        } else if (PreferencesFragment.isPreferencesFragmentOpen && onLocationChangedCounter == 3) {
            PreferencesFragment.latLngDriverLocationFixed = driverCurrentLocation;
            PreferencesFragment.setFixedLocationDisplay();
        }
        if (onLocationChangedCounter > 2) {
            stopLocationService();
        }
        driverCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("LocationServices", "LocationChanged called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Failed to start Location Service", Toast.LENGTH_LONG).show();
    }

    private void connectGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        long INTERVAL = 0;
        long FASTEST_INTERVAL = 0;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(), "Permission Denied",
                    "You need to grant permissions to use Location Services for Briver", "Settings",
                    "Exit App", Helpers.openPermissionsSettingsForMarshmallow, Helpers.exitApp);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationService() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        onLocationChangedCounter = 0;
    }

    void recursionCounter() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recursionCounter > 160 && mGoogleApiClient.isConnected()) {
                    stopLocationService();
                    Log.i("LocationServices", "Location cannot be acquired at the moment");
                } else if (mGoogleApiClient.isConnected()) {
                    recursionCounter();
                    recursionCounter++;
                }
            }
        },1000);
    }

}
