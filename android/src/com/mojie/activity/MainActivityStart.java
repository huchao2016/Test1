package com.mojie.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivityStart extends Activity {

	boolean isFirstIn;
	public static String sMallId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_start);
		
		// 获取SharedPreferences对象
		SharedPreferences sp = MainActivityStart.this.getSharedPreferences("SP", MODE_PRIVATE);
		isFirstIn = sp.getBoolean("isFirstIn", true);
		if (isFirstIn) {
			goGuide();
			Editor editor = sp.edit();
			editor.putBoolean("isFirstIn", false);
			editor.commit();
		}
		else {
			goHome();
		}
	}

	private void goHome() {
		Intent intent = new Intent(MainActivityStart.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	private void goGuide() {
		Intent intent = new Intent(MainActivityStart.this, GuideViewPager.class);
		startActivity(intent);
		finish();
	}

}
