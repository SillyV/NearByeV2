package com.sillyv.vasili.nearbye.helpers.recycler;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    private HashMap<Boolean, Integer> visiblilityMAp = new HashMap(2);
    private int lastPosition = -1;
    private final View.OnLongClickListener mLongListener;

    private Context context;

    public List<Results> Items()
    {
        return mDataset;
    }

    private List<Results> mDataset;
    private View.OnClickListener mListener;

    public void updateDistance(Location location, String unit)
    {
        for (Results results : mDataset)
        {
            results.setDistance(location,unit);
        }
    }

    public void updateNullDistance()
    {
        for (Results results : mDataset)
        {
            results.setNullDistance();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView name;
        public TextView address;
        public TextView ratings;
        public ImageView icon;
        public ImageView favorites;
        public RelativeLayout container;

        public ViewHolder(View v)
        {
            super(v);
            this.name = (TextView) v.findViewById(R.id.list_item_name);
            this.address = (TextView) v.findViewById(R.id.list_item_address);
            this.ratings = (TextView) v.findViewById(R.id.list_item_ratings);
            this.icon = (ImageView) v.findViewById(R.id.list_item_icon);
            this.favorites = (ImageView) v.findViewById(R.id.favorites_indicator);
            this.container = (RelativeLayout) v.findViewById(R.id.item_layout_container);
            //v.setOnCreateContextMenuListener(this);
        }

        public void clearAnimation()
        {
            container.clearAnimation();
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Results> myDataset, View.OnClickListener mListener, View.OnLongClickListener mLongListener, Context context)
    {
        this.context = context;
        mDataset = myDataset;
        this.mListener = mListener;
        this.mLongListener = mLongListener;
        visiblilityMAp.put(true, View.VISIBLE);
        visiblilityMAp.put(false, View.GONE);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(mListener);
        v.setOnLongClickListener(mLongListener);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.name.setText(mDataset.get(position).getName());
        holder.address.setText(mDataset.get(position).getVicinity());
        holder.ratings.setText(String.valueOf(mDataset.get(position).getDistance()));
        Picasso.with(holder.icon.getContext()).load(mDataset.get(position).getIcon()).into(holder.icon);
        //noinspection ResourceType
        holder.favorites.setVisibility(visiblilityMAp.get(mDataset.get(position).isFavorite()));
        setAnimation(holder.container, position);

    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        if (mDataset != null)
        {
            return mDataset.size();
        }
        return 0;
    }

    public void addItem(Results results)
    {
        mDataset.add(results);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder)
    {
        ((ViewHolder) holder).clearAnimation();
    }

    public void removeItem(Results results)
    {
        for (Results res : mDataset)
        {
            if (res.getId() == results.getId())
            {
                mDataset.remove(res);
                return;
            }
        }
    }
}
