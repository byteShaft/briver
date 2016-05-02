package com.byteshaft.briver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.byteshaft.briver.fragments.ChangePasswordFragment;
import com.byteshaft.briver.fragments.ContactUsFragment;
import com.byteshaft.briver.fragments.HireFragment;
import com.byteshaft.briver.fragments.HomeFragment;
import com.byteshaft.briver.fragments.PreferencesFragment;
import com.byteshaft.briver.fragments.ProfileFragment;
import com.byteshaft.briver.fragments.TimelineFragment;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        } else if (id == R.id.nav_hire) {
            fragmentClass = HireFragment.class;
        } else if (id == R.id.nav_hire_timeline) {
            fragmentClass = TimelineFragment.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = ProfileFragment.class;
        } else if (id == R.id.nav_change_password) {
            fragmentClass = ChangePasswordFragment.class;
        } else if (id == R.id.nav_logout) {
            logoutCheck = true;
        } else if (id == R.id.nav_preference) {
            fragmentClass = PreferencesFragment.class;
        } else if (id == R.id.nav_contact) {
            fragmentClass = ContactUsFragment.class;
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
                    FragmentManager fragmentManager = getSupportFragmentManager();
                     fragmentManager.beginTransaction().replace(R.id.container_main, fragment).commit();
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


    final Runnable logout = new Runnable() {
        public void run() {
            AppGlobals.setLoggedIn(false);
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }
    };

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }
}
