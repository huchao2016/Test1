package com.mojie.activity;

import org.apache.http.util.EncodingUtils;

import com.mojie.utils.ConstUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class AlipayActivity extends Activity {

	private ImageView alipayBackImg;
	private WebView webView ;
	private String sn;
	private String rmoney;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alipay);
		
		Intent intent = getIntent();
		sn = intent.getStringExtra("sn");
		rmoney = intent.getStringExtra("rmoney");
		String subject = "模界平台充值";
		
		alipayBackImg = (ImageView) findViewById(R.id.btn_about_back);
		alipayBackImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		webView = (WebView)findViewById(R.id.webView1);
	    //post方式传送参数  
		WebSettings webSettings = webView.getSettings();       
		webSettings.setJavaScriptEnabled(true); 
	    String postData = "WIDout_trade_no="+sn+"&WIDsubject="+subject+"&WIDtotal_fee="+rmoney;  
	    Log.v("lishide", postData);
	    webView.postUrl(ConstUtils.BASEURL + "alipay_wap/alipayapi.php", EncodingUtils.getBytes(postData, "base64"));
//		webView.postUrl(ConstUtils.BASEURL + "alipay_wap/alipayapi.php",postData.getBytes());
	
	}
}
