package com.mojie.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.FabaoActivity;
import com.mojie.activity.R;
import com.mojie.activity.TaskTaskClassActivity;

public class FabaoFragmentListAdapter extends BaseAdapter{
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
    public FabaoFragmentListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
        this.context=context;
        this.mArray = mArray;
    }

    @Override
    public int getCount() {
//    	return mArray.size();
    	if(mArray.size()%2>0) {
			return mArray.size()/2+1;
		} else {
			return mArray.size()/2;
		}
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private  ViewHolder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        final ViewHolder holder;

        if (convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.fabao_fragment_listview_item,null);
            
            holder.fabaoFragmentItem=(LinearLayout)convertView.findViewById(R.id.fabao_fragment_item);
            holder.baoTypeNameCn=(TextView)convertView.findViewById(R.id.bao_type_name_cn);
            holder.fabaoFragmentItem.setOnClickListener(new LvButtonListener(position)); 
            
            holder.fabaoFragmentItem2=(LinearLayout)convertView.findViewById(R.id.fabao_fragment_item2);
            holder.baoTypeNameCn2=(TextView)convertView.findViewById(R.id.bao_type_name_cn2);
            holder.fabaoFragmentItem2.setOnClickListener(new LvButtonListener(position)); 
            if (null != holder)
            {
            	convertView.setTag(holder);
            	
            }
        }else {
            holder=(ViewHolder)convertView.getTag();
            holder.fabaoFragmentItem2.setOnClickListener(new LvButtonListener(position)); 
        	holder.fabaoFragmentItem.setOnClickListener(new LvButtonListener(position));
        }
        
        if((mArray.size()%2 == 1) && (position == getCount()-1)){
        	holder.baoTypeNameCn.setText((String)mArray.get(2 * position).get("tagname_cn"));
          
            holder.fabaoFragmentItem2.setVisibility(View.INVISIBLE);
            
        }else {
        	holder.baoTypeNameCn.setText((String)mArray.get(2 * position).get("tagname_cn"));
        
            holder.fabaoFragmentItem2.setVisibility(View.VISIBLE);
            holder.baoTypeNameCn2.setText((String)mArray.get(2 * position + 1).get("tagname_cn"));
		}  
        
        return convertView;
    }
    class ViewHolder{
    	LinearLayout fabaoFragmentItem,fabaoFragmentItem2;
        TextView baoTypeNameCn,baoTypeNameEn;
        TextView baoTypeNameCn2,baoTypeNameEn2;
    }
    
    private class LvButtonListener implements View.OnClickListener {  
        private int position;  
  
        LvButtonListener(int pos) {  
            position = pos;  
        }  
  
        @Override  
        public void onClick(View v) {  
            int vid = v.getId();           
              
            if (vid == holder.fabaoFragmentItem.getId()){ //删除一行记录  
            	Intent intent = new Intent(context,FabaoActivity.class);
				intent.putExtra("id", (String)mArray.get(2*position).get("id"));
				intent.putExtra("tagname_cn", (String)mArray.get(2*position).get("tagname_cn"));
				context.startActivity(intent);
            }else if(vid == holder.fabaoFragmentItem2.getId()){//发送邮件  
            	Intent intent = new Intent(context,FabaoActivity.class);
				intent.putExtra("id", (String)mArray.get(2*position + 1).get("id"));
				intent.putExtra("tagname_cn", (String)mArray.get(2*position + 1).get("tagname_cn"));
				context.startActivity(intent);  
            }  
        } 
    }
}
