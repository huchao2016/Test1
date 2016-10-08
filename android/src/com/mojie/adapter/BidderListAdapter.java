package com.mojie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;

public class BidderListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
    public BidderListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
        this.context=context;
        this.mArray = mArray;
    }

    @Override
    public int getCount() {
    	return mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.bidder_listview_item,null);
            holder.listBidderName=(TextView)convertView.findViewById(R.id.list_bidder_name);
            holder.listBidderTime=(TextView)convertView.findViewById(R.id.list_bidder_time);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.listBidderName.setText((String)mArray.get(position).get("username"));
        holder.listBidderTime.setText((String)mArray.get(position).get("tendeddate"));
        return convertView;
    }
    class ViewHolder{
        TextView listBidderName,listBidderTime;
    }

}
