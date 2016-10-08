package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.DemandListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class MyfabaoBiddingActivity extends Activity implements OnClickListener {

	private ImageView btnMyfabaoBiddingBackImg;
	private TextView tvFabaoBiddingLabel;
	private Button btnMyfabaoBiddingSubmit;
	private TextView tvFabaoBiddingTitle;
	private TextView tvFabaoBiddingBudget;
	private TextView tvFabaoBiddingDelivery;
	private TextView tvFabaoBiddingOverview;
	private TextView tvFabaoBiddingFabaofang;
	private TextView tvFabaoBiddingMobile;
	private TextView tvFabaoBiddingEmail;
	private TextView tvFabaoBiddingQq;
	private TextView tvFabaoBiddingPrice;
	private TextView tvFabaoBiddingTime;
	
	private String taskid;
	private String task_type;
	private String title;
	private String budget;
	private String deliverieddate;
	private String description;
	private String tenderid;
	private ProgressDialog pd;
	private ListView demandListView;
	private DemandListAdapter listAdapter;
	private TextView tvFabaoBiddingDescriprion;
	private String tender_uid;
	private LinearLayout layoutFabaoBiddingResume;
	private TextView tvFabaoBiddingCity;
	private String city;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myfabao_bidding);
		
		Intent intent = getIntent();
		taskid = intent.getStringExtra("taskid");
		task_type = intent.getStringExtra("task_type");
		title = intent.getStringExtra("title");
		budget = intent.getStringExtra("budget");
		city = intent.getStringExtra("city");
		deliverieddate = intent.getStringExtra("deliverieddate");
		description = intent.getStringExtra("description");
		tenderid = intent.getStringExtra("id");
		tender_uid = intent.getStringExtra("tender_uid");
		
		btnMyfabaoBiddingBackImg = (ImageView) findViewById(R.id.btn_myfabao_bidding_back);
		btnMyfabaoBiddingBackImg.setOnClickListener(this);
		
		tvFabaoBiddingLabel = (TextView) findViewById(R.id.tv_fabao_bidding_label);
		tvFabaoBiddingTitle = (TextView) findViewById(R.id.tv_fabao_bidding_title);
		tvFabaoBiddingBudget = (TextView) findViewById(R.id.tv_fabao_bidding_budget);
		tvFabaoBiddingDelivery = (TextView) findViewById(R.id.tv_fabao_bidding_delivery);
		tvFabaoBiddingCity = (TextView) findViewById(R.id.tv_fabao_bidding_city);
		tvFabaoBiddingOverview = (TextView) findViewById(R.id.tv_fabao_bidding_overview);
		
		tvFabaoBiddingFabaofang = (TextView) findViewById(R.id.tv_fabao_bidding_jiebaofang);
		tvFabaoBiddingMobile = (TextView) findViewById(R.id.tv_fabao_bidding_mobile);
		tvFabaoBiddingEmail = (TextView) findViewById(R.id.tv_fabao_bidding_email);
		tvFabaoBiddingQq = (TextView) findViewById(R.id.tv_fabao_bidding_qq);
		tvFabaoBiddingPrice = (TextView) findViewById(R.id.tv_fabao_bidding_price);
		tvFabaoBiddingTime = (TextView) findViewById(R.id.tv_fabao_bidding_time);
		tvFabaoBiddingDescriprion = (TextView) findViewById(R.id.tv_task_details_description);
		layoutFabaoBiddingResume = (LinearLayout) findViewById(R.id.layout_fabao_bidding_resume);
		layoutFabaoBiddingResume.setOnClickListener(this);
		btnMyfabaoBiddingSubmit = (Button) findViewById(R.id.btn_myfabao_bidding_submit);
		btnMyfabaoBiddingSubmit.setOnClickListener(this);
		
		tvFabaoBiddingLabel.setText(task_type);
		tvFabaoBiddingTitle.setText(title);
		tvFabaoBiddingBudget.setText(budget);
//		tvFabaoBiddingDelivery.setText(deliverieddate);
		tvFabaoBiddingOverview.setText(description);
		String delivery = deliverieddate.substring(0,10);
		tvFabaoBiddingDelivery.setText(delivery);
		tvFabaoBiddingCity.setText(city);
		demandListView = (ListView)findViewById(R.id.task_details_demand_listview);
		listAdapter = new DemandListAdapter(MyfabaoBiddingActivity.this, mArray);
		demandListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String filename = (String)mArray.get(position).get("filename");
                String demandurl = ConstUtils.TASK_ATTACH_URL + filename;
                Intent intent = new Intent(MyfabaoBiddingActivity.this,WebViewActivity.class);
				intent.putExtra("title", "浏览");
				intent.putExtra("url", demandurl);
				startActivity(intent);
			}
		});
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tenderid", tenderid));
		new Thread(new ConnectPHPToGetJSON(URL_GETBIDSTENDERDETAILINFO,handler,params)).start(); 
		pd = new ProgressDialog(MyfabaoBiddingActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
	}

	private String URL_GETBIDSTENDERDETAILINFO = ConstUtils.BASEURL + "bidstenderdetailinfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray = new ArrayList<HashMap<String, String>>();
	protected String username;
	protected String mobile;
	protected String email;
	protected String qq;
	protected String quote;
	protected String deliveried;
	protected String headpic;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyfabaoBiddingActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						username = ((JSONObject)msg.obj).getString("username");
						mobile = ((JSONObject)msg.obj).getString("mobile");
						email = ((JSONObject)msg.obj).getString("email");
						qq = ((JSONObject)msg.obj).getString("qq");
						headpic = ((JSONObject)msg.obj).getString("headpic");
						quote = ((JSONObject)msg.obj).getString("quote");
						deliveried = ((JSONObject)msg.obj).getString("deliverieddate");
						description = ((JSONObject)msg.obj).getString("description");
						
						tvFabaoBiddingFabaofang.setText(username);
						tvFabaoBiddingMobile.setText(mobile);
						tvFabaoBiddingEmail.setText(email);
						tvFabaoBiddingQq.setText(qq);
						tvFabaoBiddingPrice.setText(quote);
//						tvFabaoBiddingTime.setText(deliveried);
						tvFabaoBiddingDescriprion.setText(description);
						String delivery = deliveried.substring(0,10);
						tvFabaoBiddingTime.setText(delivery);
						
						total_num = ((JSONObject)msg.obj).getInt("total_num");
						if (total_num != 0) {
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 String filename = jsonItem.getString("filename");
								 String filetype = jsonItem.getString("filetype");
								 String filesize = jsonItem.getString("filesize");
								 String filepath = jsonItem.getString("filepath");
							
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("filename", filename);
								 map.put("filetype", filetype);
								 map.put("filesize", filesize);
								 map.put("filepath", filepath);
								 mArray.add(map);
								 
			                }
							listAdapter.mArray = mArray;
							demandListView.setAdapter(listAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(demandListView);
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
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_myfabao_bidding_back:
			finish();
			break;
		case R.id.layout_fabao_bidding_resume:
			Intent intent = new Intent(MyfabaoBiddingActivity.this,MyresumePreviewActivity.class);
			intent.putExtra("uid", tender_uid);
			intent.putExtra("username", username);
			intent.putExtra("headpic", headpic);
			startActivity(intent);
			break;
		case R.id.btn_myfabao_bidding_submit:
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("taskid", taskid));
			params.add(new BasicNameValuePair("tenderid", tenderid));
			new Thread(new ConnectPHPToGetJSON(URL_SETBIDS,sHandler,params)).start(); 
			pd = new ProgressDialog(MyfabaoBiddingActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			break;
		}
	}

	private String URL_SETBIDS = ConstUtils.BASEURL + "setbids.php";
	private Handler sHandler = new Handler(){  
        private int sResult;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyfabaoBiddingActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					sResult = ((JSONObject)msg.obj).getInt("result");
					if(sResult == 0){
						Toast.makeText(MyfabaoBiddingActivity.this,"设置该竞标者中标成功",Toast.LENGTH_SHORT).show();
						finish();
					}else {
						Toast.makeText(MyfabaoBiddingActivity.this,"设置该竞标者中标失败",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pd.dismiss();
			super.handleMessage(msg);
		};
	};
	
}
