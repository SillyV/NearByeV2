package com.sillyv.vasili.nearbye.helpers.gson;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */
public class Location
{
    private Double lat;
    private Double lng;

    public Location(Double lat, Double lng)
    {

        this.lng = lng;
        this.lat = lat;
    }


    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLng()
    {
        return lng;
    }

    public void setLng(Double lng)
    {
        this.lng = lng;
    }
}
