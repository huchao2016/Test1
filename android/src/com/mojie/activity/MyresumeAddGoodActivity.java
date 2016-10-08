package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.MyresumeAddGoodAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.view.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyresumeAddGoodActivity extends Activity implements IXListViewListener {
	private Context context;
	private MyresumeAddGoodAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView myresymeAddGoodTypeListView;
	public int start_pos;
	public int list_num;
	private ImageView goodBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresume_add_good);
		context = this;
		
		mArray = new ArrayList<HashMap<String,String>>();

		goodBack = (ImageView) findViewById(R.id.btn_myresume_add_good_back);
		goodBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
		myresymeAddGoodTypeListView = (com.mojie.view.XListView)findViewById(R.id.myresume_add_good_type_listview);
		myresymeAddGoodTypeListView.setPullLoadEnable(true);
		myresymeAddGoodTypeListView.setRefreshTime();
		myresymeAddGoodTypeListView.setXListViewListener(this,1);
		listAdapter = new MyresumeAddGoodAdapter(context, mArray);
		
		myresymeAddGoodTypeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				Intent intent = new Intent();
				intent.putExtra("id", (String)mArray.get(position).get("id"));
				intent.putExtra("tagname_cn", (String)mArray.get(position).get("tagname_cn"));
				intent.putExtra("tagname_en", (String)mArray.get(position).get("tagname_en"));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		start_pos = 0;
		list_num = 50;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		new Thread(new ConnectPHPToGetJSON(URL_GETTASKTAGINFO,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	private String URL_GETTASKTAGINFO = ConstUtils.BASEURL + "gettasktaginfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (total_num != 0) {
						JSONArray mJSONArray= ((JSONObject)msg.obj).getJSONArray("list");
						result = ((JSONObject)msg.obj).getInt("result");
						if(result == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 String tagname_cn = jsonItem.getString("tagname_cn");
								 String tagname_en = jsonItem.getString("tagname_en");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("tagname_cn", tagname_cn);
								 map.put("tagname_en", tagname_en);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								listAdapter.mArray = mArray;
								listAdapter.notifyDataSetChanged();
							}else{
								myresymeAddGoodTypeListView.setAdapter(listAdapter);
							}
							myresymeAddGoodTypeListView.setPullLoadEnable(false);
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
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		new Thread(new ConnectPHPToGetJSON(URL_GETTASKTAGINFO,handler,params)).start(); 
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
			new Thread(new ConnectPHPToGetJSON(URL_GETTASKTAGINFO,handler,params)).start(); 
		}else{
			myresymeAddGoodTypeListView.setPullLoadEnable(false);
		}
	}
	
}