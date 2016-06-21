package com.sillyv.vasili.nearbye.activities.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.database.LocationTableHandler;
import com.sillyv.vasili.nearbye.helpers.gson.Geometry;
import com.sillyv.vasili.nearbye.helpers.gson.GoogleMapper;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.sillyv.vasili.nearbye.helpers.recycler.LocationListItem;
import com.sillyv.vasili.nearbye.helpers.recycler.MyAdapter;
import com.sillyv.vasili.nearbye.misc.Prefs;
import com.sillyv.vasili.nearbye.networking.ServiceClient;

import java.util.ArrayList;
import java.util.List;

public class ResultsFragment
        extends Fragment implements View.OnClickListener, View.OnLongClickListener
{
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private ServiceClient.Callback<GoogleMapper> mCallback = new ServiceClient.Callback<GoogleMapper>()
    {
        static final String TAG = "SillyV.SearchResults";

        @Override
        public void callback(GoogleMapper response)
        {
            mListener.setListVisible();
            mAdapter = new MyAdapter(response.getResults(), oc, olc, getContext());
            mRecyclerView.setAdapter(mAdapter);
        }
    };
    private View.OnClickListener oc;
    private View.OnLongClickListener olc;

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
        //code here

        mRecyclerView = (RecyclerView) v.findViewById(R.id.results_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
       mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(getFavorites(), oc, olc, getContext());
//        mRecyclerView.setAdapter(mAdapter);


        return v;
    }

    private List<Results> getFavorites()
    {
        List<Results> aaa = new ArrayList<>();
        LocationTableHandler db = new LocationTableHandler(getContext());
        aaa.addAll(db.getAllLocations());
        return aaa;
    }

//    public void onLocationSelected(Results results)
//    {
//        if (mListener != null)
//        {
//            mListener.onFragmentInteraction(results);
//        }
//    }

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

    public void startingSettings()
    {
        //mAdapter = new MyAdapter(getFavorites(), oc, olc, getContext());
        //mRecyclerView.setAdapter(mAdapter);
    }

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

    public void currentQueryIs(String newText)
    {
        if (newText.equals(""))
        {
            mAdapter = new MyAdapter(getFavorites(), oc, olc, getContext());
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Results results);

        void setListVisible();
    }

    public void SearchQueryINGooglePlacesWebService(final String query, final Location location)
    {

        final String myLocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());

        ServiceClient.get(getContext()).sendGetRequest("/maps/api/place/nearbysearch/json",
                mCallback, GoogleMapper.class, getStringParamsForAddBobRequest(myLocation, query));

        //  HttpRequest httpRequest = new HttpRequest(this, this, Prefs.urlBuilderLocation("31.801999,35.2093514",2000), 4001);
        //  httpRequest.runRequest();
    }


    public void SearchQueryINGooglePlacesWebService(final Location location)
    {

        final String myLocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());

        ServiceClient.get(getContext()).sendGetRequest("/maps/api/place/nearbysearch/json",
                mCallback, GoogleMapper.class, getStringParamsForAddBobRequest(myLocation));

        //  HttpRequest httpRequest = new HttpRequest(this, this, Prefs.urlBuilderLocation("31.801999,35.2093514",2000), 4001);
        //  httpRequest.runRequest();
    }

    private String getStringParamsForAddBobRequest(String myLocation)
    {
        return "?location=" + myLocation + "&radius=2000" + "&key=" + Prefs.API_KEY;
    }


    private String getStringParamsForAddBobRequest(String location, String query)
    {
        query = query.trim().replace(" ", "+");
        return "?keyword=" + query + "&location=" + location + "&rankby=distance" + "&key=" + Prefs.API_KEY;
    }
}
