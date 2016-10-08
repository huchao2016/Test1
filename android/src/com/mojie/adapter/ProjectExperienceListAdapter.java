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

public class ProjectExperienceListAdapter extends BaseAdapter{
    private Context context;
    private int[] backgroud_img = { 
			R.drawable.project_bg_yellow,
			R.drawable.project_bg_blue};
    public ArrayList<HashMap<String, String>>mArray;
    public ProjectExperienceListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.project_experience_listview_item,null);
            holder.layoutProjectItem=(LinearLayout)convertView.findViewById(R.id.layout_project_item);
            holder.projectName=(TextView)convertView.findViewById(R.id.tv_project_name);
            holder.projectStart=(TextView)convertView.findViewById(R.id.tv_project_start);
            holder.projectFinish=(TextView)convertView.findViewById(R.id.tv_project_finish);
            holder.projectReferencePeople=(TextView)convertView.findViewById(R.id.tv_project_reference_people);
            holder.projectReferenceTel=(TextView)convertView.findViewById(R.id.tv_project_reference_tel);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.layoutProjectItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
        holder.projectName.setText((String)mArray.get(position).get("title"));
//        holder.projectStart.setText((String)mArray.get(position).get("begindate"));
//        holder.projectFinish.setText((String)mArray.get(position).get("enddate"));
        holder.projectReferencePeople.setText((String)mArray.get(position).get("provedby"));
        holder.projectReferenceTel.setText((String)mArray.get(position).get("provertel"));
        
        String begin = mArray.get(position).get("begindate").substring(0,10);
        holder.projectStart.setText(begin);
        String end = mArray.get(position).get("begindate").substring(0,10);
        holder.projectFinish.setText(end);
        return convertView;
    }
    class ViewHolder{
    	LinearLayout layoutProjectItem;
        TextView projectName,projectStart,projectFinish,projectReferencePeople,projectReferenceTel;
    }

}
