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

public class UploadFileListAdapter extends BaseAdapter{
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
    public UploadFileListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.upload_file_listview_item,null);
            holder.uploadFileName = (TextView)convertView.findViewById(R.id.upload_file_name);
            holder.uploadFileAddIcon = (ImageView) convertView.findViewById(R.id.icon_upload_file_add);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        
        if(mArray.get(position).get("type").equals("0")){
        	holder.uploadFileAddIcon.setImageResource(R.drawable.icon_upload);
        	holder.uploadFileAddIcon.setVisibility(View.VISIBLE);
        	holder.uploadFileName.setVisibility(View.GONE);
        }else {
        	holder.uploadFileAddIcon.setVisibility(View.GONE);
        	holder.uploadFileName.setVisibility(View.VISIBLE);
        	holder.uploadFileName.setText((String)mArray.get(position).get("filename"));
		}
        
        return convertView;
    }
    class ViewHolder{
    	ImageView uploadFileAddIcon;
        TextView uploadFileName;
    }
    
}
