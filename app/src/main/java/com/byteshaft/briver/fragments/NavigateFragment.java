package com.byteshaft.briver.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by fi8er1 on 13/07/2016.
 */
public class NavigateFragment extends android.support.v4.app.Fragment {


    private static GoogleMap mMap = null;
    View baseViewNavigateFragment;
    private FragmentManager fm;
    private SupportMapFragment mapFragment;
    private RoutingListener mRoutingListener;
    private LatLng currentLatLngAuto = null;
    private Boolean simpleMapView = true;
    private boolean routeBuildExecuted;
    private Menu actionsMenu;
    final Runnable openLocationServiceSettings = new Runnable() {
        public void run() {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };
    final Runnable recheckLocationServiceStatus = new Runnable() {
        public void run() {
            if (!Helpers.isAnyLocationServiceAvailable()) {
                Helpers.AlertDialogWithPositiveNegativeFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver hiring", "Settings", "ReCheck",
                        openLocationServiceSettings, recheckLocationServiceStatus);
            }
        }
    };

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            currentLatLngAuto = new LatLng(location.getLatitude(), location.getLongitude());
            Helpers.dismissProgressWithPositiveButtonDialog();
            if (!routeBuildExecuted) {
                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(mRoutingListener)
                        .waypoints(currentLatLngAuto, Helpers.latLngForNavigation)
                        .build();
                routing.execute();
                routeBuildExecuted = true;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseViewNavigateFragment = inflater.inflate(R.layout.fragment_navigate, container, false);
        fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_navigate);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    getActivity().onBackPressed();
                } else if (!AppGlobals.checkPlayServicesAvailability()) {
                    Helpers.AlertDialogWithPositiveNegativeFunctions(getActivity(), "Location components missing",
                            "You need to install GooglePlayServices to continue using Briver", "Install",
                            "Exit App", Helpers.openPlayServicesInstallation, Helpers.exitApp);
                    getActivity().onBackPressed();
                } else if (!Helpers.isAnyLocationServiceAvailable()) {
                    Helpers.AlertDialogWithPositiveNegativeFunctions(getActivity(), "Location Service disabled",
                            "Enable device GPS to continue driver hiring", "Settings", "ReCheck",
                            openLocationServiceSettings, recheckLocationServiceStatus);
                    getActivity().onBackPressed();
                } else {
                    Helpers.showProgressDialogWithPositiveButton(getActivity(), "Acquiring current location", "Dismiss", null);
                }

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                mMap.addMarker(new MarkerOptions().position(Helpers.latLngForNavigation)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_hire)));
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(Helpers.latLngForNavigation)
                                .zoom(14.0f)
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        mRoutingListener = new RoutingListener() {
            @Override
            public void onRoutingFailure() {

            }

            @Override
            public void onRoutingStart() {

            }

            @Override
            public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
                mMap.addPolyline(new PolylineOptions()
                        .addAll(polylineOptions.getPoints())
                        .width(12)
                        .geodesic(true)
                        .color(Color.parseColor("#80000000")));

                mMap.addPolyline(new PolylineOptions()
                        .addAll(polylineOptions.getPoints())
                        .width(6)
                        .geodesic(true)
                        .color(Color.RED));
            }

            @Override
            public void onRoutingCancelled() {

            }
        };
        Helpers.showProgressDialogWithPositiveButton(getActivity(), "Acquiring current location", "Dismiss", null);
        getActivity().setTitle("Navigate");
        return baseViewNavigateFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
        actionsMenu = menu;
        actionsMenu.removeItem(R.id.action_search_map);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_current_location:
                if (Helpers.isAnyLocationServiceAvailable()) {
                    if (mMap != null) {
                        if (currentLatLngAuto != null) {
                            CameraPosition cameraPosition =
                                    new CameraPosition.Builder()
                                            .target(currentLatLngAuto)
                                            .zoom(16.0f)
                                            .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            Toast.makeText(getActivity(), "Error: Location not available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error: Map not ready", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: Location Service disabled", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_change_map:
                if (simpleMapView) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    setActionIcon(false);
                    simpleMapView = false;
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    setActionIcon(true);
                    simpleMapView = true;
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionIcon(boolean simpleMap) {
        MenuItem item = actionsMenu.findItem(R.id.action_change_map);
        if (actionsMenu != null) {
            if (simpleMap) {
                item.setIcon(R.mipmap.ic_action_map_satellite);
            } else {
                item.setIcon(R.mipmap.ic_action_map_simple);
            }
        }
    }
}
