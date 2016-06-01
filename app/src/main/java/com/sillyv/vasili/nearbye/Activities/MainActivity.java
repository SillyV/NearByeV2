package com.sillyv.vasili.nearbye.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
    private ResultsFragment resultsFragment;
    private MapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment map;
    MenuItem searchMenuItem;
    private Toolbar mToolBar;

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
                resultsFragment.SearchQueryINGooglePlacesWebService(query);
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
                    //// TODO: 23-May-16 Call functions to change Fragment Visibility
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

        fm.beginTransaction()
                .show(mapFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();

        fm.beginTransaction()
                .hide(resultsFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }


    private void goToList()
    {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(mapFragment)
                .commit();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(resultsFragment)
                .commit();

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
        resultsFragment.startingSettings(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));


    }


    @Override
    public void onFragmentInteraction(Results results)
    {
        mapFragment.setLocation(results,this);
        searchMenuItem.collapseActionView();
        getSupportActionBar().setTitle(results.getName());
        goToMaps();
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
