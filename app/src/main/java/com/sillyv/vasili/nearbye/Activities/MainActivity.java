package com.sillyv.vasili.nearbye.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.activities.fragments.MapFragment;
import com.sillyv.vasili.nearbye.activities.fragments.ResultsFragment;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.sillyv.vasili.nearbye.helpers.recycler.LocationListItem;
import com.sillyv.vasili.nearbye.misc.Prefs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ResultsFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback
{
    private static final String TAG = "SillyV.MainActivity";
    private static final int PLACE_PICKER_REQUEST = 40001;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 6615;
    private ResultsFragment resultsFragment;
    private MapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment map;
    MenuItem searchMenuItem;
    private Toolbar mToolBar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean searchRequested = false;
    private boolean locationFound = false;
    private boolean initialSearchPerformed = false;
    private Location myLocation;
    private String myQuery;

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
    }

    private void LocationSetup()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
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
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void makeUseOfNewLocation(Location location)
    {
        myLocation = location;
        locationFound = true;
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

    private void setUpActivityControls()
    {
        mToolBar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolBar);
        resultsFragment = (ResultsFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        goToMaps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
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
                resultsFragment.currentQueryIs(newText);
                return false;
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                // Set styles for expanded state here
                if (getSupportActionBar() != null)
                {
                    goToList();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                // Set styles for collapsed state here
                if (getSupportActionBar() != null)
                {
                    goToMaps();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    //// TODO: 23-May-16 Call functions to change Fragment Visibility
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    public void onPlacesClick(View view)
    {

    }

    private static Map<String, String> getParamsForAddBobRequest(String location, String radius, String query)
    {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", query);
        params.put("location", location);
        params.put("radius", radius);
        params.put("key", Prefs.API_KEY);
        return params;
    }

    private void goToMaps()
    {
        FragmentManager fm = getSupportFragmentManager();

        showTransaction(mapFragment, fm);
        hideTransaction(resultsFragment, fm);
    }

    private void goToList()
    {
        FragmentManager fm = getSupportFragmentManager();
        hideTransaction(mapFragment, fm);
        showTransaction(resultsFragment, fm);
    }

    private void goToPrefrences()
    {
        FragmentManager fm = getSupportFragmentManager();
        hideTransaction(resultsFragment, fm);
        hideTransaction(mapFragment, fm);
    }

    private void goToFavorites()
    {
        goToList();
        resultsFragment.goToFavorites();
    }

    private void showTransaction(Fragment fragment, FragmentManager fm)
    {
        fm.beginTransaction()
                .show(fragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }

    private void hideTransaction(Fragment fragment, FragmentManager fm)
    {
        fm.beginTransaction()
                .hide(fragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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


}
