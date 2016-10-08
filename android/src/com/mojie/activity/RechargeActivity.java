package com.mojie.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends Activity implements OnClickListener {
	private Context context;

	private ImageView rechargeBackImg;
	private com.mojie.view.RoundedWebImageView imgUserHead;
	private TextView tvUserName;
	private LinearLayout rechargeBankCardChoose;
	private com.mojie.view.RoundedWebImageView rechargeBankCardImg;
	private TextView rechargeBankCardEndnum;
	private TextView rechargeBankCardName;
	private EditText rechargeAmount;
	private EditText rechargePassword;
	private Button btnRecharge;
	private String money;
	private String psd;
	private ProgressDialog pd;
	private SharedPreferences shared;
	private int id;
	private int uid;
	private String username;
	private String r_bank_num;
	private String rBalance;
	private String rBenefit;
	private TextView bankCardNum;
	private TextView walletNum;
	private TextView vouchersNum;
	private String headpic;
	private LinearLayout layoutRechargeBankCard;
	
	private static final int REQUEST_CODE_PAYMENT = 2;
	private static final String CHANNEL_UPACP = "upacp";// 银联支付渠道
	private static final String CHANNEL_WECHAT = "wx";// 微信支付渠道
	private static final String CHANNEL_ALIPAY = "alipay";// 支付宝支付渠道
	private static String YOUR_URL = "http://218.244.151.190/demo/charge";
	public static final String URL = YOUR_URL;
	
	private String URL_ALIPAYRECHARGE = ConstUtils.BASEURL + "recharge.php";
	protected int result;
	protected String sn ;
	protected String rmoney;
	
	private Handler aHandler = new Handler(){  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(RechargeActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						sn = ((JSONObject)msg.obj).getString("sn");
						rmoney = ((JSONObject)msg.obj).getString("money");
						double proMoney = Double.parseDouble(rmoney);
						double resMoney = proMoney*100;
//						Intent intent = new Intent(RechargeActivity.this,AlipayActivity.class);
//						intent.putExtra("sn", sn);
//						intent.putExtra("rmoney", rmoney);
//						startActivity(intent);
						
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("uid", ""+uid));
						params.add(new BasicNameValuePair("channel", "alipay"));
						params.add(new BasicNameValuePair("amount", ""+resMoney));
		        		params.add(new BasicNameValuePair("order_no", sn));
		        		params.add(new BasicNameValuePair("subject", "模界平台充值"));
		        		params.add(new BasicNameValuePair("body", "模界平台充值"));
		        		new Thread(new ConnectPHPToGetJSON(URL_ALIPAY,alipayHandler,params)).start(); 
						
					} else if(result == 1){
						Toast.makeText(RechargeActivity.this,"支付失败",Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(RechargeActivity.this,"支付密码错误",Toast.LENGTH_LONG).show();
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
	
	
	private String URL_BANKRECHARGE = ConstUtils.BASEURL + "recharge.php";
	protected int bResult;
	protected String b_sn ;
	protected String b_rmoney;
	
	private Handler bHandler = new Handler(){  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(RechargeActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					bResult = ((JSONObject)msg.obj).getInt("result");
					if(bResult == 0){
						b_sn = ((JSONObject)msg.obj).getString("sn");
						b_rmoney = ((JSONObject)msg.obj).getString("money");
//						Intent intent = new Intent(RechargeActivity.this,AlipayActivity.class);
//						intent.putExtra("sn", b_sn);
//						intent.putExtra("rmoney", b_rmoney);
//						startActivity(intent);
						
					}
					else if(bResult == 1){
						Toast.makeText(RechargeActivity.this,"支付失败",Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(RechargeActivity.this,"支付密码错误",Toast.LENGTH_LONG).show();
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
	
	private String URL_ALIPAY = ConstUtils.BASEURL + "pingpp/pay.php";
	protected int alResult;

	protected String paymsg;
	private Handler alipayHandler = new Handler(){  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(RechargeActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					alResult = ((JSONObject)msg.obj).getInt("result");
					if(alResult == 0){
						JSONObject data = ((JSONObject)msg.obj).getJSONObject("data");
				
						paymsg = data.toString();
						Intent intent = new Intent();
						String packageName = getPackageName();
						Log.i("pwj", "packageName==" + packageName);
						ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
						intent.setComponent(componentName);
						intent.putExtra(PaymentActivity.EXTRA_CHARGE, paymsg);
						startActivityForResult(intent, REQUEST_CODE_PAYMENT);

					}else{
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
		setContentView(R.layout.activity_recharge);
		context = this;
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		username = shared.getString("username", "");
		
		Intent intent = getIntent();
		r_bank_num = intent.getStringExtra("r_bank_num");
		rBalance = intent.getStringExtra("rBalance");
		rBenefit = intent.getStringExtra("rBenefit");
		
		initView();
		
	}

	private void initView() {
		rechargeBackImg = (ImageView) findViewById(R.id.btn_recharge_back);
		rechargeBackImg.setOnClickListener(this);
		
		imgUserHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_user_head);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvUserName.setText(username);
		layoutRechargeBankCard = (LinearLayout) findViewById(R.id.layout_recharge_bank_card);
		layoutRechargeBankCard.setOnClickListener(this);
		bankCardNum = (TextView) findViewById(R.id.tv_recharge_bank_card_num);
		walletNum = (TextView) findViewById(R.id.tv_recharge_wallet_num);
		vouchersNum = (TextView) findViewById(R.id.tv_recharge_vouchers_num);
		bankCardNum.setText(r_bank_num);
		walletNum.setText(rBalance);
		vouchersNum.setText(rBenefit);
		rechargeBankCardChoose = (LinearLayout) findViewById(R.id.layout_recharge_bank_card_choose);
		rechargeBankCardChoose.setOnClickListener(this);
		
		rechargeBankCardImg = (com.mojie.view.RoundedWebImageView) findViewById(R.id.recharge_bank_card_img);
		rechargeBankCardName = (TextView) findViewById(R.id.tv_recharge_bank_card_name);
		rechargeBankCardEndnum = (TextView) findViewById(R.id.tv_recharge_bank_card_endnum);
		rechargeAmount = (EditText) findViewById(R.id.recharge_amount);
		rechargePassword = (EditText) findViewById(R.id.recharge_password);
		
		btnRecharge = (Button) findViewById(R.id.btn_recharge);
		btnRecharge.setOnClickListener(this);
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
			 imgUserHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(RechargeActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgUserHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(RechargeActivity.this, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 imgUserHead.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_recharge_back:
			finish();
			break;
		case R.id.layout_recharge_bank_card:
			intent = new Intent(RechargeActivity.this,BankCardListActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_recharge_bank_card_choose:
			intent = new Intent(RechargeActivity.this,PaywayActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.btn_recharge:
			
			money = rechargeAmount.getText().toString();
			psd = rechargePassword.getText().toString();
			if(way != 0 && way != 2 && way !=3) {
		        Toast.makeText(RechargeActivity.this,"请选择支付方式",Toast.LENGTH_SHORT).show();
			} else if("".equals(money)) {
		        Toast.makeText(RechargeActivity.this,"请输入金额",Toast.LENGTH_SHORT).show();
			} 
//			else if("".equals(psd)) {
//				Toast.makeText(RechargeActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
//			}
			else {
//				if (way == 0) {
////					new PaymentTask().execute(new PingPaymentRequest(CHANNEL_ALIPAY, amount));
//				} else if(way == 2) {
////					new PaymentTask().execute(new PingPaymentRequest(CHANNEL_WECHAT, amount));
//				} else if(way == 3) {
////					new PaymentTask().execute(new PingPaymentRequest(CHANNEL_UPACP, amount));
//				}
				if (way == 0) {
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("uid", ""+uid));
					params.add(new BasicNameValuePair("payment_id", ""+way));
					params.add(new BasicNameValuePair("money", money));
//	        		params.add(new BasicNameValuePair("paypass", psd));
	        		params.add(new BasicNameValuePair("poundage", ""+0));
	        		params.add(new BasicNameValuePair("amount", money));
	        		params.add(new BasicNameValuePair("status", ""+1));
	        		params.add(new BasicNameValuePair("intro", "模界平台充值"));
	        		params.add(new BasicNameValuePair("createdby", username));
	        		new Thread(new ConnectPHPToGetJSON(URL_ALIPAYRECHARGE,aHandler,params)).start(); 
	        		pd = new ProgressDialog(RechargeActivity.this);
	    			pd.setMessage("请稍后…");
	    			pd.show();
//				}else {
//					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//					params.add(new BasicNameValuePair("uid", ""+uid));
//					params.add(new BasicNameValuePair("payment_id", ""+way));
//					params.add(new BasicNameValuePair("money", money));
////	        		params.add(new BasicNameValuePair("paypass", psd));
//	        		params.add(new BasicNameValuePair("poundage", ""+0));
//	        		params.add(new BasicNameValuePair("amount", money));
//	        		params.add(new BasicNameValuePair("status", ""+1));
//	        		params.add(new BasicNameValuePair("intro", "模界平台充值"));
//	        		params.add(new BasicNameValuePair("createdby", username));
//	        		new Thread(new ConnectPHPToGetJSON(URL_BANKRECHARGE,bHandler,params)).start(); 
//	        		pd = new ProgressDialog(RechargeActivity.this);
//	    			pd.setMessage("请稍后…");
//	    			pd.show();
				}
			}
			break;
		}
	}
		
	int way = 0xff;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
			if(data.getIntExtra("payway", 0) == 0){ //alipay
				rechargeBankCardName.setText("支付宝");
				rechargeBankCardImg.setImageResource(R.drawable.icon_alipay);
				rechargeBankCardEndnum.setVisibility(View.GONE);
				way = 0;
			}else if (data.getIntExtra("payway", 0) == 2) {
				rechargeBankCardName.setText("微信支付");
				rechargeBankCardImg.setImageResource(R.drawable.ic_weixin);
				rechargeBankCardEndnum.setVisibility(View.GONE);
				way = 2;
				
			}else if (data.getIntExtra("payway", 0) == 3) {
				rechargeBankCardName.setText("银联支付");
				rechargeBankCardImg.setImageResource(R.drawable.ic_yinlian);
				rechargeBankCardEndnum.setVisibility(View.GONE);
				way = 3;
				
			}else{
				rechargeBankCardName.setText("银行卡");
				rechargeBankCardImg.setImageResource(R.drawable.icon_bao_budget);
				way = 1;
			}
		}else if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String resultString = "";
                if (result.equals("success")) {
                	resultString = "支付成功";
				} else if (result.equals("fail")) {
					resultString = "支付失败";
				} else if (result.equals("cancel")) {
					resultString = "取消支付";
				} else if (result.equals("invalid")) {
					resultString = "支付插件无效";
				}
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(resultString, errorMsg, extraMsg);
            }
		}
	}
	
//	class PaymentTask extends AsyncTask<PingPaymentRequest, Void, String> {
//
//		@Override
//		protected void onPreExecute() {
//
//			// 按键点击之后的禁用，防止重复点击
////			btnConfirmPayment.setOnClickListener(null);
//		}
//
//		@Override
//		protected String doInBackground(PingPaymentRequest... pr) {
//
//			PingPaymentRequest paymentRequest = pr[0];
//			String data = null;
//			String json = new Gson().toJson(paymentRequest);
//			try {
//				// 向Your Ping++ Server SDK请求数据
//				data = postJson(URL, json);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return data;
//		}
//
//		/**
//		 * 获得服务端的charge，调用ping++ sdk。
//		 */
//		@Override
//		protected void onPostExecute(String data) {
//			if (null == data) {
//				showMsg("请求出错", "请检查URL", "URL无法获取charge");
//				return;
//			}
//			Log.d("charge", data);
//			Intent intent = new Intent();
//			String packageName = getPackageName();
//			Log.i("pwj", "packageName=="+packageName);
//			ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
//			intent.setComponent(componentName);
//			intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
//			startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//		}
//
//	}

	public void showMsg(String title, String msg1, String msg2) {
		String str = title;
		Log.i("pwj", "title=="+title);
		if (null != msg1 && msg1.length() != 0) {
			str += "\n错误信息:"+msg1;
			Log.i("pwj", "msg1=="+msg1);
		}
		if (null != msg2 && msg2.length() != 0) {
			str += "\n错误信息:"+msg2;
			Log.i("pwj", "msg2=="+msg2);
		}
		str += "\n是否离开该页面?"; 
		Log.i("pwj", "str=="+str);
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(str);
		builder.setTitle("支付结果:");
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();	
			}
		});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

//	private static String postJson(String url, String json) throws IOException {
//		MediaType type = MediaType.parse("application/json; charset=utf-8");
//		RequestBody body = RequestBody.create(type, json);
//		Request request = new Request.Builder().url(url).post(body).build();
//
//		OkHttpClient client = new OkHttpClient();
//		Response response = client.newCall(request).execute();
//
//		return response.body().string();
//	}
//
//	class PingPaymentRequest {
//		String channel;
//		int amount;
//
//		public PingPaymentRequest(String channel, int amount) {
//			this.channel = channel;
//			this.amount = amount;
//		}
//	}

}
