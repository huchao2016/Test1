package com.mojie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;
import com.mojie.activity.TaskTaskClassActivity;

public class TaskClassListAdapter extends BaseAdapter{
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
    public TaskClassListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.task_class_listview_item,null);
            
            holder.taskClassItem=(RelativeLayout)convertView.findViewById(R.id.task_class_item);
            holder.taskClassNameCn=(TextView)convertView.findViewById(R.id.task_class_name_cn);
            holder.taskClassNameEn=(TextView)convertView.findViewById(R.id.task_class_name_en);
            holder.taskClassNum=(TextView)convertView.findViewById(R.id.tv_list_class_task_num);
            holder.taskClassItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context,TaskTaskClassActivity.class);
					intent.putExtra("id", (String)mArray.get(2*position).get("id"));
					intent.putExtra("tagname_cn", (String)mArray.get(2*position).get("tagname_cn"));
					context.startActivity(intent);
				}
			});
            
            holder.taskClassItem2=(RelativeLayout)convertView.findViewById(R.id.task_class_item2);
            holder.taskClassNameCn2=(TextView)convertView.findViewById(R.id.task_class_name_cn2);
            holder.taskClassNameEn2=(TextView)convertView.findViewById(R.id.task_class_name_en2);
            holder.taskClassNum2=(TextView)convertView.findViewById(R.id.tv_list_class_task_num2);
            holder.taskClassItem2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context,TaskTaskClassActivity.class);
					intent.putExtra("id", (String)mArray.get(2*position + 1).get("id"));
					intent.putExtra("tagname_cn", (String)mArray.get(2*position + 1).get("tagname_cn"));
					context.startActivity(intent);
				}
			});
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
            holder.taskClassItem2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context,TaskTaskClassActivity.class);
					intent.putExtra("id", (String)mArray.get(2*position + 1).get("id"));
					intent.putExtra("tagname_cn", (String)mArray.get(2*position + 1).get("tagname_cn"));
					context.startActivity(intent);
				}
			});
            holder.taskClassItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context,TaskTaskClassActivity.class);
					intent.putExtra("id", (String)mArray.get(2*position).get("id"));
					intent.putExtra("tagname_cn", (String)mArray.get(2*position).get("tagname_cn"));
					context.startActivity(intent);
				}
			});
        }
//        holder.taskClassItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
        
            if((mArray.size()%2 == 1) && (position == getCount()-1)){
            	holder.taskClassNameCn.setText((String)mArray.get(2 * position).get("tagname_cn"));
                holder.taskClassNameEn.setText((String)mArray.get(2 * position).get("tagname_en"));
                if (mArray.get(2 * position).get("task_num").equals("0")) {
                	holder.taskClassNum.setVisibility(View.GONE);
        		}else {
        			holder.taskClassNum.setVisibility(View.VISIBLE);
        			holder.taskClassNum.setText((String)mArray.get(2 * position).get("task_num"));
        		}
                
                holder.taskClassItem2.setVisibility(View.INVISIBLE);
                
            }else {
            	holder.taskClassNameCn.setText((String)mArray.get(2 * position).get("tagname_cn"));
                holder.taskClassNameEn.setText((String)mArray.get(2 * position).get("tagname_en"));
                if (mArray.get(2 * position).get("task_num").equals("0")) {
                	holder.taskClassNum.setVisibility(View.GONE);
        		}else {
        			holder.taskClassNum.setVisibility(View.VISIBLE);
        			holder.taskClassNum.setText((String)mArray.get(2 * position).get("task_num"));
        		}
                holder.taskClassItem2.setVisibility(View.VISIBLE);
                holder.taskClassNameCn2.setText((String)mArray.get(2 * position + 1).get("tagname_cn"));
                holder.taskClassNameEn2.setText((String)mArray.get(2 * position+ 1).get("tagname_en"));
                if (mArray.get(2 * position+ 1).get("task_num").equals("0")) {
                	holder.taskClassNum2.setVisibility(View.GONE);
        		}else {
        			holder.taskClassNum2.setVisibility(View.VISIBLE);
        			holder.taskClassNum2.setText((String)mArray.get(2 * position+ 1).get("task_num"));
        		}
			}  
        return convertView;
    }
    class ViewHolder{
    	RelativeLayout taskClassItem,taskClassItem2;
        TextView taskClassNameCn,taskClassNameEn,taskClassNum;
        TextView taskClassNameCn2,taskClassNameEn2,taskClassNum2;
    }

}
