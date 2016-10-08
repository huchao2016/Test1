package com.mojie.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewActivity extends Activity {

	private ImageView aboutBackImg;
	private WebView webView;
	private TextView wvTitle;
	private String url;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		
		aboutBackImg = (ImageView) findViewById(R.id.btn_about_back);
		aboutBackImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		wvTitle = (TextView) findViewById(R.id.wv_title);
		wvTitle.setText(title);
		
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();       
		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
		webSettings.setJavaScriptEnabled(true);  
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
	}
}
