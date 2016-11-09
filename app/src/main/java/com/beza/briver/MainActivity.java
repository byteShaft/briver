package com.beza.briver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beza.briver.fragments.ChangePasswordFragment;
import com.beza.briver.fragments.ContactUsFragment;
import com.beza.briver.fragments.ContractBriverHiringFragment;
import com.beza.briver.fragments.HireFragment;
import com.beza.briver.fragments.HomeFragment;
import com.beza.briver.fragments.PaymentDetailsFragment;
import com.beza.briver.fragments.PreferencesFragment;
import com.beza.briver.fragments.ProfileFragment;
import com.beza.briver.fragments.TermsFragment;
import com.beza.briver.fragments.TimelineFragment;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.DriverLocationAlarmHelper;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.WebServiceHelpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import static com.beza.briver.Tasks.HiringTask.isHiringTaskRunning;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isMainActivityRunning;
    public static TextView tvPersonName;
    public static int responseCode;
    static FragmentManager fragmentManager;
    private static MainActivity sInstance;
    final Runnable logout = new Runnable() {
        public void run() {
            AppGlobals.setLoggedIn(false);
            AppGlobals.setPushNotificationsEnabled(false);
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            if (AppGlobals.getUserType() == 1) {
                DriverLocationAlarmHelper.cancelAlarm();
            }
            finish();
        }
    };
    boolean isDriverStatusTaskRunning;
    Fragment fragment;
    String fragmentName = "";
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    DriverStatusTask taskDriverStatus;
    HttpURLConnection connection;

    public static MainActivity getInstance() {
        return sInstance;
    }

    public static String getDriverStatusPostingString() {
        String status;
        if (isMainActivityRunning) {
            status = "2";
        } else {
            status = "1";
        }
        return "{" +
                String.format("\"status\": \"%s\"", status) +
                "}";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sInstance = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);

        navigationView.getMenu().getItem(0).setChecked(true);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container_main, new HomeFragment()).addToBackStack("HomeFragment");
        tx.commit();

        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                navigationView.removeOnLayoutChangeListener(this);

                tvPersonName = (TextView) navigationView.findViewById(R.id.tv_nav_header_person_name);
                TextView tvPersonEmail = (TextView) navigationView.findViewById(R.id.tv_nav_header_person_email);
                RatingBar rBarNav = (RatingBar) navigationView.findViewById(R.id.rBar_nav_header);
                TextView tvNumberOfRatings = (TextView) navigationView.findViewById(R.id.tv_nav_header_number_of_ratings);

                float starsValue = AppGlobals.getStarsValue();

                if (starsValue > 0.0) {
                    rBarNav.setRating(starsValue);
                } else {
                    rBarNav.setRating((float) 0.0);
                }
                tvNumberOfRatings.setText("(" + AppGlobals.getRatingCount() + ")");
                tvPersonName.setText(AppGlobals.getPeronName());
                tvPersonEmail.setText(AppGlobals.getUsername());
            }
        });

        if (AppGlobals.getUserType() == 1) {
            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            if (!AppGlobals.isAlarmSet() && AppGlobals.getLocationReportingType() == 1) {
                DriverLocationAlarmHelper.setAlarm(AppGlobals.getDriverLocationReportingIntervalTime());
            }
            if (AppGlobals.getDriverServiceStatus() != 0 && Helpers.isNetworkAvailable(this)) {
                if (isDriverStatusTaskRunning) {
                    taskDriverStatus.cancel(true);
                }
                taskDriverStatus = (DriverStatusTask) new DriverStatusTask().execute();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                navigationView.getMenu().getItem(0).setChecked(true);
                toolbar.setTitle("Home");
                Log.i("Back", "Frag");
            } else {
                super.onBackPressed();
                super.onBackPressed();
                Log.i("Back", "Main");
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        boolean logoutCheck = false;
        fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentClass = HomeFragment.class;
            fragmentName = "HomeFragment";
        } else if (id == R.id.nav_hire) {
            fragmentClass = HireFragment.class;
            fragmentName = "HireFragment";
        } else if (id == R.id.nav_hire_timeline) {
            fragmentClass = TimelineFragment.class;
            fragmentName = "TimelineFragment";
        } else if (id == R.id.nav_profile) {
            fragmentClass = ProfileFragment.class;
            fragmentName = "ProfileFragment";
        } else if (id == R.id.nav_change_password) {
            fragmentClass = ChangePasswordFragment.class;
            fragmentName = "ChangePasswordFragment";
        } else if (id == R.id.nav_logout) {
            logoutCheck = true;
        } else if (id == R.id.nav_preference) {
            fragmentClass = PreferencesFragment.class;
            fragmentName = "PreferencesFragment";
        } else if (id == R.id.nav_contact) {
            fragmentClass = ContactUsFragment.class;
            fragmentName = "ContactUSFragment";
        } else if (id == R.id.nav_contract_briver_hiring) {
            fragmentClass = ContractBriverHiringFragment.class;
            fragmentName = "ContractBriverHiring";
        } else if (id == R.id.nav_payment_details) {
            fragmentClass = PaymentDetailsFragment.class;
            fragmentName = "PaymentDetails";
        } else if (id == R.id.nav_terms) {
            fragmentClass = TermsFragment.class;
            fragmentName = "TermsOfService";
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (!logoutCheck) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            item.setCheckable(true);
            setTitle(item.getTitle());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.beginTransaction().replace(R.id.container_main, fragment).addToBackStack(fragmentName).commit();
                }
            }, 350);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Helpers.AlertDialogWithPositiveFunctionNegativeButton(MainActivity.this,
                            "Logout", "Are you sure?", "Yes", "No", logout);
                }
            }, 350);
        }
        return true;
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainActivityRunning = true;
        if (AppGlobals.getUserType() == 1 && AppGlobals.getDriverServiceStatus() != 0 && Helpers.isNetworkAvailable(this)) {
            if (isDriverStatusTaskRunning) {
                taskDriverStatus.cancel(true);
            }
            taskDriverStatus = (DriverStatusTask) new DriverStatusTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivityRunning = false;

        if (isHiringTaskRunning) {
            HireFragment.taskHiringDriver.cancel(true);
        }

        if (AppGlobals.getUserType() == 1 && AppGlobals.getDriverServiceStatus() != 0 && Helpers.isNetworkAvailable(this)) {
            if (isDriverStatusTaskRunning) {
                taskDriverStatus.cancel(true);
            }
            taskDriverStatus = (DriverStatusTask) new DriverStatusTask().execute();
        }
    }

    private class DriverStatusTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDriverStatusTaskRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url;
            try {
                url = EndPoints.BASE_ACCOUNTS_ME;
                connection = WebServiceHelpers.openConnectionForUrl(url, "PUT", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getDriverStatusPostingString());
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isDriverStatusTaskRunning = false;
            if (responseCode == 200) {
                Log.i("StatusChanged", "Successful");
            } else {
                Log.i("StatusChanged", "Failed");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isDriverStatusTaskRunning = false;
        }
    }

}