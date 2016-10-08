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

public class CommuniAllListAdapter extends BaseAdapter{
    private Context context;
    public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private ImageLoader imageLoader;
    public CommuniAllListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.communi_all_listview_item,null);
        	
            holder.tvListCommuniAnsnum=(TextView)convertView.findViewById(R.id.tv_list_communi_ansnum);
            holder.listCommuniName=(TextView)convertView.findViewById(R.id.list_communi_name);
            holder.listCommuniContent=(TextView)convertView.findViewById(R.id.list_communi_content);
            holder.listCommuniTime=(TextView)convertView.findViewById(R.id.list_communi_time);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        com.mojie.view.RoundedWebImageView listCommuniImg=(com.mojie.view.RoundedWebImageView) convertView.findViewById(R.id.list_communi_img);
        if (mArray.get(position).get("qa_num").equals("0")) {
        	holder.tvListCommuniAnsnum.setVisibility(View.GONE);
		}else {
			holder.tvListCommuniAnsnum.setVisibility(View.VISIBLE);
			holder.tvListCommuniAnsnum.setText((String)mArray.get(position).get("qa_num"));
		}
        holder.listCommuniName.setText((String)mArray.get(position).get("username"));
        holder.listCommuniContent.setText((String)mArray.get(position).get("title"));
        holder.listCommuniTime.setText((String)mArray.get(position).get("createddate"));
       
        headpic = ConstUtils.IMGURL + (String)mArray.get(position).get("headpic");
        imageLoader.displayImage(headpic, listCommuniImg, ConstUtils.options);
        return convertView;
    }
    class ViewHolder{
//    	com.mojie.view.RoundedWebImageView listCommuniImg;
        TextView tvListCommuniAnsnum,listCommuniName,listCommuniContent,listCommuniTime;
    }

}
