package com.sillyv.vasili.nearbye.helpers.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vasil on 5/27/2016.
 */

public class LocationsDbHelper extends SQLiteOpenHelper
{
    private static final String TAG = "SillyV.DBHelper";
    static final int VERSION = 1;
    static final String DATA_BASE_NAME = "favorites_db";




    public LocationsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + LocationTableHandler.LOCATIONS_TABLE_NAME +
                "(" + LocationTableHandler.ID + " INTEGER PRIMARY KEY," + LocationTableHandler.NAME + " TEXT," +
                LocationTableHandler.LONGITUDE + " DOUBLE," + LocationTableHandler.LATTITUDE + " DOUBLE," +
                LocationTableHandler.RATING + " DOUBLE," + LocationTableHandler.ADDRESS + " TEXT," +
                LocationTableHandler.ICON_URL + " TEXT)";
        Log.d(TAG, createTableStatement);
        try {
            db.execSQL(createTableStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + LocationTableHandler.LOCATIONS_TABLE_NAME);
        onCreate(db);
    }
}
