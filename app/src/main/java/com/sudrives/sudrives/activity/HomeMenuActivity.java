package com.sudrives.sudrives.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.ListenersSocket;
import com.sudrives.sudrives.direction.DrawPolylineTask;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.fragment.FindADriverFragment;
import com.sudrives.sudrives.fragment.HomeFragment;
import com.sudrives.sudrives.fragment.HomeLocationSelectFragment;
import com.sudrives.sudrives.fragment.LiveFragment;
import com.sudrives.sudrives.fragment.MyBookingFragment;
import com.sudrives.sudrives.fragment.ReportIssueListFragement;
import com.sudrives.sudrives.fragment.ReportIssueListFragementNew;
import com.sudrives.sudrives.receiver.MySMSBroadCastReceiver;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GPSTracker;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.GpsTrackerShashank;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.socket.emitter.Emitter;

public class HomeMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SocketConnection.SocketStatusCallBacks, LocationListener, DrawPolylineTask.onDrawPolylineTaskComplete {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView toolbarTitle, tv_notification_badge,tvName, tvNumber, tvLanguage;
    RatingBar ratingBar;
    private static Context mContext;
    private static SessionPref mSessionPref;
    public static int mbadgeCounts = 0;
    private int count = 0;
    private ImageView ivProfile, iv_notification;
    TextView iv_help;
    public Fragment fragment;
    private ErrorLayout mErrorLayout;
    private String mVehicleResponse = "";
    private String mAddress = "";
    private double mLong = 0.0, mLat = 0.0;
    private final int GPS_ENABLE_REQUEST = 0x1001;
    private ArrayList<String> useridArrayList;
    protected LocationManager mLocationManager;
    private NavigationView navigationView;
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en";
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE


    };
    public static final int PERMISSION_ALL = 134;
    private boolean callHome = true;
    private ImageButton ib_left;
    private String trip_id = "";
    private boolean doubleBackToExitPressedOnce = false;
    public boolean flagLiveTripCall = true;
    private boolean showLiveTrip = false;
    private boolean showFindingDriver = false;
    private boolean flagShow = true;
    private LocationManager locationManager;
    private CountDownTimer mCustomCountdownTimer;
    private double mlat = 0.0, mlongi = 0.0;
    private boolean changeLang = true;
    private String mStrLiveTrip = "0";
    AlertDialog show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        getData();
        getControl();
        // printHashKey(getApplicationContext());
    }

    private void getData() {

        mlat = new GpsTrackerShashank(this).getLatitude();//getIntent().getDoubleExtra("lat", 0.0);
        mlongi = new GpsTrackerShashank(this).getLongitude();//getIntent().getDoubleExtra("long", 0.0);
        //   Log.e(" Home Menu  Lat and Lng", mlat + "   " + mlongi);

    }

    private void getControl() {

        mContext = HomeMenuActivity.this;
        mSessionPref = new SessionPref(mContext);
        GlobalUtil.setStatusBarColor(HomeMenuActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        disableBroadcastReceiver();

        SocketConnection.getInstance(mContext, this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarOptionTitle);

        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        iv_notification = (ImageView) toolbar.findViewById(R.id.iv_notification);
        iv_help = (TextView) toolbar.findViewById(R.id.iv_help);
        tv_notification_badge = (TextView) toolbar.findViewById(R.id.tv_notification_badge);

        ib_left = (ImageButton) toolbar.findViewById(R.id.ib_left);
        ib_left.setImageResource(R.drawable.drawer_menu_24dp);
        ib_left.setOnClickListener(this);




        View header = navigationView.getHeaderView(0);
        toolbarTitle.setText(getResources().getString(R.string.home));
        KeyboardUtil.setupUI(drawer, HomeMenuActivity.this);
        //  tvName = header.findViewById(R.id.tvName);
        tvName = header.findViewById(R.id.tvNameNav);
        ratingBar = header.findViewById(R.id.ratingBarNav);
        tvNumber = header.findViewById(R.id.tvNumberNav);

        fragment = new HomeLocationSelectFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).addToBackStack(null).commit();

        tvLanguage = header.findViewById(R.id.tvLanguage);
        tvLanguage.setOnClickListener(this);
        //FontLoader.setHelRegular(toolbarTitle, tvNumber);
        ivProfile = header.findViewById(R.id.ivProfile);
        Glide.with(HomeMenuActivity.this).load(mSessionPref.user_pic).placeholder(R.drawable.user_24dp).into(ivProfile);

        if (mSessionPref.user_fullname != null) {
            tvName.setText(mSessionPref.user_fname);
        } else {
            tvName.setVisibility(View.GONE);
        }

        if (mSessionPref.user_mobile != null) {
            tvNumber.setText("+91-" + mSessionPref.user_mobile);
        } else {
            tvNumber.setVisibility(View.GONE);
        }

        LinearLayout llNavHeader = header.findViewById(R.id.llNavHeader);
        llNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(mContext, ProfileActivity.class));


            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(mContext, ProfileActivity.class));
            }
        });

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.livetrip))) {

                } else {
                    showFindingDriver = false;
                    startActivity(new Intent(HomeMenuActivity.this, NotificationActivity.class));
                }
            }
        });

        if (SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE) != null) {
            String localeString = SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE);
            if (localeString.equalsIgnoreCase(HINDI_LOCALE)) {

                tvLanguage.setText(getResources().getString(R.string.en));

            } else {
                tvLanguage.setText(getResources().getString(R.string.hn));
            }
        }

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.ib_left:
                if (mSessionPref.user_mobile != null) {
                    tvNumber.setText("+91-" + mSessionPref.user_mobile);
                } else {
                    tvNumber.setVisibility(View.GONE);
                }
                Glide.with(HomeMenuActivity.this).load(mSessionPref.user_pic).placeholder(R.drawable.user_24dp).into(ivProfile);

                if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home))) {
                    ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                    iv_notification.setImageResource(R.drawable.notification_icon);
                    iv_notification.setVisibility(View.VISIBLE);
                    iv_help.setVisibility(View.GONE);
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    iv_notification.setClickable(true);
                    iv_notification.setEnabled(true);
                    ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                    iv_notification.setVisibility(View.VISIBLE);
                    iv_help.setVisibility(View.GONE);
                    toolbarTitle.setText(getResources().getString(R.string.home));
                    iv_notification.setImageResource(R.drawable.notification_icon);
                    fragment = new HomeLocationSelectFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                    navigationView.getMenu().getItem(0).setChecked(true);
                    showBadge();
                }

                break;
            case R.id.tvLanguage:
                if (SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE).equalsIgnoreCase("en")) {
                    tvLanguage.setText(getResources().getString(R.string.hn));
                    Config.SELECTED_LANG = getResources().getString(R.string.hindi);
                    setLocales(HINDI_LOCALE);

                } else {
                    tvLanguage.setText(getResources().getString(R.string.en));
                    Config.SELECTED_LANG = getResources().getString(R.string.english);
                    setLocales(ENGLISH_LOCALE);

                }

                break;


        }
    }

    public void setLocales(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, lang);

        drawer.closeDrawer(GravityCompat.START);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recreate();
        super.onConfigurationChanged(newConfig);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

       /* if (fragment instanceof LiveFragment){
            exitDialog();
        }else {*/
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {

            }
            LayoutInflater inflater = getLayoutInflater();
            // Inflate the Layout
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_layout));

            TextView text = (TextView) layout.findViewById(R.id.textToShow);
            //Set the Text to show in TextView
            text.setText(getResources().getString(R.string.press_again_to_exit));

            if (fragment instanceof HomeLocationSelectFragment) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    exitDialog();

               /* if (doubleBackToExitPressedOnce) {
                    exitDialog();
                    return;
                }*/
                }
            /*this.doubleBackToExitPressedOnce = true;
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);*/


            } else {
                if (fragment instanceof FindADriverFragment) {
                    // getActionBar().hide();
                } else {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {

                    }
                    toolbarTitle.setText(getResources().getString(R.string.home));
                    getSupportActionBar().show();
                    fragment = new HomeLocationSelectFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                    navigationView.getMenu().getItem(0).setChecked(true);
                    if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home))) {
                        ib_left.setImageResource(R.drawable.drawer_menu_24dp);

                        iv_notification.setImageResource(R.drawable.notification_icon);
                        iv_notification.setVisibility(View.VISIBLE);
                        iv_help.setVisibility(View.GONE);
                        //drawer.openDrawer(GravityCompat.START);
                        showBadge();
                    } else {

                    }
                }
            }
        //}


    }

    private void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage(getString(R.string.are_your_sure_you_want_to_exit));

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finishAffinity();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_home:
                iv_notification.setImageResource(R.drawable.notification_icon);

                if (!(fragment instanceof HomeLocationSelectFragment)) {
                    fragment = new HomeLocationSelectFragment();
                    toolbarTitle.setText(getResources().getString(R.string.home));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                }
                break;


            case R.id.nav_bookings:
                showFindingDriver = false;
                if (!(fragment instanceof MyBookingFragment)) {
                    fragment = new MyBookingFragment();
                    toolbarTitle.setText(getResources().getString(R.string.my_bookings));

                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).addToBackStack("nav").commit();
                }
                break;


            case R.id.nav_payment:


                startActivity(new Intent(HomeMenuActivity.this, MyWalletActivity.class));


                break;


            case R.id.nav_offers:

                startActivity(new Intent(HomeMenuActivity.this, OffersActivity.class));

                break;

            case R.id.nav_refernwin:
                showFindingDriver = false;
                startActivity(new Intent(HomeMenuActivity.this, ReferNWinActivity.class));
                break;

            case R.id.nav_report_issue:
                showFindingDriver = false;

                // startActivity(new Intent(HomeMenuActivity.this, ReportIssueListActivity.class));

                if (!toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.reported_issue))) {
                    if (!(fragment instanceof ReportIssueListFragement)) {
                        fragment = new ReportIssueListFragementNew();
                        toolbarTitle.setText(getResources().getString(R.string.reported_issue));

                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).addToBackStack("nav").commit();
                    }
                }
                break;

            case R.id.nav_contactus:
                showFindingDriver = false;

                Intent i=new Intent(mContext,WebviewActivity.class);
                i.putExtra("tool_title","Contact Us");
                i.putExtra("value","https://sudrives.com/contactus.html");
                startActivity(i);

                break;

            case R.id.nav_aboutus:
                showFindingDriver = false;

                Intent i1=new Intent(mContext,WebviewActivity.class);
                i1.putExtra("tool_title","About Us");
                i1.putExtra("value","https://sudrives.com/aboutus.html");
                startActivity(i1);

                break;

            case R.id.nav_logout:
                onShowdialog(true, getResources().getString(R.string.are_you_sure_want_to_logout));
                break;
        }
        if (!(fragment instanceof HomeLocationSelectFragment)) {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    protected void onDestroy() {
        SocketConnection.disconnect();
        mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyboardUtil.hideKeyboard(this);
        try {
            try {
                mSessionPref = new SessionPref(HomeMenuActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
                finishAffinity();
                startActivity(new Intent(HomeMenuActivity.this, Splash.class));
            }
            if (ConnectivityReceiver.isConnected()) {
                if (!showFindingDriver) {
                    notificationListApi();
                } else {
                }
            } else {
                //  mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

            }

            if (flagShow) {
                init();
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(ConfigNotif.PUSH_NOTIFICATION_USER));

            // clear the notification area when the app is opened
            NotificationUtils.clearNotifications(getApplicationContext());
            navigationView.getMenu().getItem(0).setChecked(true);


            if (showLiveTrip) {                 //  condition to redirect on live trip when app is in background and request accepted by driver
                showLiveTrip = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("liveeeeeeeeee",showLiveTrip+"");

                        fragment = new LiveFragment();
                        try {

                            getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commit();

                            iv_help.setVisibility(View.GONE);
                            iv_notification.setVisibility(View.GONE);
                            tv_notification_badge.setVisibility(View.GONE);

                            toolbarTitle.setText(getResources().getString(R.string.livetrip));
                            ib_left.setImageResource(R.drawable.arrow_back_24dp);
                        } catch (Exception e) {

                            ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                            iv_notification.setImageResource(R.drawable.notification_icon);
                            iv_notification.setVisibility(View.VISIBLE);
                            iv_help.setVisibility(View.GONE);
                            toolbarTitle.setText(getResources().getString(R.string.home));
                            fragment = new HomeLocationSelectFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commit();

                            e.printStackTrace();
                        }
                        //  }
                    }
                });
            }
            if (callHome) {
                callHome = false;
                fragment = new HomeLocationSelectFragment();
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishAffinity();
                    startActivity(new Intent(HomeMenuActivity.this, Splash.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishAffinity();
            startActivity(new Intent(HomeMenuActivity.this, Splash.class));

        }

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }

    //check permission
    void startCheckPermissions() {
        if (!canAccessCoarseLocation() && !canAccessFineLocation()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
        } else {
            if (callHome) {
                callHome = false;
                fragment = new HomeLocationSelectFragment();
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private boolean canAccessExternalStorage() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }


    private boolean canAccessFineLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoarseLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }


    private void init() {
        if (!hasPermissions(mContext, INITIAL_PERMS)) {
            // showSettingsAlert();
            ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, PERMISSION_ALL);
            startCheckPermissions();
        } else {

            if (callHome) {
                callHome = false;
                fragment = new HomeLocationSelectFragment();
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        //  Log.e("Lat and Lng Home", location.getLatitude() + "   " + location.getLongitude());
        // latCurrent = location.getLatitude();
        // longCurrent = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            //showGPSDiabledDialog(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                if (grantResults.length > 0) {
                    boolean value = false;

                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            value = true;
                        }
                    }
                    if (value) {
                        fragment = new HomeLocationSelectFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                    } else {

                    }

                } else {
                    startActivity(new Intent(HomeMenuActivity.this, Splash.class));
                }
            }
            break;

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPS_ENABLE_REQUEST) {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            }
        } else {
            if (requestCode == GetLocation.REQUEST_CHECK_SETTINGS) {
                /*if (fragment instanceof HomeLocationSelectFragment) {
                    ((HomeFragment) fragment).onLocationEnabled();
                }*/
                if (fragment instanceof HomeFragment) {
                    ((HomeFragment) fragment).onLocationEnabled();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        //  }
    }
    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     * @param view
     */
    public void disableBroadcastReceiver(){
        ComponentName receiver = new ComponentName(this, MySMSBroadCastReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
       // Toast.makeText(this, "Disabled broadcst receiver", Toast.LENGTH_SHORT).show();
    }
    private void onShowdialog(boolean popupShow, String message) {
        int layoutPopup = 0;
        boolean cancleBtn = false;
        String title = "", btnYes = "", btnNo = "";

        layoutPopup = R.layout.duuble_button_dialog;
        if (popupShow) {
            cancleBtn = true;
            title = getResources().getString(R.string.logout);
            btnYes = getResources().getString(R.string.yes);
            btnNo = getResources().getString(R.string.no);

        } else {
            cancleBtn = false;
            title = getResources().getString(R.string.cancel_booking);
            btnYes = getResources().getString(R.string.ok);
            btnNo = getResources().getString(R.string.cancel);
        }

        AppDialogs.DoubleButtonWithCallBackVersionDialog(HomeMenuActivity.this, layoutPopup, cancleBtn, false, 0, message,
                title, btnYes,
                btnNo,
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        if (popupShow) {
                            requestLogout();
                            //  SessionPref.clearSharedPref(HomeMenuActivity.this);

                        } else {

                        }

                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {
                        toolbarTitle.setText(getString(R.string.home));
                        navigationView.getMenu().getItem(0).setChecked(true);
                    }


                }, new AppDialogs.Crosscallback() {

                    @Override
                    public void crossButtonCallback(String from) {

                    }


                });
    }


    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // checking for type intent filter
            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received
                String response = intent.getStringExtra("response");
                if (ConnectivityReceiver.isConnected()) {
                    if (showFindingDriver) {
                        notificationListApi();
                    } else {

                    }
                } else {
                    //   dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
                }

            }
        }
    };

    @Override
    public void drawPolyline(PolylineOptions lineOptions, String origin, String destination) {
        //  Log.e("Route Drawn", "New Route drawn");

        if (fragment instanceof LiveFragment) {
            ((LiveFragment) fragment).onDirectionResponse(lineOptions, origin, destination);
        }

        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).onDirectionResponse(lineOptions, origin, destination);
        }

    }

    private void notificationListApi() {
        String user_id = "";

        try {
            mSessionPref = new SessionPref(mContext);

            user_id = mSessionPref.user_userid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseRequest mBaseRequest = new BaseRequest(mContext, false);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", user_id, "token", mSessionPref.token, "language", Config.SELECTED_LANG, "page", "0");

        mBaseRequest.setBaseRequest(jsonObj, "api/get_notifications", user_id, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                try {
                    mbadgeCounts = 0;
                    if (response != null) {
                        JSONObject JsonRes = new JSONObject(response.toString());

                        if (JsonRes.getString("status").equals("1")) {
                            JSONArray arr = JsonRes.getJSONArray("result");
                            count = Integer.parseInt(JsonRes.optString("notification_unread", ""));
                            mbadgeCounts = count;

                            if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.livetrip))) {
                            } else if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.reported_issue))) {
                            } else if (toolbarTitle.getText().toString().equalsIgnoreCase(getResources().getString(R.string.finding_a_ride))) {
                            } else {
                                iv_notification.setImageResource(R.drawable.notification_icon);
                                iv_notification.setVisibility(View.VISIBLE);
                                showBadge();
                            }
                        } else {
                            tv_notification_badge.setVisibility(View.GONE);
                            iv_notification.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void getError(Object response, String error) {
                //   if (!checkConnection()) {
                //    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                //  } else {
                //   mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                //  }

            }
        }, false);

    }

    private void requestLogout() {
        try {
            if (SocketConnection.isConnected()) {
                SocketConnection.emitToServer(Config.EMIT_DISCONNECT, mSessionPref.user_userid);
                try {
                    SocketConnection.emitToServer(Config.EMIT_LOGOUT, ListenersSocket.geJasonForSocket(mContext));
                    navigationView.getMenu().getItem(0).setChecked(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SocketConnection.attachSingleEventListener(Config.LISTNER_GET_LOGOUT, getLogoutRes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *set title for finding driver screen*/
    private void titleFindingDriver() {
        ib_left.setVisibility(View.GONE);
        iv_help.setVisibility(View.GONE);
        iv_notification.setVisibility(View.GONE);
        tv_notification_badge.setVisibility(View.GONE);
    }
    /*
     *
     * Notification badge show and hide*/

    private void showBadge() {
        if (mbadgeCounts > 0) {
            tv_notification_badge.setVisibility(View.VISIBLE);
            tv_notification_badge.setText(String.valueOf(mbadgeCounts));
        } else {
            tv_notification_badge.setVisibility(View.GONE);
        }

    }

    @Override
    public void onSocketConnected() {
        //  Log.e("I am connected", "I am connected" + "   " + mSessionPref.user_userid);

        if (mSessionPref.user_userid != null) {
            if (ConnectivityReceiver.isConnected()) {
                try {
                    SocketConnection.emitToServer(Config.EMIT_ROOM, mSessionPref.user_userid);

                    ListenersSocket.requestTripCompleteList(mContext);
                    ListenersSocket.requestBookingStatus(mContext);
                    ListenersSocket.requestprofile(mContext);
                    responseVehichleList();
                    SocketConnection.attachListener(Config.LISTNER_GET_BOOKING_ACCEPT, getacceptedTrip);
                    SocketConnection.attachListener(Config.LISTENER_GET_RESPONSE_COMPLETE_START, getStartTrip);
                    SocketConnection.attachListener(Config.LISTNER_GET_RESPONSE_END_TRIP, endTripListner);
                    SocketConnection.attachListener(Config.LISTENER_GET_RESPONSE_COMPLETE_TRIP, getCompleteTrip);
                    SocketConnection.attachListener(Config.LISTENER_GET_RESPONSE_DRIVER_CURRENT_LOCATION_UPDATE, getDriverCurrentLocationListener);
                    SocketConnection.attachListener(Config.LISTNER_GET_BOOKING_AUTO_CANCEL, getAutoCancel);
                    SocketConnection.attachListener(Config.LISTNER_GET_RESPONSE_CANCELLED_TRIP, cancelledTripListner);
                    SocketConnection.attachListener(Config.LISTNER_GET_RESPONSE_ARRIVED_TRIP, getArrivedTrip);
                    SocketConnection.attachListener(Config.LISTENER_GET_PROFILE, getProfile);
                    SocketConnection.attachListener(Config.LISTENER_GET_BOOKING_STATUS, bookingStatusListner);
                    SocketConnection.attachListener(Config.LISTENER_GET_DRIVER_PROJECTION, getDriverProjectListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

            }
        }
    }

    @Override
    public void onSocketDisconnected() {
        Log.e("Socket Dis-Connected", "Socket Dis-Connected");


    }

    @Override
    public void onSocketReconnected() {
        Log.e("Socket Re-Connected", "Socket Re-Connected");
    }

    @Override
    public void onSocketConnectionError() {
         Log.e("Socket Error", "Socket Error");
    }

    @Override
    public void onSocketConnectionTimeout() {
        Log.e("Socket Timeout", "Socket Timeout");
    }

    public void requestVehichleList(String vehicleFor, String lat, String longi) {
        Log.e("request  ======>", lat+  " : "  +longi+  " : "  +vehicleFor);

        JSONObject jsonObject = new JSONObject();
        SocketConnection.detachListener(Config.LISTENER_GET_VEHICHLE, getVehichleListener);
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("vehicle_for", vehicleFor);
            jsonObject.put("language", Config.SELECTED_LANG);

            if (lat.equalsIgnoreCase("0.0") && longi.equalsIgnoreCase("0.0")) {
                jsonObject.put("user_lat", mlat);
                jsonObject.put("user_long", mlongi);

            } else {
                jsonObject.put("user_lat", lat);
                jsonObject.put("user_long", longi);
            }
            //Log.e("request  ======>", jsonObject.toString());


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("requestError======>",e+"");
        }
        SocketConnection.emitToServer(Config.EMIT_GET_VEHICHLES, jsonObject);
    }

    public void responseVehichleList() {
        SocketConnection.attachListener(Config.LISTENER_GET_VEHICHLE, getVehichleListener);

    }

    Emitter.Listener getVehichleListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            mStrLiveTrip = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVehicleResponse = (String) args[0];
                    BaseModel mBaseModel = new BaseModel(mContext);
                    if (mBaseModel.isParse(mVehicleResponse)) {

                        if (mVehicleResponse != null) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(mVehicleResponse.toString());
                                mStrLiveTrip = obj.getString("active_trip_status");


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (mStrLiveTrip.equalsIgnoreCase("1")) {
                                try {
                                    JSONObject jsonObjs = new JSONObject(mVehicleResponse);
                                    if (jsonObjs.getString("error_code").equals("461")) {     // check for session
                                        if (!jsonObjs.getString("token").equals(mSessionPref.token)) {
                                            try {
                                                requestLogout();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        //2ndtime
                                        if (!flagLiveTripCall) {
                                            if (mBaseModel.isParse(mVehicleResponse)) {

                                                JSONArray mTruckList = mBaseModel.getResultArray();
                                                Log.e("dataVahical", mTruckList + "");
                                                // parseVehicleData(mTruckList);
                                                ListenersSocket.parseVehicleData(fragment, mTruckList, mVehicleResponse);
                                            }

                                        } else {
                                            flagLiveTripCall = false;
                                            iv_help.setVisibility(View.GONE);
                                            iv_notification.setVisibility(View.GONE);
                                            tv_notification_badge.setVisibility(View.GONE);

                                            toolbarTitle.setText(getResources().getString(R.string.livetrip));
                                            ib_left.setImageResource(R.drawable.arrow_back_24dp);
                                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                                drawer.closeDrawer(GravityCompat.START);
                                            } else {

                                            }

                                            fragment = new LiveFragment();
                                            try {
                                                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                                                    getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commit();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (mStrLiveTrip.equalsIgnoreCase("0")) {

                                if (mBaseModel.isParse(mVehicleResponse)) {

                                    JSONArray mTruckList = mBaseModel.getResultArray();
                                    //  parseVehicleData(mTruckList);
                                    ListenersSocket.parseVehicleData(fragment, mTruckList, mVehicleResponse);
                                } else {
                                    try {
                                        JSONObject jsonObjs = new JSONObject(mVehicleResponse);
                                        if (jsonObjs.getString("error_code").equals("461")) {     // check for session
                                            if (!jsonObjs.getString("token").equals(mSessionPref.token)) {
                                                try {

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            //   }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {


                        }
                    } else {
                        if (fragment instanceof HomeFragment) {
                            ((HomeFragment) fragment).setVehicleData("", "",
                                    "", "", "", "", "", "1x", "", "", "0");
                        }

                    }
                } // }
            });
        }
    };

    Emitter.Listener getArrivedTrip = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //   Log.e("Get trip started", (String) args[0]);
            showLiveTrip = false;
            showFindingDriver = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flagLiveTripCall = false;
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, "");
                            obj = new JSONObject(mStarted.toString());
                            mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ListenersSocket.parseArrivedTrip(fragment, iv_help, iv_notification);
                    }
                }
            });
        }
    };

    Emitter.Listener getProfile = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Get profile", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(mStarted.toString());
                            if (obj.getString("status") != null) {
                                if (obj.getString("status").equalsIgnoreCase("0")) {
                                Log.e("session", mStarted);
                                    if (obj.getString("error_code") != null) {
                                        if (obj.getString("error_code").equalsIgnoreCase("461")) {
                                            //mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);
                                            String tokenServer = obj.getString("token");
                                            try {
                                                if (!tokenServer.trim().equalsIgnoreCase(mSessionPref.token.trim())) {
                                                    try {
                                                        AppDialogs.singleButtonVersionDialog(mContext, R.layout.failure_dialog, "Session Expired", R.drawable.failed_24dp, getResources().getString(R.string.please_login_to_continue),
                                                                getString(R.string.ok),
                                                                new AppDialogs.SingleButoonCallback() {
                                                                    @Override
                                                                    public void singleButtonSuccess(String from) {
                                                                        requestLogout();

                                                                    }
                                                                }, true);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (obj.getString("status").equalsIgnoreCase("1")) {

                                    String tokenServer = obj.getJSONObject("result").getString("token");
                                    try {
                                        if (!tokenServer.trim().equalsIgnoreCase(mSessionPref.token.trim())) {
                                            try {
                                                AppDialogs.singleButtonVersionDialog(mContext, R.layout.failure_dialog, "Session Expired", R.drawable.failed_24dp, getResources().getString(R.string.please_login_to_continue),
                                                        getString(R.string.ok),
                                                        new AppDialogs.SingleButoonCallback() {
                                                            @Override
                                                            public void singleButtonSuccess(String from) {
                                                                requestLogout();

                                                            }
                                                        }, true);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    Emitter.Listener getacceptedTrip = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showFindingDriver = false;
                    String mAccepted = (String) args[0];
                    if (mAccepted != null) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(mAccepted);
                            mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);
                            ListenersSocket.parseAcceptedTrip(mContext, fragment);
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            } else {

                            }
                            BaseModel mBaseModel = new BaseModel(mContext);
                            if (mAccepted != null) {
                                if (mBaseModel.isParse(mAccepted)) {
                                    SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, "");

                                    JSONObject jsonObject = mBaseModel.getResultObject().getJSONObject("booking_info");
                                    String mBookingType = jsonObject.getString("type_of_booking");
                                    if (mBookingType.equals("1")) {

                                        if (mbadgeCounts > 0) {
                                            tv_notification_badge.setVisibility(View.VISIBLE);
                                            tv_notification_badge.setText(String.valueOf(mbadgeCounts));
                                        } else {
                                            tv_notification_badge.setVisibility(View.GONE);
                                        }
                                        ib_left.setVisibility(View.VISIBLE);
                                        ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                        iv_notification.setImageResource(R.drawable.notification_icon);
                                        iv_notification.setVisibility(View.VISIBLE);
                                        iv_help.setVisibility(View.GONE);

                                        toolbarTitle.setText(getResources().getString(R.string.home));
                                        fragment = new HomeLocationSelectFragment();
                                        getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commitAllowingStateLoss();
                                        // getSupportFragmentManager().executePendingTransactions();

                                    } else {

                                        showLiveTrip = true;
                                        flagLiveTripCall = false;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                try {
                                                    fragment = new LiveFragment();

                                                    // if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                                                    getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commitAllowingStateLoss();
                                                    //  getSupportFragmentManager().executePendingTransactions();
                                                    // }
                                                    iv_help.setVisibility(View.GONE);
                                                    iv_notification.setVisibility(View.GONE);
                                                    tv_notification_badge.setVisibility(View.GONE);
                                                    toolbarTitle.setText(getResources().getString(R.string.livetrip));
                                                    ib_left.setImageResource(R.drawable.arrow_back_24dp);
                                                    ib_left.setVisibility(View.VISIBLE);
                                                } catch (Exception e) {
                                                    ib_left.setVisibility(View.VISIBLE);
                                                    ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                                    iv_notification.setImageResource(R.drawable.notification_icon);
                                                    iv_notification.setVisibility(View.VISIBLE);
                                                    iv_help.setVisibility(View.GONE);
                                                    toolbarTitle.setText(getResources().getString(R.string.home));
                                                    fragment = new HomeLocationSelectFragment();
                                                    getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commitAllowingStateLoss();
                                                    // getSupportFragmentManager().executePendingTransactions();
                                                    e.printStackTrace();
                                                }
                                                //  }
                                            }
                                        });
                                    }

                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    Emitter.Listener getStartTrip = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Get trip started", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            showFindingDriver = false;
                            BaseModel mBaseModel = new BaseModel(mContext);

                            if (mStarted != null) {
                                if (mBaseModel.isParse(mStarted)) {
                                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Info, true);
                                    SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, "");

                                    String mBookingType = mBaseModel.getResultObject().getString("type_of_booking");

                                    if (mBookingType.equals("1")) {

                                    } else {
                                        obj = new JSONObject(mStarted);

                                        trip_id = obj.getString("tripid");
                                        showLiveTrip = true;
                                        flagLiveTripCall = false;
                                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                                            drawer.closeDrawer(GravityCompat.START);
                                        } else {

                                        }
                                        if (fragment instanceof LiveFragment) {
                                            ((LiveFragment) fragment).reloadLivetripData();
                                        } else {
                                            iv_help.setVisibility(View.GONE);
                                            iv_notification.setVisibility(View.GONE);
                                            tv_notification_badge.setVisibility(View.GONE);

                                            toolbarTitle.setText(getResources().getString(R.string.livetrip));
                                            ib_left.setImageResource(R.drawable.arrow_back_24dp);


                                            fragment = new LiveFragment();
                                            try {
                                                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                                                    getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commitAllowingStateLoss();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                }
                            }


                            ListenersSocket.parseStartTrip(mContext, fragment, trip_id, mbadgeCounts, tv_notification_badge, iv_notification);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    Emitter.Listener endTripListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("End Trip", (String) args[0]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mEnd = (String) args[0];
                    showLiveTrip = false;
                    showFindingDriver = false;
                    BaseModel mBaseModel = new BaseModel(mContext);
                    if (mEnd != null) {
                        if (mBaseModel.isParse(mEnd)) {
                            if (mBaseModel.getResultObject().length() != 0) {

                                try {
                                    ListenersSocket.parseEndTrip(mContext, fragment, mBaseModel.getResultObject().getString("id"), mbadgeCounts, tv_notification_badge);
                                    if (fragment instanceof LiveFragment) {
                                        ib_left.setVisibility(View.VISIBLE);
                                        ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                        iv_notification.setImageResource(R.drawable.notification_icon);
                                        iv_notification.setVisibility(View.VISIBLE);
                                        if (mbadgeCounts > 0) {
                                            tv_notification_badge.setVisibility(View.VISIBLE);
                                            tv_notification_badge.setText(String.valueOf(mbadgeCounts));
                                        } else {
                                            tv_notification_badge.setVisibility(View.GONE);
                                        }
                                        fragment = new HomeLocationSelectFragment();
                                        toolbarTitle.setText(getResources().getString(R.string.home));

                                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    }
                }
            });

        }
    };

    Emitter.Listener getCompleteTrip = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Get trip Completed ", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLiveTrip = false;
                    String mCompleted = (String) args[0];
                    BaseModel mBaseModel = new BaseModel(mContext);
                    if (mCompleted != null) {
                        if (mBaseModel.isParse(mCompleted)) {
                            if (mBaseModel.getResultArray().length() != 0) {
                                try {
                                    ListenersSocket.parseCompleteTrip(mContext, fragment, mBaseModel.getResultArray().getJSONObject(0).getString("tripid"));
                                    if (fragment instanceof LiveFragment) {
                                        ib_left.setVisibility(View.VISIBLE);
                                        ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                        iv_notification.setImageResource(R.drawable.notification_icon);
                                        iv_notification.setVisibility(View.VISIBLE);
                                        if (mbadgeCounts > 0) {
                                            tv_notification_badge.setVisibility(View.VISIBLE);
                                            tv_notification_badge.setText(String.valueOf(mbadgeCounts));
                                        } else {
                                            tv_notification_badge.setVisibility(View.GONE);
                                        }
                                        fragment = new HomeLocationSelectFragment();
                                        toolbarTitle.setText(getResources().getString(R.string.home));
                                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
            });
        }
    };

    Emitter.Listener cancelledTripListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Cancelled Trip", (String) args[0]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resp = (String) args[0];
                    BaseModel mBaseModel = new BaseModel(mContext);
                    if (mBaseModel.isParse((String) args[0])) {
                        JSONObject obj = null;
                        try {
                            showFindingDriver = false;
                            showLiveTrip = false;
                            obj = new JSONObject(resp.toString());
                            SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, "");
                            mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                            if (fragment instanceof MyBookingFragment) {

                                //BaseModel mBaseModel = new BaseModel(mContext);
                                if (resp != null) {
                                    if (mBaseModel.isParse(resp)) {
                                        if (mBaseModel.getResultObject().length() != 0) {

                                            if (mBaseModel.getResultObject().getInt("booking_status") == 5) {
                                                ((MyBookingFragment) fragment).reloadData(true);
                                            }
                                        }
                                    }
                                }
                            }
                            if (mBaseModel.getResultObject().getInt("booking_status") == 5) {
                                if (fragment instanceof HomeLocationSelectFragment) {
                                    onShowdialog(false, obj.getString("message"));
                                    // ((HomeLocationSelectFragment) fragment).tripStatus(false, "");
                                }
                            }
                            if (fragment instanceof LiveFragment) {

                                // ((LiveFragment) fragment).reloadLivetripData();
                                try {
                                    try {
                                        if (!mBaseModel.getResultObject().getString("cancel_by_id").equalsIgnoreCase(mSessionPref.user_userid)) {
                                            if (fragment instanceof LiveFragment) {
                                                ib_left.setVisibility(View.VISIBLE);
                                                ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                                iv_notification.setImageResource(R.drawable.notification_icon);
                                                iv_notification.setVisibility(View.VISIBLE);
                                                if (mbadgeCounts > 0) {
                                                    tv_notification_badge.setVisibility(View.VISIBLE);
                                                    tv_notification_badge.setText(String.valueOf(mbadgeCounts));
                                                } else {
                                                    tv_notification_badge.setVisibility(View.GONE);
                                                }
                                                fragment = new HomeLocationSelectFragment();
                                                toolbarTitle.setText(getResources().getString(R.string.home));
                                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            });

        }
    };

    Emitter.Listener getDriverCurrentLocationListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //   Log.e("Get  Driver lat long ", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mDriverCurrentLocationResponse = (String) args[0];
                    if (fragment instanceof LiveFragment) {
                        ((LiveFragment) fragment).setDriverCurrentLocationData(mDriverCurrentLocationResponse);
                    }
                }
            });
        }
    };
    Emitter.Listener getDriverProjectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // if (mBaseModel.isParse((String) args[0])) {
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            showFindingDriver = false;


                            try {
                                obj = new JSONObject(mStarted);
                                if (obj.optString("status", "").equalsIgnoreCase("0")) {

                                    JSONObject obj1 = obj.getJSONObject("result");
                                    String driver_id = obj1.getString("driver_id");
                                    String angle = obj1.getString("angle");
                                    Double lat = obj1.getDouble("lat");
                                    Double lang = obj1.getDouble("lang");
                                    if (fragment instanceof HomeFragment) {
                                        ((HomeFragment) fragment).setDriverAngle(driver_id, angle, lat, lang);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    // }
                }
            });
        }
    };

    Emitter.Listener bookingStatusListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  Log.e("Pending ", (String) args[0]);
                    String data = (String) args[0];
                    Log.e("dataaaaaa", data);
                    BaseModel mBaseModel = new BaseModel(mContext);
                    if (mBaseModel.isParse(data)) {
                        //    Log.e("Pending ", "in parse");
                        showLiveTrip = false;
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(data);
                            String result = jsonObj.optString("result", "");
                            String popop_time = jsonObj.optString("popop_time", "");
                            if (result.equals("1")) {
                                  Log.e("Pending ", "in result");
                                showFindingDriver = true;



                                if (popop_time != null && !popop_time.equals("") && !popop_time.equals("0")) {
                                    //  Log.e("Pending ", "in popuptime");
                                    if (!(fragment instanceof FindADriverFragment)) {


                                        // getSupportActionBar().hide();
                                        toolbarTitle.setText(getResources().getString(R.string.finding_a_ride));
                                        titleFindingDriver();
                                        fragment = new FindADriverFragment();
                                        try {
                                            Log.e("dattttttttt",jsonObj+"");
                                            mAddress = jsonObj.optString("book_from_address", "");
                                            mLat = jsonObj.optDouble("book_from_lat");
                                            mLong = jsonObj.optDouble("book_from_long");
                                            String booking_id = jsonObj.optString("booking_id");

                                            Bundle arg = new Bundle();
                                            arg.putString("mAddress", mAddress);
                                            arg.putDouble("mLat", mLat);
                                            arg.putDouble("mLong", mLong);

                                            arg.putString("vehicleType", "auto");
                                            arg.putString("trip_id",booking_id);

                                            fragment.setArguments(arg);
                                            // getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                                            new Handler().post(new Runnable() {
                                                public void run() {
                                                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();

                                                }
                                            });

                                        } catch (Exception e) {
                                            if (fragment instanceof FindADriverFragment) {
                                                // getSupportActionBar().hide();
                                                toolbarTitle.setText(getResources().getString(R.string.finding_a_ride));
                                                titleFindingDriver();
                                            } else {
                                                //  getSupportActionBar().show();

                                            }
                                            e.printStackTrace();
                                        }
                                        if (popop_time != null && !popop_time.equals("") && !popop_time.equals("0")) {
                                            int pendingTime = 150;//Integer.valueOf(popop_time);

                                            mCustomCountdownTimer = new CountDownTimer(pendingTime * 1000, 1000) {
                                                @SuppressLint("NewApi")
                                                public void onTick(long millisUntilFinished) {

                                                    Log.e("countdown", millisUntilFinished+"finish");


                                                }

                                                public void onFinish() {
                                                    // Log.d("live trip1", pendingTime + "" + showLoader);
                                                    //   mErrorLayout.showAlert(getResources().getString(R.string.booking_not_accepted_by_driver), ErrorLayout.MsgType.Info, true);
                                                    Log.e("countdown", "finish");
                                                    mCustomCountdownTimer.cancel();
                                                   /* if (fragment instanceof FindADriverFragment) {
                                                        //  mErrorLayout.showAlert(getResources().getString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                                                    }*/
                                                    homeCall();

                                                }
                                            };
                                            mCustomCountdownTimer.start();
                                        }
                                    } else {

                                        // Log.e("Pending ", "in else");

                                        if (fragment instanceof FindADriverFragment) {
                                            // getSupportActionBar().hide();
                                            titleFindingDriver();

                                        } else {
                                            // getSupportActionBar().show();

                                        }
                                    }
                                } else {
                                    //  mErrorLayout.showAlert(booking_msg, ErrorLayout.MsgType.Info, true);
                                    homeCall();

                                }
                            } else {
                                //  mErrorLayout.showAlert(booking_msg, ErrorLayout.MsgType.Info, true);
                                homeCall();

                            }
                        } catch (JSONException e) {
                            //  Log.e("Pending ", "in catch");

                            if (fragment instanceof FindADriverFragment) {
                                titleFindingDriver();

                            } else {
                                getSupportActionBar().show();


                            }
                            e.printStackTrace();
                        }

                    }

                }
            });

        }
    };

    public void homeCall() {
        if (mCustomCountdownTimer != null){
            mCustomCountdownTimer.cancel();
        }
        if (fragment instanceof FindADriverFragment) {
            if (mbadgeCounts > 0) {
                tv_notification_badge.setVisibility(View.VISIBLE);
                tv_notification_badge.setText(String.valueOf(mbadgeCounts));
            } else {
                tv_notification_badge.setVisibility(View.GONE);
            }
            ib_left.setVisibility(View.VISIBLE);
            ib_left.setImageResource(R.drawable.drawer_menu_24dp);
            iv_notification.setImageResource(R.drawable.notification_icon);
            iv_notification.setVisibility(View.VISIBLE);
            iv_help.setVisibility(View.GONE);
            toolbarTitle.setText(getResources().getString(R.string.home));
            fragment = new HomeLocationSelectFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment).commitAllowingStateLoss();
        }
    }

    Emitter.Listener getAutoCancel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("getAutoCancel", (String) args[0]);
            showFindingDriver = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mStarted = (String) args[0];
                    Log.e("response", mStarted);
                    if (mStarted != null) {
                        showLiveTrip = false;

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(mStarted.toString());
                            if (obj.getString("status").equals("1")) {
                                useridArrayList = new ArrayList<String>();
                                SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, "");


                                if (obj.getJSONArray("cancel_trip_user_id").length() != 0) {

                                    for (int i = 0; i < obj.getJSONArray("cancel_trip_user_id").length(); i++) {

                                        useridArrayList.add(obj.getJSONArray("cancel_trip_user_id").getString(i));
                                    }

                                    if (useridArrayList.contains(mSessionPref.user_userid)) {
                                        mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                                        if (fragment instanceof HomeLocationSelectFragment) {

                                            // ((HomeLocationSelectFragment) fragment).tripStatus(false, "");
                                        }
                                        if (fragment instanceof FindADriverFragment) {
                                            notificationListApi();
                                            ib_left.setVisibility(View.VISIBLE);
                                            ib_left.setImageResource(R.drawable.drawer_menu_24dp);
                                            iv_notification.setImageResource(R.drawable.notification_icon);
                                            iv_notification.setVisibility(View.VISIBLE);
                                            iv_help.setVisibility(View.GONE);
                                            toolbarTitle.setText(getResources().getString(R.string.home));

                                            fragment = new HomeLocationSelectFragment();
                                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commitAllowingStateLoss();
                                            getSupportFragmentManager().executePendingTransactions();


                                        }
                                    }

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    Emitter.Listener getLogoutRes = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("logout", (String) args[0]);
            String logout = (String) args[0];
            JSONObject obj = null;
            try {
                obj = new JSONObject(logout);

                if (obj.getString("status").equalsIgnoreCase("1")) {
                    mbadgeCounts = 0;
                    mSessionPref.clearSessionPrefUser(HomeMenuActivity.this);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(mContext, LoginActivity.class);

                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("lat", mlat);
                    intent.putExtra("long", mlongi);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
