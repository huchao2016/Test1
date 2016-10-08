package com.mojie.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.ChatActivity;
import com.mojie.activity.ConsultActivity;
import com.mojie.activity.R;
import com.mojie.adapter.CommuniAllListAdapter;
import com.mojie.adapter.CommuniMeListAdapter;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CommunicationFragment extends Fragment implements OnClickListener,IXListViewListener {
	private Context context;
	private Button btnCommuniAll;
	private Button btnCommuniMe;
	
	private CommuniAllListAdapter communiAllListAdapter;
	private CommuniMeListAdapter communiMeListAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView communiAllListView;
	private com.mojie.view.XListView communiMeListView;
	public int start_pos;
	public int list_num;
	private TextView tvConsult;
	private SharedPreferences shared;
	private int uid;
	private int midwindows_height;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.communication_fragment, null);
		context = getActivity();
		shared = context.getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		aArray = new ArrayList<HashMap<String,String>>();
		mArray = new ArrayList<HashMap<String,String>>();

		btnCommuniAll = (Button) view.findViewById(R.id.btn_communi_all);
		btnCommuniMe = (Button) view.findViewById(R.id.btn_communi_me);
		btnCommuniAll.setOnClickListener(this);
		btnCommuniMe.setOnClickListener(this);
		tvConsult = (TextView) view.findViewById(R.id.tv_consult);
		tvConsult.setOnClickListener(this);
		
		communiMeListView = (com.mojie.view.XListView)view.findViewById(R.id.communi_me_listview);
		communiMeListView.setPullLoadEnable(true);
		communiMeListView.setRefreshTime();
		communiMeListView.setXListViewListener(this,1);
		communiMeListAdapter = new CommuniMeListAdapter(context, mArray);
		communiMeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,ChatActivity.class);
				intent.putExtra("id", (String)mArray.get(position).get("id"));
				intent.putExtra("touid", (String)mArray.get(position).get("touid"));
				intent.putExtra("fromuid", (String)mArray.get(position).get("fromuid"));
				intent.putExtra("username", (String)mArray.get(position).get("username"));
				intent.putExtra("headpic", (String)mArray.get(position).get("headpic"));
				startActivity(intent);
			}
		});
		
		communiAllListView = (com.mojie.view.XListView)view.findViewById(R.id.communi_all_listview);
		communiAllListView.setPullLoadEnable(true);
		communiAllListView.setRefreshTime();
		communiAllListView.setXListViewListener(this,1);
		communiAllListAdapter = new CommuniAllListAdapter(context, aArray);
		
		communiAllListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,ChatActivity.class);
				intent.putExtra("id", (String)aArray.get(position).get("id"));
				intent.putExtra("touid", (String)aArray.get(position).get("touid"));
				intent.putExtra("fromuid", (String)aArray.get(position).get("fromuid"));
				intent.putExtra("username", (String)aArray.get(position).get("username_expert"));
				intent.putExtra("headpic", (String)aArray.get(position).get("headpic_expert"));
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
		communiMeListView.setPullLoadEnable(true);
		communiAllListView.setPullLoadEnable(true);
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 30 - 93;
		
		start_pos = 0;
		list_num = 50;
		aArray.clear();
		communiAllListAdapter.mArray = aArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETALLCOMMUNI,aHandler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		mArray.clear();
		communiMeListAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("uid", ""+uid));
		params2.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params2.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETMECOMMUNI,mHandler,params2)).start(); 
	}
	
	private String URL_GETALLCOMMUNI = ConstUtils.BASEURL + "getallcommunication.php";
	protected int aResult;
	protected int a_total_num;
	public ArrayList<HashMap<String, String>>aArray;
	private Handler aHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					a_total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (a_total_num !=0) {
						JSONArray mJSONArray= ((JSONObject)msg.obj).getJSONArray("list");
						aResult = ((JSONObject)msg.obj).getInt("result");
						if(aResult == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 int fromuid = jsonItem.getInt("fromuid");
								 int touid = jsonItem.getInt("touid");
								 String title = jsonItem.getString("title");
								 String content = jsonItem.getString("content");
								 String createddate = jsonItem.getString("createddate");
								 int qa_num = jsonItem.getInt("qa_num");
								 String username = jsonItem.getString("username");
								 String headpic = jsonItem.getString("headpic");
								 String username_expert = jsonItem.getString("username_expert");
								 String headpic_expert = jsonItem.getString("headpic_expert");

								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("fromuid", ""+fromuid);
								 map.put("touid", ""+touid);
								 map.put("title", title);
								 map.put("content", content);
								 map.put("createddate", createddate);
								 map.put("qa_num", ""+qa_num);
								 map.put("username", username);
								 map.put("headpic", headpic);
								 map.put("username_expert", username_expert);
								 map.put("headpic_expert", headpic_expert);
								 aArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								communiAllListAdapter.mArray = aArray;
								communiAllListAdapter.notifyDataSetChanged();
							}else{
								communiAllListView.setAdapter(communiAllListAdapter);
							}
							if (aArray.size() == a_total_num) {
								communiAllListView.setPullLoadEnable(false);
							}
//							setListPosition(communiAllListView,aArray);
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
	
	private String URL_GETMECOMMUNI = ConstUtils.BASEURL + "getuseraboutcommunication.php";
	protected int mResult;
	protected int m_total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler mHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					m_total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (m_total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						mResult = ((JSONObject)msg.obj).getInt("result");
						if(mResult == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 int fromuid = jsonItem.getInt("fromuid");
								 int touid = jsonItem.getInt("touid");
								 String title = jsonItem.getString("title");
								 String content = jsonItem.getString("content");
								 String createddate  = jsonItem.getString("createddate");
								 int qa_num = jsonItem.getInt("qa_num");
								 String username  = jsonItem.getString("username");
								 String headpic  = jsonItem.getString("headpic");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("fromuid", ""+fromuid);
								 map.put("touid", ""+touid);
								 map.put("title", title);
								 map.put("content", content);
								 map.put("createddate", createddate);
								 map.put("qa_num", ""+qa_num);
								 map.put("username", username);
								 map.put("headpic", headpic);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								communiMeListAdapter.mArray = mArray;
								communiMeListAdapter.notifyDataSetChanged();
							}else{
								communiMeListView.setAdapter(communiMeListAdapter);
							}
							if (mArray.size() == m_total_num) {
								communiMeListView.setPullLoadEnable(false);
							}
//							setListPosition(communiMeListView,mArray);
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
		aArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETALLCOMMUNI,aHandler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		mArray.clear();
		ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("uid", ""+uid));
		params2.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params2.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETMECOMMUNI,mHandler,params2)).start(); 
	}

	boolean loadmoreFlg = false;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < a_total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETALLCOMMUNI,aHandler,params)).start(); 
		}else if(start_pos < m_total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
			params2.add(new BasicNameValuePair("uid", ""+uid));
			params2.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params2.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETMECOMMUNI,mHandler,params2)).start(); 
		}else{
			communiAllListView.setPullLoadEnable(false);
			communiMeListView.setPullLoadEnable(false);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.tv_consult:
			intent = new Intent(context,ConsultActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_communi_all:
			setStatus(1);
			break;
		case R.id.btn_communi_me:
			setStatus(2);
			break;
		}
	}
	
	private void setStatus(int status) {
		if (status == 1) {
			btnCommuniAll.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.communi_all_selected));
			btnCommuniMe.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.communi_me_unselected));
			
			communiAllListView.setVisibility(View.VISIBLE);
			communiMeListView.setVisibility(View.INVISIBLE);

		} else if (status == 2) {
			btnCommuniMe.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.communi_me_selected));
			btnCommuniAll.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.communi_all_unselected));
			
			communiAllListView.setVisibility(View.INVISIBLE);
			communiMeListView.setVisibility(View.VISIBLE);
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