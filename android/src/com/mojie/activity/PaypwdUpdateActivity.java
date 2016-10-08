package com.mojie.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PaypwdUpdateActivity extends Activity implements OnClickListener {

	private ImageView paypwdBackImg;
	private Button paypwdSubmit;
	private EditText paypwdUpdatePsw;
	private EditText paypwdUpdatePswConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tradingpwd_update);
		
		paypwdUpdatePsw = (EditText)findViewById(R.id.paypwd_update_psw);
		paypwdUpdatePswConfirm = (EditText)findViewById(R.id.paypwd_update_psw_confirm);
		
		paypwdBackImg = (ImageView) findViewById(R.id.btn_paypwd_back);
		paypwdBackImg.setOnClickListener(this);
		
		paypwdSubmit = (Button) findViewById(R.id.paypwd_update_submit);
		paypwdSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_paypwd_back:
			finish();
			break;
		case R.id.paypwd_update_submit:
			
			break;
		}
	}

}
