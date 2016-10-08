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

public class InfoLatestListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
    public InfoLatestListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.info_all_listview_item,null);
            holder.infoAllTitle=(TextView)convertView.findViewById(R.id.list_info_all_title);
            holder.infoAllDescription=(TextView)convertView.findViewById(R.id.list_info_all_description);
            holder.infoAllTime=(TextView)convertView.findViewById(R.id.list_info_all_time);
            holder.infoAllSee=(TextView)convertView.findViewById(R.id.list_info_all_see);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        
        holder.infoAllTitle.setText((String)mArray.get(position).get("title"));
        holder.infoAllDescription.setText((String)mArray.get(position).get("description"));
        holder.infoAllTime.setText((String)mArray.get(position).get("createddate"));
        holder.infoAllSee.setText((String)mArray.get(position).get("clicks")+"人看过");
        
        return convertView;
    }
    class ViewHolder{
        TextView infoAllTitle,infoAllDescription,infoAllTime,infoAllSee;
    }
    

}
