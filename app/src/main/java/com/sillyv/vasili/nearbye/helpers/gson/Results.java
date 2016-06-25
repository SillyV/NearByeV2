package com.sillyv.vasili.nearbye.helpers.gson;

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
}
