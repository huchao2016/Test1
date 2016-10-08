package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.TaskSearchAdapter;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskSearchActivity extends Activity implements IXListViewListener {

	private Context context;
	private TaskSearchAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView taskSearchListView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private EditText search;
	private TextView taskKeyword;
	private String keyword;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_search);
		context = this;
		
		Intent intent = getIntent();
		keyword = intent.getStringExtra("keyword");
		
		kArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_task_search_back);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		search = (EditText) findViewById(R.id.et_task_search_search);
		search.setText(keyword);
		search.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(TaskSearchActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
					
					keyword = search.getText().toString();
					searchShopbyKeyword();
				}
				return false;
			}
		});
		taskKeyword = (TextView) findViewById(R.id.task_search_keyword);
		taskKeyword.setText(keyword);
		taskSearchListView = (com.mojie.view.XListView)findViewById(R.id.task_search_listview);
		taskSearchListView.setPullLoadEnable(true);
		taskSearchListView.setRefreshTime();
		taskSearchListView.setXListViewListener(this,1);
		listAdapter = new TaskSearchAdapter(TaskSearchActivity.this, kArray);
		
		taskSearchListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				Intent intent = new Intent(context,TaskOverviewBiddingActivity.class);
				intent.putExtra("uid", (String)kArray.get(position).get("uid"));
				intent.putExtra("task_id", (String)kArray.get(position).get("task_id"));
				intent.putExtra("task_type", (String)kArray.get(position).get("task_type"));
				intent.putExtra("title", (String)kArray.get(position).get("title"));
				intent.putExtra("budget", (String)kArray.get(position).get("budget"));
				intent.putExtra("city", (String)kArray.get(position).get("city"));
				intent.putExtra("deliverieddate", (String)kArray.get(position).get("deliverieddate"));
				intent.putExtra("description", (String)kArray.get(position).get("description"));
				intent.putExtra("taskoverflg", (String)kArray.get(position).get("taskoverflg"));
				startActivity(intent);
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
		kArray.clear();
		listAdapter.mArray = kArray;
		ArrayList<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
		params3.add(new BasicNameValuePair("key_word", keyword));
		params3.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params3.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETKEYTASK,kHandler,params3)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	private void searchShopbyKeyword(){
		start_pos = 0;
		list_num = 50;
		taskKeyword.setText("");
		kArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("key_word", keyword));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETKEYTASK,kHandler,params)).start(); 
		pd = new ProgressDialog(TaskSearchActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	private String URL_GETKEYTASK = ConstUtils.BASEURL + "gettaskbykeyword.php";
	protected int kResult;
	protected int k_total_num;
	public ArrayList<HashMap<String, String>>kArray;
	private Handler kHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			taskKeyword.setText(keyword);
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					k_total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (k_total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						kResult = ((JSONObject)msg.obj).getInt("result");
						if(kResult == 0){
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
								 int status = jsonItem.getInt("status");
								 String description = jsonItem.getString("description");
								 String taskfee = jsonItem.getString("taskfee");
								 String headpic = jsonItem.getString("headpic");
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
								 map.put("headpic", headpic);
								 map.put("taskoverflg", ""+taskoverflg);
								 kArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								listAdapter.mArray = kArray;
								listAdapter.notifyDataSetChanged();
							}else{
								taskSearchListView.setAdapter(listAdapter);
							}
							if (kArray.size() == k_total_num) {
								taskSearchListView.setPullLoadEnable(false);
							}
//							setListPosition(taskSearchListView,kArray);
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
		kArray.clear();
		ArrayList<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
		params3.add(new BasicNameValuePair("key_word", keyword));
		params3.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params3.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETKEYTASK,kHandler,params3)).start(); 
	}

	boolean loadmoreFlg = false;
	private int midwindows_height;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < k_total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
			params3.add(new BasicNameValuePair("key_word", keyword));
			params3.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params3.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETKEYTASK,kHandler,params3)).start(); 
		}else{
			taskSearchListView.setPullLoadEnable(false);
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
