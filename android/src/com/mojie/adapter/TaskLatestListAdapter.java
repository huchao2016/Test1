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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mojie.utils.ConstUtils;

public class TaskLatestListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private int uid;
	private ImageLoader imageLoader;
    public TaskLatestListAdapter(Context context,ArrayList<HashMap<String, String>>mArray,int id){
        this.context=context;
        this.mArray = mArray;
        this.uid = id;
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
            convertView=inflater.inflate(R.layout.task_latest_listview_item,null);
            holder.taskLatestType=(TextView)convertView.findViewById(R.id.list_task_latest_type);
            holder.taskLatestName=(TextView)convertView.findViewById(R.id.list_task_latest_name);
            holder.taskLatestTime=(TextView)convertView.findViewById(R.id.list_task_latest_time);
            holder.taskLatestBiddingNum=(TextView)convertView.findViewById(R.id.list_task_latest_bidding_num);
            holder.taskLatestBudget=(TextView)convertView.findViewById(R.id.list_task_latest_budget);
            holder.taskLatestDeliveryTime=(TextView)convertView.findViewById(R.id.list_task_latest_delivery_time);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        com.mojie.view.RoundedWebImageView listTaskLatestImg=(com.mojie.view.RoundedWebImageView) convertView.findViewById(R.id.list_task_latest_img);
        
        holder.taskLatestType.setText((String)mArray.get(position).get("task_type"));
        holder.taskLatestName.setText((String)mArray.get(position).get("title"));
//        holder.taskLatestTime.setText((String)mArray.get(position).get("createddate"));
        String delivery = mArray.get(position).get("createddate").substring(0,10);
        holder.taskLatestTime.setText(delivery);
        if (mArray.get(position).get("status").equals("2")) {
        	holder.taskLatestBiddingNum.setText((String)mArray.get(position).get("bids")+"人竞标中");
		}else if(mArray.get(position).get("status").equals("3")) {
			holder.taskLatestBiddingNum.setText("已中标");
		}
        holder.taskLatestBudget.setText("预算 "+(String)mArray.get(position).get("budget"));
        holder.taskLatestDeliveryTime.setText("交付 "+(String)mArray.get(position).get("deliverieddate"));
        holder.taskLatestDeliveryTime.setVisibility(View.GONE);
        
        headpic = ConstUtils.IMGURL + mArray.get(position).get("headpic");
		imageLoader.displayImage(headpic, listTaskLatestImg, ConstUtils.options);
        
        return convertView;
    }
    class ViewHolder{
//    	com.mojie.view.RoundedWebImageView listTaskLatestImg;
        TextView taskLatestType,taskLatestName,taskLatestTime,taskLatestBiddingNum,taskLatestBudget,taskLatestDeliveryTime;
    }
    

}
