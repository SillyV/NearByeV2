package com.sillyv.vasili.nearbye.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sillyv.vasili.nearbye.helpers.gson.GooglePlacesHolder;
import com.sillyv.vasili.nearbye.misc.Prefs;

import java.util.Map;

/**
 * Created by jbt on 5/8/2016.
 * Edited by Vasili.Fedotov
 */
public class ServiceClient
{

    private static final String TAG = "SillyV.ServiceClient";
    private Context context;
    private static ServiceClient instance;
    private RequestQueue queue;

    public interface Callback<T>
    {
        public void callback(T response);
    }

    private ServiceClient(Context context)
    {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public static ServiceClient get(Context context)
    {
        if (instance == null)
        {
            instance = new ServiceClient(context);
        }
        return instance;
    }

    private static final String SERVER_HOST = "https://maps.googleapis.com";

    public void sendRequest(String action, int method, final Callback<GooglePlacesHolder> callback, final Class<GooglePlacesHolder> responseClass, final Map<String, String> params)
    {

        String url = SERVER_HOST + action;

        // Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response)
                    {
                        Log.d(TAG, "Response is: " + response);
                        Gson gson = new Gson();
                        GooglePlacesHolder t = gson.fromJson(response, responseClass);
                        // Display the first 500 characters of the response string.
                        if (t.getResults().size() > 0)
                        {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Prefs.SAVED_JSON_RESULT, response);
                            editor.commit();
                        }
                        callback.callback(t);

                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, error.getMessage());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public <T> void sendGetRequest(String action, final Callback<GooglePlacesHolder> callback, final Class<GooglePlacesHolder> responseClass, String params)
    {
        sendRequest(action + params, Request.Method.GET, callback, responseClass, null);
    }
}
