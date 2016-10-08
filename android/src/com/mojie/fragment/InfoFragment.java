package com.mojie.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.InfoInfoClassActivity;
import com.mojie.activity.R;
import com.mojie.activity.WebViewActivity;
import com.mojie.adapter.InfoClassListAdapter;
import com.mojie.adapter.InfoLatestListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.XListView;
import com.mojie.view.XListView.IXListViewListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InfoFragment extends Fragment implements OnClickListener,IXListViewListener {
	private Context context;
	private SharedPreferences shared;
	
	private InfoLatestListAdapter latestListAdapter;
	private InfoClassListAdapter classListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView infoLatestListView;
	private com.mojie.view.XListView infoClassListView;
	public int start_pos;
	public int list_num;
	public int id;
	private int midwindows_height;
	private Button btnInfoLatest;
	private Button btnInfoClass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.info_fragment, null);
		context = getActivity();
		shared = context.getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		mArray = new ArrayList<HashMap<String,String>>();
		cArray = new ArrayList<HashMap<String,String>>();

		btnInfoLatest = (Button) view.findViewById(R.id.btn_info_new);
		btnInfoLatest.setOnClickListener(this);
		btnInfoClass = (Button) view.findViewById(R.id.btn_info_class);
		btnInfoClass.setOnClickListener(this);
		
		infoClassListView = (com.mojie.view.XListView)view.findViewById(R.id.info_class_listview);
		infoClassListView.setPullLoadEnable(true);
		infoClassListView.setRefreshTime();
		infoClassListView.setXListViewListener(this,1);
		classListAdapter = new InfoClassListAdapter(context, cArray);
		
		infoClassListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				Intent intent= new Intent(context,InfoInfoClassActivity.class);
				intent.putExtra("id", (String)cArray.get(position).get("id"));
				intent.putExtra("name", (String)cArray.get(position).get("name"));
				startActivity(intent);
			}
		});
		
		infoLatestListView = (com.mojie.view.XListView)view.findViewById(R.id.info_new_listview);
		infoLatestListView.setPullLoadEnable(true);
		infoLatestListView.setRefreshTime();
		infoLatestListView.setXListViewListener(this,1);
		latestListAdapter = new InfoLatestListAdapter(context, mArray);
		
		infoLatestListView.setOnItemClickListener(new OnItemClickListener() {
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
		
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 30 - 93;
		
		start_pos = 0;
		list_num = 50;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+id));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWNEWS,nHandler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETCLASSNEWS,cHandler,params2)).start(); 
		
		setStatus(1);
		
		return view;
	}
	
	private String URL_GETNEWNEWS = ConstUtils.BASEURL + "getnewnews.php";
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
								latestListAdapter.mArray = mArray;
								latestListAdapter.notifyDataSetChanged();
							}else{
								infoLatestListView.setAdapter(latestListAdapter);
							}
							if (mArray.size() == total_num) {
								infoLatestListView.setPullLoadEnable(false);
							}
//							setListPosition(infoLatestListView,mArray);
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
	
	private String URL_GETCLASSNEWS = ConstUtils.BASEURL + "getnewstype.php";
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
								 String name = jsonItem.getString("name");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("name", name);
								 cArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								classListAdapter.mArray = cArray;
								classListAdapter.notifyDataSetChanged();
							}else{
								infoClassListView.setAdapter(classListAdapter);
							}
							if (cArray.size() == c_total_num) {
								infoClassListView.setPullLoadEnable(false);
							}
//							setListPosition(infoClassListView,cArray);
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
		params.add(new BasicNameValuePair("id", ""+id));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWNEWS,nHandler,params)).start();
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		cArray.clear();
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETCLASSNEWS,cHandler,params)).start(); 
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
			new Thread(new ConnectPHPToGetJSON(URL_GETNEWNEWS,nHandler,params)).start(); 
			
			ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
			params2.add(new BasicNameValuePair("id", ""+id));
			new Thread(new ConnectPHPToGetJSON(URL_GETCLASSNEWS,cHandler,params)).start(); 
			
		}else if(start_pos < c_total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
			params2.add(new BasicNameValuePair("id", ""+id));
			new Thread(new ConnectPHPToGetJSON(URL_GETCLASSNEWS,cHandler,params2)).start(); 
			
		}else{
			infoLatestListView.setPullLoadEnable(false);
			infoClassListView.setPullLoadEnable(false);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_info_new:
			setStatus(1);
			break;
		case R.id.btn_info_class:
			setStatus(2);
			break;
		}
	}
	
	private void setStatus(int status) {
		if (status == 1) {
			btnInfoClass.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.info_class_unselected));
			btnInfoLatest.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.info_new_selected));
			
			infoLatestListView.setVisibility(View.VISIBLE);
			infoClassListView.setVisibility(View.INVISIBLE);
			
		} else if (status == 2) {
			btnInfoLatest.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.info_new_unselected));
			btnInfoClass.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.info_class_selected));
			infoLatestListView.setVisibility(View.INVISIBLE);
			infoClassListView.setVisibility(View.VISIBLE);
			
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
//			Log.v("liuchao", "More midwindows_height:"+midwindows_height+"chatListViewH :"+chatListViewH);
		}else{
			xListView.setPullLoadEnable(false);
//			Log.v("liuchao", "Less midwindows_height:"+midwindows_height+"chatListViewH :"+chatListViewH);
		}
	}

}