package com.mojie.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;
import com.mojie.view.ToastView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	private EditText userName;
	private EditText password;
	private Button login;
	private Button register;
	private TextView forget;
	private SharedPreferences shared;
	private Editor editor;
	private String name;
	private String psd;
	private TextView tvLoginUserName;
	private com.mojie.view.RoundedWebImageView imgLoginHead;
	
	private String URL_LOGIN = ConstUtils.BASEURL + "login.php";
	protected int result;
	protected int id;
	protected String username;
	protected String mobile;
	protected String email;
	protected String qq;
	protected String headpic;
	protected String mojiejpushalias;
	
	private Handler handler = new Handler(){  
		private String jobtitle;

		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(LoginActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						id = ((JSONObject)msg.obj).getInt("id");
						username = ((JSONObject)msg.obj).getString("username");
						jobtitle = ((JSONObject)msg.obj).getString("jobtitle");
						mobile = ((JSONObject)msg.obj).getString("mobile");
						email = ((JSONObject)msg.obj).getString("email");
						qq = ((JSONObject)msg.obj).getString("qq");
						headpic = ((JSONObject)msg.obj).getString("headpic");
						mojiejpushalias = ((JSONObject)msg.obj).getString("mojiejpushalias");
						editor.putString("username",username );
						editor.putString("jobtitle",jobtitle );
						editor.putString("password",psd );
						editor.putInt("id", id);
						editor.putString("mobile",mobile );
						editor.putString("email",email );
						editor.putString("qq",qq );
						editor.putString("headpic",headpic );
						editor.commit();
						
						JPushInterface.setDebugMode(true);
						JPushInterface.init(LoginActivity.this);
						JPushInterface.setAlias(LoginActivity.this, mojiejpushalias, new TagAliasCallback() {
							@Override
							public void gotResult(int arg0, String arg1, Set<String> arg2) {
								Log.d("JPush","set Alias result is "+arg0);
							}
						});
						
						Bundle bundle = new Bundle();
						bundle.putString("name", name);
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}else{
						Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
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
	private String URL_GETUSERNUM = ConstUtils.BASEURL + "getusernum.php";
	protected int numResult;
	protected String usernum;
	private Handler numHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(LoginActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					numResult = ((JSONObject)msg.obj).getInt("result");
					if(numResult == 0){
						usernum = ((JSONObject)msg.obj).getString("user_num");
						tvLoginUsernum.setText("模界之家已有 "+usernum+" 人");
					}else{
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.handleMessage(msg);
		};
	};
	private ProgressDialog pd;
	private TextView tvLoginSkip;
	private TextView tvLoginUsernum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		shared = getSharedPreferences("userInfo", 0);
		editor = shared.edit();
		String name_str=shared.getString("username", "");
		String mobile_str=shared.getString("mobile", "");
		
		userName = (EditText) findViewById(R.id.login_name);
		password = (EditText) findViewById(R.id.login_password);
		forget = (TextView) findViewById(R.id.tv_login_forget);
		forget.setOnClickListener(this);
		
		tvLoginUserName = (TextView) findViewById(R.id.tv_login_user_name);
		imgLoginHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_login_head);
		userName.setText(mobile_str);
		tvLoginUserName.setText(name_str);
		//光标设置到文本末尾
		CharSequence text = userName.getText();
        if (text instanceof Spannable) {
        	Spannable spanText = (Spannable)text;
        	Selection.setSelection(spanText, text.length());
        }
		login = (Button) findViewById(R.id.login_login);
		register = (Button) findViewById(R.id.login_register);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		tvLoginSkip = (TextView) findViewById(R.id.tv_login_skip);
		tvLoginSkip.setOnClickListener(this);
		
		tvLoginUsernum = (TextView) findViewById(R.id.tv_login_usernum);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		new Thread(new ConnectPHPToGetJSON(URL_GETUSERNUM,numHandler,params)).start(); 
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
			 imgLoginHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(LoginActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgLoginHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(LoginActivity.this, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 imgLoginHead.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Resources resource = (Resources) getBaseContext().getResources();
        String usern="请输入账号";
        String pass="请输入密码";
        String hold="请稍后...";
		Intent intent;
		switch(v.getId()) {
		case R.id.login_login:
			
			name = userName.getText().toString();
			psd = password.getText().toString();
			if("".equals(name)) {
		        Toast.makeText(LoginActivity.this,usern,Toast.LENGTH_SHORT).show();
			} else if("".equals(psd)) {
				Toast.makeText(LoginActivity.this,pass,Toast.LENGTH_SHORT).show();
			} else {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        		params.add(new BasicNameValuePair("username", name));
        		params.add(new BasicNameValuePair("password", psd));
        		new Thread(new ConnectPHPToGetJSON(URL_LOGIN,handler,params)).start(); 
        		pd = new ProgressDialog(LoginActivity.this);
				pd.setMessage(hold);
				pd.show();
			}
			break;
		case R.id.login_register:
			intent = new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_login_forget:
			intent = new Intent(LoginActivity.this,FindPasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_login_skip:
			intent = new Intent(LoginActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}
	
	private boolean isExit = false;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if (isExit == false) {
				isExit = true;
//				Resources resource = (Resources) getBaseContext()
//						.getResources();
//				String exit = resource.getString(R.string.again_exit);
//				ToastView toast = new ToastView(getApplicationContext(), exit);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
				Toast.makeText(LoginActivity.this, "再按一次退出APP",Toast.LENGTH_SHORT).show();
				exitHandler.sendEmptyMessageDelayed(0, 3000);

				return true;
			} else {
				finish();
				ToastView.cancel();
				return false;
			}
		}
		return true;
	}
	
	Handler exitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

}
