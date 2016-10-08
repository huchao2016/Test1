package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.MyresumeAddGoodListAdapter;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MyresumeEditActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private TextView myresumeEditSave;
	private ImageView btnMyresumeBack;
	
	private ListView myresumeEditGoodListView;
	private MyresumeAddGoodListAdapter myresumeEditGoodListAdapter;
	private ArrayList<HashMap<String, String>> mArray;
	private int type;
	private String id = "";
	private String tagname_cn = "";
	private String tagname_en = "";
	private String username;
	private SharedPreferences shared;
	private int uid;
	private ProgressDialog pd;
	private com.mojie.view.RoundedWebImageView imgMyresumeEditHead;
	private TextView tvMyresumEditName;
	private TextView myresumeEditTitle;
	private RadioGroup radioGroupMyresumeEditMemberType;
	private RadioButton radioMyresumeEditPersonal;
	private RadioButton radioMyresumeEditEnterprise;
	private LinearLayout layoutMyresumeEditCompany;
	private View viewMyresumeEditDivider;
	private EditText myresumeEditContact;
	private EditText myresumeEditMobile;
	private EditText myresumeEditQQ;
	private EditText myresumeEditEmail;
	private EditText myresumeEditIntroduce;
	private LinearLayout myresumeEditProjectExperience;
	private Button btnMyresumeEditSave;
//	private EditText myresumeEditJob;
	private EditText myresumeEditExperience;
	private EditText myresumeEditAddress;
	private EditText myresumeEditIdnumber;
	private String contactStr;
	private String mobileStr;
	private String qqStr;
	private String emailStr;
	private String experienceStr;
	private String addressStr;
	private String idnumberStr;
	private String introduceStr;
	private String headpic;
	private RadioButton radioMyresumeEditJobOn;
	private RadioButton radioMyresumeEditJobDep;
	private RadioGroup radioGroupMyresumeEditJobYype;
	private Spinner experienceSpinner;
	private EditText etMyresumeEditCompany;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresume_edit);
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_RESUMEPREVIEW,pHandler,params)).start(); 
		pd = new ProgressDialog(MyresumeEditActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		btnMyresumeBack = (ImageView) findViewById(R.id.btn_myresume_edit_back);
		btnMyresumeBack.setOnClickListener(this);

		myresumeEditTitle = (TextView) findViewById(R.id.myresume_edit_title);
		myresumeEditSave = (TextView) findViewById(R.id.tv_myresume_edit_save);
		myresumeEditSave.setOnClickListener(this);

		imgMyresumeEditHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_myresume_edit_head);
		tvMyresumEditName = (TextView) findViewById(R.id.tv_myresume_edit_username);
		tvMyresumEditName.setText(username);
		
		radioGroupMyresumeEditMemberType = (RadioGroup) findViewById(R.id.radioGroup_myresume_edit_member_type);
		radioMyresumeEditPersonal = (RadioButton) findViewById(R.id.radio_myresume_edit_personal);
		radioMyresumeEditEnterprise = (RadioButton) findViewById(R.id.radio_myresume_edit_enterprise);
	
		layoutMyresumeEditCompany = (LinearLayout) findViewById(R.id.layout_myresume_edit_company);
		viewMyresumeEditDivider = (View) findViewById(R.id.view_myresume_edit_divider);
		etMyresumeEditCompany = (EditText) findViewById(R.id.et_myresume_edit_company);
		
		myresumeEditContact = (EditText) findViewById(R.id.et_myresume_edit_contact);
		myresumeEditMobile = (EditText) findViewById(R.id.et_myresume_edit_mobile);
		myresumeEditQQ = (EditText) findViewById(R.id.et_myresume_edit_qq);
		myresumeEditEmail = (EditText) findViewById(R.id.et_myresume_edit_email);
//		myresumeEditJob = (EditText) findViewById(R.id.et_myresume_edit_job);
		radioGroupMyresumeEditJobYype = (RadioGroup) findViewById(R.id.radioGroup_myresume_edit_job_type);
		radioMyresumeEditJobOn = (RadioButton) findViewById(R.id.radio_myresume_edit_job_on);
		radioMyresumeEditJobDep = (RadioButton) findViewById(R.id.radio_myresume_edit_job_dep);
		
//		myresumeEditExperience = (EditText) findViewById(R.id.et_myresume_edit_experience);
		experienceSpinner = (Spinner) findViewById(R.id.spinner_myresume_edit_experience);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getApplicationContext(), R.array.experience, R.layout.spinner_experience);
		experienceSpinner.setAdapter(adapter);
		experienceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				position = position + 1;
				experienceStr = ""+position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		myresumeEditAddress = (EditText) findViewById(R.id.et_myresume_edit_address);
		myresumeEditIdnumber = (EditText) findViewById(R.id.et_myresume_edit_idnumber);
		myresumeEditIntroduce = (EditText) findViewById(R.id.et_myresume_edit_introduce);

		myresumeEditProjectExperience = (LinearLayout) findViewById(R.id.layout_myresume_edit_project_experience);
		myresumeEditProjectExperience.setOnClickListener(this);

		btnMyresumeEditSave = (Button) findViewById(R.id.btn_myresume_edit_save);
		btnMyresumeEditSave.setOnClickListener(this);

		radioGroupMyresumeEditMemberType.setOnCheckedChangeListener(this);
		radioGroupMyresumeEditJobYype.setOnCheckedChangeListener(this);
		
		myresumeEditGoodListView = (ListView)findViewById(R.id.myresume_edit_good_listview);
		myresumeEditGoodListAdapter = new MyresumeAddGoodListAdapter(MyresumeEditActivity.this, mArray);
		myresumeEditGoodListView.setAdapter(myresumeEditGoodListAdapter);
		myresumeEditGoodListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mArray.get(position).get("type").equals("0")){
					Intent intent = new Intent(MyresumeEditActivity.this,MyresumeAddGoodActivity.class);
					startActivityForResult(intent, 2);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		headpic = shared.getString("headpic", "");
		if(!headpic.equals("")){
			putImg();
		}
	}
	
	private void putImg(){
		 Bitmap bitmap = ConstUtils.mCache.getBitmapFromMemCache(headpic);
		 if(bitmap != null){
			 imgMyresumeEditHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(MyresumeEditActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgMyresumeEditHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(MyresumeEditActivity.this, headpic, bitmap);
								}
							}
						}
					});
			 }else{
				 imgMyresumeEditHead.setImageBitmap(bitmap);
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
//	public ArrayList<HashMap<String, String>>pArray;
	private Handler pHandler = new Handler(){  
		private String index;

		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyresumeEditActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
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
						
						address = ((JSONObject)msg.obj).getString("address");
						idcardno = ((JSONObject)msg.obj).getString("idcardno");
						description = ((JSONObject)msg.obj).getString("description");
						
						if (resumeType == 1) {
							radioMyresumeEditPersonal.setChecked(true);
						}else if (resumeType == 2){
							radioMyresumeEditEnterprise.setChecked(true);
							etMyresumeEditCompany.setText(comname);
						}
						myresumeEditContact.setText(linkname);
						myresumeEditMobile.setText(mobile);
						myresumeEditQQ.setText(qq);
						myresumeEditEmail.setText(email);
						if (status == 0) {
							radioMyresumeEditJobOn.setChecked(true);
						}else {
							radioMyresumeEditJobDep.setChecked(true);
						}
						if (index != null && !index.equals("")) {
							experienceSpinner.setSelection(Integer.valueOf(index) - 1);
						}
						
						myresumeEditAddress.setText(address);
						myresumeEditIdnumber.setText(idcardno);
						myresumeEditIntroduce.setText(description);
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
								 map.put("id", tid);
								 map.put("tagname_cn", tagname_cn);
								 map.put("tagname_en", tagname_en);
								 map.put("type", ""+1);
								 mArray.add(map);
								
			                }
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("tid", "");
							map.put("tagname_cn",  "");
							map.put("tagname_en",  "");
							map.put("type", ""+0);
							mArray.add(map);
							 
							myresumeEditGoodListAdapter.mArray = mArray;
							myresumeEditGoodListView.setAdapter(myresumeEditGoodListAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeEditGoodListView);
						}else{
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("tid", "");
							map.put("tagname_cn",  "");
							map.put("tagname_en",  "");
							map.put("type", ""+0);
							mArray.add(map);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeEditGoodListView);
						}
					}
					else{
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};
	};
	private String comStr;
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_myresume_edit_back:
			finish();
			break;
		case R.id.layout_myresume_edit_project_experience:
			intent = new Intent(MyresumeEditActivity.this, ProjectExperienceListActivity.class);
			intent.putExtra("uid", ""+uid);
			intent.putExtra("writeable", ""+0);
			startActivity(intent);
			break;
		case R.id.tv_myresume_edit_save:
		case R.id.btn_myresume_edit_save:
			comStr = etMyresumeEditCompany.getText().toString();
			contactStr = myresumeEditContact.getText().toString();
			mobileStr = myresumeEditMobile.getText().toString();
			qqStr = myresumeEditQQ.getText().toString();
			emailStr = myresumeEditEmail.getText().toString();
//			jobStr = myresumeEditJob.getText().toString();
//			experienceStr = myresumeEditExperience.getText().toString();
			addressStr = myresumeEditAddress.getText().toString();
			idnumberStr = myresumeEditIdnumber.getText().toString();
			introduceStr = myresumeEditIntroduce.getText().toString();
			if (resumeType == 2) {
				if ("".equals(comStr)) {
					Toast.makeText(MyresumeEditActivity.this, "请输入企业名称",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
			if ("".equals(contactStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入联系人姓名",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(mobileStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入手机号码",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(qqStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入联系QQ",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(emailStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入联系E-mail",
						Toast.LENGTH_LONG).show();
				break;
			}
//			else if ("".equals(jobStr)) {
//				Toast.makeText(MyresumeEditActivity.this, "请输入职业状态",
//						Toast.LENGTH_LONG).show();
//				break;
//			}
			else if ("".equals(experienceStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入行业经验",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(addressStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入通讯地址",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(idnumberStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入证件号码",
						Toast.LENGTH_LONG).show();
				break;
			} else if (resumeType == 1 && idnumberStr.length() != 18) {
				Toast.makeText(MyresumeEditActivity.this, "身份证号位数不正确，请重新输入",
						Toast.LENGTH_LONG).show();
				break;
			} else if (resumeType == 2 && idnumberStr.length() != 15) {
				Toast.makeText(MyresumeEditActivity.this, "营业执照号位数不正确，请重新输入",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(introduceStr)) {
				Toast.makeText(MyresumeEditActivity.this, "请输入自我介绍",
						Toast.LENGTH_LONG).show();
				break;
			}
			pd = new ProgressDialog(MyresumeEditActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("type", ""+resumeType));
			params.add(new BasicNameValuePair("comname", comStr));
			params.add(new BasicNameValuePair("linkname", contactStr));
			params.add(new BasicNameValuePair("mobile", mobileStr));
			params.add(new BasicNameValuePair("qq", qqStr));
			params.add(new BasicNameValuePair("email", emailStr));
			params.add(new BasicNameValuePair("status", ""+status));
			params.add(new BasicNameValuePair("workinglife", experienceStr));
			params.add(new BasicNameValuePair("address", addressStr));
			params.add(new BasicNameValuePair("idcardno", idnumberStr));
			params.add(new BasicNameValuePair("description", introduceStr));
			params.add(new BasicNameValuePair("goodin_item_num", ""+(mArray.size() - 1)));
		
			for(int i = 0; i < (mArray.size() - 1); i++){
				params.add(new BasicNameValuePair("goodin_item"+(i+1), ""+mArray.get(i).get("id")));
			}
			new Thread(new ConnectPHPToGetJSON(URL_ADDRESUME, handler, params))
					.start();
			break;
		}
	}

	private String URL_ADDRESUME = ConstUtils.BASEURL + "editresumecommit.php";
	private Handler handler = new Handler() {
		private int result;
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyresumeEditActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
					try {
						result = ((JSONObject) msg.obj).getInt("result");
						if (result == 0) {
							Toast.makeText(MyresumeEditActivity.this, "简历修改成功",
									Toast.LENGTH_SHORT).show();
							finish();
							} else if (result == 1) {
								Toast.makeText(MyresumeEditActivity.this, "简历修改失败",
										Toast.LENGTH_SHORT).show();
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
	
//	int resumeType;
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		if (checkedId == radioMyresumeEditPersonal.getId()) {
//			myresumeEditTitle.setText("个人介绍");
			resumeType = 1;
			layoutMyresumeEditCompany.setVisibility(View.GONE);
			viewMyresumeEditDivider.setVisibility(View.GONE);
			myresumeEditIdnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)}); 
		} else if (checkedId == radioMyresumeEditEnterprise.getId()) {
//			myresumeEditTitle.setText("企业介绍");
			resumeType = 2;
			layoutMyresumeEditCompany.setVisibility(View.VISIBLE);
			viewMyresumeEditDivider.setVisibility(View.VISIBLE);
			myresumeEditIdnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); 
		} else if (checkedId == radioMyresumeEditJobOn.getId()) {
			status = 0;
		} else if (checkedId == radioMyresumeEditJobDep.getId()) {
			status = 1;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK) {
			id = data.getStringExtra("id");
			tagname_cn = data.getStringExtra("tagname_cn");
			tagname_en = data.getStringExtra("tagname_en");
			HashMap<String, String> map = new HashMap<String, String>();
			 map.put("type", ""+1);
			 map.put("id", id);
			 map.put("tagname_cn", tagname_cn);
			 map.put("tagname_en", tagname_en);
			 mArray.add(myresumeEditGoodListAdapter.getCount()-1,map);
			 myresumeEditGoodListAdapter.mArray = mArray;
			 myresumeEditGoodListAdapter.notifyDataSetChanged();
			 ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeEditGoodListView);
		}
		
	}

}
