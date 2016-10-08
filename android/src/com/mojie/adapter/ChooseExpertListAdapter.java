package com.mojie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojie.activity.R;
import com.mojie.utils.ConstUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChooseExpertListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private ImageLoader imageLoader;
    public ChooseExpertListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.expert_choose_listview_item,null);
           
            holder.listExpertZan=(TextView)convertView.findViewById(R.id.list_expert_zan);
            holder.listExpertName=(TextView)convertView.findViewById(R.id.list_expert_name);
            holder.listExpertJob=(TextView)convertView.findViewById(R.id.list_expert_job);
            holder.listExpertProfile=(TextView)convertView.findViewById(R.id.list_expert_profile);
            holder.listExpertChoose=(ImageView) convertView.findViewById(R.id.list_expert_choose);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        if ("".equals(mArray.get(position).get("score"))) {
        	holder.listExpertZan.setVisibility(View.GONE);
		}else {
			holder.listExpertZan.setVisibility(View.VISIBLE);
			holder.listExpertZan.setText((String)mArray.get(position).get("score"));
		}
        holder.listExpertName.setText((String)mArray.get(position).get("realname"));
        holder.listExpertJob.setText((String)mArray.get(position).get("jobtitle"));
        holder.listExpertProfile.setText((String)mArray.get(position).get("description"));
        if (mArray.get(position).get("choose").equals("0")) {
        	holder.listExpertChoose.setBackgroundResource(R.drawable.icon_choose_unselected);
		}else {
			holder.listExpertChoose.setBackgroundResource(R.drawable.communi_ans_bg);
		}
        
        com.mojie.view.RoundedWebImageView listExpertHead=(com.mojie.view.RoundedWebImageView) convertView.findViewById(R.id.list_expert_head);
        headpic = ConstUtils.IMGURL + mArray.get(position).get("headpic");
        imageLoader.displayImage(headpic, listExpertHead, ConstUtils.options);
        return convertView;
    }
    class ViewHolder{
//    	com.mojie.view.RoundedWebImageView listExpertHead;
    	ImageView listExpertChoose;
        TextView listExpertZan,listExpertName,listExpertJob,listExpertProfile;
    }

}
