package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.InfoLatestListAdapter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoInfoClassActivity extends Activity implements IXListViewListener {

	private Context context;
	private InfoLatestListAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView classInfoInfoListView;
	private String catid;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private TextView taskLabel;
	private String name;
	private SharedPreferences shared;
	private int uid;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_info_info);
		context = this;
		shared = context.getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		catid = intent.getStringExtra("id");
		name = intent.getStringExtra("name");
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_info_class_back);
		backImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
	
		taskLabel = (TextView) findViewById(R.id.tv_info_class_name);
		taskLabel.setText(name);
		classInfoInfoListView = (com.mojie.view.XListView)findViewById(R.id.class_info_info_listview);
		classInfoInfoListView.setPullLoadEnable(true);
		classInfoInfoListView.setRefreshTime();
		classInfoInfoListView.setXListViewListener(this,1);
		listAdapter = new InfoLatestListAdapter(InfoInfoClassActivity.this, mArray);
		
		classInfoInfoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				String id = (String)mArray.get(position).get("id");
				String newsUrl = ConstUtils.NEWS_BASE_URL + id;
				
				Intent intent = new Intent(context,WebViewActivity.class);
				intent.putExtra("title", (String)mArray.get(position).get("title"));
				intent.putExtra("url", newsUrl);
				startActivity(intent);
//				Intent intent = new Intent(context,InfoDetailsActivity.class);
//				intent.putExtra("id", (String)mArray.get(position).get("id"));
//				intent.putExtra("title", (String)mArray.get(position).get("title"));
//				intent.putExtra("description", (String)mArray.get(position).get("description"));
//				intent.putExtra("clicks", (String)mArray.get(position).get("clicks"));
//				intent.putExtra("createddate", (String)mArray.get(position).get("createddate"));
//				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 100 - 93;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		listAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+uid));
		params.add(new BasicNameValuePair("catid", ""+catid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWSBYTYPE,tHandler,params)).start(); 
		pd = new ProgressDialog(InfoInfoClassActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	private String URL_GETNEWSBYTYPE = ConstUtils.BASEURL + "getnewsbytype.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler tHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(InfoInfoClassActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
								 String title = jsonItem.getString("title");
								 String description = jsonItem.getString("description");
								 int clicks = jsonItem.getInt("clicks");
								 String createddate = jsonItem.getString("createddate");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("title", title);
								 map.put("description", description);
								 map.put("clicks", ""+clicks);
								 map.put("createddate", createddate);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								listAdapter.mArray = mArray;
								listAdapter.notifyDataSetChanged();
							}else{
								classInfoInfoListView.setAdapter(listAdapter);
							}
							if (mArray.size() == total_num) {
								classInfoInfoListView.setPullLoadEnable(false);
							}
//							setListPosition(classInfoInfoListView,mArray);
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
		params.add(new BasicNameValuePair("id", ""+uid));
		params.add(new BasicNameValuePair("catid", ""+catid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWSBYTYPE,tHandler,params)).start(); 
	}

	boolean loadmoreFlg = false;
	private int midwindows_height;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", ""+uid));
			params.add(new BasicNameValuePair("catid", ""+catid));
			params.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETNEWSBYTYPE,tHandler,params)).start(); 
		}else{
			classInfoInfoListView.setPullLoadEnable(false);
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
