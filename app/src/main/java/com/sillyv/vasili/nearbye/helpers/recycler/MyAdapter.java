package com.sillyv.vasili.nearbye.helpers.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.gson.Results;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vasili on 5/27/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{


    public List<Results> Items()
    {
        return mDataset;
    }

    private List<Results> mDataset;

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
        public ImageView photo;

        public ViewHolder(View v)
        {
            super(v);
            this.name = (TextView) v.findViewById(R.id.list_item_name);
            this.address = (TextView) v.findViewById(R.id.list_item_address);
            this.ratings = (TextView) v.findViewById(R.id.list_item_ratings);
            this.icon = (ImageView) v.findViewById(R.id.list_item_icon);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Results> myDataset)
    {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position).getName());
        holder.address.setText(mDataset.get(position).getVicinity());
        holder.ratings.setText(String.valueOf(mDataset.get(position).getRating()) + " ★");
        Picasso.with(holder.icon.getContext()).load(mDataset.get(position).getIcon()).into(holder.icon);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
}
