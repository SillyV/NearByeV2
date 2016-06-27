package com.sillyv.vasili.nearbye.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.database.LocationTableHandler;
import com.sillyv.vasili.nearbye.helpers.gson.GooglePlacesHolder;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.sillyv.vasili.nearbye.helpers.recycler.MyAdapter;
import com.sillyv.vasili.nearbye.misc.Prefs;
import com.sillyv.vasili.nearbye.networking.ServiceClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili.Fedotov on 6/22/2016.
 */
public class ResultsFragment
        extends Fragment implements View.OnClickListener, View.OnLongClickListener
{
    //Constants
    private static final String TAG = "test";

    //Primitive Fields
    private boolean hasLocation = false;

    //Complex Fields
    private MyAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private View.OnClickListener oc;
    private View.OnLongClickListener olc;
    private Location myLocation;
    //Custom Class Fields
    private ServiceClient.Callback<GooglePlacesHolder> mCallback = new ServiceClient.Callback<GooglePlacesHolder>()
    {
        static final String TAG = "SillyV.SearchResults";

        @Override
        public void callback(GooglePlacesHolder response)
        {
            mListener.setListVisible();
            mAdapter = new MyAdapter(response.getResults(), oc, olc, getContext());
            if (hasLocation)
            {
                mAdapter.updateDistance(myLocation, Prefs.getUnit(getContext()));
            }
            else
            {
                mAdapter.updateNullDistance();
            }
            mRecyclerView.setAdapter(mAdapter);
        }
    };


    //Constructors / onCreate methods

    public ResultsFragment()
    {
    }

    public static ResultsFragment newInstance(String param1, String param2)
    {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_results, container, false);
        oc = this;
        olc = this;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.results_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        return v;
    }





    //Override Methods
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    //Implemented Methods
    @Override
    public void onClick(View v)
    {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        mListener.onFragmentInteraction(mAdapter.Items().get(itemPosition));
    }

    @Override
    public boolean onLongClick(View v)
    {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        LocationTableHandler db = new LocationTableHandler(getContext());
        if (db.isGoogleIDAlreadyInDB(mAdapter.Items().get(itemPosition).getId()))
        {
            db.removeLocation(mAdapter.Items().get(itemPosition).getId());
            mAdapter.Items().get(itemPosition).setFavorite(false);
        }
        else
        {
            db.addLocation(mAdapter.Items().get(itemPosition));
            mAdapter.Items().get(itemPosition).setFavorite(true);
        }
        mAdapter.notifyDataSetChanged();
        return false;
    }

    //Subs

    public void updateUnits()
    {
        mAdapter.updateDistance(myLocation,Prefs.getUnit(getContext()));
        mAdapter.notifyDataSetChanged();
    }
    public void goToFavorites()
    {
        mCallback.callback(new GooglePlacesHolder(null, null, getFavorites(), null));
    }

    //    public void currentQueryIs(String newText)
//    {
//        if (newText.equals(""))
//        {
//            mAdapter = new MyAdapter(getFavorites(), oc, olc, getContext());
//            mRecyclerView.setAdapter(mAdapter);
//
//        }
//    }

    public void goToHistory()
    {
        mCallback.callback(getHistory());
    }

    public void SearchQueryINGooglePlacesWebService(final String query, final Location location)
    {
        final String myLocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        ServiceClient.get(getContext()).sendGetRequest("/maps/api/place/nearbysearch/json",
                mCallback, GooglePlacesHolder.class, Prefs.getStringParamForSearch(myLocation, query));
    }


    public void SearchQueryINGooglePlacesWebService(final Location location)
    {
        final String myLocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        ServiceClient.get(getContext()).sendGetRequest("/maps/api/place/nearbysearch/json",
                mCallback, GooglePlacesHolder.class, Prefs.getStringParamForSearch(myLocation));
    }

    public void locationChanged(android.location.Location location)
    {
        this.myLocation = location;
        this.hasLocation = true;
    }

    //Functions

    private GooglePlacesHolder getHistory()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonString = sharedPreferences.getString(Prefs.SAVED_JSON_RESULT, "");
        if (!jsonString.equals(""))
        {
            GooglePlacesHolder googlePlacesHolder = new Gson().fromJson(jsonString, GooglePlacesHolder.class);
            return (googlePlacesHolder);
        }
        return null;
    }

    private List<Results> getFavorites()
    {
        List<Results> resultsArrayList = new ArrayList<>();
        LocationTableHandler db = new LocationTableHandler(getContext());
        resultsArrayList.addAll(db.getAllLocations());
        return resultsArrayList;
    }

    //Interface

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Results results);

        void setListVisible();
    }
}
