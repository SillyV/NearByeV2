package com.sillyv.vasili.nearbye.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sillyv.vasili.nearbye.R;


/**
 * Created by Vasili.Fedotov on 23-May-16.
 */
public class Prefs
{
    public static final String API_KEY = "AIzaSyDTiFKjG8U9Swd0L0bbC9AB56SOUV9nCg0";

    public static final String stringOne = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String stringOneAlternate = "keyword=";
    public static final String stringTwo = "location=";
    public static final String stringThree = "&radius=";
    public static final String stringFour = "&key=";
    public static final String stringFive = "\n";
    private static final String TAG = "SillyV.Prefs";
    public static final String SAVED_JSON_RESULT = "saved_json_result";
    public static final String METERS = "km";
    public static final String MILES = "Miles";


//https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=coffee&location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=AIzaSyDTiFKjG8U9Swd0L0bbC9AB56SOUV9nCg0

    public static String getStringParamForSearch(String myLocation)
    {
        return "?" + "location=" + myLocation + "&radius=2000&key=" + Prefs.API_KEY;
    }

    public static String getStringParamForSearch(String location, String query)
    {
        query = query.trim().replace(" ", "+");
        return "?" + "keyword=" + query + "&" + "location=" + location + "&" + "rankby=distance" + "&" + "key=" + Prefs.API_KEY;
    }

    public static String getUnit(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
       String strValue = sp.getString(context.getResources().getString(R.string.unit_list_key), "0");
       return context.getResources().getStringArray(R.array.entries_list_preference)[Integer.valueOf(strValue)];
    }


}
