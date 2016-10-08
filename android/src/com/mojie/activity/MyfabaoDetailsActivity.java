package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyfabaoDetailsActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private ImageView btnMyfabaoDetailsBackImg;
	private TextView tvFabaoDetailsLabel;
	private Button btnMyfabaoDetailsSubmit;
	private TextView tvFabaoDetailsTitle;
	private TextView tvFabaoDetailsBudget;
	private TextView tvFabaoDetailsDelivery;
	private TextView tvFabaoDetailsOverview;
	private TextView tvFabaoDetailsFabaofang;
	private TextView tvFabaoDetailsMobile;
	private TextView tvFabaoDetailsEmail;
	private TextView tvFabaoDetailsQq;
	private TextView tvFabaoDetailsPrice;
	private TextView tvFabaoDetailsTime;
	private RadioGroup radioGroupFabaoDetailsPingjia;
	private RadioButton radioFabaoDetailsGood;
	private RadioButton radioFabaoDetailsMiddle;
	private RadioButton radioFabaoDetailsBad;
	private EditText etFabaoDetailsPingjia;

	private String taskid;
	private String task_type;
	private String title;
	private String budget;
	private String deliverieddate;
	private String description;
	private String tenderid;
	private ProgressDialog pd;
	private SharedPreferences shared;
	private int s_uid;
	private ListView demandListView;
	private DemandListAdapter listAdapter;
	private TextView tvFabaoBiddingDescriprion;
	private TextView tvFabaoDetailsCity;
	private String city;
	private LinearLayout layoutFabaoDetailsResume;
	private String tenderuid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myfabao_details);
		
		shared = getSharedPreferences("userInfo", 0);
		s_uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		taskid = intent.getStringExtra("task_id");
		task_type = intent.getStringExtra("task_type");
		title = intent.getStringExtra("title");
		budget = intent.getStringExtra("budget");
		city = intent.getStringExtra("city");
		deliverieddate = intent.getStringExtra("deliverieddate");
		description = intent.getStringExtra("description");
		tenderid = intent.getStringExtra("id");
		tenderuid = intent.getStringExtra("tenderuid");
		
		btnMyfabaoDetailsBackImg = (ImageView) findViewById(R.id.btn_myfabao_details_back);
		btnMyfabaoDetailsBackImg.setOnClickListener(this);
		
		tvFabaoDetailsLabel = (TextView) findViewById(R.id.tv_fabao_details_label);
		tvFabaoDetailsTitle = (TextView) findViewById(R.id.tv_fabao_details_title);
		tvFabaoDetailsBudget = (TextView) findViewById(R.id.tv_fabao_details_budget);
		tvFabaoDetailsDelivery = (TextView) findViewById(R.id.tv_fabao_details_delivery);
		tvFabaoDetailsCity = (TextView) findViewById(R.id.tv_fabao_details_city);
		tvFabaoDetailsOverview = (TextView) findViewById(R.id.tv_fabao_details_overview);
		
		tvFabaoDetailsFabaofang = (TextView) findViewById(R.id.tv_fabao_details_jiebaofang);
		tvFabaoDetailsMobile = (TextView) findViewById(R.id.tv_fabao_details_mobile);
		tvFabaoDetailsEmail = (TextView) findViewById(R.id.tv_fabao_details_email);
		tvFabaoDetailsQq = (TextView) findViewById(R.id.tv_fabao_details_qq);
		tvFabaoDetailsPrice = (TextView) findViewById(R.id.tv_fabao_details_price);
		tvFabaoDetailsTime = (TextView) findViewById(R.id.tv_fabao_details_time);
		tvFabaoBiddingDescriprion = (TextView) findViewById(R.id.tv_task_details_description);
		
		radioGroupFabaoDetailsPingjia = (RadioGroup) findViewById(R.id.radioGroup_fabao_details_pingjia);
		radioFabaoDetailsGood = (RadioButton) findViewById(R.id.radio_fabao_details_good);
		radioFabaoDetailsMiddle = (RadioButton) findViewById(R.id.radio_fabao_details_middle);
		radioFabaoDetailsBad = (RadioButton) findViewById(R.id.radio_fabao_details_bad);
		radioGroupFabaoDetailsPingjia.setOnCheckedChangeListener(this);
		radioFabaoDetailsGood.setChecked(true);
		score = 5;
		etFabaoDetailsPingjia = (EditText) findViewById(R.id.et_fabao_details_pingjia);
		
		btnMyfabaoDetailsSubmit = (Button) findViewById(R.id.btn_myfabao_details_submit);
		btnMyfabaoDetailsSubmit.setOnClickListener(this);
		layoutFabaoDetailsResume = (LinearLayout) findViewById(R.id.layout_fabao_details_resume);
		layoutFabaoDetailsResume.setOnClickListener(this);
		
		tvFabaoDetailsLabel.setText(task_type);
		tvFabaoDetailsTitle.setText(title);
		tvFabaoDetailsBudget.setText(budget);
		tvFabaoDetailsCity.setText(city);
//		tvFabaoDetailsDelivery.setText(deliverieddate);
		tvFabaoDetailsOverview.setText(description);
		String delivery = deliverieddate.substring(0,10);
		tvFabaoDetailsDelivery.setText(delivery);
		
		demandListView = (ListView)findViewById(R.id.task_details_demand_listview);
		listAdapter = new DemandListAdapter(MyfabaoDetailsActivity.this, mArray);
		demandListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String filename = (String)mArray.get(position).get("filename");
                String demandurl = ConstUtils.TASK_ATTACH_URL + filename;
                Intent intent = new Intent(MyfabaoDetailsActivity.this,WebViewActivity.class);
				intent.putExtra("title", "浏览");
				intent.putExtra("url", demandurl);
				startActivity(intent);
			}
		});
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tenderid", tenderid));
		new Thread(new ConnectPHPToGetJSON(URL_GETBIDSTENDERDETAILINFO,handler,params)).start(); 
		pd = new ProgressDialog(MyfabaoDetailsActivity.this);
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
	protected String tenderdescriptiono;
	protected String headpic;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyfabaoDetailsActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
						score = ((JSONObject)msg.obj).getInt("score");
						tenderdescriptiono = ((JSONObject)msg.obj).getString("tenderdescriptiono");
				
						tvFabaoDetailsFabaofang.setText(username);
						tvFabaoDetailsMobile.setText(mobile);
						tvFabaoDetailsEmail.setText(email);
						tvFabaoDetailsQq.setText(qq);
						tvFabaoDetailsPrice.setText(quote);
//						tvFabaoDetailsTime.setText(deliveried);
						tvFabaoBiddingDescriprion.setText(description);
						String delivery = deliveried.substring(0,10);
						tvFabaoDetailsTime.setText(delivery);
						if (score == 5) {
							radioFabaoDetailsGood.setChecked(true);
						}else if (score == 3) {
							radioFabaoDetailsMiddle.setChecked(true);
						}else if (score == 1) {
							radioFabaoDetailsMiddle.setChecked(true);
						}
						etFabaoDetailsPingjia.setText(tenderdescriptiono);
						
						
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
	
	private String pingjiaContent;
	int score;
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_myfabao_details_back:
			finish();
			break;
		case R.id.layout_fabao_details_resume:
			Intent intent = new Intent(MyfabaoDetailsActivity.this,MyresumePreviewActivity.class);
			intent.putExtra("uid", tenderuid);
			intent.putExtra("username", username);
			intent.putExtra("headpic", headpic);
			startActivity(intent);
			break;
		case R.id.btn_myfabao_details_submit:
			pingjiaContent = etFabaoDetailsPingjia.getText().toString();
			if("".equals(pingjiaContent)) {
				Toast.makeText(MyfabaoDetailsActivity.this,"请输入您的评价",Toast.LENGTH_LONG).show();
				break;
			}
			pd = new ProgressDialog(MyfabaoDetailsActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("tenderid",  tenderid));
			params.add(new BasicNameValuePair("evaluaterid", ""+s_uid));
			params.add(new BasicNameValuePair("score", ""+score));
			params.add(new BasicNameValuePair("description", pingjiaContent));
    		new Thread(new ConnectPHPToGetJSON(URL_EVALUATE,eHandler,params)).start(); 
			break;
		}
	}

	private String URL_EVALUATE = ConstUtils.BASEURL + "renderevaluatesubmit.php";
	private Handler eHandler = new Handler(){  
        private int eResult;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyfabaoDetailsActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					eResult = ((JSONObject)msg.obj).getInt("result");
					if(eResult == 0){
						Toast.makeText(MyfabaoDetailsActivity.this,"对接包方评价成功",Toast.LENGTH_SHORT).show();
						finish();
					}else{
						Toast.makeText(MyfabaoDetailsActivity.this,"对接包方评价失败",Toast.LENGTH_SHORT).show();
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

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		if (checkedId == radioFabaoDetailsGood.getId()) {
			score = 5;
		} else if (checkedId == radioFabaoDetailsMiddle.getId()) {
			score = 3;
		} else if (checkedId == radioFabaoDetailsBad.getId()) {
			score = 1;
		}
	}
}
