package com.beza.briver.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.beza.briver.MainActivity;
import com.beza.briver.WelcomeActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by fi8er1 on 26/04/2016.
 */
public class AppGlobals extends Application {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LAST_KNOWN_LOCATION = "last_known_location";
    private static final String DRIVER_DOC_ONE = "doc_one";
    private static final String DRIVER_DOC_TWO = "doc_two";
    private static final String DRIVER_DOC_THREE = "doc_three";
    private static final String DRIVER_SEARCH_RADIUS = "driver_radius";
    private static final String DRIVER_SERVICE_STATUS = "driver_service_status";
    private static final String ALARM_STATUS = "alarm_status";
    private static final String LOGGED_IN = "logged_in";
    private static final String PUSH_NOTIFICATIONS_ENABLED = "push_notifications_enabled";
    private static final String USER_NAME = "user_name";
    private static final String PERSON_NAME = "person_name";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_ID = "user_id";
    private static final String USER_TYPE = "user_type";
    private static final String REVIEW_COUNT = "review_count";
    private static final String STARS_VALUE = "stars_value";
    private static final String LOCATION_INTERVAL = "location_interval";
    private static final String LOCATION_REPORTING_TYPE = "location_reporting_type";
    private static final String TOKEN = "token";
    private static final String PENDING_LOCATION_UPLOAD = "pending_location_update";
    private static final String GCM_TOKEN = "gcm_token";
    private static final String USER_DATA = "user_data";
    private static final String DRIVER_BIO = "driver_bio";
    private static final String DRIVING_EXPERIENCE = "driving_experience";
    private static final String VEHICLE_TYPE = "vehicle_type";
    private static final String VEHICLE_MAKE = "vehicle_make";
    private static final String VEHICLE_MODEL = "vehicle_model";
    private static final String NUMBER_OF_HIRES = "total_hires";
    private static final String TRANSMISSION_TYPE = "transmission_type";
    private static final String FIRST_RUN_HIRE_FRAGMENT = "first_run_hire_fragment";
    private static final String FIRST_RUN_NEARBY_DRIVERS_FRAGMENT = "first_run__fragment";
    private static Context sContext;
    private static SharedPreferences sPreferences;

    public static Context getContext() {
        return sContext;
    }

    public static boolean isLoggedIn() {
        return sPreferences.getBoolean(LOGGED_IN, false);
    }

    public static void setPushNotificationsEnabled(boolean enabled) {
        sPreferences.edit().putBoolean(PUSH_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public static boolean isPushNotificationsEnabled() {
        return sPreferences.getBoolean(PUSH_NOTIFICATIONS_ENABLED, false);
    }

    public static void setLoggedIn(boolean loggedIn) {
        sPreferences.edit().putBoolean(LOGGED_IN, loggedIn).apply();
    }

    public static boolean isLocationUploadPending() {
        return sPreferences.getBoolean(PENDING_LOCATION_UPLOAD, false);
    }

    public static void setLocationUploadPending(boolean pendingLocationUpload) {
        sPreferences.edit().putBoolean(PENDING_LOCATION_UPLOAD, pendingLocationUpload).apply();
    }

    public static boolean isAlarmSet() {
        return sPreferences.getBoolean(ALARM_STATUS, false);
    }

    public static void setAlarmStatus(boolean status) {
        sPreferences.edit().putBoolean(ALARM_STATUS, status).apply();
    }

    public static float getStarsValue() {
        return sPreferences.getFloat(STARS_VALUE, (float) 0.0);
    }

    public static void putStarsValue(float value) {
        sPreferences.edit().putFloat(STARS_VALUE, value).apply();
    }

    public static boolean isHireFragmentFirstRun() {
        return sPreferences.getBoolean(FIRST_RUN_HIRE_FRAGMENT, true);
    }

    public static void setHireFragmentFirstRun(boolean firstRunHireFragment) {
        sPreferences.edit().putBoolean(FIRST_RUN_HIRE_FRAGMENT, firstRunHireFragment).apply();
    }

    public static boolean isNearbyDriversFragmentFirstRun() {
        return sPreferences.getBoolean(FIRST_RUN_NEARBY_DRIVERS_FRAGMENT, true);
    }

    public static void setNearbyDriversFragmentFirstRun(boolean firstRunHireFragment) {
        sPreferences.edit().putBoolean(FIRST_RUN_NEARBY_DRIVERS_FRAGMENT, firstRunHireFragment).apply();
    }

    public static void putUsername(String username) {
        sPreferences.edit().putString(USER_NAME, username).apply();
    }

    public static String getUsername() {
        return sPreferences.getString(USER_NAME, null);
    }

    public static void putDocOne(String docOne) {
        sPreferences.edit().putString(DRIVER_DOC_ONE, docOne).apply();
    }

    public static String getDocOne() {
        return sPreferences.getString(DRIVER_DOC_ONE, null);
    }

    public static void putDocTwo(String docTwo) {
        sPreferences.edit().putString(DRIVER_DOC_TWO, docTwo).apply();
    }

    public static String getDocTwo() {
        return sPreferences.getString(DRIVER_DOC_TWO, null);
    }

    public static void putDocThree(String docThree) {
        sPreferences.edit().putString(DRIVER_DOC_THREE, docThree).apply();
    }

    public static String getDocThree() {
        return sPreferences.getString(DRIVER_DOC_THREE, null);
    }


    public static void putPhoneNumber(String phone) {
        sPreferences.edit().putString(PHONE_NUMBER, phone).apply();
    }

    public static String getPhoneNumber() {
        return sPreferences.getString(PHONE_NUMBER, null);
    }


    public static void putVehicleMake(String vehicleMake) {
        sPreferences.edit().putString(VEHICLE_MAKE, vehicleMake).apply();
    }

    public static String getVehicleMake() {
        return sPreferences.getString(VEHICLE_MAKE, null);
    }

    public static void putVehicleModel(String vehicleModel) {
        sPreferences.edit().putString(VEHICLE_MODEL, vehicleModel).apply();
    }

    public static String getVehicleModel() {
        return sPreferences.getString(VEHICLE_MODEL, null);
    }

    public static void putDriverBio(String bio) {
        sPreferences.edit().putString(DRIVER_BIO, bio).apply();
    }

    public static String getDriverBio() {
        return sPreferences.getString(DRIVER_BIO, null);
    }

    public static void putVehicleType(int vehicleType) {
        sPreferences.edit().putInt(VEHICLE_TYPE, vehicleType).apply();
    }

    public static int getVehicleType() {
        return sPreferences.getInt(VEHICLE_TYPE, 2);
    }

    public static void putRatingCount(String reviewCount) {
         sPreferences.edit().putString(REVIEW_COUNT, reviewCount).apply();
    }

    public static String getRatingCount() {
        return sPreferences.getString(REVIEW_COUNT, "0");
    }

    public static void putUserID(int userID) {
        sPreferences.edit().putInt(USER_ID, userID).apply();
    }

    public static int getUserID() {
        return sPreferences.getInt(USER_ID, -1);
    }

    public static void putTransmissionType(int transmissionType) {
        sPreferences.edit().putInt(TRANSMISSION_TYPE, transmissionType).apply();
    }

    public static int getTransmissionType() {
        return sPreferences.getInt(TRANSMISSION_TYPE, -1);
    }

    public static void putLocationReportingType(int locationReportingType) {
        sPreferences.edit().putInt(LOCATION_REPORTING_TYPE, locationReportingType).apply();
    }

    public static int getLocationReportingType() {
        return sPreferences.getInt(LOCATION_REPORTING_TYPE, 1);
    }


    public static void putDrivingExperience(String drivingExperience) {
        sPreferences.edit().putString(DRIVING_EXPERIENCE, drivingExperience).apply();
    }

    public static String getDrivingExperience() {
        return sPreferences.getString(DRIVING_EXPERIENCE, "-1");
    }

    public static void putDriverSearchRadius(int radius) {
        sPreferences.edit().putInt(DRIVER_SEARCH_RADIUS, radius).apply();
    }

    public static int getDriverSearchRadius() {
        return sPreferences.getInt(DRIVER_SEARCH_RADIUS, 15);
    }

    public static void putDriverServiceStatus(int status) {
        sPreferences.edit().putInt(DRIVER_SERVICE_STATUS, status).apply();
    }

    public static int getDriverServiceStatus() {
        return sPreferences.getInt(DRIVER_SERVICE_STATUS, 1);
    }

    public static void saveUserDataForPushNotifications(String userData) {
        sPreferences.edit().putString(USER_DATA, userData).apply();
    }

    public static String getUserDataForPushNotifications() {
        return sPreferences.getString(USER_DATA, null);
    }

    public static void putToken(String token) {
        sPreferences.edit().putString(TOKEN, token).apply();
    }

    public static String getToken() {
        return sPreferences.getString(TOKEN, null);
    }

    public static void putGcmToken(String gcmToken) {
        sPreferences.edit().putString(GCM_TOKEN, gcmToken).apply();
    }

    public static String getGcmToken() {
        return sPreferences.getString(GCM_TOKEN, null);
    }

    public static String getPeronName() {
        return sPreferences.getString(PERSON_NAME, null);
    }

    public static void putPersonName(String name) {
        sPreferences.edit().putString(PERSON_NAME, name).apply();
    }

    public static int getNumberOfHires() {
        return sPreferences.getInt(NUMBER_OF_HIRES, 0);
    }

    public static void putNumberOfHires(int hires) {
        sPreferences.edit().putInt(NUMBER_OF_HIRES, hires).apply();
    }

    public static int getUserType() {
        return sPreferences.getInt(USER_TYPE, 0);
    }

    public static void putUserType(int userType) {
        sPreferences.edit().putInt(USER_TYPE, userType).apply();
    }

    public static int getDriverLocationReportingIntervalTime() {
        return sPreferences.getInt(LOCATION_INTERVAL, 2);
    }

    public static void putDriverLocationReportingIntervalTime(int interval) {
        sPreferences.edit().putInt(LOCATION_INTERVAL, interval).apply();
    }

    public static void putUserPassword(String password) {
        sPreferences.edit().putString(USER_PASSWORD, password).apply();
    }

    public static String getUserPassword() {
        return sPreferences.getString(USER_PASSWORD, null);
    }

    public static Activity getRunningActivityInstance() {
        if (MainActivity.isMainActivityRunning) {
            return MainActivity.getInstance();
        } else if (WelcomeActivity.isWelcomeActivityRunning) {
            return WelcomeActivity.getInstance();
        }
        return null;
    }

    public static boolean checkPlayServicesAvailability() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getRunningActivityInstance());
        if (resultCode != ConnectionResult.SUCCESS) {
            Helpers.AlertDialogWithPositiveNegativeFunctions(getRunningActivityInstance(), "PlayServices not found",
                    "You need to install Google Play Services to continue using Briver", "Install", "Exit App", Helpers.openPlayServicesInstallation, Helpers.exitApp);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
