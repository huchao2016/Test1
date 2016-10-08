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
import android.util.Log;
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

public class MyjiebaoDetailsActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener {

	private ImageView btnMyjiebaoDetailsBackImg;
	private TextView tvJiebaoDetailsLabel;
	private Button btnMyjiebaoDetailsSubmit;
	private TextView tvJiebaoDetailsTitle;
	private TextView tvJiebaoDetailsBudget;
	private TextView tvJiebaoDetailsDelivery;
	private TextView tvJiebaoDetailsOverview;
	private TextView tvJiebaoDetailsFabaofang;
	private TextView tvJiebaoDetailsMobile;
	private TextView tvJiebaoDetailsEmail;
	private TextView tvJiebaoDetailsQq;
	private TextView tvJiebaoDetailsPrice;
	private TextView tvJiebaoDetailsTime;
	private RadioGroup radioGroupJiebaoDetailsPingjia;
	private RadioButton radioJiebaoDetailsGood;
	private RadioButton radioJiebaoDetailsMiddle;
	private RadioButton radioJiebaoDetailsBad;
	private EditText etJiebaoDetailsPingjia;

	private SharedPreferences shared;
	private ProgressDialog pd;
	private int s_uid;
	private String title;
	private String task_type;
	private String budget;
	private String deliverieddate;
	private String description;
	private int i_uid;
	private int taskid;
	private ListView demandListView;
	private DemandListAdapter listAdapter;
	private int status;
	private LinearLayout layout_jiebao_detail_pingjia_title;
	private LinearLayout layout_jiebao_detail_score;
	private LinearLayout layout_jiebao_detail_pingjia;
	private LinearLayout layout_jiebao_detail_pingjia_submit;
	private int bid_tenderuid;
	private String city;
	private TextView tvJiebaoDetailsCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myjiebao_details);

		shared = getSharedPreferences("userInfo", 0);
		s_uid = shared.getInt("id", 0);

		Intent intent = getIntent();
		i_uid = Integer.parseInt(intent.getStringExtra("uid"));
		taskid = Integer.parseInt(intent.getStringExtra("task_id"));
		task_type = intent.getStringExtra("task_type");
		title = intent.getStringExtra("title");
		budget = intent.getStringExtra("budget");
		city = intent.getStringExtra("city");
		deliverieddate = intent.getStringExtra("deliverieddate");
		description = intent.getStringExtra("description");
		status = Integer.parseInt(intent.getStringExtra("status"));
		bid_tenderuid = Integer.parseInt(intent.getStringExtra("bid_tenderuid"));

		btnMyjiebaoDetailsBackImg = (ImageView) findViewById(R.id.btn_myjiebao_details_back);
		btnMyjiebaoDetailsBackImg.setOnClickListener(this);

		tvJiebaoDetailsLabel = (TextView) findViewById(R.id.tv_jiebao_details_label);
		tvJiebaoDetailsTitle = (TextView) findViewById(R.id.tv_jiebao_details_title);
		tvJiebaoDetailsBudget = (TextView) findViewById(R.id.tv_jiebao_details_budget);
		tvJiebaoDetailsDelivery = (TextView) findViewById(R.id.tv_jiebao_details_delivery);
		tvJiebaoDetailsCity = (TextView) findViewById(R.id.tv_jiebao_details_city);
		tvJiebaoDetailsOverview = (TextView) findViewById(R.id.tv_jiebao_details_overview);

		tvJiebaoDetailsFabaofang = (TextView) findViewById(R.id.tv_jiebao_details_fabaofang);
		tvJiebaoDetailsMobile = (TextView) findViewById(R.id.tv_jiebao_details_mobile);
		tvJiebaoDetailsEmail = (TextView) findViewById(R.id.tv_jiebao_details_email);
		tvJiebaoDetailsQq = (TextView) findViewById(R.id.tv_jiebao_details_qq);
		tvJiebaoDetailsPrice = (TextView) findViewById(R.id.tv_jiebao_details_price);
		tvJiebaoDetailsTime = (TextView) findViewById(R.id.tv_jiebao_details_time);
		radioGroupJiebaoDetailsPingjia = (RadioGroup) findViewById(R.id.radioGroup_jiebao_details_pingjia);
		radioJiebaoDetailsGood = (RadioButton) findViewById(R.id.radio_jiebao_details_good);
		radioJiebaoDetailsMiddle = (RadioButton) findViewById(R.id.radio_jiebao_details_middle);
		radioJiebaoDetailsBad = (RadioButton) findViewById(R.id.radio_jiebao_details_bad);
		radioJiebaoDetailsGood.setChecked(true);
		score = 5;
		radioGroupJiebaoDetailsPingjia.setOnCheckedChangeListener(this);

		etJiebaoDetailsPingjia = (EditText) findViewById(R.id.et_jiebao_details_pingjia);

		btnMyjiebaoDetailsSubmit = (Button) findViewById(R.id.btn_myjiebao_details_submit);
		btnMyjiebaoDetailsSubmit.setOnClickListener(this);
		layout_jiebao_detail_pingjia_title = (LinearLayout) findViewById(R.id.layout_jiebao_detail_pingjia_title);
		layout_jiebao_detail_score = (LinearLayout) findViewById(R.id.layout_jiebao_detail_score);
		layout_jiebao_detail_pingjia = (LinearLayout) findViewById(R.id.layout_jiebao_detail_pingjia);
		layout_jiebao_detail_pingjia_submit = (LinearLayout) findViewById(R.id.layout_jiebao_detail_pingjia_submit);
		if (status == 2 || bid_tenderuid != s_uid) {
			layout_jiebao_detail_pingjia_title.setVisibility(View.GONE);
			layout_jiebao_detail_score.setVisibility(View.GONE);
			layout_jiebao_detail_pingjia.setVisibility(View.GONE);
			layout_jiebao_detail_pingjia_submit.setVisibility(View.GONE);
		}

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", "" + s_uid));
		params.add(new BasicNameValuePair("taskid", "" + taskid));
		new Thread(new ConnectPHPToGetJSON(URL_GETMYTENDERDETAILINFO, handler,
				params)).start();
		pd = new ProgressDialog(MyjiebaoDetailsActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();

		tvJiebaoDetailsLabel.setText(task_type);
		tvJiebaoDetailsTitle.setText(title);
		tvJiebaoDetailsBudget.setText(budget);
		tvJiebaoDetailsCity.setText(city);
//		tvJiebaoDetailsDelivery.setText(deliverieddate);
		tvJiebaoDetailsOverview.setText(description);
		String delivery = deliverieddate.substring(0,10);
		tvJiebaoDetailsDelivery.setText(delivery);
		
		demandListView = (ListView) findViewById(R.id.task_details_demand_listview);
		listAdapter = new DemandListAdapter(MyjiebaoDetailsActivity.this,
				mArray);
		demandListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String filename = (String)mArray.get(position).get("filename");
                String demandurl = ConstUtils.TASK_ATTACH_URL + filename;
                Intent intent = new Intent(MyjiebaoDetailsActivity.this,WebViewActivity.class);
				intent.putExtra("title", "浏览");
				intent.putExtra("url", demandurl);
				startActivity(intent);
			}
		});
	}

	private String URL_GETMYTENDERDETAILINFO = ConstUtils.BASEURL + "getmytendertaskdetailinfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>> mArray = new ArrayList<HashMap<String, String>>();
	protected String username;
	protected String mobile;
	protected String email;
	protected String qq;
	protected String quote;
	protected String deliveried;
	protected String tenderdescriptiono;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyjiebaoDetailsActivity.this, "网络连接失败",
						Toast.LENGTH_LONG).show();
			} else {
				try {
					result = ((JSONObject) msg.obj).getInt("result");
					if (result == 0) {
						username = ((JSONObject) msg.obj).getString("username");
						mobile = ((JSONObject) msg.obj).getString("mobile");
						email = ((JSONObject) msg.obj).getString("email");
						qq = ((JSONObject) msg.obj).getString("qq");
						quote = ((JSONObject) msg.obj).getString("quote");
						deliveried = ((JSONObject) msg.obj)
								.getString("deliverieddate");
						score = ((JSONObject) msg.obj).getInt("score");
						tenderdescriptiono = ((JSONObject) msg.obj)
								.getString("description");

						tvJiebaoDetailsFabaofang.setText(username);
						tvJiebaoDetailsMobile.setText(mobile);
						tvJiebaoDetailsEmail.setText(email);
						tvJiebaoDetailsQq.setText(qq);
						tvJiebaoDetailsPrice.setText(quote);
//						tvJiebaoDetailsTime.setText(deliveried);
						String delivery = deliveried.substring(0,10);
						tvJiebaoDetailsTime.setText(delivery);
						if (score == 5) {
							radioJiebaoDetailsGood.setChecked(true);
						} else if (score == 3) {
							radioJiebaoDetailsMiddle.setChecked(true);
						} else if (score == 1) {
							radioJiebaoDetailsBad.setChecked(true);
						}
						etJiebaoDetailsPingjia.setText(tenderdescriptiono);

						total_num = ((JSONObject) msg.obj).getInt("total_num");
						
						if (total_num != 0) {
							JSONArray mJSONArray = ((JSONObject) msg.obj).getJSONArray("list");
							for (int i = 0; i < mJSONArray.length(); i++) {
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
					} else {
						Toast.makeText(MyjiebaoDetailsActivity.this, "没有数据",
								Toast.LENGTH_SHORT).show();
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
		switch (v.getId()) {
		case R.id.btn_myjiebao_details_back:
			finish();
			break;
		case R.id.btn_myjiebao_details_submit:
			pingjiaContent = etJiebaoDetailsPingjia.getText().toString();
			if ("".equals(pingjiaContent)) {
				Toast.makeText(MyjiebaoDetailsActivity.this, "请输入您的评价",
						Toast.LENGTH_SHORT).show();
				break;
			}
			pd = new ProgressDialog(MyjiebaoDetailsActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("taskid", "" + taskid));
			params.add(new BasicNameValuePair("evaluaterid", "" + s_uid));
			params.add(new BasicNameValuePair("score", "" + score));
			params.add(new BasicNameValuePair("description", pingjiaContent));
			Log.v("lishide", "score " + score);
			new Thread(new ConnectPHPToGetJSON(URL_EVALUATE, eHandler, params))
					.start();
			break;
		}
	}

	private String URL_EVALUATE = ConstUtils.BASEURL + "taskevaluatesubmit.php";
	private Handler eHandler = new Handler() {
		private int eResult;

		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyjiebaoDetailsActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					eResult = ((JSONObject) msg.obj).getInt("result");
					if (eResult == 0) {
						Toast.makeText(MyjiebaoDetailsActivity.this,
								"对发包方评价成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(MyjiebaoDetailsActivity.this,
								"对发包方评价失败", Toast.LENGTH_SHORT).show();
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
		if (checkedId == radioJiebaoDetailsGood.getId()) {
			score = 5;
		} else if (checkedId == radioJiebaoDetailsMiddle.getId()) {
			score = 3;
		} else if (checkedId == radioJiebaoDetailsBad.getId()) {
			score = 1;
		}
	}

}
