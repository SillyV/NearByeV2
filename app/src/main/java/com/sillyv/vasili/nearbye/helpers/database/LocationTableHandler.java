package com.sillyv.vasili.nearbye.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.sillyv.vasili.nearbye.helpers.gson.Geometry;
import com.sillyv.vasili.nearbye.helpers.gson.Location;
import com.sillyv.vasili.nearbye.helpers.gson.Results;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */

public class LocationTableHandler
{


    public static final String LOCATIONS_TABLE_NAME = "locations_table";
    public static final String ID = "id";
    public static final String NAME = "title";
    public static final String LONGITUDE = "longitude";
    public static final String LATTITUDE = "lattitude";
    public static final String ICON_URL = "icon_url";
    public static final String RATING = "ratings";
    public static final String ADDRESS = "address";
    public static final String GOOGLE_ID = "google_id";
    private static final String TAG = "SillyV.TableHandler";
    private final LocationsDbHelper dbHelper;




    public static List<String> getStrings()
    {
      List<String> list = new ArrayList<>();
        list.add(NAME);
        list.add(ICON_URL);
        list.add(ADDRESS);
        list.add(GOOGLE_ID);
        return list;
    }
    public static List<String> getDoubles()
    {
        List<String> list = new ArrayList<>();
        list.add(LONGITUDE);
        list.add(LATTITUDE);
        list.add(RATING);
        return list;
    }
    public static List<String> getIntegers()
    {
        List<String> list = new ArrayList<>();

        return list;
    }

    public LocationTableHandler(Context context)
    {
        dbHelper = new LocationsDbHelper(context, LocationsDbHelper.DATA_BASE_NAME, null, LocationsDbHelper.VERSION);
    }
    public List<Results> getAllLocations()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(LOCATIONS_TABLE_NAME, null, null, null, null, null, null);
        List<Results> results = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Results result = getLocation(cursor);
            results.add(result);
        }
        return results;
    }

    public Results getLocation(int id)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(LOCATIONS_TABLE_NAME, null, ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst())
        {
            Results result = getLocation(cursor);
            return result;
        }
        return null;
    }

    public boolean isGoogleIDAlreadyInDB(String gid)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(LOCATIONS_TABLE_NAME, null, GOOGLE_ID + " = '" + gid + "'", null, null, null, null);
        if (cursor.moveToFirst())
        {
            return true;
        }
        return false;
    }




    public void addLocation(Results newResult)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getLocationValues(newResult);
        try
        {
            db.insert(LOCATIONS_TABLE_NAME, null, values);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }

//    public void updateLocation(Results editedResult)
//    {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = getLocationValues(editedResult);
//        try
//        {
//            db.update(LOCATIONS_TABLE_NAME, values, ID + " = " + editedResult.getID(), null);
//        }
//        catch (SQLiteException e)
//        {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
//        db.close();
//    }

    public void removeLocation(int id)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.delete(LOCATIONS_TABLE_NAME, ID + " = " + id, null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }
    public void removeLocation(String gid)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.delete(LOCATIONS_TABLE_NAME, GOOGLE_ID + " = '" + gid + "'", null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }
    private static ContentValues getLocationValues(Results result)
    {
        ContentValues values = new ContentValues();
        values.put(GOOGLE_ID,result.getId());
        values.put(NAME, result.getName());
        values.put(LONGITUDE, result.getGeometry().getLocation().getLng());
        values.put(LATTITUDE, result.getGeometry().getLocation().getLat());
        values.put(ICON_URL, result.getIcon());
        values.put(RATING, result.getRating());
        values.put(ADDRESS, result.getVicinity());
        return values;
    }

    private Results getLocation(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String gid = cursor.getString(cursor.getColumnIndex(GOOGLE_ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String icon = cursor.getString(cursor.getColumnIndex(ICON_URL));
        String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
        double lat = cursor.getDouble(cursor.getColumnIndex(LATTITUDE));
        double lon = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
        double ratings = cursor.getDouble(cursor.getColumnIndex(RATING));
        return new Results(new Geometry(new Location(lat, lon)), icon, gid, name, null, ratings, "Should I put an easter egg here?", null, address, true);
    }


    public void removeAll()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.delete(LOCATIONS_TABLE_NAME, null, null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }
}
