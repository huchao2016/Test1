package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyresumeActivity extends Activity implements OnClickListener{

	private Context context;
	private TextView myresumeAdd;
	private ImageView btnMyresumeBack;
	private com.mojie.view.RoundedWebImageView imgMyresumeHead;
	private TextView tvMyresumName;
	private TextView tvResumeAddTime;
	private TextView tvResumeUpdateTime;
	private TextView tvResumeCompletion;
	private Button btnMyresumeEdit;
	private Button btnMyresumePreview;
	
	private SharedPreferences shared;
	private int uid;
	private ProgressDialog pd;
	private String username;
	private String headpic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresume);
		context = this;
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETRESUMEINFO,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		btnMyresumeBack = (ImageView) findViewById(R.id.btn_myresume_back);
		btnMyresumeBack.setOnClickListener(this);
		
		myresumeAdd = (TextView) findViewById(R.id.tv_myresume_add);
		myresumeAdd.setOnClickListener(this);
		
		imgMyresumeHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.img_myresume_head);
		tvMyresumName = (TextView) findViewById(R.id.tv_myresume_name);
		tvMyresumName.setText(username);
		tvResumeAddTime = (TextView) findViewById(R.id.tv_resume_add_time);
		tvResumeUpdateTime = (TextView) findViewById(R.id.tv_resume_update_time);
		tvResumeCompletion = (TextView) findViewById(R.id.tv_resume_completion);
		
		btnMyresumeEdit = (Button) findViewById(R.id.btn_myresume_edit);
		btnMyresumePreview = (Button) findViewById(R.id.btn_myresume_preview);
		btnMyresumeEdit.setOnClickListener(this);
		btnMyresumePreview.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		headpic = shared.getString("headpic", "");
		if(!headpic.equals("")){
			putImg();
		}
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uid", ""+uid));
		new Thread(new ConnectPHPToGetJSON(URL_GETRESUMEINFO,handler,params)).start(); 
	}
	
	private String URL_GETRESUMEINFO = ConstUtils.BASEURL + "getresumemaininfo.php";
	protected int result;
	public ArrayList<HashMap<String, String>>mArray;
	protected int resume_num;
	protected String createddate;
	protected String updateddate ;
	protected String percent;
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
						resume_num = ((JSONObject)msg.obj).getInt("resume_num");
						createddate = ((JSONObject)msg.obj).getString("createddate");
						updateddate = ((JSONObject)msg.obj).getString("updateddate");
						percent = ((JSONObject)msg.obj).getString("percent");
					
						if (resume_num == 1) {
							tvResumeAddTime.setText(createddate);
							tvResumeUpdateTime.setText(updateddate);
							tvResumeCompletion.setText(percent+"%");
						}else {
							tvResumeAddTime.setText("未创建");
							tvResumeUpdateTime.setText("未更新");
							tvResumeCompletion.setText("无");
						}
						
					}
					else{
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};
	
	private void putImg(){
		 Bitmap bitmap = ConstUtils.mCache.getBitmapFromMemCache(headpic);
		 if(bitmap != null){
			 imgMyresumeHead.setImageBitmap(bitmap);
			 Log.v("liuchao"," Get Mem Cache ");
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(MyresumeActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								imgMyresumeHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(MyresumeActivity.this, headpic, bitmap);
								}
								Log.v("liuchao","========"+bitmapPath);
							}
						}
					});
			 }else{
				 imgMyresumeHead.setImageBitmap(bitmap);
				 Log.v("liuchao"," Get Disk Cache ");
			 }
		 }
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_myresume_back:
			finish();
			break;
		case R.id.tv_myresume_add:
			if(resume_num == 0){
				ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
				params2.add(new BasicNameValuePair("uid", ""+uid));
				new Thread(new ConnectPHPToGetJSON(URL_RESUMECREATE,cHandler,params2)).start(); 
				pd = new ProgressDialog(context);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.show();
			}else {
				Toast.makeText(context,"您已创建过简历",Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_myresume_edit:
			if(resume_num == 0){
				Toast.makeText(context,"您尚未创建简历",Toast.LENGTH_SHORT).show();
			} else {
				intent = new Intent(MyresumeActivity.this,MyresumeEditActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
			break;
		case R.id.btn_myresume_preview:
			if(resume_num == 0){
				Toast.makeText(context,"您尚未创建简历",Toast.LENGTH_SHORT).show();
			} else {
				intent = new Intent(MyresumeActivity.this,MyresumePreviewActivity.class);
				intent.putExtra("uid", ""+uid);
				intent.putExtra("username", username);
				intent.putExtra("headpic", headpic);
				startActivity(intent);
			}
			break;
		}
	}

	private String URL_RESUMECREATE = ConstUtils.BASEURL + "createmyresume.php";
	protected int cResult;
	protected int resume_id;
	private Handler cHandler = new Handler(){  
		private Editor editor;

		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					cResult = ((JSONObject)msg.obj).getInt("result");
					if(cResult == 0){
						resume_id = ((JSONObject)msg.obj).getInt("resume_id");
						shared = getSharedPreferences("userInfo", 0);
		        		editor = shared.edit();
		        		editor.putString("resume_id", ""+resume_id) ;
						editor.commit();
						Intent intent = new Intent(MyresumeActivity.this,MyresumeAddActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
					}
					else{
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};
	};
	
	
	
}
