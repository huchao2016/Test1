package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.ProjectExperienceListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.XListView;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectExperienceListActivity extends Activity implements IXListViewListener {

	private Context context;
	private ProjectExperienceListAdapter projectExperienceListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView projectlistView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private TextView tvProjectAdd;
	private String uid;
	private String resumeid;
	private int writeable;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_experience);
		context = this;
		Intent intent = getIntent();
		uid = intent.getStringExtra("uid");
		writeable = Integer.parseInt(intent.getStringExtra("writeable"));
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_project_back);
		backImg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
		tvProjectAdd = (TextView) findViewById(R.id.tv_project_add);
		if (writeable == 1) {
			tvProjectAdd.setVisibility(View.GONE);
		}
		tvProjectAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(context,ProjectExperienceAddActivity.class);
				intent.putExtra("resumeid", ""+resumeid);
				startActivity(intent);
			}
		});
		
		projectlistView = (com.mojie.view.XListView)findViewById(R.id.project_experience_listview);
		projectlistView.setPullLoadEnable(true);
		projectlistView.setRefreshTime();
		projectlistView.setXListViewListener(this,1);
		projectExperienceListAdapter = new ProjectExperienceListAdapter(ProjectExperienceListActivity.this, mArray);
		
		projectlistView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				if (writeable != 1) {
					Intent intent = new Intent(context,ProjectExperienceEditActivity.class);
					intent.putExtra("resumeid", (String)mArray.get(position).get("resumeid"));
					intent.putExtra("id", (String)mArray.get(position).get("id"));
					intent.putExtra("title", (String)mArray.get(position).get("title"));
					intent.putExtra("begindate", (String)mArray.get(position).get("begindate"));
					intent.putExtra("enddate", (String)mArray.get(position).get("enddate"));
					intent.putExtra("provedby", (String)mArray.get(position).get("provedby"));
					intent.putExtra("provertel", (String)mArray.get(position).get("provertel"));
					intent.putExtra("description", (String)mArray.get(position).get("description"));
					startActivity(intent);
				}else {
					Intent intent = new Intent(context,ProjectExperiencePreviewActivity.class);
					intent.putExtra("resumeid", (String)mArray.get(position).get("resumeid"));
					intent.putExtra("id", (String)mArray.get(position).get("id"));
					intent.putExtra("title", (String)mArray.get(position).get("title"));
					intent.putExtra("begindate", (String)mArray.get(position).get("begindate"));
					intent.putExtra("enddate", (String)mArray.get(position).get("enddate"));
					intent.putExtra("provedby", (String)mArray.get(position).get("provedby"));
					intent.putExtra("provertel", (String)mArray.get(position).get("provertel"));
					intent.putExtra("description", (String)mArray.get(position).get("description"));
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
		projectExperienceListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETEXPERIENCELIST,handler,params)).start(); 
		pd = new ProgressDialog(ProjectExperienceListActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	private String URL_GETEXPERIENCELIST = ConstUtils.BASEURL + "getresumeexperiencelist.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(ProjectExperienceListActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						result = ((JSONObject)msg.obj).getInt("result");
						if(result == 0){
							for(int i = 0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int resumeid = jsonItem.getInt("resumeid");
								 int id = jsonItem.getInt("id");
								 String title = jsonItem.getString("title");
								 String begindate = jsonItem.getString("begindate");
								 String enddate = jsonItem.getString("enddate");
								 String provedby = jsonItem.getString("provedby");
								 String provertel = jsonItem.getString("provertel");
								 String description = jsonItem.getString("description");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("resumeid", ""+resumeid);
								 map.put("id", ""+id);
								 map.put("title", title);
								 map.put("begindate", begindate);
								 map.put("enddate", enddate);
								 map.put("provedby", provedby);
								 map.put("provertel", provertel);
								 map.put("description", description);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								projectExperienceListAdapter.mArray = mArray;
								projectExperienceListAdapter.notifyDataSetChanged();
							}else{
								projectlistView.setAdapter(projectExperienceListAdapter);
							}
							if (mArray.size() == total_num) {
								projectlistView.setPullLoadEnable(false);
							}
//							setListPosition(projectlistView,mArray);
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
		new Thread(new ConnectPHPToGetJSON(URL_GETEXPERIENCELIST,handler,params)).start(); 
		pd = new ProgressDialog(ProjectExperienceListActivity.this);
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
			new Thread(new ConnectPHPToGetJSON(URL_GETEXPERIENCELIST,handler,params)).start(); 
		}else{
			projectlistView.setPullLoadEnable(false);
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
