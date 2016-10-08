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

public class TaskSearchAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private ImageLoader imageLoader;
    public TaskSearchAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.task_task_class_listview_item,null);
            
            holder.taskTaskType=(TextView)convertView.findViewById(R.id.list_task_task_class_type);
            holder.taskTaskName=(TextView)convertView.findViewById(R.id.list_task_task_class_name);
            holder.taskTaskTime=(TextView)convertView.findViewById(R.id.list_task_task_class_time);
            holder.taskTaskBiddingNum=(TextView)convertView.findViewById(R.id.list_task_task_class_bidding_num);
            holder.taskTaskBudget=(TextView)convertView.findViewById(R.id.list_task_task_class_budget);
            holder.taskTaskDeliveryTime=(TextView)convertView.findViewById(R.id.list_task_task_class_delivery_time);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        com.mojie.view.RoundedWebImageView listTaskTaskImg=(com.mojie.view.RoundedWebImageView) convertView.findViewById(R.id.list_task_task_class_img);
        holder.taskTaskType.setText((String)mArray.get(position).get("task_type"));
        holder.taskTaskName.setText((String)mArray.get(position).get("title"));
//        holder.taskTaskTime.setText((String)mArray.get(position).get("createddate"));
        String delivery = mArray.get(position).get("createddate").substring(0,10);
        holder.taskTaskTime.setText(delivery);
        if (mArray.get(position).get("status").equals("2")) {
        	holder.taskTaskBiddingNum.setText(""+(String)mArray.get(position).get("bids")+"人竞标中");
		}else if(mArray.get(position).get("status").equals("3")) {
			holder.taskTaskBiddingNum.setText("已中标");
		}
        holder.taskTaskBudget.setText("预算 "+(String)mArray.get(position).get("budget"));
        holder.taskTaskDeliveryTime.setText("交付 "+(String)mArray.get(position).get("deliverieddate"));

        headpic =ConstUtils.IMGURL + (String)mArray.get(position).get("headpic");
        imageLoader.displayImage(headpic, listTaskTaskImg, ConstUtils.options);
        return convertView;
    }
    class ViewHolder{
//    	com.mojie.view.RoundedWebImageView listTaskTaskImg;
        TextView taskTaskType,taskTaskName,taskTaskTime,taskTaskBiddingNum,taskTaskBudget,taskTaskDeliveryTime;
    }

}
