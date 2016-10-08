package com.mojie.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.WebViewActivity;
import com.mojie.activity.BankCardListActivity;
import com.mojie.activity.LoginActivity;
import com.mojie.activity.LoginpwdUpdateActivity;
import com.mojie.activity.MainActivity;
import com.mojie.activity.MobileProveActivity;
import com.mojie.activity.MyLetterListActivity;
import com.mojie.activity.MyfabaoListActivity;
import com.mojie.activity.MyjiebaoListActivity;
import com.mojie.activity.MyresumeActivity;
import com.mojie.activity.NameProveActivity;
import com.mojie.activity.R;
import com.mojie.activity.RechargeActivity;
import com.mojie.activity.TradingpwdUpdateActivity;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.network.ConnectPHPToUpLoadFile;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;
import com.mojie.view.MyDialogBt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineFragment extends Fragment implements OnClickListener{
	private Context context;
	private TextView resume;
	private com.mojie.view.RoundedWebImageView mineHeadImg;
	private TextView mineUsername;
	private LinearLayout mineBankCardLayout;
	private TextView mineBankCardNum;
	private LinearLayout mineWalletLayout;
	private TextView mineWalletNum;
	private LinearLayout mineVouchersLayout;
	private TextView mineVouchersNum;
	private LinearLayout layoutMineUser;
	private TextView tvMineUserName;
	private LinearLayout layoutMineMobile;
	private TextView tvMineMobile;
	private LinearLayout layoutMineLoginPwd;
	private TextView tvMineLoginPwd;
	private LinearLayout layoutMineTradingPwd;
	private TextView tvMineTradingPwd;
	private LinearLayout layoutMineFabao;
	private TextView tvMineFabao;
	private LinearLayout layoutMineJiebao;
	private TextView tvMineJiebao;
	private LinearLayout layoutMineConsult;
	private TextView tvMineConsult;
	private LinearLayout layoutMineLetter;
	private TextView tvMineLetter;
	private LinearLayout layoutMineService;
	private TextView tvMineService;
	private LinearLayout layoutMineAbout;
	private LinearLayout layoutMineHelp;
	private LinearLayout layoutMineVersion;
	private TextView tvMineVersion;
	
	private SharedPreferences shared;
	private int id;
	private ProgressDialog pd;
	private Button btnMineExitLogin;
	private TextView mineUserJobtitle;
	private String jobtitle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_fragment, null);
		context=getActivity();
		shared = context.getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		jobtitle = shared.getString("jobtitle", "");
		
		resume = (TextView) view.findViewById(R.id.tv_resume);
		mineHeadImg=(com.mojie.view.RoundedWebImageView)view.findViewById(R.id.img_mine_head);
		mineHeadImg.setOnClickListener(this);
		mineUsername=(TextView)view.findViewById(R.id.mine_user_name);
		mineUserJobtitle=(TextView)view.findViewById(R.id.mine_user_jobtitle);
		mineBankCardLayout=(LinearLayout)view.findViewById(R.id.layout_mine_bank_card);
		mineBankCardNum=(TextView)view.findViewById(R.id.tv_mine_bank_card_num);
		mineWalletLayout=(LinearLayout)view.findViewById(R.id.layout_mine_wallet);
		mineWalletNum=(TextView)view.findViewById(R.id.tv_mine_wallet_num);
		mineVouchersLayout=(LinearLayout)view.findViewById(R.id.layout_mine_vouchers);
		mineVouchersNum=(TextView)view.findViewById(R.id.tv_mine_vouchers_num);
		
		resume.setOnClickListener(this);
		mineBankCardLayout.setOnClickListener(this);
		mineWalletLayout.setOnClickListener(this);
		mineVouchersLayout.setOnClickListener(this);
		
		//实名认证
		layoutMineUser =(LinearLayout)view.findViewById(R.id.layout_mine_user);
		tvMineUserName=(TextView)view.findViewById(R.id.tv_mine_user_name);
		layoutMineUser.setOnClickListener(this);
		
		//手机认证
		layoutMineMobile =(LinearLayout)view.findViewById(R.id.layout_mine_mobile);
		tvMineMobile=(TextView)view.findViewById(R.id.tv_mine_mobile);
		layoutMineMobile.setOnClickListener(this);
		
		//登录密码
		layoutMineLoginPwd =(LinearLayout)view.findViewById(R.id.layout_mine_login_pwd);
		tvMineLoginPwd=(TextView)view.findViewById(R.id.tv_mine_login_pwd);
		layoutMineLoginPwd.setOnClickListener(this);
		
		//交易密码
		layoutMineTradingPwd =(LinearLayout)view.findViewById(R.id.layout_mine_trading_pwd);
		tvMineTradingPwd=(TextView)view.findViewById(R.id.tv_mine_trading_pwd);
		layoutMineTradingPwd.setOnClickListener(this);
		
		//发包
		layoutMineFabao =(LinearLayout)view.findViewById(R.id.layout_mine_fabao);
		tvMineFabao=(TextView)view.findViewById(R.id.tv_mine_fabao);
		layoutMineFabao.setOnClickListener(this);
		
		//接包
		layoutMineJiebao =(LinearLayout)view.findViewById(R.id.layout_mine_jiebao);
		tvMineJiebao=(TextView)view.findViewById(R.id.tv_mine_jiebao);
		layoutMineJiebao.setOnClickListener(this);
		
		//咨询
		layoutMineConsult =(LinearLayout)view.findViewById(R.id.layout_mine_consult);
		tvMineConsult=(TextView)view.findViewById(R.id.tv_mine_consult);
		layoutMineConsult.setOnClickListener(this);
		
		//信
		layoutMineLetter =(LinearLayout)view.findViewById(R.id.layout_mine_letter);
		tvMineLetter=(TextView)view.findViewById(R.id.tv_mine_letter);
		layoutMineLetter.setOnClickListener(this);
		
		//服务
		layoutMineService =(LinearLayout)view.findViewById(R.id.layout_mine_service);
		tvMineService=(TextView)view.findViewById(R.id.tv_mine_service);
		layoutMineService.setOnClickListener(this);
		
		//about
		layoutMineAbout =(LinearLayout)view.findViewById(R.id.layout_mine_about);
		layoutMineAbout.setOnClickListener(this);
		
		//help
		layoutMineHelp =(LinearLayout)view.findViewById(R.id.layout_mine_help);
		layoutMineHelp.setOnClickListener(this);

		//version
		layoutMineVersion =(LinearLayout)view.findViewById(R.id.layout_mine_version);
		tvMineVersion=(TextView)view.findViewById(R.id.tv_mine_version);
		layoutMineVersion.setOnClickListener(this);
		
		btnMineExitLogin = (Button) view.findViewById(R.id.btn_mine_exit_login);
		btnMineExitLogin.setOnClickListener(this);
		
		headpic = shared.getString("headpic", "");
		if(!headpic.equals("")){
			putImg();
		}
		
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETMINEINFO,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	private void putImg(){
		 Bitmap bitmap = ConstUtils.mCache.getBitmapFromMemCache(headpic);
		 if(bitmap != null){
			 mineHeadImg.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(context, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								mineHeadImg.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(context, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 mineHeadImg.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}
	
	private String URL_GETMINEINFO = ConstUtils.BASEURL + "getmehomeinfo.php";
	protected int result;
	public ArrayList<HashMap<String, String>>mArray;
	private String headpic;
	private String username;
	protected int bank_num;
	protected String balance;
	protected String benefit;
	protected String realname;
	protected int certificated;
	protected String mobile;
	protected int certificated_mobile;
	protected int password_status;
	protected int paypass_status;
	protected int sendtask_num;
	protected int rendertask_num;
	protected int communication_num;
	protected int pms_num;
	protected String service_tel;
	protected int resume_num;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						headpic = ((JSONObject)msg.obj).getString("headpic");
						username = ((JSONObject)msg.obj).getString("username");
						bank_num = ((JSONObject)msg.obj).getInt("bank_num");
						balance = ((JSONObject)msg.obj).getString("balance");
						benefit = ((JSONObject)msg.obj).getString("benefit");
						realname = ((JSONObject)msg.obj).getString("realname");
						certificated = ((JSONObject)msg.obj).getInt("certificated");
						mobile = ((JSONObject)msg.obj).getString("mobile");
						certificated_mobile = ((JSONObject)msg.obj).getInt("certificated_mobile");
						password_status = ((JSONObject)msg.obj).getInt("password_status");
						paypass_status = ((JSONObject)msg.obj).getInt("paypass_status");
						sendtask_num = ((JSONObject)msg.obj).getInt("sendtask_num");
						rendertask_num = ((JSONObject)msg.obj).getInt("rendertask_num");
						communication_num = ((JSONObject)msg.obj).getInt("communication_num");
						pms_num = ((JSONObject)msg.obj).getInt("pms_num");
						service_tel = ((JSONObject)msg.obj).getString("service_tel");
						resume_num = ((JSONObject)msg.obj).getInt("resume_num");
						
						mineUsername.setText(username);
						mineUserJobtitle.setText(jobtitle);
						mineBankCardNum.setText(String.valueOf(bank_num));
						mineWalletNum.setText(balance);
						mineVouchersNum.setText(benefit);
						if (certificated == 1) {
							tvMineUserName.setText(realname);
						}else {
							tvMineUserName.setText("未认证");
						}
						if (certificated_mobile == 1) {
							tvMineMobile.setText(mobile);
						}else {
							tvMineMobile.setText("未认证");
						}
						
						if (password_status == 1) {
							tvMineLoginPwd.setText("已设置");
						}else {
							tvMineLoginPwd.setText("未设置");
						}
						if (paypass_status == 1) {
							tvMineTradingPwd.setText("已设置");
						}else {
							tvMineTradingPwd.setText("未设置");
						}
						tvMineFabao.setText(String.valueOf(sendtask_num));
						tvMineJiebao.setText(String.valueOf(rendertask_num));
						tvMineConsult.setText(String.valueOf(communication_num));
						tvMineLetter.setText(String.valueOf(pms_num));
						tvMineService.setText(service_tel);
						
					}
					else{
						Toast.makeText(context,"获取信息失败",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};
	
	MyDialogBt mDialogBt;
	private String timeString;
	private String cutnameString;
	private Editor editor;
	private String filename;
	
	private Handler upLoadFileHander = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				Toast.makeText(context, "文件上传成功",Toast.LENGTH_SHORT).show();
				
				pd.show();
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("id", ""+id));
				params.add(new BasicNameValuePair("headpic",cutnameString ));
				new Thread(new ConnectPHPToGetJSON(URL_CHANGEUSERIMG, hHandler, params))
						.start();
				
				break;
			case 1:
				Toast.makeText(context, "文件上传失败",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
	private String URL_CHANGEUSERIMG = ConstUtils.BASEURL + "changeuserimg.php";
	private Handler hHandler = new Handler() {
		private int hResult;
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(context, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					hResult = ((JSONObject) msg.obj).getInt("result");
					if (hResult == 0) {
						Toast.makeText(context, "头像修改成功",
								Toast.LENGTH_SHORT).show();
					} else if (hResult == 1) {
						Toast.makeText(context, "头像修改失败",
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
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		
		case R.id.img_mine_head:
			mDialogBt = new MyDialogBt(context, "");
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
		case R.id.tv_resume:
			intent = new Intent(context,MyresumeActivity.class);
			intent.putExtra("username", mineUsername.getText());
			startActivity(intent);
			break;
		case R.id.layout_mine_bank_card:
			intent = new Intent(context,BankCardListActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_wallet:
			intent = new Intent(context,RechargeActivity.class);
			intent.putExtra("r_bank_num", ""+bank_num);
			intent.putExtra("rBalance", ""+balance);
			intent.putExtra("rBenefit", ""+benefit);
			startActivity(intent);
			break;
		case R.id.layout_mine_vouchers:
			
			break;
		case R.id.layout_mine_user:
			intent = new Intent(context,NameProveActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_mobile:
			intent = new Intent(context,MobileProveActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_login_pwd:
			intent = new Intent(context,LoginpwdUpdateActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_trading_pwd:
			intent = new Intent(context,TradingpwdUpdateActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_fabao:
			intent = new Intent(context,MyfabaoListActivity.class);
			startActivityForResult(intent, 5);
			break;
		case R.id.layout_mine_jiebao:
			intent = new Intent(context,MyjiebaoListActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_consult:
			MainActivity mainActivity = (MainActivity)getActivity();
			mainActivity.setChanged(2,R.id.radio_communication);
			break;
		case R.id.layout_mine_letter:
			intent = new Intent(context,MyLetterListActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_mine_service:
//			String serviceTel = tvMineService.getText().toString();
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +service_tel ));
            startActivity(intent);
			
//			new AlertDialog.Builder(context)
//            .setTitle("")
//            .setMessage("确认退出吗？")
//            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            })
//            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(context, "确定", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//
//                }
//            })
//            .show();
			
			break;
		case R.id.layout_mine_about:
			intent = new Intent(context,WebViewActivity.class);
			intent.putExtra("title", "关于我们");
			intent.putExtra("url", ConstUtils.ABOUT_BASE_URL+"page/about");
			startActivity(intent);
			break;
		case R.id.layout_mine_help:
			intent = new Intent(context,WebViewActivity.class);
			intent.putExtra("title", "会员帮助");
			intent.putExtra("url", ConstUtils.ABOUT_BASE_URL+"page/userguide");
			startActivity(intent);
			break;
		case R.id.layout_mine_version:
			
			break;
		case R.id.btn_mine_exit_login:
			intent = new Intent(context,LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			editor = shared.edit();
			editor.putInt("id", 0);
			editor.commit();
			break;
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
			}else if(requestCode == 5){
				MainActivity mainActivity = (MainActivity)getActivity();
				mainActivity.setChanged(1,R.id.radio_fabao);
			}

		}

	}
	
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			savaBitmap(photo);
			mineHeadImg.setImageBitmap(photo);// 将图片显示在ImageView里
				
			ConstUtils.mCache.addBitmapToDiskCache(context, cutnameString, photo);
			editor = shared.edit();
			editor.putString("headpic",cutnameString );
			editor.commit();
			Log.v("liuchao", "user img :"+cutnameString);
			new Thread(new ConnectPHPToUpLoadFile(filename, upLoadFileHander)).start();
		}
	}
	
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
	
public void savaBitmap(Bitmap bitmap) {
		
		final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		 
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	 
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String uniqueId = deviceUuid.toString();
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'UIMG'_yyyyMMddHHmmss");
		String timeStr = dateFormat.format(date);
		
		cutnameString = timeStr+uniqueId+".jpg";
		
		//filename = Environment.getExternalStorageDirectory().getPath() + "/mojie/" + cutnameString;
		filename = context.getCacheDir().getPath()+"/"+cutnameString;
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