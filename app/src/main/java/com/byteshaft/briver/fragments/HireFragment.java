package com.byteshaft.briver.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.Tasks.HiringTask;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fi8er1 on 01/05/2016.
 */

public class HireFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static int responseCode;
    public static ArrayList<Integer> driversIdList;
    public static HashMap<Integer, ArrayList<String>> hashMapDriverData;
    private static GoogleMap mMap = null;
    public static HiringTask taskHiringDriver;
    public static final String[] itemsForHoursSelectingDialog = {"2 Hours", "4 Hours", "6 Hours", "8 Hours", "12 Hours", "24 Hours", "48 Hours"};
    private static String[] stringArrayForDriverHiring;
    private String driverIdForHiring;
    private String driverTimeSpanForHiring;
    private String driverTimeOfHiring;
    Timer textChangeTimer;
    final Runnable btnCustomHireDialogHire = new Runnable() {
        public void run() {
            Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Confirmation",
                    "Do you really want to hire this driver?", "Yes", "Cancel", hire);
        }
    };
    final Runnable hire = new Runnable() {
        public void run() {
            driverTimeSpanForHiring = Helpers.spinnerServiceHours.getSelectedItem().toString().substring(0, 2).trim();
            driverTimeOfHiring = Helpers.getCurrentTimeOfDevice();
            stringArrayForDriverHiring = new String[]{driverIdForHiring, driverTimeSpanForHiring, driverTimeOfHiring};
            taskHiringDriver = (HiringTask) new HiringTask().execute(stringArrayForDriverHiring);
        }
    };
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
    View baseViewHireFragment;
    HttpURLConnection connection;
    public static String serviceStartTime;
    boolean gettingNearbyDriversToShowMarkers;
    boolean driversMarkersShownOnMap;
    boolean isGetNearbyDriversTaskRunning;
    boolean isHireFragmentOpen;
    public static boolean isQuickHire;

    TimePickerDialog tpd;
    Marker hireMeetUpPointMarker;
    Button btnQuickHire;
    Button btnScheduledHire;
    public static int totalHoursOfService;
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
    private Animation animLayoutMapSearchBarTopUp;
    private Animation animLayoutMapSearchBarTopDown;
    private String hireMeetUpPoint;
    private GetNearbyDriversAvailableToHire getNearbyDriversTask;
    private LinearLayout llMapHireButtons;
    private TextView tvMapHireAddress;
    private ImageButton btnMapHireRemoveMarker;
    private EditText etMapSearch;
    private String inputMapSearch;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Helpers.dismissProgressWithPositiveButtonDialog();
            currentLatLngAuto = new LatLng(location.getLatitude(), location.getLongitude());
            if (!cameraAnimatedToCurrentLocation) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLngAuto, 15.0f), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (AppGlobals.isHireFragmentFirstRun()) {
                            Helpers.AlertDialogMessage(getActivity(), "One Time Message",
                                    "Tap and hold anywhere on the MapView to set MeetUp point and hire a driver\n" +
                                            "\nYou can also QuickHire a driver by tapping on the driver location marker", "Ok");
                            AppGlobals.setHireFragmentFirstRun(false);
                        } else {
                            Helpers.showSnackBar(baseViewHireFragment, "Tap and hold to set MeetUp point",
                                    Snackbar.LENGTH_LONG, "#ffffff");
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (AppGlobals.isHireFragmentFirstRun()) {
                            Helpers.AlertDialogMessage(getActivity(), "One Time Message",
                                    "Tap and hold on the MapView to set MeetUp point and hire a driver\n" +
                                            "\nYou can also QuickHire a driver by tapping on the driver location marker", "Ok");
                            AppGlobals.setHireFragmentFirstRun(false);
                        } else {
                            Helpers.showSnackBar(baseViewHireFragment, "Tap and hold to set MeetUp point",
                                    Snackbar.LENGTH_LONG, "#ffffff");
                        }
                    }
                });
                cameraAnimatedToCurrentLocation = true;
            }
            if (!driversMarkersShownOnMap) {
                functionToSetDriverMarkersOnMap(location.getLatitude() + "," + location.getLongitude());
            }
        }
    };

    public static String getNearbyDriversString(
            String location, int radius, String dateTime, int timeSpan) {
        return
                String.format("base_location=%s&", location) +
                        String.format("radius=%s&", radius) +
                        String.format("start_time=%s&", dateTime) +
                        String.format("time_span=%s", timeSpan);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHireFragment = inflater.inflate(R.layout.fragment_hire, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        llMapHireButtons = (LinearLayout) baseViewHireFragment.findViewById(R.id.layout_map_hire);
        animLayoutBottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_up);
        animLayoutBottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_down);
        animLayoutMapSearchBarTopUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_top_up);
        animLayoutMapSearchBarTopDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_top_down);
        tvMapHireAddress = (TextView) baseViewHireFragment.findViewById(R.id.tv_map_hire_address);
        btnMapHireRemoveMarker = (ImageButton) baseViewHireFragment.findViewById(R.id.btn_map_hire_cancel);
        etMapSearch = (EditText) baseViewHireFragment.findViewById(R.id.et_map_search);
        btnQuickHire = (Button) baseViewHireFragment.findViewById(R.id.btn_map_hire_quick);
        btnScheduledHire = (Button) baseViewHireFragment.findViewById(R.id.btn_map_hire_scheduled);
        driversIdList = new ArrayList<>();
        hashMapDriverData = new HashMap<>();

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

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    public boolean onMarkerClick(final Marker marker) {
                        final String snippet = marker.getSnippet();
                        driverIdForHiring = snippet;
                        if (snippet.equals("-1")) {
                            return true;
                        }
                        new AsyncTask<Void, Void, Void>() {
                            int id = -1;
                            String addressString;
                            boolean taskSuccess;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Helpers.showProgressDialog(getActivity(), "Retrieving driver info");
                                if (llMapHireButtons.isShown()) {
                                    btnMapHireRemoveMarker.callOnClick();
                                }
                                if (isSearchEditTextVisible) {
                                    setSearchBarVisibility(false);
                                }
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    id = Integer.parseInt(snippet);
                                    String[] latLngToString = hashMapDriverData.get(driversIdList.get(id)).get(3).split(",");
                                    double latitude = Double.parseDouble(latLngToString[0]);
                                    double longitude = Double.parseDouble(latLngToString[1]);
                                    addressString = Helpers.getAddress(getActivity(), new LatLng(latitude, longitude));
                                    taskSuccess = true;
                                } catch (NumberFormatException e) {
                                    taskSuccess = false;
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                Helpers.dismissProgressDialog();
                                if (taskSuccess) {
                                    Helpers.customDialogWithPositiveFunctionNegativeButtonForOnMapMarkerClickHiring(getActivity(),
                                            hashMapDriverData.get(driversIdList.get(id)).get(0), hashMapDriverData.get(driversIdList.get(id)).get(1),
                                            hashMapDriverData.get(driversIdList.get(id)).get(2), addressString, hashMapDriverData.get(driversIdList.get(id)).get(4),
                                            hashMapDriverData.get(driversIdList.get(id)).get(5), hashMapDriverData.get(driversIdList.get(id)).get(6),
                                            hashMapDriverData.get(driversIdList.get(id)).get(7), hashMapDriverData.get(driversIdList.get(id)).get(8),
                                            hashMapDriverData.get(driversIdList.get(id)).get(9), hashMapDriverData.get(driversIdList.get(id)).get(10),
                                            btnCustomHireDialogHire);
                                } else {
                                    Helpers.showSnackBar(getView(), "Driver info cannot be retrieved at the moment", Snackbar.LENGTH_LONG, "#f44336");
                                }
                            }
                        }.execute();
                        return true;
                    }
                });

                mMap.setOnMyLocationChangeListener(myLocationChangeListener);

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(final LatLng latLng) {
                        if (isSearchEditTextVisible) {
                            setSearchBarVisibility(false);
                        }
                        if (!hireMarkerAdded) {
                            hireMeetUpPoint = latLng.latitude + "," + latLng.longitude;
                            hireMeetUpPointMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_hire)).snippet("-1"));
                            llMapHireButtons.setVisibility(View.VISIBLE);
                            llMapHireButtons.startAnimation(animLayoutBottomUp);
                            hireMarkerAdded = true;
                            btnMapHireRemoveMarker.setVisibility(View.VISIBLE);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    final String address = Helpers.getAddress(getActivity(), latLng);
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

            Timer textChangeTimer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChangeTimer.cancel();
                inputMapSearch = etMapSearch.getText().toString();
                if (inputMapSearch.length() > 2 && isHireFragmentOpen) {
                    textChangeTimer = new Timer();
                    textChangeTimer.schedule(new TimerTask() {
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

        animLayoutMapSearchBarTopUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                etMapSearch.setText("");
                Helpers.closeSoftKeyboard(getActivity());
                isSearchEditTextVisible = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animLayoutMapSearchBarTopDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                etMapSearch.requestFocus();
                Helpers.openSoftKeyboardOnEditText(getActivity(), etMapSearch);
                isSearchEditTextVisible = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
                            hireMeetUpPointMarker.remove();
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

                isQuickHire = true;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Service Duration");
                builder.setCancelable(false);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setSingleChoiceItems(itemsForHoursSelectingDialog, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                totalHoursOfService = 2;
                                break;
                            case 1:
                                totalHoursOfService = 4;
                                break;
                            case 2:
                                totalHoursOfService = 6;
                                break;
                            case 3:
                                totalHoursOfService = 8;
                                break;
                            case 4:
                                totalHoursOfService = 12;
                                break;
                            case 5:
                                totalHoursOfService = 24;
                                break;
                            case 6:
                                totalHoursOfService = 48;
                                break;
                        }
                        Log.i("SelectedHoursOfService", "" + totalHoursOfService);
                        dialog.dismiss();
                        serviceStartTime = Helpers.getCurrentTimeOfDevice();
                        Log.i("ServiceStartTime", "" + serviceStartTime);
                        gettingNearbyDriversToShowMarkers = false;
                        if (isGetNearbyDriversTaskRunning) {
                            getNearbyDriversTask.cancel(true);
                        }
                        getNearbyDriversTask = (GetNearbyDriversAvailableToHire) new GetNearbyDriversAvailableToHire().execute();
                    }
                });
                levelDialogQuickHire = builder.create();
                levelDialogQuickHire.show();
                break;

            case R.id.btn_map_hire_scheduled:
                AlertDialog levelDialogScheduledHire;

                isQuickHire = false;
                AlertDialog.Builder builderTwo = new AlertDialog.Builder(getActivity());
                builderTwo.setTitle("Select service duration");
                builderTwo.setCancelable(false);
                builderTwo.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderTwo.setSingleChoiceItems(itemsForHoursSelectingDialog, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                totalHoursOfService = 2;
                                break;
                            case 1:
                                totalHoursOfService = 4;
                                break;
                            case 2:
                                totalHoursOfService = 6;
                                break;
                            case 3:
                                totalHoursOfService = 8;
                                break;
                            case 4:
                                totalHoursOfService = 12;
                                break;
                            case 5:
                                totalHoursOfService = 24;
                                break;
                            case 6:
                                totalHoursOfService = 48;
                                break;
                        }
                        Log.i("SelectedHoursOfService", "" + totalHoursOfService);
                        Calendar newCalendar = Calendar.getInstance();

                        tpd = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        mHour = hourOfDay;
                                        mMinute = minute;
                                        Calendar newCalendar = Calendar.getInstance();
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                Calendar newDate = Calendar.getInstance();
                                                newDate.set(year, monthOfYear, dayOfMonth);
                                                Log.i("Date", "Set: " + dayOfMonth + "-" + monthOfYear + "-" + year);
                                                mDay = dayOfMonth;
                                                mMonth = monthOfYear + 1;
                                                mYear = year;
                                                gettingNearbyDriversToShowMarkers = false;
                                                serviceStartTime = mYear + "-" + mMonth + "-" + mDay + "T" + mHour + ":" + mMinute + ":" + "00";
                                                Log.i("ServiceStartTime", "Scheduled: " + serviceStartTime);
                                                Calendar c = Calendar.getInstance();
                                                if (!(Helpers.getTimeInMillis(serviceStartTime) >= c.getTimeInMillis() - 120000)) {
                                                    Helpers.showSnackBar(getView(), "Selected schedule time is invalid, select a future time", Snackbar.LENGTH_LONG, "#f44336");
                                                    return;
                                                }
                                                if (isGetNearbyDriversTaskRunning) {
                                                    getNearbyDriversTask.cancel(true);
                                                }
                                                getNearbyDriversTask = (GetNearbyDriversAvailableToHire) new GetNearbyDriversAvailableToHire().execute();
                                            }
                                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                                        datePickerDialog.show();
                                    }
                                }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);
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

    private void setSearchBarVisibility(boolean visibility) {
        if (!visibility) {
            etMapSearch.startAnimation(animLayoutMapSearchBarTopUp);
            etMapSearch.setVisibility(View.GONE);
        } else {
            etMapSearch.setVisibility(View.VISIBLE);
            etMapSearch.startAnimation(animLayoutMapSearchBarTopDown);
        }
    }

    protected void searchAnimateCamera(List<Address> addresses) {
        final Address addressForSearch = addresses.get(0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng latLngSearch = new LatLng(addressForSearch.getLatitude(), addressForSearch.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngSearch, 15.0f));
                mMap.clear();
                btnMapHireRemoveMarker.callOnClick();
                gettingNearbyDriversToShowMarkers = true;
                functionToSetDriverMarkersOnMap(addressForSearch.getLatitude() + "," + addressForSearch.getLongitude());
            }
        });
    }

    public void onGetNearbyDriversSuccess() {
        drawNearbyDriversOnMapWithMarkers();
    }

    public void onGetNearbyDriversFailed() {
        Helpers.showSnackBar(getView(), "Failed to retrieve nearby available drivers",
                Snackbar.LENGTH_LONG, "#f44336");
    }

    private void drawNearbyDriversOnMapWithMarkers() {
        for (int i = 0; i < driversIdList.size(); i++) {
            hashMapDriverData.get(driversIdList.get(i));
            Log.i("Data", "Location: " + "ID: " + i + " " + hashMapDriverData.get(driversIdList.get(i)).get(3));
            String[] latLngString = hashMapDriverData.get(driversIdList.get(i)).get(3).split(",");
            double latitude = Double.parseDouble(latLngString[0]);
            double longitude = Double.parseDouble(latLngString[1]);
            LatLng latLngDriverPosition = new LatLng(latitude, longitude);
            if (Integer.parseInt(hashMapDriverData.get(driversIdList.get(i)).get(8)) == 1) {
                mMap.addMarker(new MarkerOptions().position(latLngDriverPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_driver_normal)).snippet("" + i));
            } else {
                mMap.addMarker(new MarkerOptions().position(latLngDriverPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_driver_online)).snippet("" + i));
            }
        }
    }

    public void functionToSetDriverMarkersOnMap(String latitudeLongitude) {
        serviceStartTime = android.text.format.DateFormat.format("yyyy-MM-ddThh:mm:ss",
                new java.util.Date()).toString();
        hireMeetUpPoint = latitudeLongitude;
        gettingNearbyDriversToShowMarkers = true;
        totalHoursOfService = 2 * 60;
        if (isGetNearbyDriversTaskRunning) {
            getNearbyDriversTask.cancel(true);
        }
        getNearbyDriversTask = (GetNearbyDriversAvailableToHire) new GetNearbyDriversAvailableToHire().execute();
        driversMarkersShownOnMap = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isHireFragmentOpen = false;
        if (isGetNearbyDriversTaskRunning) {
            getNearbyDriversTask.cancel(true);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void loadFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.anim_transition_fragment_slide_right_enter, R.anim.anim_transition_fragment_slide_left_exit,
                R.anim.anim_transition_fragment_slide_left_enter, R.anim.anim_transition_fragment_slide_right_exit);
        ft.replace(R.id.container_main, fragment, "NearbyDriversFragment").addToBackStack("NearbyDriversFragment");
        ft.commit();
    }

    private class GetNearbyDriversAvailableToHire extends AsyncTask<Void, Integer, Void> {

        boolean dataEmpty;

        @Override
        protected void onPreExecute() {
            isGetNearbyDriversTaskRunning = true;
            if (!gettingNearbyDriversToShowMarkers) {
                Helpers.showProgressDialog(getActivity(), "Retrieving nearby drivers");
            }
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("getNearbyDrivers", " RUN");
                String url;
                if (gettingNearbyDriversToShowMarkers) {
                    url = EndPoints.BASE_FILTER_DRIVERS + getNearbyDriversString(
                            hireMeetUpPoint, 50, serviceStartTime, totalHoursOfService * 60);
                } else {
                    url = EndPoints.BASE_FILTER_DRIVERS + getNearbyDriversString(
                            hireMeetUpPoint, AppGlobals.getDriverSearchRadius(), serviceStartTime, totalHoursOfService * 60);
                }
                connection = WebServiceHelpers.openConnectionForUrl(url, "GET", true);
                JSONArray jsonArray = new JSONArray(WebServiceHelpers.readResponse(connection));
                if (jsonArray.toString().equals("[]") && !gettingNearbyDriversToShowMarkers) {
                    dataEmpty = true;
                    Helpers.dismissProgressDialog();
                    return null;
                }
                Log.i("IncomingData", " UserData: " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!driversIdList.contains(jsonObject.getInt("id")) && Integer.parseInt(jsonObject.getString("status")) != 0) {
                        driversIdList.add(jsonObject.getInt("id"));
                        ArrayList<String> arrayListString = new ArrayList<>();
                        arrayListString.add(jsonObject.getString("full_name"));
                        arrayListString.add(jsonObject.getString("email"));
                        arrayListString.add(jsonObject.getString("phone_number"));
                        arrayListString.add(jsonObject.getString("location"));
                        arrayListString.add(jsonObject.getString("location_last_updated"));
                        arrayListString.add(jsonObject.getString("driving_experience"));
                        arrayListString.add(jsonObject.getString("number_of_hires"));
                        arrayListString.add(jsonObject.getString("bio"));
                        arrayListString.add(jsonObject.getString("status"));
                        arrayListString.add(jsonObject.getString("review_count"));
                        arrayListString.add(jsonObject.getString("review_stars"));
                        hashMapDriverData.put(jsonObject.getInt("id"), arrayListString);
                    }
                }
                responseCode = connection.getResponseCode();
                Log.i("ResponseCode", "" + responseCode);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isGetNearbyDriversTaskRunning = false;
            if (dataEmpty) {
                Helpers.AlertDialogMessage(getActivity(), "List is empty",
                        "No drivers found in the nearby area for the set criteria", "Ok");
            } else {
                if (!gettingNearbyDriversToShowMarkers) {
                    Helpers.dismissProgressDialog();
                    if (responseCode == 200) {
                        loadFragment(new NearbyDriversFragment());
                    }
                }
                if (responseCode == 200) {
                    onGetNearbyDriversSuccess();
                } else {
                    onGetNearbyDriversFailed();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isGetNearbyDriversTaskRunning = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isHireFragmentOpen = true;
    }
}
