package com.sillyv.vasili.nearbye.misc;

/**
 * Created by vasili on 23-May-16.
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

//https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=coffee&location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=AIzaSyDTiFKjG8U9Swd0L0bbC9AB56SOUV9nCg0

    public static String urlBuilderSearch(String query, String location, int radius)
    {
        return stringOne + stringOneAlternate + query + "&" + otherParams(location,radius);
    }

    public static String urlBuilderLocation(String location, int radius)
    {
        return stringOne + otherParams(location,radius);
    }

    private static String otherParams(String location, int radius)
    {
        return  stringTwo + location + stringThree + radius + stringFour + API_KEY + stringFive;
    }

}
