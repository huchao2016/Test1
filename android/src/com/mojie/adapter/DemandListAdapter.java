package com.mojie.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;

public class DemandListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
    public DemandListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            holder.listFileName=(TextView)convertView.findViewById(R.id.list_bidder_name);
            holder.listFileDown=(TextView)convertView.findViewById(R.id.list_bidder_time);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.listFileName.setText((String)mArray.get(position).get("filename"));
        holder.listFileDown.setText("");
//        holder.listFileDown.setText("下载");
        holder.listFileDown.setTextColor(Color.BLUE);
        
        return convertView;
    }
    class ViewHolder{
        TextView listFileName,listFileDown;
    }

}
