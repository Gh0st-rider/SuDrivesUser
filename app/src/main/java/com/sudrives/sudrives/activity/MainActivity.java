package com.sudrives.sudrives.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.fragment.HomeFragment;
import com.sudrives.sudrives.fragment.HomeLocationSelectFragment;
import com.sudrives.sudrives.fragment.MyBookingFragment;
import com.sudrives.sudrives.fragment.ReportIssueListFragement;
import com.sudrives.sudrives.fragment.ReportIssueListFragementNew;

public class MainActivity extends AppCompatActivity {

    String mlat = "", mlongi = "";
    private boolean doubleBackToExitPressedOnce = false;


    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.container_home, new HomeLocationSelectFragment(), "").addToBackStack("Homemenu");
        ft.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
    }


    private void getData() {

        mlat = getIntent().getStringExtra("lat");
        mlongi = getIntent().getStringExtra("long");
        //   Log.e(" Home Menu  Lat and Lng", mlat + "   " + mlongi);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce = false;
            }
        }, 3000);


    }

   // @SuppressWarnings("StatementWithEmptyBody")
   // @Override
    /*public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_home:
                iv_notification.setImageResource(R.drawable.notification_icon);

                if (!(fragment instanceof HomeFragment)) {
                    fragment = new HomeFragment();
                    toolbarTitle.setText(getResources().getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                }
                break;


            case R.id.nav_bookings:
                showFindingDriver = false;
                if (!(fragment instanceof MyBookingFragment)) {
                    fragment = new MyBookingFragment();
                    toolbarTitle.setText(getResources().getString(R.string.my_bookings));

                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                }
                break;


            case R.id.nav_payment:


                startActivity(new Intent(MainActivity.this, MyWalletActivity.class));


                break;


            case R.id.nav_offers:

                startActivity(new Intent(MainActivity.this, OffersActivity.class));

                break;

            case R.id.nav_refernwin:
                showFindingDriver = false;
                startActivity(new Intent(MainActivity.this, ReferNWinActivity.class));
                break;

            case R.id.nav_report_issue:
                showFindingDriver = false;

                // startActivity(new Intent(HomeMenuActivity.this, ReportIssueListActivity.class));

                if (!toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.reported_issue))) {
                    if (!(fragment instanceof ReportIssueListFragement)) {
                        fragment = new ReportIssueListFragementNew();
                        toolbarTitle.setText(getResources().getString(R.string.reported_issue));

                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                    }
                }
                break;

            case R.id.nav_contactus:
                showFindingDriver = false;

                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));

                break;

            case R.id.nav_aboutus:
                showFindingDriver = false;

                startActivity(new Intent(MainActivity.this, AboutActivity.class));

                break;

            case R.id.nav_logout:
                onShowdialog(true, getResources().getString(R.string.are_you_sure_want_to_logout));
                break;
        }
        if (!(fragment instanceof HomeFragment)) {
            ib_left.setImageResource(R.drawable.arrow_back_24dp);
        }
        if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.livetrip))) {
            tv_notification_badge.setVisibility(View.GONE);
            //iv_help.setImageResource(R.mipmap.help);
            iv_help.setVisibility(View.GONE);
        } else if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.reported_issue))) {


        } else {
            iv_notification.setVisibility(View.VISIBLE);
            iv_help.setVisibility(View.GONE);
            iv_notification.setImageResource(R.drawable.notification_icon);
            showBadge();

            //     tv_notification_badge.setVisibility(View.VISIBLE);
        }
        navigationView.getMenu().getItem(0).setChecked(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }*/
}