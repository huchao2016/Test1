package com.mojie.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.MsgCheckUtil;

import android.os.Bundle;
import android.os.CountDownTimer;
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

public class FindPasswordActivity extends Activity implements OnClickListener {

	private ImageView findBackImg;
	private Button findCodeIbtn;
	private Button findSubmit;
	private EditText findTel;
	private EditText findCode;
	private EditText findPwd;
	private EditText findPwdConfirm;
	private String password;
	private String passwordConfirm;
	private String mobileStr;
	private String security_code;
	private ProgressDialog pd;
	public int id;
	private SharedPreferences shared;
	private String code = "";
	private TimeCount time;
	
	private String URL_FINDPWD = ConstUtils.BASEURL + "findpassword.php";
	private Handler handler = new Handler() {
		private int result;
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(FindPasswordActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					result = ((JSONObject) msg.obj).getInt("result");
					if (result == 0) {
						Toast.makeText(FindPasswordActivity.this, "登录密码修改成功",
								Toast.LENGTH_SHORT).show();
						finish();
					} else if (result == 1) {
						Toast.makeText(FindPasswordActivity.this, "登录密码修改失败",
								Toast.LENGTH_SHORT).show();
					} else if (result == 2) {
						Toast.makeText(FindPasswordActivity.this, "验证码错误",
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		initView();
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
	}

	private void initView() {
		findTel = (EditText) findViewById(R.id.find_tel);
		findCode = (EditText) findViewById(R.id.find_code);
		findPwd = (EditText) findViewById(R.id.find_psw);
		findPwdConfirm = (EditText) findViewById(R.id.find_psw_confirm);

		findBackImg = (ImageView) findViewById(R.id.btn_find_back);
		findBackImg.setOnClickListener(this);

		findCodeIbtn = (Button) findViewById(R.id.find_code_ibtn);
		findCodeIbtn.setOnClickListener(this);

		findSubmit = (Button) findViewById(R.id.find_submit);
		findSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_find_back:
			finish();
			break;
		case R.id.find_code_ibtn:
			mobileStr = findTel.getText().toString();
			if ("".equals(mobileStr)) {
				Toast.makeText(FindPasswordActivity.this, "请输入手机号码",
						Toast.LENGTH_SHORT).show();
				break;
			}
			MsgCheckUtil checkUtil = new MsgCheckUtil(msgHandler,mobileStr);
			checkUtil.sendMsgReq();
			time.start();// 开始计时
			break;
		case R.id.find_submit:
			mobileStr = findTel.getText().toString();
			security_code = findCode.getText().toString();
			password = findPwd.getText().toString();
			passwordConfirm = findPwdConfirm.getText().toString();
			if ("".equals(mobileStr)) {
				Toast.makeText(FindPasswordActivity.this, "请输入手机号码",
						Toast.LENGTH_SHORT).show();
				break;
			} else if ("".equals(security_code)) {
				Toast.makeText(FindPasswordActivity.this, "请输入验证码",
						Toast.LENGTH_SHORT).show();
				break;
			} else if (!code.equals(security_code)) {
				Toast.makeText(FindPasswordActivity.this, "验证码有误",
						Toast.LENGTH_SHORT).show();
				break;
			} else if ("".equals(password)) {
				Toast.makeText(FindPasswordActivity.this, "请输入密码",
						Toast.LENGTH_SHORT).show();
				break;
			} else if (!password.equals(passwordConfirm)) {
				Toast.makeText(FindPasswordActivity.this, "密码输入不一致，请重新输入",
						Toast.LENGTH_SHORT).show();
				break;
			} else {
				pd = new ProgressDialog(FindPasswordActivity.this);
				pd.setMessage("请稍后…");
				pd.show();
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("mobile", mobileStr));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("security_code",security_code));
				new Thread(new ConnectPHPToGetJSON(URL_FINDPWD, handler, params)).start();
			}
			break;
		}
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			findCodeIbtn.setText("重新发送");
			findCodeIbtn.setBackgroundResource(R.drawable.bg_send);
			findCodeIbtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			findCodeIbtn.setClickable(false);
			findCodeIbtn.setBackgroundResource(R.drawable.bg_send_gray);
			findCodeIbtn.setText("重新发送(" + millisUntilFinished / 1000 + "秒)");
		}
	}
	
	private Handler msgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (((String)(msg.obj)).equals("")) {// 获取数据失败
				Toast.makeText(FindPasswordActivity.this, "验证码获取失败",
						Toast.LENGTH_SHORT).show();
				findCodeIbtn.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.ibtn_verification_yes));
				findCodeIbtn.setClickable(true);
			} else {
				code = (String)(msg.obj);
			}
			super.handleMessage(msg);
		};
	};
}
