package com.mojie.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.R;
import com.mojie.adapter.FabaoFragmentListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.view.XListViewTwoItem;
import com.mojie.view.XListViewTwoItem.IXListViewListenerTwoItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FabaoFragment extends Fragment implements IXListViewListenerTwoItem {
	private Context context;
	
	private FabaoFragmentListAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListViewTwoItem fabaoFragmentListView;
	public int start_pos;
	public int list_num;
	private SharedPreferences shared;
	private int id;
	private int midwindows_height;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fabao_fragment, null);
		context = getActivity();
		shared = context.getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		mArray = new ArrayList<HashMap<String,String>>();

		fabaoFragmentListView = (com.mojie.view.XListViewTwoItem)view.findViewById(R.id.fabao_fragment_listview);
		fabaoFragmentListView.setPullLoadEnable(true);
		fabaoFragmentListView.setRefreshTime();
		fabaoFragmentListView.setXListViewListener(this,1);
		listAdapter = new FabaoFragmentListAdapter(context, mArray);
		
//		fabaoFragmentListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				position = position - 1;
//				Intent intent = new Intent(context,FabaoActivity.class);
//				intent.putExtra("id", (String)mArray.get(position).get("id"));
//				intent.putExtra("tagname_cn", (String)mArray.get(position).get("tagname_cn"));
//				startActivity(intent);
//			}
//		});
		
		
		return view;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		listAdapter.mArray = mArray;
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
								fabaoFragmentListView.setAdapter(listAdapter);
							}
							if (mArray.size() == total_num) {
								fabaoFragmentListView.setPullLoadEnable(false);
							}
//							fabaoFragmentListView.setPullLoadEnable(false);
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
			fabaoFragmentListView.setPullLoadEnable(false);
		}
	}

	@Override
	public void onListItemClick(XListViewTwoItem l, View v, int position, long id) {
		// TODO Auto-generated method stub
		l.getItemAtPosition(position);
		Log.v("lishide", "Get onListItemClice position = "+position);
	}
		
}