package com.mojie.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultActivity extends Activity implements OnClickListener{

	private ImageView backImg;
	private LinearLayout layoutConsultExpert;
	private TextView tvConsultExpertName;
	private EditText etConsultTitle;
	private EditText etConsultOverview;
	private Button btnConsultSubmit;
	private ProgressDialog pd;
	private SharedPreferences shared;
	private int uid;
	private String title;
	private String content;
	String euid;
	
	private String URL_CONSULT = ConstUtils.BASEURL + "sendcommunication.php";
	private Handler handler = new Handler(){  
        private int result;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(ConsultActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						Toast.makeText(ConsultActivity.this,"发送咨询成功",Toast.LENGTH_SHORT).show();
						finish();
					}else if(result == 1){
						Toast.makeText(ConsultActivity.this,"发送咨询失败",Toast.LENGTH_SHORT).show();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consult);
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		backImg = (ImageView) findViewById(R.id.btn_consult_back);
		backImg.setOnClickListener(this);
		
		layoutConsultExpert = (LinearLayout) findViewById(R.id.layout_consult_expert);
		layoutConsultExpert.setOnClickListener(this);
		
		tvConsultExpertName = (TextView) findViewById(R.id.tv_consult_expert_name);
		
		etConsultTitle = (EditText) findViewById(R.id.et_consult_title);
		etConsultOverview = (EditText) findViewById(R.id.et_consult_overview);
		
		btnConsultSubmit = (Button) findViewById(R.id.btn_consult_submit);
		btnConsultSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_consult_back:
			finish();
			break;
		case R.id.layout_consult_expert:
			intent = new Intent(ConsultActivity.this,ChooseExpertListActivity.class);
			startActivityForResult(intent, 3);
			break;
		case R.id.btn_consult_submit:
			title = etConsultTitle.getText().toString();
			content = etConsultOverview.getText().toString();
			if("".equals(title)) {
				Toast.makeText(ConsultActivity.this,"请输入标题",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(content)) {
				Toast.makeText(ConsultActivity.this,"请输入任务概述",Toast.LENGTH_LONG).show();
				break;
			}
			
			pd = new ProgressDialog(ConsultActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("fromuid", ""+uid));
			params.add(new BasicNameValuePair("touid", ""+euid));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("content", content));
    		new Thread(new ConnectPHPToGetJSON(URL_CONSULT,handler,params)).start(); 
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3 && resultCode == RESULT_OK) {
			euid = data.getStringExtra("euid");
			String realname = data.getStringExtra("realname");
			tvConsultExpertName.setText(realname);
		}
		
	}
	
}
