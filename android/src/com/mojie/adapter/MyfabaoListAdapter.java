package com.mojie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;


public class MyfabaoListAdapter extends BaseAdapter{
    private Context context;
    private int[] backgroud_img = { 
    		R.drawable.jiebao_bg_orange,
    		R.drawable.jiebao_bg_blue,
			R.drawable.jiebao_bg_yellow,
			R.drawable.jiebao_bg_green };
    public ArrayList<HashMap<String, String>>mArray;
    private int uid;
    public MyfabaoListAdapter(Context context,ArrayList<HashMap<String, String>>mArray, int uid){
        this.context = context;
        this.mArray = mArray;
        this.uid = uid;
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
            convertView=inflater.inflate(R.layout.myfabao_listview_item,null);
            holder.layoutMyfabaoItem=(LinearLayout)convertView.findViewById(R.id.layout_myfabao_item);
            holder.fabaoType=(TextView)convertView.findViewById(R.id.tv_fabao_type);
            holder.fabaoName=(TextView)convertView.findViewById(R.id.tv_fabao_name);
            holder.fabaoTime=(TextView)convertView.findViewById(R.id.tv_fabao_time);
            holder.fabaoBiddingNum=(TextView)convertView.findViewById(R.id.tv_fabao_bidding_num);
            holder.fabaoBudget=(TextView)convertView.findViewById(R.id.tv_fabao_budget);
            holder.fabaoDeliveryTime=(TextView)convertView.findViewById(R.id.tv_fabao_delivery_time);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.layoutMyfabaoItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
        holder.fabaoType.setText((String)mArray.get(position).get("task_type"));
        holder.fabaoName.setText((String)mArray.get(position).get("title"));
//        holder.fabaoTime.setText((String)mArray.get(position).get("createddate"));
        String delivery = mArray.get(position).get("createddate").substring(0,10);
        holder.fabaoTime.setText(delivery);
        
//        if (mArray.get(position).get("taskoverflg").equals("1")
//        && (!mArray.get(position).get("status").equals("3"))) {
//        	holder.fabaoBiddingNum.setText("任务过期");
//		}
//        if (mArray.get(position).get("status").equals("2")) {
//        	holder.fabaoBiddingNum.setText((String)mArray.get(position).get("bids")+"人竞标中");
//		}else if (mArray.get(position).get("status").equals("3")){
//			if (mArray.get(position).get("bid_tenderuid").equals(""+uid)) {
//				holder.fabaoBiddingNum.setText("您已中标");
//			}else {
//				holder.fabaoBiddingNum.setText("他人已中标");
//			}
//		}
        
        if (mArray.get(position).get("status").equals("3")){
			if (mArray.get(position).get("bid_tenderuid").equals(""+uid)) {
				holder.fabaoBiddingNum.setText("完成投标");
			}else {
				holder.fabaoBiddingNum.setText("完成投标");
			}
		}else{
			if (mArray.get(position).get("taskoverflg").equals("1")){
				holder.fabaoBiddingNum.setText("任务过期");
			}else{
				if (mArray.get(position).get("status").equals("2")) {
		        	holder.fabaoBiddingNum.setText((String)mArray.get(position).get("bids")+"人竞标中");
				}else if (mArray.get(position).get("status").equals("1")) {
		        	holder.fabaoBiddingNum.setText("待审核");
				}else if (mArray.get(position).get("status").equals("-1")) {
		        	holder.fabaoBiddingNum.setText("审核退回");
				}else if (mArray.get(position).get("status").equals("-2")) {
					holder.fabaoBiddingNum.setText("投标失败");
				}else if (mArray.get(position).get("status").equals("0")) {
		        	holder.fabaoBiddingNum.setText("新建");
				}
			}
			
		}
        
        holder.fabaoBudget.setText("预算 "+(String)mArray.get(position).get("budget"));
        holder.fabaoDeliveryTime.setText("交付 "+(String)mArray.get(position).get("deliverieddate"));
        
        return convertView;
    }
    class ViewHolder{
    	LinearLayout layoutMyfabaoItem;
        TextView fabaoType,fabaoName,fabaoTime,fabaoBiddingNum,fabaoBudget,fabaoDeliveryTime;
    }

}
