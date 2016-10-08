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
import com.mojie.utils.ConstUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyLetterReceivedListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private ImageLoader imageLoader;
    public MyLetterReceivedListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
        this.context=context;
        this.mArray = mArray;
        imageLoader = ImageLoader.getInstance();
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
            convertView=inflater.inflate(R.layout.myletter_received_listview_item,null);
            
            holder.listMyletterName=(TextView)convertView.findViewById(R.id.list_myletter_name);
            holder.listMyletterContent=(TextView)convertView.findViewById(R.id.list_myletter_content);
            holder.listMyletterTime=(TextView)convertView.findViewById(R.id.list_myletter_time);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.listMyletterName.setText((String)mArray.get(position).get("username"));
        holder.listMyletterContent.setText((String)mArray.get(position).get("content"));
        holder.listMyletterTime.setText((String)mArray.get(position).get("createddate"));
        com.mojie.view.RoundedWebImageView listMyletterImg=(com.mojie.view.RoundedWebImageView) convertView.findViewById(R.id.list_myletter_img);
        headpic = ConstUtils.IMGURL + (String)mArray.get(position).get("headpic");
        imageLoader.displayImage(headpic, listMyletterImg, ConstUtils.options);
        
        return convertView;
    }
    class ViewHolder{
//    	com.mojie.view.RoundedWebImageView listMyletterImg;
        TextView listMyletterName,listMyletterContent,listMyletterTime;
    }

}
