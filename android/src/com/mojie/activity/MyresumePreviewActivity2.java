package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.EvaluationTaskListAdapter;
import com.mojie.adapter.EvaluationTenderListAdapter;
import com.mojie.adapter.MyresumePreviewGoodListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MyresumePreviewActivity2 extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private ImageView btnMyresumeBack;
	private com.mojie.view.RoundedWebImageView imgMyresumePreviewHead;
	private TextView tvMyresumPreviewUsername;

	private TextView myresumePreviewTitle;
	
	private ListView myresumeAddGoodListView;
	private MyresumePreviewGoodListAdapter myresumePreviewGoodListAdapter;
	private EvaluationTaskListAdapter evaluationTaskListAdapter;
	private EvaluationTenderListAdapter evaluationTenderListAdapter;
	private ArrayList<HashMap<String, String>> mArray;
	private int type;
	private String tagname_cn = "";
	private String tagname_en = "";
//	private RadioGroup radioGroupMyresumePreviewMemberType;
//	private RadioButton radioMyresumePreviewPersonal;
//	private RadioButton radioMyresumePreviewEnterprise;
	private LinearLayout layoutMyresumePreviewCompany;
	private View viewMyresumePreviewDivider;
	private TextView myresumePreviewName;
	private TextView myresumePreviewMobile;
	private TextView myresumePreviewQq;
	private TextView myresumePreviewEmail;
	private TextView myresumePreviewJob;
	private TextView myresumePreviewExperience;
	private TextView myresumePreviewAddress;
	private TextView myresumePreviewIdnumber;
	private TextView myresumePreviewIntroduce;
	private LinearLayout myresumePreviewProjectExperience;
	private String username;
	private SharedPreferences shared;
//	private int uid;
	private String uid;
	private ProgressDialog pd;
	private String headpic;
	private TextView tvMyresumePreviewMemberType;
	private TextView tvMyresumePreviewCompany;
	private TextView tvMyresumeChoose;
	private String flgActivity;
	private ListView myresumeEvaluationTaskListView;
	private ListView myresumeEvaluationTenderListView;
	private String cid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresume_preview);
//		shared = getSharedPreferences("userInfo", 0);
//		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		flgActivity = intent.getStringExtra("flgActivity");
		cid = intent.getStringExtra("cid");
		uid = intent.getStringExtra("uid");
		username = intent.getStringExtra("username");
		headpic = intent.getStringExtra("headpic");

		mArray = new ArrayList<HashMap<String,String>>();
		pArray = new ArrayList<HashMap<String,String>>();
		eArray = new ArrayList<HashMap<String,String>>();
		tenderArray = new ArrayList<HashMap<String,String>>();
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_RESUMEPREVIEW,pHandler,params)).start(); 
		pd = new ProgressDialog(MyresumePreviewActivity2.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		btnMyresumeBack = (ImageView) findViewById(R.id.btn_myresume_preview_back);
		btnMyresumeBack.setOnClickListener(this);

		myresumePreviewTitle = (TextView) findViewById(R.id.myresume_preview_title);

		imgMyresumePreviewHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_myresume_preview_head);
		tvMyresumPreviewUsername = (TextView) findViewById(R.id.tv_myresume_preview_username);
		tvMyresumPreviewUsername.setText(username);
		tvMyresumeChoose = (TextView) findViewById(R.id.tv_myresume_choose);
		if (flgActivity.equals("0")) {
			tvMyresumeChoose.setText("选择");
		}else {
			tvMyresumeChoose.setText("打赏");
		}
		tvMyresumeChoose.setOnClickListener(this);
//		radioGroupMyresumePreviewMemberType = (RadioGroup) findViewById(R.id.radioGroup_myresume_preview_member_type);
//		radioMyresumePreviewPersonal = (RadioButton) findViewById(R.id.radio_myresume_preview_personal);
//		radioMyresumePreviewEnterprise = (RadioButton) findViewById(R.id.radio_myresume_preview_enterprise);
		layoutMyresumePreviewCompany = (LinearLayout) findViewById(R.id.layout_myresume_preview_company);
		viewMyresumePreviewDivider = (View) findViewById(R.id.view_myresume_preview_divider);
		tvMyresumePreviewCompany = (TextView) findViewById(R.id.tv_myresume_preview_company);
		tvMyresumePreviewMemberType = (TextView) findViewById(R.id.tv_myresume_preview_member_type);
		 
		myresumePreviewName = (TextView) findViewById(R.id.tv_myresume_preview_name);
		myresumePreviewMobile = (TextView) findViewById(R.id.tv_myresume_preview_mobile);
		myresumePreviewQq = (TextView) findViewById(R.id.tv_myresume_preview_qq);
		myresumePreviewEmail = (TextView) findViewById(R.id.tv_myresume_preview_email);
		myresumePreviewJob = (TextView) findViewById(R.id.tv_myresume_preview_job);
		myresumePreviewExperience = (TextView) findViewById(R.id.tv_myresume_preview_experience);
		myresumePreviewAddress = (TextView) findViewById(R.id.tv_myresume_preview_address);
		myresumePreviewIdnumber = (TextView) findViewById(R.id.tv_myresume_preview_idnumber);
		myresumePreviewIntroduce = (TextView) findViewById(R.id.tv_myresume_preview_introduce);

		myresumePreviewProjectExperience = (LinearLayout) findViewById(R.id.layout_myresume_preview_project_experience);
		myresumePreviewProjectExperience.setOnClickListener(this);

//		radioGroupMyresumePreviewMemberType.setOnCheckedChangeListener(this);
		
		myresumeAddGoodListView = (ListView)findViewById(R.id.myresume_add_good_listview);
		myresumePreviewGoodListAdapter = new MyresumePreviewGoodListAdapter(MyresumePreviewActivity2.this, mArray);
		myresumeAddGoodListView.setAdapter(myresumePreviewGoodListAdapter);
		
		myresumeEvaluationTaskListView = (ListView)findViewById(R.id.myresume_evaluation_task_listview);
		evaluationTaskListAdapter = new EvaluationTaskListAdapter(MyresumePreviewActivity2.this, eArray);
		myresumeEvaluationTaskListView.setAdapter(evaluationTaskListAdapter);
		
		myresumeEvaluationTenderListView = (ListView)findViewById(R.id.myresume_evaluation_tender_listview);
		evaluationTenderListAdapter = new EvaluationTenderListAdapter(MyresumePreviewActivity2.this, tenderArray);
		myresumeEvaluationTenderListView.setAdapter(evaluationTenderListAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		headpic = shared.getString("headpic", "");
		if(!headpic.equals("")){
			putImg();
		}
	}
	
	private void putImg(){
		 Bitmap bitmap = ConstUtils.mCache.getBitmapFromMemCache(headpic);
		 if(bitmap != null){
			 imgMyresumePreviewHead.setImageBitmap(bitmap);
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(MyresumePreviewActivity2.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgMyresumePreviewHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(MyresumePreviewActivity2.this, headpic, bitmap);
								}
							}
						}
					});
			 }else{
				 imgMyresumePreviewHead.setImageBitmap(bitmap);
			 }
		 }
	}
	
	private String URL_RESUMEPREVIEW = ConstUtils.BASEURL + "getresumedetailinfo.php";
	protected int pResult;
	protected int resumeType;
	protected String linkname;
	protected String mobile;
	protected String qq;
	protected String email;
	protected int status;
	protected String workinglife;
	protected String address;
	protected String idcardno;
	protected int total_num;
	protected String description;
	protected String comname;
	public ArrayList<HashMap<String, String>>pArray;
	public ArrayList<HashMap<String, String>>eArray;
	public ArrayList<HashMap<String, String>>tenderArray;
	protected int evaluation_task_total_num;
	protected int evaluation_tender_total_num;
	private Handler pHandler = new Handler(){  
		private String index;

		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyresumePreviewActivity2.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					pResult = ((JSONObject)msg.obj).getInt("result");
					if(pResult == 0){
						resumeType = ((JSONObject)msg.obj).getInt("type");
						comname = ((JSONObject)msg.obj).getString("comname");
						linkname = ((JSONObject)msg.obj).getString("linkname");
						mobile = ((JSONObject)msg.obj).getString("mobile");
						qq = ((JSONObject)msg.obj).getString("qq");
						email = ((JSONObject)msg.obj).getString("email");
						status = ((JSONObject)msg.obj).getInt("status");
						index = ((JSONObject)msg.obj).getString("workinglife");
						if (index.equals("1")) {
							workinglife = "1-5年";
						} else if (index.equals("2")) {
							workinglife = "5-10年";
						} else if (index.equals("3")) {
							workinglife = "10-15年";
						} else if (index.equals("4")) {
							workinglife = "15年以上";
						}
						address = ((JSONObject)msg.obj).getString("address");
						idcardno = ((JSONObject)msg.obj).getString("idcardno");
						description = ((JSONObject)msg.obj).getString("description");
				
						if (resumeType == 1) {
//							radioMyresumePreviewPersonal.setChecked(true);
							tvMyresumePreviewMemberType.setText("个人会员");
							layoutMyresumePreviewCompany.setVisibility(View.GONE);
							viewMyresumePreviewDivider.setVisibility(View.GONE);
						}else if (resumeType == 2) {
//							radioMyresumePreviewEnterprise.setChecked(true);
							tvMyresumePreviewMemberType.setText("企业会员");
							layoutMyresumePreviewCompany.setVisibility(View.VISIBLE);
							viewMyresumePreviewDivider.setVisibility(View.VISIBLE);
							tvMyresumePreviewCompany.setText(comname);
						}
						myresumePreviewName.setText(linkname);
						myresumePreviewMobile.setText(mobile);
						myresumePreviewQq.setText(qq);
						myresumePreviewEmail.setText(email);
						if (status == 0) {
							myresumePreviewJob.setText("在职");
						}else {
							myresumePreviewJob.setText("离职");
						}
						myresumePreviewExperience.setText(workinglife);
						
						myresumePreviewAddress.setText(address);
						myresumePreviewIdnumber.setText(idcardno);
						myresumePreviewIntroduce.setText(description);
						
						total_num = ((JSONObject)msg.obj).getInt("total_num");
						if( total_num != 0 ){
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 String tid = jsonItem.getString("tid");
								 String tagname_cn = jsonItem.getString("tagname_cn");
								 String tagname_en = jsonItem.getString("tagname_en");

								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("tid", tid);
								 map.put("tagname_cn", tagname_cn);
								 map.put("tagname_en", tagname_en);
								 pArray.add(map);
								
			                }
							myresumePreviewGoodListAdapter.mArray = pArray;
							myresumeAddGoodListView.setAdapter(myresumePreviewGoodListAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeAddGoodListView);
						}
						//============
						evaluation_task_total_num = ((JSONObject)msg.obj).getInt("evaluation_task_total_num");
						if( evaluation_task_total_num != 0 ){
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("evaluation_task_list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 String taskid = jsonItem.getString("taskid");
								 String description = jsonItem.getString("description");

								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("taskid", taskid);
								 map.put("description", description);
								 eArray.add(map);
								
			                }
							evaluationTaskListAdapter.mArray = eArray;
							myresumeEvaluationTaskListView.setAdapter(evaluationTaskListAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeEvaluationTaskListView);
						}
						//=========================
						//============
						evaluation_tender_total_num = ((JSONObject)msg.obj).getInt("evaluation_tender_total_num");
						if( evaluation_tender_total_num != 0 ){
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("evaluation_tender_list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
							{
								JSONObject jsonItem = mJSONArray.getJSONObject(i);
								String tenderid = jsonItem.getString("tenderid");
								String description = jsonItem.getString("description");
								
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("tenderid", tenderid);
								map.put("description", description);
								tenderArray.add(map);
								
							}
							evaluationTenderListAdapter.mArray = tenderArray;
							myresumeEvaluationTenderListView.setAdapter(evaluationTenderListAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeEvaluationTenderListView);
						}
						//=========================
						
					}
					else{
						Toast.makeText(MyresumePreviewActivity2.this,"失败",Toast.LENGTH_SHORT).show();
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
		switch (v.getId()) {
		case R.id.btn_myresume_preview_back:
			finish();
			break;
		case R.id.tv_myresume_choose:
			if (flgActivity.equals("0")) {
				intent = new Intent();
				intent.putExtra("euid", ""+uid);
				intent.putExtra("realname", linkname);
				setResult(RESULT_OK, intent);
				finish();
			}else {
				intent = new Intent(MyresumePreviewActivity2.this,PayShangActivity.class);
				intent.putExtra("cid", ""+cid);
				intent.putExtra("toid", ""+uid);
				startActivity(intent);
			}
			break;
		case R.id.layout_myresume_preview_project_experience:
			intent = new Intent(MyresumePreviewActivity2.this,ProjectExperienceListActivity.class);
			intent.putExtra("uid", uid);
			intent.putExtra("writeable", ""+1);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
//		if (checkedId == radioMyresumePreviewPersonal.getId()) {
////			myresumePreviewTitle.setText("个人介绍");
//
//			layoutMyresumePreviewCompany.setVisibility(View.GONE);
//			viewMyresumePreviewDivider.setVisibility(View.GONE);
//
//		} else if (checkedId == radioMyresumePreviewEnterprise.getId()) {
////			myresumePreviewTitle.setText("企业介绍");
//
//			layoutMyresumePreviewCompany.setVisibility(View.VISIBLE);
//			viewMyresumePreviewDivider.setVisibility(View.VISIBLE);
//		}

	}

}
