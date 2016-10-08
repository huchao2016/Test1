package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.BidderListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class TaskOverviewBiddingActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView tvOverviewTaskLabel;
	private TextView tvOverviewTaskTitle;
	private TextView tvOverviewTaskBudget;
	private TextView tvOverviewTaskDelivery;
	private TextView tvOverviewTaskOverview;
	private Button btnOverviewTaskSubmit;
	
	private ProgressDialog pd;
	private ListView bidderListView;
	private int taskid;
	private Context context;
	
	private BidderListAdapter listAdapter;
	private String title;
	private String task_type;
	private String budget;
	private String deliverieddate;
	private String description;
	private int i_uid;
	private SharedPreferences shared;
	private int s_uid;
	private int taskoverflg;
	private TextView tvOverviewTaskBidding;
	private String city;
	private TextView tvOverviewTaskCity;
	private LinearLayout layoutBidding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview_task_bidding);
		context = this;
		shared = context.getSharedPreferences("userInfo", 0);
		s_uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		i_uid = Integer.parseInt(intent.getStringExtra("uid"));
		taskid = Integer.parseInt(intent.getStringExtra("task_id"));
		task_type = intent.getStringExtra("task_type");
		title = intent.getStringExtra("title");
		budget = intent.getStringExtra("budget");
		deliverieddate = intent.getStringExtra("deliverieddate");
		city = intent.getStringExtra("city");
		description = intent.getStringExtra("description");
		taskoverflg = Integer.parseInt(intent.getStringExtra("taskoverflg"));
		
		mArray = new ArrayList<HashMap<String,String>>();
		backImg = (ImageView) findViewById(R.id.btn_overview_task_back);
		backImg.setOnClickListener(this);
		
		tvOverviewTaskLabel = (TextView) findViewById(R.id.tv_overview_task_label);
		tvOverviewTaskTitle = (TextView) findViewById(R.id.tv_overview_task_title);
		tvOverviewTaskBudget = (TextView) findViewById(R.id.tv_overview_task_budget);
		tvOverviewTaskDelivery = (TextView) findViewById(R.id.tv_overview_task_delivery);
		tvOverviewTaskCity = (TextView) findViewById(R.id.tv_overview_task_city);
		tvOverviewTaskOverview = (TextView) findViewById(R.id.tv_overview_task_overview);

		btnOverviewTaskSubmit = (Button) findViewById(R.id.btn_overview_task_bidding);
		btnOverviewTaskSubmit.setOnClickListener(this);
		tvOverviewTaskBidding = (TextView) findViewById(R.id.tv_overview_task_bidding);
		tvOverviewTaskBidding.setOnClickListener(this);
	
		layoutBidding = (LinearLayout) findViewById(R.id.layout_bidding);
		
		bidderListView = (ListView)findViewById(R.id.overview_task_bidder_bidding_listview);
		listAdapter = new BidderListAdapter(TaskOverviewBiddingActivity.this, mArray);
		
		tvOverviewTaskLabel.setText(task_type);
		tvOverviewTaskTitle.setText(title);
		tvOverviewTaskBudget.setText(budget);
//		tvOverviewTaskDelivery.setText(deliverieddate);
		tvOverviewTaskCity.setText(city);
		tvOverviewTaskOverview.setText(description);
		String delivery = deliverieddate.substring(0,10);
		tvOverviewTaskDelivery.setText(delivery);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("taskid", ""+taskid));
		new Thread(new ConnectPHPToGetJSON(URL_GETTENDERSSINFO,handler,params)).start(); 
		pd = new ProgressDialog(TaskOverviewBiddingActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	private String URL_GETTENDERSSINFO = ConstUtils.BASEURL + "tenderssinfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(TaskOverviewBiddingActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
								 String username = jsonItem.getString("username");
								 String tendeddate = jsonItem.getString("tendeddate");
								 int id = jsonItem.getInt("id");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("uid", ""+uid);
								 map.put("username", username);
								 map.put("tendeddate", tendeddate);
								 map.put("id", ""+id);
								 mArray.add(map);
								 
			                }
							listAdapter.mArray = mArray;
							bidderListView.setAdapter(listAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(bidderListView);
						}
						layoutBidding.setVisibility(View.VISIBLE);
					}else {
						layoutBidding.setVisibility(View.GONE);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_overview_task_back:
			finish();
			break;
		case R.id.tv_overview_task_bidding:
		case R.id.btn_overview_task_bidding:
			if (s_uid == 0) {
				intent = new Intent(context,LoginActivity.class);
				startActivity(intent);
			} else {
			if (taskoverflg == 1) {
				Toast.makeText(context, "任务已过期", Toast.LENGTH_SHORT).show();
			}else{
				if(s_uid == i_uid ){//判断任务所有者是否是自己
					Toast.makeText(context, "自己不能竞标", Toast.LENGTH_SHORT).show();
				}else {
					if(mArray.isEmpty()){
						intent = new Intent(TaskOverviewBiddingActivity.this,PayActivity.class);
						intent.putExtra("task_id", ""+taskid);
						intent.putExtra("task_type", task_type);
						intent.putExtra("title", title);
						intent.putExtra("budget", budget);
						intent.putExtra("city", city);
						intent.putExtra("deliverieddate", deliverieddate);
						intent.putExtra("description", description);
						startActivity(intent);
					}else {
						int flg = 0;
						for(int i =  0 ; i < mArray.size(); i++){
							if(mArray.get(i).get("uid").equals(""+s_uid)){
								flg = 1;
								break;
							}
						}
						if(flg == 0){
							intent = new Intent(TaskOverviewBiddingActivity.this,PayActivity.class);
							intent.putExtra("task_id", ""+taskid);
							intent.putExtra("task_type", task_type);
							intent.putExtra("title", title);
							intent.putExtra("budget", budget);
							intent.putExtra("city", city);
							intent.putExtra("deliverieddate", deliverieddate);
							intent.putExtra("description", description);
							startActivity(intent);
							finish();
						}else{
							Toast.makeText(context, "已经竞标过了", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			}
			break;
		}
	}

}
