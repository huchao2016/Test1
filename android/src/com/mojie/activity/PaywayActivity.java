package com.mojie.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PaywayActivity extends Activity implements OnClickListener {

	private ImageView paywayBackImg;
	private LinearLayout layoutAlipay;
	private LinearLayout layoutBankcard;
	private TextView tvAlipay;
	private TextView tvBankcard;
	private LinearLayout layoutWechat;
	private LinearLayout layoutUpacp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payway);
		
		paywayBackImg = (ImageView) findViewById(R.id.btn_payway_back);
		paywayBackImg.setOnClickListener(this);
		layoutAlipay = (LinearLayout) findViewById(R.id.layout_alipay);
		layoutAlipay.setOnClickListener(this);
		layoutWechat = (LinearLayout) findViewById(R.id.layout_wechat);
		layoutWechat.setOnClickListener(this);
		layoutUpacp = (LinearLayout) findViewById(R.id.layout_upacp);
		layoutUpacp.setOnClickListener(this);
		layoutBankcard = (LinearLayout) findViewById(R.id.layout_bankcard);
		layoutBankcard.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_payway_back:
			finish();
			break;
		case R.id.layout_alipay:
			
			intent = new Intent();
			intent.putExtra("payway", 0);//0 alipay 1 bank
			setResult(RESULT_OK,intent);
			finish();
			break;
		case R.id.layout_wechat:
			intent = new Intent();
			intent.putExtra("payway", 2);
			setResult(RESULT_OK,intent);
			finish();
		case R.id.layout_upacp:
			intent = new Intent();
			intent.putExtra("payway", 3);
			setResult(RESULT_OK,intent);
			finish();
		case R.id.layout_bankcard:
//			intent = new Intent(PaywayActivity.this,BankCardListActivity.class);
//			startActivity(intent);
			intent = new Intent();
			intent.putExtra("payway", 1);//0 alipay 1 bank
			setResult(RESULT_OK,intent);
			finish();
			break;
		}
	}
}
