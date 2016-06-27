package com.sillyv.vasili.nearbye.helpers.gson;

import android.location.*;

import com.sillyv.vasili.nearbye.misc.Prefs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */
public class Results
{
    private boolean favorite = false;

    public boolean isFavorite()
    {
        return favorite;
    }

    public void setFavorite(boolean favorite)
    {
        this.favorite = favorite;
    }

    private Geometry geometry;
    private String icon;
    private String id;
    private String name;
    private List<Photos> photos;
    private Double rating;
    private String reference;
    private List<String> types;
    private String vicinity;
    private int dbid;
    private String distance;


    public Results(Geometry geometry, String icon, String id, String name, List<Photos> photos, Double rating, String reference, List<String> types, String vicinity, boolean favorite)
    {
        this.favorite = favorite;
        this.geometry = geometry;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.photos = photos;
        this.rating = rating;
        this.reference = reference;
        this.types = types;
        this.vicinity = vicinity;
    }

    public Geometry getGeometry()
    {
        return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
        this.geometry = geometry;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Photos> getPhotos()
    {
        return photos;
    }

    public void setPhotos(List<Photos> photos)
    {
        this.photos = photos;
    }

    public Double getRating()
    {
        return rating;
    }

    public void setRating(Double rating)
    {
        this.rating = rating;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    public String getVicinity()
    {
        return vicinity;
    }

    public void setVicinity(String vicinity)
    {
        this.vicinity = vicinity;
    }

    public int getDbid()
    {
        return dbid;
    }

    public void setDbid(int dbid)
    {
        this.dbid = dbid;
    }


    public String getDistance()
    {
        return distance;
    }

    public void setDistance(android.location.Location myLocation, String unit)
    {
        this.distance = getDistance(myLocation.getLatitude(), myLocation.getLongitude(),
                getGeometry().getLocation().getLat(), getGeometry().getLocation().getLng(), unit);
    }

    private static double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private static String getDistance(double lat1, double lon1, double lat2, double lon2, String unit)
    {
        String answer;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == Prefs.METERS)
        {
            dist = dist * 1.609344;
        }
        if (dist >= 1)
        {
            answer = String.valueOf(round(dist, 1));
        }
        else
        {
            answer = String.valueOf(round(dist, 3));
        }
        answer += " " + unit;
        return (answer);
    }

    private static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }

    public void setNullDistance()
    {
        this.distance = "";
    }
}
