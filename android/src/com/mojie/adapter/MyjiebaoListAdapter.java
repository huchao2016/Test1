package com.mojie.adapter;

import android.content.Context;
import android.util.Log;
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


public class MyjiebaoListAdapter extends BaseAdapter{
    private Context context;
    private int[] backgroud_img = { 
    		R.drawable.jiebao_bg_orange,
    		R.drawable.jiebao_bg_blue,
			R.drawable.jiebao_bg_yellow,
			R.drawable.jiebao_bg_green };
    public ArrayList<HashMap<String, String>>mArray;
	private int uid;
    public MyjiebaoListAdapter(Context context,ArrayList<HashMap<String, String>>mArray, int uid){
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
            convertView=inflater.inflate(R.layout.myjiebao_listview_item,null);
            holder.layoutMyjiebaoItem=(LinearLayout)convertView.findViewById(R.id.layout_myjiebao_item);
            holder.jiebaoType=(TextView)convertView.findViewById(R.id.tv_jiebao_type);
            holder.jiebaoName=(TextView)convertView.findViewById(R.id.tv_jiebao_name);
            holder.jiebaoTime=(TextView)convertView.findViewById(R.id.tv_jiebao_time);
            holder.jiebaoBiddingNum=(TextView)convertView.findViewById(R.id.tv_jiebao_bidding_num);
            holder.jiebaoBudget=(TextView)convertView.findViewById(R.id.tv_jiebao_budget);
            holder.jiebaoDeliveryTime=(TextView)convertView.findViewById(R.id.tv_jiebao_delivery_time);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.layoutMyjiebaoItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
       
        Log.v("lishide", "jiebao >> task_type "+mArray.get(position).get("task_type"));
        Log.v("lishide", "jiebao >> title "+mArray.get(position).get("title"));
        Log.v("lishide", "jiebao >> "+mArray.get(position).get("createddate"));
        Log.v("lishide", "jiebao >> status "+mArray.get(position).get("status"));
        Log.v("lishide", "jiebao >> budget "+mArray.get(position).get("budget"));
        
        holder.jiebaoType.setText((String)mArray.get(position).get("task_type"));
        holder.jiebaoName.setText((String)mArray.get(position).get("title"));
//        holder.jiebaoTime.setText((String)mArray.get(position).get("createddate"));
        String delivery = mArray.get(position).get("createddate").substring(0,10);
        holder.jiebaoTime.setText(delivery);
        
        if (mArray.get(position).get("status").equals("2")) {
        	holder.jiebaoBiddingNum.setText((String)mArray.get(position).get("bids")+"人竞标中");
		}else if (mArray.get(position).get("status").equals("3")){
			if (mArray.get(position).get("bid_tenderuid").equals(""+uid)) {
				holder.jiebaoBiddingNum.setText("完成投标");
			}else {
				holder.jiebaoBiddingNum.setText("完成投标");
			}
		}else if (mArray.get(position).get("status").equals("1")) {
        	holder.jiebaoBiddingNum.setText("待审核");
		}else if (mArray.get(position).get("status").equals("-1")) {
        	holder.jiebaoBiddingNum.setText("审核退回");
		}else if (mArray.get(position).get("status").equals("-2")) {
			holder.jiebaoBiddingNum.setText("投标失败");
		}else if (mArray.get(position).get("status").equals("0")) {
        	holder.jiebaoBiddingNum.setText("新建");
		}
        
        holder.jiebaoBudget.setText("预算 "+(String)mArray.get(position).get("budget"));
        holder.jiebaoDeliveryTime.setText("交付 "+(String)mArray.get(position).get("deliverieddate"));
        
        return convertView;
    }
    class ViewHolder{
    	LinearLayout layoutMyjiebaoItem;
        TextView jiebaoType,jiebaoName,jiebaoTime,jiebaoBiddingNum,jiebaoBudget,jiebaoDeliveryTime;
    }

}
