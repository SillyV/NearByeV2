package com.sillyv.vasili.nearbye.helpers.gson;


/**
 * Created by vasil on 5/27/2016.
 */
public class Geometry
{
    private Location location;

    public Geometry(Location location)
    {

        this.location = location;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
