package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.MyfabaoListAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

public class MyfabaoListActivity extends Activity implements IXListViewListener {

	private Context context;
	private MyfabaoListAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView myfabaoListView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	
	private SharedPreferences shared;
	private int uid;
	private TextView tvFabaoTofb;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myfabao);
		context = this;
		
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_myfabao_back);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tvFabaoTofb = (TextView) findViewById(R.id.tv_fabao_tofb);
		tvFabaoTofb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		myfabaoListView = (com.mojie.view.XListView)findViewById(R.id.myfabao_listview);
		myfabaoListView.setPullLoadEnable(true);
		myfabaoListView.setRefreshTime();
		myfabaoListView.setXListViewListener(this,1);
		listAdapter = new MyfabaoListAdapter(MyfabaoListActivity.this, mArray, uid);
		
		myfabaoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				if(mArray.get(position).get("status").equals("3")) {
					Intent intent = new Intent(context,MyfabaoDetailsActivity.class);
					intent.putExtra("uid", (String)mArray.get(position).get("uid"));
					intent.putExtra("task_id", (String)mArray.get(position).get("task_id"));
					intent.putExtra("task_type", (String)mArray.get(position).get("task_type"));
					intent.putExtra("title", (String)mArray.get(position).get("title"));
					intent.putExtra("budget", (String)mArray.get(position).get("budget"));
					intent.putExtra("city", (String)mArray.get(position).get("city"));
					intent.putExtra("deliverieddate", (String)mArray.get(position).get("deliverieddate"));
					intent.putExtra("description", (String)mArray.get(position).get("description"));
					intent.putExtra("id", (String)mArray.get(position).get("bid_tenderid"));
					intent.putExtra("tenderuid", (String)mArray.get(position).get("bid_tenderuid"));
					startActivity(intent);
				} else if(mArray.get(position).get("status").equals("0") || mArray.get(position).get("status").equals("-1")) {
					Intent intent = new Intent(context,FabaoEditActivity.class);
//					intent.putExtra("uid", (String)mArray.get(position).get("uid"));
					intent.putExtra("task_id", (String)mArray.get(position).get("task_id"));
					intent.putExtra("task_type", (String)mArray.get(position).get("task_type"));
					intent.putExtra("title", (String)mArray.get(position).get("title"));
					intent.putExtra("budget", (String)mArray.get(position).get("budget"));
					intent.putExtra("deliverieddate", (String)mArray.get(position).get("deliverieddate"));
					intent.putExtra("city", (String)mArray.get(position).get("city"));
					intent.putExtra("description", (String)mArray.get(position).get("description"));
					intent.putExtra("tagsid", (String)mArray.get(position).get("tagsid"));
					intent.putExtra("id", (String)mArray.get(position).get("bid_tenderid"));
					startActivity(intent);
				}else {
					Intent intent = new Intent(context,TaskOverviewActivity.class);
					intent.putExtra("uid", (String)mArray.get(position).get("uid"));
					intent.putExtra("task_id", (String)mArray.get(position).get("task_id"));
					intent.putExtra("task_type", (String)mArray.get(position).get("task_type"));
					intent.putExtra("title", (String)mArray.get(position).get("title"));
					intent.putExtra("budget", (String)mArray.get(position).get("budget"));
					intent.putExtra("city", (String)mArray.get(position).get("city"));
					intent.putExtra("deliverieddate", (String)mArray.get(position).get("deliverieddate"));
					intent.putExtra("description", (String)mArray.get(position).get("description"));
					intent.putExtra("id", (String)mArray.get(position).get("bid_tenderid"));
					startActivity(intent);
				}
			}
		});
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 93;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		listAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETMYSENDTASK,handler,params)).start(); 
		pd = new ProgressDialog(MyfabaoListActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	private String URL_GETMYSENDTASK = ConstUtils.BASEURL + "getmysendtask.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyfabaoListActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
								 int task_id = jsonItem.getInt("task_id");
								 String task_type = jsonItem.getString("task_type");
								 String title = jsonItem.getString("title");
								 String createddate = jsonItem.getString("createddate");
								 int bids = jsonItem.getInt("bids");
								 String budget = jsonItem.getString("budget");
								 String city = jsonItem.getString("city");
								 String deliverieddate = jsonItem.getString("deliverieddate");
								 int tagsid = jsonItem.getInt("tagsid");
								 String description = jsonItem.getString("description");
								 String taskfee = jsonItem.getString("taskfee");
								 int status = jsonItem.getInt("status");
								 int bid_tenderid = 0;
								 int bid_tenderuid = 0;
								 if(status == 3){
									 bid_tenderid = jsonItem.getInt("bid_tenderid");
									 bid_tenderuid = jsonItem.getInt("bid_tenderuid");
								 }
								 int taskoverflg = jsonItem.getInt("taskoverflg");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("uid", ""+uid);
								 map.put("task_id", ""+task_id);
								 map.put("task_type", task_type);
								 map.put("title", title);
								 map.put("createddate", createddate);
								 map.put("bids", ""+bids);
								 map.put("budget", budget);
								 map.put("city", city);
								 map.put("deliverieddate", deliverieddate);
								 map.put("tagsid", ""+tagsid);
								 map.put("description", description);
								 map.put("taskfee", taskfee);
								 map.put("status", ""+status);
								 map.put("bid_tenderid", ""+bid_tenderid);
								 map.put("bid_tenderuid", ""+bid_tenderuid);
								 map.put("taskoverflg", ""+taskoverflg);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								listAdapter.mArray = mArray;
								listAdapter.notifyDataSetChanged();
							}else{
								myfabaoListView.setAdapter(listAdapter);
							}
							if (mArray.size() == total_num) {
								myfabaoListView.setPullLoadEnable(false);
							}
//							setListPosition(myfabaoListView,mArray);
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
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETMYSENDTASK,handler,params)).start(); 
		pd = new ProgressDialog(MyfabaoListActivity.this);
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
			params.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETMYSENDTASK,handler,params)).start(); 
		}else{
			myfabaoListView.setPullLoadEnable(false);
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
}
