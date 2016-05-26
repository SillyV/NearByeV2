package com.sillyv.vasili.nearbye.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vasili on 23-May-16.
 */
public class HttpRequest
{
    private static final String DEBUG_TAG = "SillyV.HttpRequest";
    private Context context;
    private HttpListener httpListener;
    private String url;
    private int requestCode;

    public interface HttpListener
    {
        public void HttpCallBack(String response, int requestCode);

        public void httpErrorCallBack(String error, int requestCode);
    }

    public HttpRequest(Context context, HttpListener httpListener, String url, int requestCode)
    {
        this.context = context;
        this.httpListener = httpListener;
        this.url = url;
        this.requestCode = requestCode;
    }

    public boolean checkConnection()
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void runRequest()
    {
        if (checkConnection())
        {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                new DownloadWebpageTask().execute(url);
            }
            else
            {
                httpListener.httpErrorCallBack("No network connection available.", requestCode);
            }
        }
        else
        {
            httpListener.httpErrorCallBack("No network connection available.", requestCode);
        }
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {

            // params comes from the execute() call: params[0] is the url.
            try
            {
                return downloadUrl(urls[0]);
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            httpListener.HttpCallBack(result, requestCode);
        }

        private String downloadUrl(String myurl) throws IOException
        {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 40000;

            try
            {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
            finally
            {
                if (is != null)
                {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
        {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }


    }
}
