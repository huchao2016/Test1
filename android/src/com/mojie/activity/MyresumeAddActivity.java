package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
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

public class MyresumeAddActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private TextView myresumeAddSave;
	private ImageView btnMyresumeBack;
	private com.mojie.view.RoundedWebImageView imgMyresumeAddHead;
	private TextView tvMyresumAddName;
	private Button btnMyresumeAddSave;
	private RadioGroup radioGroupMyresumeAddMemberType;
	private RadioButton radioMyresumeAddPersonal;
	private RadioButton radioMyresumeAddEnterprise;
	private EditText myresumeAddContact;
	private EditText myresumeAddMobile;
	private EditText myresumeAddQQ;
	private EditText myresumeAddEmail;
	private EditText myresumeAddIntroduce;
	private LinearLayout myresumeAddProjectExperience;
	private TextView myresumeAddTitle;
	private LinearLayout layoutMyresumeAddCompany;
	private View viewMyresumeAddDivider;
	
	private ListView myresumeAddGoodListView;
	private MyresumeAddGoodListAdapter myresumeAddGoodListAdapter;
	private ArrayList<HashMap<String, String>> mArray;
	private int type;
	private String id = "";
	private String tagname_cn = "";
	private String tagname_en = "";
	private String username;
//	private EditText myresumeAddJob;
	private EditText myresumeAddExperience;
	private EditText myresumeAddAddress;
	private EditText myresumeAddIdnumber;
	private String contactStr;
	private String mobileStr;
	private String qqStr;
	private String emailStr;
	private String jobStr;
	private String experienceStr;
	private String addressStr;
	private String idnumberStr;
	private ProgressDialog pd;
	private SharedPreferences shared;
	private int uid;
	private EditText myresumeAddcompany;
	private String comStr;
	private String introduceStr;
	private String headpic;
	private RadioGroup radioGroupMyresumeAddJobYype;
	private RadioButton radioMyresumeAddJobOn;
	private RadioButton radioMyresumeAddJobDep;
	private Spinner experienceSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresume_add);
		mArray = new ArrayList<HashMap<String,String>>();
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		
		type = 0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", ""+type);
		map.put("tagname_cn", tagname_cn);
		map.put("tagname_en", tagname_en);
		mArray.add(map);
		
		btnMyresumeBack = (ImageView) findViewById(R.id.btn_myresume_add_back);
		btnMyresumeBack.setOnClickListener(this);

		myresumeAddTitle = (TextView) findViewById(R.id.myresume_add_title);

		myresumeAddSave = (TextView) findViewById(R.id.tv_myresume_add_save);
		myresumeAddSave.setOnClickListener(this);

		imgMyresumeAddHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_myresume_add_head);
		tvMyresumAddName = (TextView) findViewById(R.id.tv_myresume_add_name);
		tvMyresumAddName.setText(username);
		radioGroupMyresumeAddMemberType = (RadioGroup) findViewById(R.id.radioGroup_myresume_add_member_type);
		radioMyresumeAddPersonal = (RadioButton) findViewById(R.id.radio_myresume_add_personal);
		radioMyresumeAddEnterprise = (RadioButton) findViewById(R.id.radio_myresume_add_enterprise);
		radioMyresumeAddPersonal.setChecked(true);
		resumeType = 1;
		
		layoutMyresumeAddCompany = (LinearLayout) findViewById(R.id.layout_myresume_add_company);
		viewMyresumeAddDivider = (View) findViewById(R.id.view_myresume_add_divider);
		
		myresumeAddcompany = (EditText) findViewById(R.id.et_myresume_add_company);
		myresumeAddContact = (EditText) findViewById(R.id.et_myresume_add_contact);
		myresumeAddMobile = (EditText) findViewById(R.id.et_myresume_add_mobile);
		myresumeAddQQ = (EditText) findViewById(R.id.et_myresume_add_qq);
		myresumeAddEmail = (EditText) findViewById(R.id.et_myresume_add_email);
//		myresumeAddJob = (EditText) findViewById(R.id.et_myresume_add_job);
		radioGroupMyresumeAddJobYype = (RadioGroup) findViewById(R.id.radioGroup_myresume_add_job_type);
		radioMyresumeAddJobOn = (RadioButton) findViewById(R.id.radio_myresume_add_job_on);
		radioMyresumeAddJobDep = (RadioButton) findViewById(R.id.radio_myresume_add_job_dep);
		
//		myresumeAddExperience = (EditText) findViewById(R.id.et_myresume_add_experience);
		experienceSpinner = (Spinner) findViewById(R.id.spinner_myresume_add_experience);
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
		
		myresumeAddAddress = (EditText) findViewById(R.id.et_myresume_add_address);
		myresumeAddIdnumber = (EditText) findViewById(R.id.et_myresume_add_idnumber);
		
		myresumeAddIntroduce = (EditText) findViewById(R.id.et_myresume_add_introduce);

		myresumeAddProjectExperience = (LinearLayout) findViewById(R.id.layout_myresume_add_project_experience);
		myresumeAddProjectExperience.setOnClickListener(this);

		btnMyresumeAddSave = (Button) findViewById(R.id.btn_myresume_add_save);
		btnMyresumeAddSave.setOnClickListener(this);

		radioGroupMyresumeAddMemberType.setOnCheckedChangeListener(this);
		radioGroupMyresumeAddJobYype.setOnCheckedChangeListener(this);
		
		myresumeAddGoodListView = (ListView)findViewById(R.id.myresume_add_good_listview);
		myresumeAddGoodListAdapter = new MyresumeAddGoodListAdapter(MyresumeAddActivity.this, mArray);
		myresumeAddGoodListView.setAdapter(myresumeAddGoodListAdapter);
		myresumeAddGoodListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mArray.get(position).get("type").equals("0")){
					Intent intent = new Intent(MyresumeAddActivity.this,MyresumeAddGoodActivity.class);
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
			 imgMyresumeAddHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(MyresumeAddActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgMyresumeAddHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(MyresumeAddActivity.this, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 imgMyresumeAddHead.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_myresume_add_back:
			finish();
			break;
		case R.id.layout_myresume_add_project_experience:
			intent = new Intent(MyresumeAddActivity.this, ProjectExperienceListActivity.class);
			intent.putExtra("uid", ""+uid);
			intent.putExtra("writeable", ""+0);
			startActivity(intent);
			break;
		case R.id.tv_myresume_add_save:
		case R.id.btn_myresume_add_save:
			comStr = myresumeAddcompany.getText().toString();
			contactStr = myresumeAddContact.getText().toString();
			mobileStr = myresumeAddMobile.getText().toString();
			qqStr = myresumeAddQQ.getText().toString();
			emailStr = myresumeAddEmail.getText().toString();
//			jobStr = myresumeAddJob.getText().toString();
//			experienceStr = myresumeAddExperience.getText().toString();
			addressStr = myresumeAddAddress.getText().toString();
			idnumberStr = myresumeAddIdnumber.getText().toString();
			introduceStr = myresumeAddIntroduce.getText().toString();
			if (resumeType == 2) {
				if ("".equals(comStr)) {
					Toast.makeText(MyresumeAddActivity.this, "请输入企业名称",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
			if ("".equals(contactStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入联系人姓名",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(mobileStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入手机号码",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(qqStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入联系QQ",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(emailStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入联系E-mail",
						Toast.LENGTH_LONG).show();
				break;
			}
//			else if ("".equals(jobStr)) {
//				Toast.makeText(MyresumeAddActivity.this, "请输入职业状态",
//						Toast.LENGTH_LONG).show();
//				break;
//			}
			else if ("".equals(experienceStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入行业经验",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(addressStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入通讯地址",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(idnumberStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入证件号码",
						Toast.LENGTH_LONG).show();
				break;
			} else if (resumeType == 1 && idnumberStr.length() != 18) {
				Toast.makeText(MyresumeAddActivity.this, "身份证号位数不正确，请重新输入",
						Toast.LENGTH_LONG).show();
				break;
			} else if (resumeType == 2 && idnumberStr.length() != 15) {
				Toast.makeText(MyresumeAddActivity.this, "营业执照号位数不正确，请重新输入",
						Toast.LENGTH_LONG).show();
				break;
			}  else if ("".equals(introduceStr)) {
				Toast.makeText(MyresumeAddActivity.this, "请输入自我介绍",
						Toast.LENGTH_LONG).show();
				break;
			}

			pd = new ProgressDialog(MyresumeAddActivity.this);
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
		
			Log.v("lishide", "resumeType == "+resumeType);
			Log.v("lishide", "item num "+(mArray.size() - 1));
			for(int i = 0; i < (mArray.size() - 1); i++){
				params.add(new BasicNameValuePair("goodin_item"+(i+1), ""+mArray.get(i).get("id")));
				Log.v("lishide", "item  "+mArray.get(i).get("id"));
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
				Toast.makeText(MyresumeAddActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
					try {
						result = ((JSONObject) msg.obj).getInt("result");
						if (result == 0) {
							Toast.makeText(MyresumeAddActivity.this, "简历创建成功",
									Toast.LENGTH_SHORT).show();
							finish();
							} else if (result == 1) {
								Toast.makeText(MyresumeAddActivity.this, "简历创建失败",
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
	
	int resumeType;
	private int status;
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		if (checkedId == radioMyresumeAddPersonal.getId()) {
//			myresumeAddTitle.setText("个人介绍");
			resumeType = 1;
			layoutMyresumeAddCompany.setVisibility(View.GONE);
			viewMyresumeAddDivider.setVisibility(View.GONE);
			myresumeAddIdnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)}); 

		} else if (checkedId == radioMyresumeAddEnterprise.getId()) {
//			myresumeAddTitle.setText("企业介绍");
			resumeType = 2;
			layoutMyresumeAddCompany.setVisibility(View.VISIBLE);
			viewMyresumeAddDivider.setVisibility(View.VISIBLE);
			myresumeAddIdnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); 
		} else if (checkedId == radioMyresumeAddJobOn.getId()) {
			status = 0;
		} else if (checkedId == radioMyresumeAddJobDep.getId()) {
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
			 mArray.add(myresumeAddGoodListAdapter.getCount()-1,map);
			 myresumeAddGoodListAdapter.mArray = mArray;
			 myresumeAddGoodListAdapter.notifyDataSetChanged();
			 ListViewSetHeightUtil.setListViewHeightBasedOnChildren(myresumeAddGoodListView);
		}
	}

}
