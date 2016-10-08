package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.BankCardListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.XListView;
import com.mojie.view.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class BankCardListActivity extends Activity implements IXListViewListener {

	private Context context;
	private BankCardListAdapter bankCardListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView banklistView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private ImageView addImg;
	private SharedPreferences shared;
	private int uid;
	private int midwindows_height;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_card);
		context = this;
		shared = context.getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_bank_back);
		backImg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		addImg = (ImageView) findViewById(R.id.btn_bank_add);
		addImg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(context,AddBankcardActivity.class);
				startActivity(intent);
			}
		});
		
		banklistView = (com.mojie.view.XListView)findViewById(R.id.bank_card_listview);
		banklistView.setPullLoadEnable(true);
		banklistView.setRefreshTime();
		banklistView.setXListViewListener(this,1);
		bankCardListAdapter = new BankCardListAdapter(BankCardListActivity.this, mArray);
		
		banklistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position-1;
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context,ShopDetailsActivity.class);
//				intent.putExtra("user_id", (String)mArray.get(position).get("user_id"));
//                intent.putExtra("user_name", (String)mArray.get(position).get("user_name"));
//                intent.putExtra("pay_points", (String)mArray.get(position).get("pay_points"));
//                intent.putExtra("mobile_phone", (String)mArray.get(position).get("mobile_phone"));
//				startActivity(intent);
			}
		});
		
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 93;
//		
//		headpic = shared.getString("headpic", "");
//		if(!headpic.equals("")){
//			putImg();
//		}
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		bankCardListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETBANK,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	private String URL_GETBANK = ConstUtils.BASEURL + "getuserbank.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(BankCardListActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						result = ((JSONObject)msg.obj).getInt("result");
						if(result == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 String account = jsonItem.getString("account");
								 String banktype = jsonItem.getString("banktype");
								 int status = jsonItem.getInt("status");
								 int default_card = jsonItem.getInt("default_card");
								 int cardtype = jsonItem.getInt("cardtype");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("account", account);
								 map.put("banktype", banktype);
								 map.put("status", ""+status);
								 map.put("default_card", ""+default_card);
								 map.put("cardtype", ""+cardtype);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								bankCardListAdapter.mArray = mArray;
								bankCardListAdapter.notifyDataSetChanged();
							}else{
								banklistView.setAdapter(bankCardListAdapter);
							}
							if (mArray.size() == total_num) {
								banklistView.setPullLoadEnable(false);
							}
//							setListPosition(banklistView,mArray);
						}
					}else {
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadmoreFlg = true;
		mArray.clear();
		bankCardListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETBANK,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	boolean loadmoreFlg = false;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("uid", ""+uid));
			new Thread(new ConnectPHPToGetJSON(URL_GETBANK,handler,params)).start(); 
		}else{
			banklistView.setPullLoadEnable(false);
		}
	}
	
	// 获取屏幕高度
	private int getDisplayMetricsHeight() {
		int i = getWindowManager().getDefaultDisplay().getWidth();
		int j = getWindowManager().getDefaultDisplay().getHeight();
		return Math.max(i, j);
	}
	
	private void setListPosition(XListView xListView,ArrayList<HashMap<String, String>> alist){
		int ListViewH = ListViewSetHeightUtil.getXListViewHeight(xListView);
		xListView.setSelection(0);
		if(midwindows_height < ListViewH){
			xListView.setPullLoadEnable(true);
		}else{
			xListView.setPullLoadEnable(false);
		}
	}
}
