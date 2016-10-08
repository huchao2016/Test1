package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.PHPLOADIMGUtils;
import com.mojie.utils.PHPLOADIMGUtils.OnLoadImageListener;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PayShangActivity extends Activity implements OnClickListener {

	private ImageView payBackImg;
	private TextView recharge;
	private com.mojie.view.RoundedWebImageView imgUserHead;
	private TextView tvUserName;
	private LinearLayout payBankCardChoose;
	private ImageView payBankCardImg;
	private Button btnPay;
	private EditText payPassword;
	private EditText payAmount;
	private TextView payMoney;
	private TextView payType;
	private TextView payBankCardEndnum;
	private TextView payBankCardName;
	private String pay_money;
	private String paypass;
	private ProgressDialog pd;
	private SharedPreferences shared;
	private Editor editor;
	protected int uid;
	
	private String URL_GETRENDERDATA = ConstUtils.BASEURL + "getexperttipdata.php";
	protected int rResult;
	protected int r_bank_num;
	protected double rBalance ;
	protected double rBenefit;
	protected String tipfee;
	public ArrayList<HashMap<String, String>>rArray;
	String[] sourceStrArray;
	private Handler rHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(PayShangActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {		
					rResult = ((JSONObject)msg.obj).getInt("result");
					if(rResult == 0){
						r_bank_num = ((JSONObject)msg.obj).getInt("bank_num");
						rBalance = ((JSONObject)msg.obj).getDouble("balance");
						rBenefit = ((JSONObject)msg.obj).getDouble("benefit");
						tipfee = ((JSONObject)msg.obj).getString("tipfee");
						sourceStrArray = tipfee.split("\\|");
						list = new ArrayList<String>();  
						for (int i = 0; i < sourceStrArray.length; i++) {
							list.add(sourceStrArray[i]);
				        }
						
						moneySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								pay_money = sourceStrArray[position];
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								
							}
						});
						
				        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PayShangActivity.this,R.layout.spinner_experience, R.id.text1,list);  
						moneySpinner.setAdapter(adapter);
						
						bankCardNum.setText(String.valueOf(r_bank_num));
						walletNum.setText(String.valueOf(rBalance));
						vouchersNum.setText(String.valueOf(rBenefit));
						 
						if( r_bank_num != 0 ){
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 String account = jsonItem.getString("account");
								 String banktype = jsonItem.getString("banktype");
								 String cardtype = jsonItem.getString("cardtype");
								 int default_card = jsonItem.getInt("default_card");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("account", account);
								 map.put("banktype", banktype);
								 map.put("cardtype", cardtype);
								 map.put("default_card", ""+default_card);
								 rArray.add(map);
									
			                }
						}
						
					}else{
						Toast.makeText(PayShangActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.handleMessage(msg);
		};
	};
	
	
	private String URL_PAY = ConstUtils.BASEURL + "innerpaytip.php";
	protected int pResult;
	protected String pBalance;
	protected String pBenefit;
	public ArrayList<HashMap<String, String>>pArray;
	
	private Handler pHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(PayShangActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					pResult = ((JSONObject)msg.obj).getInt("result");
					if(pResult == 0){
						pBalance = ((JSONObject)msg.obj).getString("balance");
						pBenefit = ((JSONObject)msg.obj).getString("benefit");
						Toast.makeText(PayShangActivity.this,"打赏成功",Toast.LENGTH_SHORT).show();
						finish();
					}else if(pResult == 1){
						Toast.makeText(PayShangActivity.this,"打赏失败",Toast.LENGTH_SHORT).show();
					}
					else if(pResult == 2){
						Toast.makeText(PayShangActivity.this,"支付密码错误",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.handleMessage(msg);
		};
	};
	private TextView bankCardNum;
	private String username;
	private TextView walletNum;
	private TextView vouchersNum;
	private int pay_type;
	private String headpic;
	private Spinner moneySpinner;
	private String toid;
	private List<String> list;
	private LinearLayout layoutPayShangBankCard;
	private String cid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_shang);
		
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		username = shared.getString("username", "");
		Intent intent = getIntent();
		cid = intent.getStringExtra("cid");
		toid = intent.getStringExtra("toid");

		rArray = new ArrayList<HashMap<String,String>>();
		pArray = new ArrayList<HashMap<String,String>>();
		initView();
		Toast.makeText(PayShangActivity.this,"打赏功能不能使用代金券",Toast.LENGTH_SHORT).show();
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETRENDERDATA,rHandler,params)).start(); 
		
	}

	private void initView() {
		payBackImg = (ImageView) findViewById(R.id.btn_pay_back);
		payBackImg.setOnClickListener(this);
		
		recharge = (TextView) findViewById(R.id.tv_recharge);
		recharge.setOnClickListener(this);
		
		imgUserHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_user_head);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvUserName.setText(username);
		layoutPayShangBankCard = (LinearLayout) findViewById(R.id.layout_payshang_bank_card);
		layoutPayShangBankCard.setOnClickListener(this);
		bankCardNum = (TextView) findViewById(R.id.tv_pay_bank_card_num);
		walletNum = (TextView) findViewById(R.id.tv_pay_wallet_num);
		vouchersNum = (TextView) findViewById(R.id.tv_pay_vouchers_num);
		
		payBankCardChoose = (LinearLayout) findViewById(R.id.layout_pay_bank_card_choose);
		payBankCardChoose.setOnClickListener(this);
		payBankCardImg = (ImageView) findViewById(R.id.pay_bank_card_img);
		payBankCardName = (TextView) findViewById(R.id.tv_pay_bank_card_name);
		payBankCardEndnum = (TextView) findViewById(R.id.tv_pay_bank_card_endnum);
		payType = (TextView) findViewById(R.id.tv_pay_type);
//		payMoney = (TextView) findViewById(R.id.tv_pay_money);
//		payAmount = (EditText) findViewById(R.id.et_pay_shang_money);
		moneySpinner = (Spinner) findViewById(R.id.spinner_shang_money);
		
//		payAmount = (EditText) findViewById(R.id.pay_amount);
		payPassword = (EditText) findViewById(R.id.pay_password);
		
		btnPay = (Button) findViewById(R.id.btn_pay);
		btnPay.setOnClickListener(this);
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
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(PayShangActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgUserHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(PayShangActivity.this, headpic, bitmap);
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
		case R.id.btn_pay_back:
			finish();
			break;
		case R.id.layout_payshang_bank_card:
			intent = new Intent(PayShangActivity.this,BankCardListActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_recharge:
			intent = new Intent(PayShangActivity.this,RechargeActivity.class);
			intent.putExtra("r_bank_num", ""+r_bank_num);
			intent.putExtra("rBalance", ""+rBalance);
			intent.putExtra("rBenefit", ""+rBenefit);
			startActivity(intent);
			break;
//		case R.id.layout_pay_bank_card_choose:
//			intent = new Intent(PayShangActivity.this,BankCardListActivity.class);
//			startActivity(intent);
//			break;
		case R.id.btn_pay:
//			pay_money = payAmount.getText().toString();
			paypass = payPassword.getText().toString();
			pay_type = 1;
			if("".equals(pay_money)) {
				Toast.makeText(PayShangActivity.this,"请输入金额",Toast.LENGTH_LONG).show();
				break;
			}
			else if("".equals(paypass)) {
				Toast.makeText(PayShangActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
				break;
			}
			else{
				if(rBalance < Double.parseDouble(pay_money)) {
					Toast.makeText(PayShangActivity.this,"余额不足，请充值",Toast.LENGTH_LONG).show();
				}else {
					pd = new ProgressDialog(PayShangActivity.this);
					pd.setMessage("请稍后…");
					pd.show();
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("uid", ""+uid));
					params.add(new BasicNameValuePair("cid", ""+cid));
					params.add(new BasicNameValuePair("pay_money", pay_money));
					params.add(new BasicNameValuePair("paypass", paypass));
					params.add(new BasicNameValuePair("pay_type", ""+pay_type));
					params.add(new BasicNameValuePair("toid", toid));
		    		new Thread(new ConnectPHPToGetJSON(URL_PAY,pHandler,params)).start(); 
				}
			}
			break;
		}
	}

}
