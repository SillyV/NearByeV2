package com.sillyv.vasili.nearbye.helpers.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili.Fedotov on 6/22/2016.
 */

public class LocationsDbHelper extends SQLiteOpenHelper
{
    private static final String TAG = "SillyV.DBHelper";
    static final int VERSION = 2;
    static final String DATA_BASE_NAME = "favorites_db";


    public LocationsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String createTableStatement = TableBuilder(LocationTableHandler.LOCATIONS_TABLE_NAME, LocationTableHandler.ID, LocationTableHandler.getDoubles(), LocationTableHandler.getStrings(), LocationTableHandler.getIntegers());

        Log.d(TAG, createTableStatement);
        try
        {
            db.execSQL(createTableStatement);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < 2)
        {
            Database_move_from_1_to_2(db);
        }
    }

    private String TableBuilder(String tableName, String id, List<String> doubles, List<String> strings, List<String> integers)
    {
        String createTableStatement = "CREATE TABLE " + tableName + "(" + id + " INTEGER PRIMARY KEY";
        for (String dbl : doubles)
        {
            createTableStatement += "," + dbl + " DOUBLE";
        }
        for (String str : strings)
        {
            createTableStatement += "," + str + " TEXT";
        }
        for (String myint : integers)
        {
            createTableStatement += "," + myint + " INTEGER";
        }
        createTableStatement += ")";
        return createTableStatement;

    }

    private void Database_move_from_1_to_2(SQLiteDatabase db)
    {
        String Database_move_from_1_to_2 = "ALTER TABLE " + LocationTableHandler.LOCATIONS_TABLE_NAME + " ADD COLUMN " + LocationTableHandler.GOOGLE_ID + " TEXT;";
        try
        {
            db.execSQL(Database_move_from_1_to_2);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }


}
