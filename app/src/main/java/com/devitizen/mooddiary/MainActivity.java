package com.devitizen.mooddiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.devitizen.mooddiary.data.WeatherResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Creates MainActivity that consists of five fragments.
 * For the List menu, Fragment1 and FragmentMenu are required.
 * For the New menu, Fragment2 and Fragment2Menu are required.
 * For the Stat menu, Fragment3 and FragmentMenu are required.
 * FragmentMenu is commonly used for the List and Stat menu, but Fragment2Menu is only used for the New menu.
 * Some methods that should be in the activity but for the fragments are located here.
 */
public class MainActivity extends AppCompatActivity
        implements MyApplication.OnResponseListener, AutoPermissionsListener {

    private static final String TAG = "MainActivity";

    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private FragmentMenu fragmentMenu;
    private Fragment2Menu fragment2Menu;
    private NoteDatabase database;
    private OnBackPressedListener backPressedListener;

    private Location currentLocation;
    private GPSListener gpsListener;

    /**
     * Creates instances of fragments
     * Gets necessary permissions and open the database
     *
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragmentMenu = new FragmentMenu();
        fragment2Menu = new Fragment2Menu();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_menu, fragmentMenu).commit();

        // Get permissions
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        // Open database
        openDatabase();

        // AdMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adEvent(adView);
    }

    /**
     * Close the called database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
            database = null;
        }
    }

    /**
     * Request permissions
     *
     * @param requestCode  request code
     * @param permissions  requested permissions
     * @param grantResults received listener
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    /**
     * Implements AutoPermissionsListener
     *
     * @param i
     * @param strings
     */
    @Override
    public void onDenied(int i, String[] strings) {
    }

    /**
     * Implements AutoPermissionsListener
     *
     * @param i
     * @param strings
     */
    @Override
    public void onGranted(int i, String[] strings) {
    }

    /**
     * Processes the response from the request to weather API
     *
     * @param requestCode  request code
     * @param responseCode response code
     * @param response     replied value
     */
    @Override
    public void processResponse(int requestCode, int responseCode, String response) {
        if (responseCode == 200) {
            if (requestCode == AppConstants.REQ_WEATHER_BY_GRID) {
                Gson gson = new Gson();
                WeatherResult weatherResult = gson.fromJson(response, WeatherResult.class);
                if (weatherResult != null) {
                    if (fragment2 != null) {
                        fragment2.setWeatherIcon(weatherResult.getWeather().get(0).getIcon());
                        fragment2.setWeatherDescription(weatherResult.getWeather().get(0).getDescription());
                        stopLocationService();          // stop requesting locations
                    }
                }
            }
        }
    }

    /**
     * Calls onBackKeyPressed() or closes activity when back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (backPressedListener != null) {
            backPressedListener.onBackButtonPressed();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Invokes OnBackPressedListener when fragments resume
     *
     * @param backPressedListener OnBackPressedListener
     */
    public void setOnBackPressedListener(OnBackPressedListener backPressedListener) {
        this.backPressedListener = backPressedListener;
    }


    /**
     * Locates the fragment selected in the main menu to the activity
     *
     * @param position index of the selected fragment
     */
    public void onMenuSelected(int position) {
        switch (position) {
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_menu, fragmentMenu).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_menu, fragment2Menu).commit();
                fragment2 = new Fragment2();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_menu, fragmentMenu).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                break;
            default:
                break;
        }
    }

    /**
     * Creates the New (fragment 2) with the selected note in the List
     *
     * @param note selected note
     */
    public void showFragment2(Note note) {
        onMenuSelected(2);
        fragment2.setNote(note);
        fragment2.setMMode(AppConstants.MODE_MODIFY);

        if (!note.getPicture().equals("")) {
            fragment2.setPhotoFileSaved(true);          // set which dialog shows
        }
    }

    /**
     * Processes according to the selected menu among save, delete, close
     *
     * @param position selected menu
     */
    public void onTabSelected(int position) {
        onHideKeypad();                 // hides key pad

        switch (position) {
            case 0:                                                             // save note
                if (fragment2.getMMode() == AppConstants.MODE_INSERT) {         // insert
                    fragment2.saveNote();
                } else if (fragment2.getMMode() == AppConstants.MODE_MODIFY) {  // modify
                    fragment2.modifyNote();
                }
                break;
            case 1:                                                             // delete note
                fragment2.deleteNote();
                break;
            case 2:                                                             // close note
                break;
            default:
                break;
        }

        onMenuSelected(1);      // goes to the List menu
    }

    /**
     * Hides the key pad
     */
    public void onHideKeypad() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * Opens the database
     */
    public void openDatabase() {
        if (database != null) {
            database.close();
            database = null;
        }
        // gets database
        database = NoteDatabase.getInstance(this);

        // opens database
        if(database.open()) {
            println("Database is open.");
        } else {
            println("Database is not open.");
        }
    }

    /**
     * Gets the current date, address, weather info
     */
    public void getCurrentInfo() {
        // gets current date
        getCurrentDate();

        // gets current location
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (manager != null) {
                currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (currentLocation != null) {
                println("Last location -> Latitude : " + currentLocation.getLatitude()
                        + ", Longitude : " + currentLocation.getLongitude());

                // gets current address
                getCurrentAddress();

                // gets current weather
                getCurrentWeather();
            }

            gpsListener = new GPSListener();
            long minTime = 5000;
            float minDistance = 0;
            if (manager != null) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the location service
     */
    public void stopLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            manager.removeUpdates(gpsListener);
        }
    }

    /**
     * Gets the current date
     */
    public void getCurrentDate() {
        String currentDateString;
        Date currentDate = new Date();
        currentDateString = AppConstants.dateFormat6.format(currentDate);
        if (Locale.getDefault().getLanguage().equals("ko")) {
            currentDateString = AppConstants.dateFormat9.format(currentDate);
        }

        if (fragment2 != null) {
            fragment2.setDateString(currentDateString);
        }
    }

    /**
     * Gets the current address
     */
    public void getCurrentAddress() {
        String currentAddress;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            Address address = addresses.get(0);

            if (address.getLocality() != null) {
                currentAddress = address.getLocality();
            } else {
                currentAddress = address.getAdminArea();
            }

            if (fragment2 != null) {
                fragment2.setAddress(currentAddress);
            }
        }
    }

    /**
     * Gets the current weather (Calls OpenWeather API)
     */
    public void getCurrentWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather"
                + "?lat=" + currentLocation.getLatitude()
                + "&lon=" + currentLocation.getLongitude()
                + "&units=metric"
                + "&appid=06e594a6a0996a9fa13332415edce9d3";
        Map<String, String> params = new HashMap<>();

        MyApplication.send(AppConstants.REQ_WEATHER_BY_GRID, Request.Method.GET, url, params, this);
    }

    /**
     * Saves the user's preference on the switch button
     *
     * @param value selected button
     */
    public void savePref(int value) {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("switchValue", value);
        //editor.commit();
        editor.apply();
    }

    /**
     * Restore the preference
     *
     * @return value saved button
     */
    public int restorePref() {
        int value = 0;
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if (pref != null && pref.contains("switchValue")) {
            value = pref.getInt("switchValue", 0);
        }

        return value;
    }

    /**
     * Handles AdView events
     *
     * @param mAdView adView
     */
    public void adEvent(AdView mAdView) {
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    /**
     * Prints log
     *
     * @param msg input message
     */
    public void println(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * *************************************************************
     * Inner Class : GPSListener. This implements LocationListener.
     * *************************************************************
     */
    public class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;     // sets location
            getCurrentAddress();            // gets address info
            getCurrentWeather();            // gets weather info

            println("Last location -> Latitude : " + currentLocation.getLatitude()
                    + "\nLongitude : " + currentLocation.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

}