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

public class BankCardListAdapter extends BaseAdapter{
    private Context context;
    private int[] backgroud_img = { 
			R.drawable.bank_card_bg_green,
			R.drawable.bank_card_bg_red,
			R.drawable.bank_card_bg_orange};
    public ArrayList<HashMap<String, String>>mArray;
    public BankCardListAdapter(Context context,ArrayList<HashMap<String, String>>mArray){
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
            convertView=inflater.inflate(R.layout.bank_card_listview_item,null);
            holder.layoutBankItem=(LinearLayout)convertView.findViewById(R.id.layout_bank_item);
            holder.bankCardImg=(ImageView)convertView.findViewById(R.id.bank_card_icon);
            holder.imgBankCardDefault=(ImageView)convertView.findViewById(R.id.img_bank_card_def);
            holder.bankCardNum=(TextView)convertView.findViewById(R.id.bank_card_num);
            holder.bankCardName=(TextView)convertView.findViewById(R.id.bank_card_name);
            holder.bankCardType=(TextView)convertView.findViewById(R.id.bank_card_type);
            
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
//        holder.layoutBankItem.setBackgroundResource( backgroud_img[position%backgroud_img.length] );
//        holder.bankCardImg.setImageResource("");
        String str = mArray.get(position).get("account");
        String cardNum = str.substring(str.length()-4,str.length());
        holder.bankCardNum.setText("尾号 "+cardNum);
        holder.bankCardName.setText(""+(String)mArray.get(position).get("banktype"));
        
        if (mArray.get(position).get("default_card").equals("1")) {
        	 holder.imgBankCardDefault.setImageResource(R.drawable.communi_ans_bg);
		}else {
			 holder.imgBankCardDefault.setImageResource(R.drawable.icon_choose_unselected);
		}
        if (mArray.get(position).get("cardtype").equals("1")) {
        	holder.bankCardType.setText("信用卡");
        }else {
        	holder.bankCardType.setText("储蓄卡");
        }
        
        return convertView;
    }
    class ViewHolder{
    	LinearLayout layoutBankItem;
    	ImageView bankCardImg,imgBankCardDefault;
        TextView bankCardNum,bankCardName,bankCardType;
    }

}
