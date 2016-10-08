package com.mojie.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.MsgCheckUtil;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MobileProveActivity extends Activity implements OnClickListener {

	private ImageView proveMobileBackImg;
	private com.mojie.view.RoundedWebImageView proveNameHead;
	private EditText proveMobileNum;
	private EditText proveMobileCode;
	private Button proveMobileSubmit;
	private Button proveMobileCodeIbtn;

	private SharedPreferences shared;
	private int id;
	private ProgressDialog pd;
	private String mobileNum;
	private String security_code;
	private String code = "";
	private String headpic;
	private String mobile;
	private TimeCount time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prove_mobile);
		
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		mobile = shared.getString("mobile", "");
		
		proveMobileNum = (EditText)findViewById(R.id.prove_mobile_num);
		proveMobileCode = (EditText)findViewById(R.id.prove_mobile_code);
		proveNameHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.prove_mobile_head);
		
		proveMobileBackImg = (ImageView) findViewById(R.id.prove_mobile_back);
		proveMobileBackImg.setOnClickListener(this);
		
		proveMobileCodeIbtn = (Button)findViewById(R.id.prove_mobile_code_ibtn);
		proveMobileCodeIbtn.setOnClickListener(this);
		
		proveMobileSubmit = (Button) findViewById(R.id.btn_prove_mobile_submit);
		proveMobileSubmit.setOnClickListener(this);
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
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
			 proveNameHead.setImageBitmap(bitmap);
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(MobileProveActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								proveNameHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(MobileProveActivity.this, headpic, bitmap);
								}
							}
						}
					});
			 }else{
				 proveNameHead.setImageBitmap(bitmap);
			 }
		 }
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.prove_mobile_back:
			finish();
			break;
		case R.id.prove_mobile_code_ibtn:
			mobileNum = proveMobileNum.getText().toString();
			if ("".equals(mobileNum)) {
				Toast.makeText(MobileProveActivity.this, "请输入手机号码",
						Toast.LENGTH_SHORT).show();
				break;
			} else if (!mobile.equals(mobileNum)) {
				Toast.makeText(MobileProveActivity.this, "您输入的手机号码与绑定的手机号码不一致，请重新输入！",Toast.LENGTH_SHORT).show();
				break;
			}
			MsgCheckUtil checkUtil = new MsgCheckUtil(msgHandler,mobileNum);
			checkUtil.sendMsgReq();
			time.start();// 开始计时
			break;
		case R.id.btn_prove_mobile_submit:
			mobileNum = proveMobileNum.getText().toString();
			security_code = proveMobileCode.getText().toString();
			if("".equals(mobileNum)) {
				Toast.makeText(MobileProveActivity.this,"请输入手机号码",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(security_code)) {
				Toast.makeText(MobileProveActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
				break;
			}else if (!code.equals(security_code)) {
				Toast.makeText(MobileProveActivity.this, "验证码有误",Toast.LENGTH_SHORT).show();
				break;
			}
			pd = new ProgressDialog(MobileProveActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", ""+id));
			params.add(new BasicNameValuePair("mobile", mobileNum));
			params.add(new BasicNameValuePair("security_code", security_code));
			new Thread(new ConnectPHPToGetJSON(URL_GETPROVEMOBILE,handler,params)).start(); 
			break;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			proveMobileCodeIbtn.setText("重新发送");
			proveMobileCodeIbtn.setBackgroundResource(R.drawable.bg_send);
			proveMobileCodeIbtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			proveMobileCodeIbtn.setClickable(false);
			proveMobileCodeIbtn.setBackgroundResource(R.drawable.bg_send_gray);
			proveMobileCodeIbtn.setText("重新发送(" + millisUntilFinished / 1000 + "秒)");
		}
	}
	
	private String URL_GETPROVEMOBILE = ConstUtils.BASEURL + "usermobilecertification.php";
	private Handler handler = new Handler(){  
        private int result;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MobileProveActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						Toast.makeText(MobileProveActivity.this,"手机认证成功",Toast.LENGTH_SHORT).show();
						finish();
					}else if(result == 1){
						Toast.makeText(MobileProveActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
					}else if(result == 2){
						Toast.makeText(MobileProveActivity.this,"手机认证失败",Toast.LENGTH_SHORT).show();
					}else if(result == 3){
					Toast.makeText(MobileProveActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
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
	
	private Handler msgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (((String)(msg.obj)).equals("")) {// 获取数据失败
				Toast.makeText(MobileProveActivity.this, "验证码获取失败",
						Toast.LENGTH_SHORT).show();
			} else {
				code = (String)(msg.obj);
			}
			super.handleMessage(msg);
		};
	};
	
}
