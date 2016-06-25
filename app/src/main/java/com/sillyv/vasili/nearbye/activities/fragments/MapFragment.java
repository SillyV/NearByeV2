package com.sillyv.vasili.nearbye.activities.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.squareup.picasso.Picasso;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */

public class MapFragment extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback
{

    private SupportMapFragment mapFragment;
    private Results result;
    private Context context;

    public void setLocation(Results result, Context context)
    {
        this.result = result;
        this.context = context;
        this.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        googleMap.clear();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()), 16));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)) // Anchors the marker on the bottom left
                .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))); //Iasi, Romania
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }
}
