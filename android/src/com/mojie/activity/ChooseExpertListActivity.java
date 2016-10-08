package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.ChooseExpertListAdapter;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ChooseExpertListActivity extends Activity implements IXListViewListener {

	private Context context;
	private ChooseExpertListAdapter chooseExpertListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView expertListView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private SharedPreferences shared;
	private int uid;
	protected int selPosition;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expert_choose);
		context = this;
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_expert_back);
		backImg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				finish();
				if(chooseFlg){
					Intent intent = new Intent();
					intent.putExtra("euid", (String)mArray.get(selPosition).get("uid"));
					intent.putExtra("realname", (String)mArray.get(selPosition).get("realname"));
					setResult(RESULT_OK, intent);
				}
				finish();
			}
		});
		
		expertListView = (com.mojie.view.XListView)findViewById(R.id.expert_listview);
		expertListView.setPullLoadEnable(true);
		expertListView.setRefreshTime();
		expertListView.setXListViewListener(this,1);
		chooseExpertListAdapter = new ChooseExpertListAdapter(ChooseExpertListActivity.this, mArray);
		
		expertListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				selPosition = position - 1;
				Intent intent = new Intent(ChooseExpertListActivity.this,MyresumePreviewActivity2.class);
				intent.putExtra("flgActivity", ""+0);
				intent.putExtra("uid", (String)mArray.get(selPosition).get("uid"));
				intent.putExtra("username", (String)mArray.get(selPosition).get("realname"));
				intent.putExtra("headpic", (String)mArray.get(selPosition).get("headpic"));
				startActivityForResult(intent, 6);
//				startActivity(intent);
//				Intent intent = new Intent();
//				intent.putExtra("euid", (String)mArray.get(position).get("uid"));
//				intent.putExtra("realname", (String)mArray.get(position).get("realname"));
//				setResult(RESULT_OK, intent);
//				finish();
			}
		});
		
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		chooseExpertListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETEXPERTLIST,handler,params)).start(); 
		pd = new ProgressDialog(ChooseExpertListActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 93;
		
	}
	
	private String URL_GETEXPERTLIST = ConstUtils.BASEURL + "getexpertlistinfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(ChooseExpertListActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
								 int uid = jsonItem.getInt("uid");
								 String realname = jsonItem.getString("realname");
								 String jobtitle = jsonItem.getString("jobtitle");
								 String headpic = jsonItem.getString("headpic");
								 String description = jsonItem.getString("description");
								 String score = jsonItem.getString("score");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("uid", ""+uid);
								 map.put("realname", realname);
								 map.put("jobtitle", jobtitle);
								 map.put("headpic", headpic);
								 map.put("description", description);
								 map.put("score", score);
								 map.put("choose", ""+0);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								chooseExpertListAdapter.mArray = mArray;
								chooseExpertListAdapter.notifyDataSetChanged();
							}else{
								expertListView.setAdapter(chooseExpertListAdapter);
							}
							if (mArray.size() == total_num) {
								expertListView.setPullLoadEnable(false);
							}
//							setListPosition(expertListView,mArray);
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
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETEXPERTLIST,handler,params)).start(); 
		pd = new ProgressDialog(ChooseExpertListActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	boolean loadmoreFlg = false;
	private int midwindows_height;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("uid", ""+uid));
			new Thread(new ConnectPHPToGetJSON(URL_GETEXPERTLIST,handler,params)).start(); 
		}else{
			expertListView.setPullLoadEnable(false);
		}
	}
	
	// 获取屏幕高度
	private int getDisplayMetricsHeight() {
		int i = this.getWindowManager().getDefaultDisplay().getWidth();
		int j = this.getWindowManager().getDefaultDisplay().getHeight();
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
	
	boolean chooseFlg = false;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 6 && resultCode == RESULT_OK) {
			HashMap<String, String> map = new HashMap<String, String>();
			map = mArray.get(selPosition);
			map.put("choose", ""+1);
			mArray.set(selPosition, map);
			chooseExpertListAdapter.mArray = mArray;
			chooseExpertListAdapter.notifyDataSetChanged();
			chooseFlg = true;
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(chooseFlg){
				Intent intent = new Intent();
				intent.putExtra("euid", (String)mArray.get(selPosition).get("uid"));
				intent.putExtra("realname", (String)mArray.get(selPosition).get("realname"));
				setResult(RESULT_OK, intent);
			}
			finish();
		}
		return true;
	}
}
