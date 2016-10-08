package com.mojie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;

public class InfoClassListAdapter extends BaseAdapter{
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
    public InfoClassListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.infos_class_listview_item,null);
            
            holder.infosClassItem=(LinearLayout)convertView.findViewById(R.id.infos_class_item);
            holder.infosClassName=(TextView)convertView.findViewById(R.id.infos_class_name);
            holder.infosClassNum=(TextView)convertView.findViewById(R.id.tv_list_class_infos_num);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.taskClassItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
        holder.infosClassName.setText((String)mArray.get(position).get("name"));
        holder.infosClassNum.setVisibility(View.GONE);
        return convertView;
    }
    class ViewHolder{
    	LinearLayout infosClassItem;
        TextView infosClassName,infosClassNum;
    }

}
