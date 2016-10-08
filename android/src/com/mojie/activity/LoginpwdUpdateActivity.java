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
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginpwdUpdateActivity extends Activity implements OnClickListener {

	private ImageView loginpwdBackImg;
	private Button loginpwdSubmit;
	private EditText loginpwdUpdatePsw;
	private EditText loginpwdUpdatePswConfirm;
	private String password;
	private String passwordConfirm;

	private SharedPreferences shared;
	protected int id;
	private ProgressDialog pd;
	
	private String URL_LOGINPWD = ConstUtils.BASEURL + "changepassword.php";
	protected int result;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(LoginpwdUpdateActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						Toast.makeText(LoginpwdUpdateActivity.this,"登录密码修改成功",Toast.LENGTH_SHORT).show();
						finish();
					}else {
						Toast.makeText(LoginpwdUpdateActivity.this,"登录密码修改失败",Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_loginpwd_update);
		
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		loginpwdUpdatePsw = (EditText)findViewById(R.id.loginpwd_update_psw);
		loginpwdUpdatePswConfirm = (EditText)findViewById(R.id.loginpwd_update_psw_confirm);
		
		loginpwdBackImg = (ImageView) findViewById(R.id.btn_loginpwd_back);
		loginpwdBackImg.setOnClickListener(this);
		
		loginpwdSubmit = (Button) findViewById(R.id.loginpwd_update_submit);
		loginpwdSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_loginpwd_back:
			finish();
			break;
		case R.id.loginpwd_update_submit:
			password = loginpwdUpdatePsw.getText().toString();
			passwordConfirm = loginpwdUpdatePswConfirm.getText().toString();
			if("".equals(password)) {
				Toast.makeText(LoginpwdUpdateActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
				break;
			}else if(!password.equals(passwordConfirm)) {
				Toast.makeText(LoginpwdUpdateActivity.this,"密码输入不一致，请重新输入",Toast.LENGTH_LONG).show();
				break;
			}
			else {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        		params.add(new BasicNameValuePair("id", ""+id));
        		params.add(new BasicNameValuePair("password", password));
        		new Thread(new ConnectPHPToGetJSON(URL_LOGINPWD,handler,params)).start(); 
        		pd = new ProgressDialog(LoginpwdUpdateActivity.this);
    			pd.setMessage("请稍后…");
    			pd.show();
			}
			break;
		}
	}

}
