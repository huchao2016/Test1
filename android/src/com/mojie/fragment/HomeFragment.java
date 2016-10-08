package com.mojie.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.MainActivity;
import com.mojie.activity.R;
import com.mojie.activity.TaskOverviewBiddingActivity;
import com.mojie.activity.TaskSearchActivity;
import com.mojie.adapter.TaskClassListAdapter;
import com.mojie.adapter.TaskLatestListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;
import com.mojie.view.XListView;
import com.mojie.view.XListView.IXListViewListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class HomeFragment extends Fragment implements OnClickListener,IXListViewListener {
	private Context context;
	private com.mojie.view.RoundedWebImageView ivHomeHead;
	private ImageView ivHomeMenu;
	private EditText etHomeSearch;
	private Button btnTaskLatest;
	private Button btnTaskClass;
	private SharedPreferences shared;
	
	private TaskLatestListAdapter latestListAdapter;
	private TaskClassListAdapter classListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView taskLatestListView;
	private com.mojie.view.XListView taskClassListView;
	public int start_pos;
	public int list_num;
	public int id;
	private String keyword = "";
	private com.mojie.view.RoundedWebImageView ivHomeSearch;
	private String headpic;
	private int midwindows_height;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, null);
		context = getActivity();
		shared = context.getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		mArray = new ArrayList<HashMap<String,String>>();
		cArray = new ArrayList<HashMap<String,String>>();

		ivHomeMenu = (ImageView) view.findViewById(R.id.iv_home_menu);
		ivHomeMenu.setOnClickListener(this);
		ivHomeHead = (com.mojie.view.RoundedWebImageView) view.findViewById(R.id.iv_home_head);
		ivHomeHead.setOnClickListener(this);
		etHomeSearch = (EditText) view.findViewById(R.id.et_home_search);
		etHomeSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) etHomeSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
					getActivity()
					.getCurrentFocus()
					.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
					
					keyword = etHomeSearch.getText().toString();
					if("".equals(keyword)) {
						Toast.makeText(context,"请输入搜索关键字",Toast.LENGTH_LONG).show();
					}else {
						Intent intent = new Intent(context,TaskSearchActivity.class);
						intent.putExtra("keyword", keyword);
						startActivity(intent);
						etHomeSearch.setText("");
					}
				}
				return false;
			}
		});
		
		btnTaskLatest = (Button) view.findViewById(R.id.btn_task_latest);
		btnTaskClass = (Button) view.findViewById(R.id.btn_task_class);
		btnTaskLatest.setOnClickListener(this);
		btnTaskClass.setOnClickListener(this);
		
		taskClassListView = (com.mojie.view.XListView)view.findViewById(R.id.task_class_listview);
		taskClassListView.setPullLoadEnable(true);
		taskClassListView.setRefreshTime();
		taskClassListView.setXListViewListener(this,1);
		classListAdapter = new TaskClassListAdapter(context, cArray);
//		taskClassListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				position = position - 1;
//				Intent intent = new Intent(context,TaskTaskClassActivity.class);
//				intent.putExtra("id", (String)cArray.get(position).get("id"));
//				intent.putExtra("tagname_cn", (String)cArray.get(position).get("tagname_cn"));
//				startActivity(intent);
//			}
//		});
		
		taskLatestListView = (com.mojie.view.XListView)view.findViewById(R.id.task_latest_listview);
		taskLatestListView.setPullLoadEnable(true);
		taskLatestListView.setRefreshTime();
		taskLatestListView.setXListViewListener(this,1);
		latestListAdapter = new TaskLatestListAdapter(context, mArray, id);
		
		taskLatestListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				Intent intent = new Intent(context,TaskOverviewBiddingActivity.class);
				intent.putExtra("uid", (String)mArray.get(position).get("uid"));
				intent.putExtra("task_id", (String)mArray.get(position).get("task_id"));
				intent.putExtra("task_type", (String)mArray.get(position).get("task_type"));
				intent.putExtra("title", (String)mArray.get(position).get("title"));
				intent.putExtra("budget", (String)mArray.get(position).get("budget"));
				intent.putExtra("deliverieddate", (String)mArray.get(position).get("deliverieddate"));
				intent.putExtra("city", (String)mArray.get(position).get("city"));
				intent.putExtra("description", (String)mArray.get(position).get("description"));
				intent.putExtra("taskoverflg", (String)mArray.get(position).get("taskoverflg"));
				
				startActivity(intent);
			}
		});
		
		setStatus(1);
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		taskLatestListView.setPullLoadEnable(true);
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 100 - 93;
		
		headpic = shared.getString("headpic", "");
		if(!headpic.equals("")){
			putImg();
		}
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		latestListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+id));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWTASK,nHandler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		cArray.clear();
		classListAdapter.mArray = cArray;
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETCLASSTASK,cHandler,params2)).start(); 
	}
	
	private String URL_GETNEWTASK = ConstUtils.BASEURL + "getnewtask.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler nHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
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
								 String deliverieddate = jsonItem.getString("deliverieddate");
								 String city = jsonItem.getString("city");
								 int tagsid = jsonItem.getInt("tagsid");
								 String description = jsonItem.getString("description");
								 String taskfee = jsonItem.getString("taskfee");
								 String headpic = jsonItem.getString("headpic");
								 int status = jsonItem.getInt("status");
								 
								 Log.v("lishide", "handler headpic == "+headpic);
								 
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
								 map.put("headpic", headpic);
								 map.put("status", ""+status);
								 map.put("bid_tenderid", ""+bid_tenderid);
								 map.put("bid_tenderuid", ""+bid_tenderuid);
								 map.put("taskoverflg", ""+taskoverflg);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								latestListAdapter.mArray = mArray;
								latestListAdapter.notifyDataSetChanged();
							}else{
								taskLatestListView.setAdapter(latestListAdapter);
							}
							if (mArray.size() == total_num) {
								taskLatestListView.setPullLoadEnable(false);
							}
//							setListPosition(taskLatestListView,mArray);
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
	
	private String URL_GETCLASSTASK = ConstUtils.BASEURL + "gettasktype.php";
	protected int cResult;
	protected int c_total_num;
	public ArrayList<HashMap<String, String>>cArray;
	private Handler cHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					c_total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (c_total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						cResult = ((JSONObject)msg.obj).getInt("result");
						if(cResult == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 String tagname_cn = jsonItem.getString("tagname_cn");
								 String tagname_en = jsonItem.getString("tagname_en");
								 int task_num = jsonItem.getInt("task_num");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("tagname_cn", tagname_cn);
								 map.put("tagname_en", tagname_en);
								 map.put("task_num", ""+task_num);
								 cArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								classListAdapter.mArray = cArray;
								classListAdapter.notifyDataSetChanged();
							}else{
								taskClassListView.setAdapter(classListAdapter);
							}
							taskClassListView.setPullLoadEnable(false);
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
	
	private void putImg(){
		 Bitmap bitmap = ConstUtils.mCache.getBitmapFromMemCache(headpic);
		 Log.v("lishide"," headpic == "+headpic);
		 if(bitmap != null){
			 ivHomeHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(context, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								ivHomeHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(context, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 ivHomeHead.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}
	
	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadmoreFlg = true;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+id));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWTASK,nHandler,params)).start();
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		cArray.clear();
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETCLASSTASK,cHandler,params2)).start(); 
	}

	boolean loadmoreFlg = false;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", ""+id));
			params.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETNEWTASK,nHandler,params)).start(); 
			
		}else if(start_pos < c_total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
			params2.add(new BasicNameValuePair("id", ""+id));
			new Thread(new ConnectPHPToGetJSON(URL_GETCLASSTASK,cHandler,params2)).start(); 
			
		}else{
			taskLatestListView.setPullLoadEnable(false);
			taskClassListView.setPullLoadEnable(false);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.iv_home_head:
		case R.id.iv_home_menu:
			MainActivity mainActivity = (MainActivity)getActivity();
			mainActivity.setChanged(4,R.id.radio_mine);
			break;
		case R.id.btn_task_latest:
			setStatus(1);
			break;
		case R.id.btn_task_class:
			setStatus(2);
			
			break;
		}
	}
	
	private void setStatus(int status) {
		if (status == 1) {
			btnTaskLatest.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.task_latest_selected));
			btnTaskClass.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.task_class_unselected));
			
			taskLatestListView.setVisibility(View.VISIBLE);
			taskClassListView.setVisibility(View.INVISIBLE);

		} else if (status == 2) {
			btnTaskClass.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.task_class_selected));
			btnTaskLatest.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.task_latest_unselected));
			
			taskLatestListView.setVisibility(View.INVISIBLE);
			taskClassListView.setVisibility(View.VISIBLE);
		}

	}
	
	// 获取屏幕高度
	private int getDisplayMetricsHeight() {
		int i = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int j = getActivity().getWindowManager().getDefaultDisplay().getHeight();
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