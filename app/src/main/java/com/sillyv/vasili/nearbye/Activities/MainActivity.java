package com.sillyv.vasili.nearbye.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.activities.fragments.MapFragment;
import com.sillyv.vasili.nearbye.activities.fragments.PrefsFragment;
import com.sillyv.vasili.nearbye.activities.fragments.ResultsFragment;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.sillyv.vasili.nearbye.misc.Prefs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasili.Fedotov on 6/22/2016.
 */
public class MainActivity extends AppCompatActivity implements ResultsFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback
{
    //Constants
    private static final String TAG = "SillyV.MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 6615;

    //Primitive Fields
    private String myQuery;
    private boolean searchRequested = false;
    private boolean locationFound = false;
    private boolean initialSearchPerformed = false;

    //Complex Fields
    private ResultsFragment resultsFragment;
    private MapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private MenuItem searchMenuItem;
    private Toolbar mToolBar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location myLocation;
    private PrefsFragment prefrecesFragment;

    //Constructors / onCreate methods
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpActivityControls();
        mappingSetup();
        LocationSetup();
        registerBatteryReciever();

    }


    //Initialization Methods
    private void setUpActivityControls()
    {
        mToolBar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolBar);
        resultsFragment = (ResultsFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        prefrecesFragment = (PrefsFragment) getFragmentManager().findFragmentById(R.id.prefrences_fragment);
        if (!isNetworkConnected())
        {
            gotoHistory();
            initialSearchPerformed = true;
            return;
        }
        goToMaps();
    }

    private void mappingSetup()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void LocationSetup()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            public void onProviderEnabled(String provider)
            {
            }

            public void onProviderDisabled(String provider)
            {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            askPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

    private void askPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else
        {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    private void registerBatteryReciever()
    {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(null, iFilter);
    }
    //Override Methods


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                }
                else
                {

                    finish();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);

        final MenuItem searchNearBye = menu.findItem(R.id.nearbye);
        if (searchMenuItem == null)
        {
            return true;
        }
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                myQuery = query;
                searchRequested = true;
                if (locationFound)
                {
                    initiateSearch();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (findViewById(R.id.portrait_indicator) != null)
                {
                    //  resultsFragment.currentQueryIs(newText);
                }
                return false;
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                if (getSupportActionBar() != null)
                {
                    searchNearBye.setVisible(false);
                    goToList();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                if (getSupportActionBar() != null)
                {
                    searchNearBye.setVisible(true);
                    goToMaps();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                }
                return true;
            }
        });
        Log.d(TAG, "ASD");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                goToPrefrences();
                break;
            case R.id.favorites:
                goToFavorites();
                break;
            case R.id.nearbye:
                searchNearbye();
        }
        initialSearchPerformed = true;
        return super.onOptionsItemSelected(item);
    }

    private void searchNearbye()
    {
        if(locationFound)
        {
            resultsFragment.SearchQueryINGooglePlacesWebService(myLocation);
            initialSearchPerformed = true;
        }
    }

    protected void onStart()
    {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        if (resultsFragment.isVisible())
        {
            finish();
        }
        else
        {
            goToList();
            resultsFragment.updateUnits();
        }
    }

    //Implemented Methods

    @Override
    public void onFragmentInteraction(Results results)
    {
        mapFragment.setLocation(results, this);
        searchMenuItem.collapseActionView();
        getSupportActionBar().setTitle(results.getName());
        goToMaps();
    }

    @Override
    public void setListVisible()
    {
        goToList();
        if (findViewById(R.id.portrait_indicator) != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(47.17, 27.5699), 16));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)) // Anchors the marker on the bottom left
                .position(new LatLng(47.17, 27.5699))); //Iasi, Romania
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    //subs
    private void makeUseOfNewLocation(Location location)
    {
        myLocation = location;
        locationFound = true;
        resultsFragment.locationChanged(location);
        if (searchRequested)
        {
            initiateSearch();
        }
        else if (!initialSearchPerformed)
        {
            resultsFragment.SearchQueryINGooglePlacesWebService(myLocation);
            initialSearchPerformed = true;
        }
    }

    private void initiateSearch()
    {
        searchRequested = false;
        initialSearchPerformed = true;
        resultsFragment.SearchQueryINGooglePlacesWebService(myQuery, myLocation);
        Log.d(TAG, "location found and search started");
    }

    private void gotoHistory()
    {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        goToList();
        resultsFragment.goToHistory();
        FragmentManager fm = getSupportFragmentManager();
        hideSupportTransaction(mapFragment, fm);
    }

    private void goToMaps()
    {

        if (findViewById(R.id.linear_container) == null)
        {
            FragmentManager fm = getSupportFragmentManager();
            showSupportTransaction(mapFragment, fm);
            hideSupportTransaction(resultsFragment, fm);
        }
        else
        {
            findViewById(R.id.linear_container).setVisibility(View.VISIBLE);
        }
        android.app.FragmentManager fm1 = getFragmentManager();
        hideTransaction(prefrecesFragment, fm1);
    }

    private void goToList()
    {
        if (findViewById(R.id.linear_container) == null)
        {
            FragmentManager fm = getSupportFragmentManager();
            hideSupportTransaction(mapFragment, fm);
            showSupportTransaction(resultsFragment, fm);
        }
        else
        {
            findViewById(R.id.linear_container).setVisibility(View.VISIBLE);
        }
        android.app.FragmentManager fm1 = getFragmentManager();
        hideTransaction(prefrecesFragment, fm1);
    }

    private void goToPrefrences()
    {
        if (findViewById(R.id.linear_container) != null)
        {
            findViewById(R.id.linear_container).setVisibility(View.GONE);
        }
        FragmentManager fm = getSupportFragmentManager();
        hideSupportTransaction(resultsFragment, fm);
        hideSupportTransaction(mapFragment, fm);
        android.app.FragmentManager fm1 = getFragmentManager();
        showTransaction(prefrecesFragment, fm1);
    }

    private void goToFavorites()
    {
        goToList();
        resultsFragment.goToFavorites();
    }

    private void showSupportTransaction(Fragment fragment, FragmentManager fm)
    {
        if (fragment != null && !fragment.isVisible())
        {
            fm.beginTransaction()
                    .show(fragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }

    private void hideSupportTransaction(Fragment fragment, FragmentManager fm)
    {
        if (fragment != null)
        {
            fm.beginTransaction()
                    .hide(fragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }

    private void hideTransaction(android.app.Fragment fragment, android.app.FragmentManager fm)
    {
        if (fragment != null)
        {
            fm.beginTransaction()
                    .hide(fragment)
                    .commit();
        }
    }

    private void showTransaction(android.app.Fragment fragment, android.app.FragmentManager fm)
    {
        if (fragment != null && !fragment.isVisible())
        {
            fm.beginTransaction()
                    .show(fragment)
                    .commit();
        }
    }

    //Functions
    private static Map<String, String> getParamsForAddBobRequest(String location, String radius, String query)
    {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", query);
        params.put("location", location);
        params.put("radius", radius);
        params.put("key", Prefs.API_KEY);
        return params;
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
