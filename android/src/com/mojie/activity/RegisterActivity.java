package com.mojie.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.network.ConnectPHPToUpLoadFile;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.MsgCheckUtil;
import com.mojie.view.MyDialogBt;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

public class RegisterActivity extends Activity implements OnClickListener {

	private ImageView registerBackImg;
	private Button registerCodeIbtn;
	private Button registerSubmit;
	private ImageView registerHeadImg;
	private EditText registerName;
	private EditText registerTel;
	private EditText registerCode;
	private EditText registerPwd;
	private EditText registerPwdConfirm;

	private ProgressDialog pd;
	private String psd1;
	private String psd2;
	private String usernameStr;
	private String code = "";
	private String mobileStr;
	private String timeString;

	private String URL_REGISTER = ConstUtils.BASEURL + "regist.php";
	protected String user_num;
	private Handler handler = new Handler() {
		private int result;

		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(RegisterActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					result = ((JSONObject) msg.obj).getInt("result");
					if (result == 0) {
						user_num = ((JSONObject) msg.obj).getString("user_num");
						Toast.makeText(RegisterActivity.this, "您是第"+user_num+"位模具人，家人欢迎您回家！",
								Toast.LENGTH_SHORT).show();
//						Toast.makeText(RegisterActivity.this, "已经注册会员"+user_num+"人！", Toast.LENGTH_SHORT).show();
						finish();
					} else if (result == 1) {
						Toast.makeText(RegisterActivity.this, "注册失败",
								Toast.LENGTH_SHORT).show();
					} else if (result == 2) {
						Toast.makeText(RegisterActivity.this, "用户名已注册",
								Toast.LENGTH_SHORT).show();
					} else if (result == 3) {
						Toast.makeText(RegisterActivity.this, "验证码错误",
								Toast.LENGTH_SHORT).show();
					}else if (result == 4) {
						Toast.makeText(RegisterActivity.this, "手机号码已注册",
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
	private CheckBox register_checkBox_agree;
	private TextView tvRegisterAgreement;
	private TimeCount time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
	}

	private void initView() {
		registerName = (EditText) findViewById(R.id.register_name);
		registerTel = (EditText) findViewById(R.id.register_tel);
		registerCode = (EditText) findViewById(R.id.register_code);
		registerPwd = (EditText) findViewById(R.id.register_psw);
		registerPwdConfirm = (EditText) findViewById(R.id.register_psw_confirm);

		registerBackImg = (ImageView) findViewById(R.id.btn_register_back);
		registerBackImg.setOnClickListener(this);

		registerHeadImg = (ImageView) findViewById(R.id.register_head);
		registerHeadImg.setOnClickListener(this);

		registerCodeIbtn = (Button) findViewById(R.id.register_code_ibtn);
		registerCodeIbtn.setOnClickListener(this);

		registerSubmit = (Button) findViewById(R.id.register_submit);
		registerSubmit.setOnClickListener(this);
		
		register_checkBox_agree = (CheckBox) findViewById(R.id.register_checkBox_agree);
		register_checkBox_agree.setOnClickListener(this);
		register_checkBox_agree.setChecked(true);
		
		tvRegisterAgreement = (TextView) findViewById(R.id.tv_register_agreement);
		tvRegisterAgreement.setOnClickListener(this);
		
	}

	MyDialogBt mDialogBt;
	private String registerCodeStr;

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_register_back:
			finish();
			break;
		case R.id.tv_register_agreement:
			intent = new Intent(RegisterActivity.this,WebViewActivity.class);
			intent.putExtra("title", "会员注册服务协议");
			intent.putExtra("url", ConstUtils.ABOUT_BASE_URL+"page/protocol");
			startActivity(intent);
			break;
		case R.id.register_head:
			mDialogBt = new MyDialogBt(RegisterActivity.this, "");
			mDialogBt.show();
			mDialogBt.registerCamera.setOnClickListener(this);
			mDialogBt.registerPhoto.setOnClickListener(this);
			mDialogBt.negativeCancel.setOnClickListener(this);
			break;
		case R.id.register_camera_bt:
			mDialogBt.dismiss();
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"'IMG'_yyyyMMddHHmmss");
			timeString = dateFormat.format(date);
			createSDCardDir();
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory() + "/DCIM/Camera",
					timeString + ".jpg")));
			startActivityForResult(intent, 1);

			break;
		case R.id.register_photo_bt:
			mDialogBt.dismiss();

			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 2);
			break;
		case R.id.register_cancel_bt:
			mDialogBt.dismiss();
			break;

		case R.id.register_code_ibtn:
			mobileStr = registerTel.getText().toString();
			if ("".equals(mobileStr)) {
				Toast.makeText(RegisterActivity.this, "请输入手机号码",
						Toast.LENGTH_LONG).show();
				break;
			}
			MsgCheckUtil checkUtil = new MsgCheckUtil(msgHandler,mobileStr);
			checkUtil.sendMsgReq();
			time.start();// 开始计时
			break;
		case R.id.register_checkBox_agree:
			if(register_checkBox_agree.isSelected()){
				register_checkBox_agree.setSelected(false);
			}else{
				register_checkBox_agree.setSelected(true);
			}
			break;
		case R.id.register_submit:
			psd1 = registerPwd.getText().toString();
			psd2 = registerPwdConfirm.getText().toString();
			usernameStr = registerName.getText().toString();
			mobileStr = registerTel.getText().toString();
			registerCodeStr = registerCode.getText().toString();

			if ("".equals(usernameStr)) {
				Toast.makeText(RegisterActivity.this, "请输入账号",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(mobileStr)) {
				Toast.makeText(RegisterActivity.this, "请输入手机号码",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(registerCodeStr)) {
				Toast.makeText(RegisterActivity.this, "请输入验证码",
						Toast.LENGTH_LONG).show();
				break;
			} else if (!code.equals(registerCodeStr)) {
				Toast.makeText(RegisterActivity.this, "验证码有误",
						Toast.LENGTH_LONG).show();
				break;
			} else if ("".equals(psd1)) {
				Toast.makeText(RegisterActivity.this, "请输入密码",
						Toast.LENGTH_LONG).show();
				break;
			} else if (!psd1.equals(psd2)) {
				Toast.makeText(RegisterActivity.this, "密码输入不一致，请重新输入",
						Toast.LENGTH_LONG).show();
				break;
			}else if(register_checkBox_agree.isSelected()){
				Toast.makeText(RegisterActivity.this, "请同意《会员注册服务协议》",
						Toast.LENGTH_LONG).show();
				break;
			}
			pd = new ProgressDialog(RegisterActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("username", usernameStr));
			params.add(new BasicNameValuePair("mobile", mobileStr));
			params.add(new BasicNameValuePair("security_code", registerCodeStr));
			params.add(new BasicNameValuePair("password", psd1));
			params.add(new BasicNameValuePair("headpic",cutnameString ));
			new Thread(new ConnectPHPToGetJSON(URL_REGISTER, handler, params))
					.start();
			break;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			registerCodeIbtn.setText("重新发送");
			registerCodeIbtn.setBackgroundResource(R.drawable.bg_send);
			registerCodeIbtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			registerCodeIbtn.setClickable(false);
			registerCodeIbtn.setBackgroundResource(R.drawable.bg_send_gray);
			registerCodeIbtn.setText("重新发送(" + millisUntilFinished / 1000 + "秒)");
		}
	}
	
	private File file;
	private String cutnameString;// 用户头像名字
	private String filename;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == 1) {
				File temp = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/DCIM/Camera/" + timeString + ".jpg");
				startPhotoZoom(Uri.fromFile(temp));

			} else if (requestCode == 2) {
				startPhotoZoom(data.getData());
			} else if (requestCode == 3) {
				if (data != null) {
					setPicToView(data);
				}
			}

		}

	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		
		Log.v("liuchao", "path = "+uri.getPath());
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	 private Handler upLoadFileHander = new Handler() {
			public void handleMessage(Message msg) {
	
				switch (msg.what) {
				case 0:
					Toast.makeText(RegisterActivity.this, "文件上传成功",Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(RegisterActivity.this, "文件上传失败",Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			};
		};
	private SharedPreferences shared;
	private Editor editor;
	
	private Handler msgHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (((String)(msg.obj)).equals("")) {// 获取数据失败
				Toast.makeText(RegisterActivity.this, "验证码获取失败",
						Toast.LENGTH_SHORT).show();
			} else {
				code = (String)(msg.obj);
			}
			super.handleMessage(msg);
		};
	};
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			savaBitmap(photo);
			registerHeadImg.setImageBitmap(photo);// 将图片显示在ImageView里
				
			ConstUtils.mCache.addBitmapToDiskCache(RegisterActivity.this, cutnameString, photo);
			
			shared = getSharedPreferences("userInfo", 0);
			editor = shared.edit();
			editor.putString("headpic",cutnameString );
			editor.commit();
			
			Log.v("liuchao", "user img :"+cutnameString);
			new Thread(new ConnectPHPToUpLoadFile(filename, upLoadFileHander)).start();
		}
	}

	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/DCIM/Camera";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();

			}
		}
	}

	// 将剪切后的图片保存到本地图片上！
	public void savaBitmap(Bitmap bitmap) {
		
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		 
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	 
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String uniqueId = deviceUuid.toString();
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'UIMG'_yyyyMMddHHmmss");
		String timeStr = dateFormat.format(date);
		
		cutnameString = timeStr+uniqueId+".jpg";
		
		//filename = Environment.getExternalStorageDirectory().getPath() + "/" + cutnameString;
		filename = getCacheDir().getPath()+"/"+cutnameString;
		File f = new File(filename);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);// 把Bitmap对象解析成流
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
