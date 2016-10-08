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

public class TradingpwdUpdateActivity extends Activity implements OnClickListener {

	private ImageView tradingpwdBackImg;
	private Button tradingpwdSubmit;
	private EditText tradingpwdUpdatePsw;
	private EditText tradingpwdUpdatePswConfirm;
	private String paypass;
	private String paypassConfirm;
	private SharedPreferences shared;
	protected int id;
	private ProgressDialog pd;
	
	private String URL_TRADINGPWD = ConstUtils.BASEURL + "changepaypass.php";
	protected int result;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(TradingpwdUpdateActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						Toast.makeText(TradingpwdUpdateActivity.this,"交易密码修改成功",Toast.LENGTH_SHORT).show();
						finish();
					}else {
						Toast.makeText(TradingpwdUpdateActivity.this,"交易密码修改失败",Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tradingpwd_update);
		
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		tradingpwdUpdatePsw = (EditText)findViewById(R.id.tradingpwd_update_psw);
		tradingpwdUpdatePswConfirm = (EditText)findViewById(R.id.tradingpwd_update_psw_confirm);
		
		tradingpwdBackImg = (ImageView) findViewById(R.id.btn_tradingpwd_back);
		tradingpwdBackImg.setOnClickListener(this);
		
		tradingpwdSubmit = (Button) findViewById(R.id.tradingpwd_update_submit);
		tradingpwdSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_tradingpwd_back:
			finish();
			break;
		case R.id.tradingpwd_update_submit:
			paypass = tradingpwdUpdatePsw.getText().toString();
			paypassConfirm = tradingpwdUpdatePswConfirm.getText().toString();
			if("".equals(paypass)) {
				Toast.makeText(TradingpwdUpdateActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
				break;
			}else if(!paypass.equals(paypassConfirm)) {
				Toast.makeText(TradingpwdUpdateActivity.this,"密码输入不一致，请重新输入",Toast.LENGTH_LONG).show();
				break;
			}
			else {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        		params.add(new BasicNameValuePair("id", ""+id));
        		params.add(new BasicNameValuePair("paypass", paypass));
        		new Thread(new ConnectPHPToGetJSON(URL_TRADINGPWD,handler,params)).start(); 
        		pd = new ProgressDialog(TradingpwdUpdateActivity.this);
    			pd.setMessage("请稍后…");
    			pd.show();
			}
			break;
		}
	}

}
