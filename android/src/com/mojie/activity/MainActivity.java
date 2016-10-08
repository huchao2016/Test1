package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.ToastView;
import com.mojie.fragment.CommunicationFragment;
import com.mojie.fragment.FabaoFragment;
import com.mojie.fragment.HomeFragment;
import com.mojie.fragment.InfoFragment;
import com.mojie.fragment.MineFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity {

	FragmentManager manager;
	List<Fragment> fragments;
	RadioGroup main_tab_RadioGroup;
	int choosed = 0;
	private SharedPreferences shared;
	private Editor editor;
	private int id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shared = getSharedPreferences("userInfo", 0);
		editor = shared.edit();
		id = shared.getInt("id", 0);
		
		main_tab_RadioGroup = (RadioGroup) findViewById(R.id.main_tab_RadioGroup);
		main_tab_RadioGroup.check(R.id.radio_home);
		initFragments();

		android.support.v4.app.FragmentTransaction transaction = manager
				.beginTransaction();
		transaction.add(R.id.container_tab, fragments.get(0));
		transaction.commit();
		main_tab_RadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						
						Intent intent;
						if (id == 0 && checkedId != R.id.radio_home && checkedId != R.id.radio_info) {
							intent = new Intent(MainActivity.this,LoginActivity.class);
							startActivity(intent);
							finish();
						} else {
							int current = 0;
							switch (checkedId) {
							case R.id.radio_home:
								current = 0;
								break;
							case R.id.radio_fabao:
								current = 1;
								break;
							case R.id.radio_communication:
								current = 2;
								break;
							case R.id.radio_info:
								current = 3;
								break;
							case R.id.radio_mine:
								current = 4;
								break;
							}
						
							if (choosed != current) {
								choosed = current;
	
								android.support.v4.app.FragmentTransaction transaction = manager
										.beginTransaction();
								transaction.replace(R.id.container_tab,
										fragments.get(choosed));
								transaction.commit();
							}
						}
					}
				});

	}
	
	public void setChanged(int current,int btnid) {
		if (choosed != current) {
			choosed = current;
			((RadioButton) findViewById(btnid))
					.setChecked(true);
			android.support.v4.app.FragmentTransaction transaction = manager
					.beginTransaction();
			transaction.replace(R.id.container_tab, fragments.get(choosed));
			transaction.commit();
		}
	}
	
	void initFragments() {
		manager = getSupportFragmentManager();
		fragments = new ArrayList<Fragment>();
		HomeFragment homeFragment = new HomeFragment();
		FabaoFragment fabaoFragment = new FabaoFragment();
		CommunicationFragment communicationFragment = new CommunicationFragment();
		InfoFragment infoFragment = new InfoFragment();
		MineFragment mineFragment = new MineFragment();
		fragments.add(homeFragment);
		fragments.add(fabaoFragment);
		fragments.add(communicationFragment);
		fragments.add(infoFragment);
		fragments.add(mineFragment);
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
				Toast.makeText(MainActivity.this, "再按一次退出APP",Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessageDelayed(0, 3000);

				return true;
			} else {
				finish();
				ToastView.cancel();
				editor.putInt("id", 0);
				editor.commit();
				return false;
			}
		}
		return true;
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

}