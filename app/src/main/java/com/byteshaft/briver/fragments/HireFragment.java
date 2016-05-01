package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class HireFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewHireFragment;

    private FragmentManager fm;

    private SupportMapFragment mapFragment;
    private static GoogleMap mMap = null;
    private LatLng currentLatLngAuto = null;
    private Boolean simpleMapView = true;
    private Menu actionsMenu;
    private boolean hireMarkerAdded;
    private Animation animLayoutBottomUp;
    private Animation animLayoutBottomDown;

    private LinearLayout llMapHireButtons;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHireFragment = inflater.inflate(R.layout.fragment_hire, container, false);

        llMapHireButtons = (LinearLayout) baseViewHireFragment.findViewById(R.id.layout_map_hire);
        animLayoutBottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_up);
        animLayoutBottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_down);

        fm = getChildFragmentManager();

        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        return false;
                    }
                });

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if (!hireMarkerAdded) {
                            mMap.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_hire)));
                            llMapHireButtons.setVisibility(View.VISIBLE);
                            llMapHireButtons.startAnimation(animLayoutBottomUp);
                            hireMarkerAdded = true;
                        }

                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (hireMarkerAdded) {
                            mMap.clear();
                            llMapHireButtons.setVisibility(View.GONE);
                            llMapHireButtons.startAnimation(animLayoutBottomDown);
                            hireMarkerAdded = false;
                        }
                    }
                });
            }
        });
        return baseViewHireFragment;
    }

    @Override
    public void onClick(View v) {

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_current_location:
                if (Helpers.isAnyLocationServiceAvailable()) {
                    if (mMap != null) {
                    if (AppGlobals.getUserType() == 0) {
                            currentLatLngAuto = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                            if (currentLatLngAuto != null) {
                                CameraPosition cameraPosition =
                                        new CameraPosition.Builder()
                                                .target(currentLatLngAuto)
                                                .zoom(16.0f)
                                                .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            } else {
                                Toast.makeText(getActivity(), "Location is not available at the moment", Toast.LENGTH_SHORT).show();
                            }
                        }
//                     if (AppGlobals.getUserType() == 1) {
//                         if (DriverService.driverLocationReportingServiceIsRunning && DriverService.driverCurrentLocation != null) {
//                             CameraPosition cameraPosition =
//                                     new CameraPosition.Builder()
//                                             .target(DriverService.driverCurrentLocation)
//                                             .zoom(16.0f)
//                                             .build();
//                             mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                         } else if (!DriverService.driverLocationReportingServiceIsRunning && mMap != null) {
//                             currentLatLngAuto = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
//                             CameraPosition cameraPosition =
//                                     new CameraPosition.Builder()
//                                             .target(currentLatLngAuto)
//                                             .zoom(16.0f)
//                                             .build();
//                             mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                         } else {
//                             Toast.makeText(getActivity(), "Location is not available at the moment", Toast.LENGTH_SHORT).show();
//                         }
//                     }
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
                return super.onOptionsItemSelected(item);
        }
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
