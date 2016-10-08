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

public class MyresumeAddGoodListAdapter extends BaseAdapter{
    private Context context;
    private int[] backgroud_img = {
    		R.drawable.bao_bg_orange,
			R.drawable.bao_bg_tlan,
    		R.drawable.bao_bg_red,
			R.drawable.bao_bg_qgreen,
			R.drawable.bao_bg_tuhuang,
			R.drawable.bao_bg_purple,
			R.drawable.bao_bg_lan };
    public ArrayList<HashMap<String, String>>mArray;
	private int type;
    public MyresumeAddGoodListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.myresume_add_good_listview_item,null);
            
            holder.myresumeAddGoodItem = (LinearLayout)convertView.findViewById(R.id.myresume_add_good_item);
            holder.myresumeAddGoodNameCn = (TextView)convertView.findViewById(R.id.myresume_add_good_name_cn);
            holder.myresumeAddGoodNameEn = (TextView)convertView.findViewById(R.id.myresume_add_good_name_en);
            holder.myresumeAddGoodIcon = (ImageView) convertView.findViewById(R.id.myresume_add_good_icon);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.myresumeAddGoodItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
        if(mArray.get(position).get("type").equals("0")){
        	holder.myresumeAddGoodIcon.setImageResource(R.drawable.icon_myresume_add_good);
        	holder.myresumeAddGoodNameCn.setVisibility(View.GONE);
        	holder.myresumeAddGoodNameEn.setVisibility(View.GONE);
        }else {
        	holder.myresumeAddGoodIcon.setVisibility(View.GONE);
        	holder.myresumeAddGoodNameCn.setVisibility(View.VISIBLE);
//        	holder.myresumeAddGoodNameEn.setVisibility(View.VISIBLE);
        	holder.myresumeAddGoodNameCn.setText((String)mArray.get(position).get("tagname_cn"));
//            holder.myresumeAddGoodNameEn.setText((String)mArray.get(position).get("tagname_en"));
            
		}
        
        return convertView;
    }
    class ViewHolder{
    	LinearLayout myresumeAddGoodItem;
    	ImageView myresumeAddGoodIcon;
        TextView myresumeAddGoodNameCn,myresumeAddGoodNameEn;
    }
    

}
