package com.byteshaft.briver.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class HireFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static GoogleMap mMap = null;
    View baseViewHireFragment;
    private FragmentManager fm;
    private SupportMapFragment mapFragment;
    private LatLng currentLatLngAuto = null;
    private Boolean simpleMapView = true;
    private Menu actionsMenu;
    private boolean hireMarkerAdded;
    private boolean cameraAnimatedToCurrentLocation;
    private boolean isSearchEditTextVisible;
    private Animation animLayoutBottomUp;
    private Animation animLayoutBottomDown;
    private Animation animLayoutTopUp;
    private Animation animLayoutTopDown;
    private LatLng latLngLongClickMapHire;

    private LinearLayout llMapHireButtons;
    private TextView tvMapHireAddress;
    private ImageButton btnMapHireRemoveMarker;
    Button btnQuickHire;
    Button btnScheduledHire;
    private AutoCompleteTextView etMapSearch;
    private String inputMapSearch;
    String totalHoursOfService;
    private int mYear, mMonth, mDay, mHour, mMinute;
    final CharSequence[] itemsForHoursSelectingDialog = {" 2 Hours ", " 4 Hours ", " 6 Hours ", " 8 Hours "};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHireFragment = inflater.inflate(R.layout.fragment_hire, container, false);

        llMapHireButtons = (LinearLayout) baseViewHireFragment.findViewById(R.id.layout_map_hire);
        animLayoutBottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_up);
        animLayoutBottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_down);
        animLayoutTopUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_top_up);
        animLayoutTopDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_top_down);
        tvMapHireAddress = (TextView) baseViewHireFragment.findViewById(R.id.tv_map_hire_address);
        btnMapHireRemoveMarker = (ImageButton) baseViewHireFragment.findViewById(R.id.btn_map_hire_cancel);
        etMapSearch = (AutoCompleteTextView) baseViewHireFragment.findViewById(R.id.et_map_search);
        btnQuickHire = (Button) baseViewHireFragment.findViewById(R.id.btn_map_hire_quick);
        btnScheduledHire = (Button) baseViewHireFragment.findViewById(R.id.btn_map_hire_scheduled);

        btnMapHireRemoveMarker.setOnClickListener(this);
        btnQuickHire.setOnClickListener(this);
        btnScheduledHire.setOnClickListener(this);

        fm = getChildFragmentManager();

        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.685677, 79.408410), 4.0f));

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (isSearchEditTextVisible) {
                            setSearchBarVisibility(false);
                        }
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        currentLatLngAuto = new LatLng(location.getLatitude(), location.getLongitude());
                        if (!cameraAnimatedToCurrentLocation) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLngAuto, 15.0f), new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    Helpers.showSnackBar(baseViewHireFragment, "Tap and hold to set MeetUp point",
                                            Snackbar.LENGTH_LONG, "#ffffff");
                                }

                                @Override
                                public void onCancel() {
                                    Helpers.showSnackBar(baseViewHireFragment, "Tap and hold to set MeetUp point",
                                            Snackbar.LENGTH_LONG, "#ffffff");
                                }
                            });
                            cameraAnimatedToCurrentLocation = true;
                        }
                    }
                });

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(final LatLng latLng) {
                        if (isSearchEditTextVisible) {
                            setSearchBarVisibility(false);
                        }
                        if (!hireMarkerAdded) {
                            latLngLongClickMapHire = latLng;
                            mMap.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_hire))).setTitle("Hire");
                            llMapHireButtons.setVisibility(View.VISIBLE);
                            llMapHireButtons.startAnimation(animLayoutBottomUp);
                            hireMarkerAdded = true;
                            btnMapHireRemoveMarker.setVisibility(View.VISIBLE);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    final String address = Helpers.getAddress(getActivity(), latLngLongClickMapHire);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (address != null) {
                                                tvMapHireAddress.setText(address);
                                                tvMapHireAddress.setVisibility(View.VISIBLE);
                                            } else {
                                                tvMapHireAddress.setText("Address not found");
                                                tvMapHireAddress.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });

        etMapSearch.addTextChangedListener(new TextWatcher() {

            Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timer.cancel();
                inputMapSearch = etMapSearch.getText().toString();
                if (inputMapSearch.length() > 2) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Geocoder geocoder = new Geocoder(getActivity());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocationName(inputMapSearch, 3);
                                if (addresses != null && !addresses.equals("")) {
                                    searchAnimateCamera(addresses);
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return baseViewHireFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_map_hire_cancel:
                if (hireMarkerAdded) {
                    btnMapHireRemoveMarker.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();
                            llMapHireButtons.setVisibility(View.GONE);
                            llMapHireButtons.startAnimation(animLayoutBottomDown);
                            hireMarkerAdded = false;
                            tvMapHireAddress.setText("");
                            tvMapHireAddress.setVisibility(View.GONE);
                        }
                    }, 300);
                }
                break;
            case R.id.btn_map_hire_quick:
                AlertDialog levelDialogQuickHire;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select service duration");
                builder.setSingleChoiceItems(itemsForHoursSelectingDialog, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item)
                        {
                            case 0:
                                totalHoursOfService = "2";
                                break;
                            case 1:
                                totalHoursOfService = "4";
                                break;
                            case 2:
                                totalHoursOfService = "6";
                                break;
                            case 3:
                                totalHoursOfService = "8";
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                levelDialogQuickHire = builder.create();
                levelDialogQuickHire.show();
                break;

            case R.id.btn_map_hire_scheduled:
                AlertDialog levelDialogScheduledHire;

                AlertDialog.Builder builderTwo = new AlertDialog.Builder(getActivity());
                builderTwo.setTitle("Select service duration");
                builderTwo.setSingleChoiceItems(itemsForHoursSelectingDialog, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item)
                        {
                            case 0:
                                totalHoursOfService = "2";
                                break;
                            case 1:
                                totalHoursOfService = "4";
                                break;
                            case 2:
                                totalHoursOfService = "6";
                                break;
                            case 3:
                                totalHoursOfService = "8";
                                break;
                        }
                        final TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        Log.i("Time", "Set: " + hourOfDay + ":" + minute);
                                        Calendar newCalendar = Calendar.getInstance();
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                Calendar newDate = Calendar.getInstance();
                                                newDate.set(year, monthOfYear, dayOfMonth);
                                                Log.i("Date", "Set: " + dayOfMonth + "-" + monthOfYear + "-" + year);
                                            }
                                        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                                        datePickerDialog.show();
                                    }
                                }, mHour, mMinute, false);
                        tpd.show();
                        dialog.dismiss();
                    }
                });
                levelDialogScheduledHire = builderTwo.create();
                levelDialogScheduledHire.show();
        }

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
        if (AppGlobals.getUserType() == 1) {
            actionsMenu.getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_map:
                setSearchBarVisibility(!isSearchEditTextVisible);
                break;
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

    private void setSearchBarVisibility(boolean visibility) {
        if (!visibility) {
            etMapSearch.clearFocus();
            etMapSearch.setVisibility(View.GONE);
            etMapSearch.startAnimation(animLayoutTopUp);
            etMapSearch.setText("");
            isSearchEditTextVisible = false;
        } else {
            etMapSearch.setVisibility(View.VISIBLE);
            etMapSearch.startAnimation(animLayoutTopDown);
            isSearchEditTextVisible = true;
        }
    }

    protected void searchAnimateCamera(List<Address> addresses) {
        final Address addressForSearch = addresses.get(0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng latLngSearch = new LatLng(addressForSearch.getLatitude(), addressForSearch.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngSearch, 15.0f));
            }
        });
    }
}
