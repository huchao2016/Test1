package com.mojie.activity;

import java.util.ArrayList;

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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NameProveActivity extends Activity implements OnClickListener {

	private ImageView proveNameBackImg;
	private com.mojie.view.RoundedWebImageView proveNameHead;
	private EditText proveNameName;
	private EditText proveNameID;
	private Button proveNameSubmit;

	private SharedPreferences shared;
	private int id;
	private ProgressDialog pd;
	private String proveName;
	private String proveID;
	private String headpic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prove_name);
		
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		proveNameName = (EditText)findViewById(R.id.prove_name_name);
		proveNameID = (EditText)findViewById(R.id.prove_name_id);
		
		proveNameBackImg = (ImageView) findViewById(R.id.prove_name_back);
		proveNameBackImg.setOnClickListener(this);
		
		proveNameHead = (com.mojie.view.RoundedWebImageView) findViewById(R.id.prove_name_head);
		
		proveNameSubmit = (Button) findViewById(R.id.btn_prove_name_submit);
		proveNameSubmit.setOnClickListener(this);
		
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
			 proveNameHead.setImageBitmap(bitmap);
		 }else{
			 bitmap = ConstUtils.mCache.getBitmapFromDiskCache(NameProveActivity.this, headpic, -1);
			// ConstUtils.mCache.addBitmapToMemCache(headpic, bitmap);
			 if(bitmap == null){
				 PHPLOADIMGUtils.onLoadImage(ConstUtils.IMGURL+headpic, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								proveNameHead.setImageBitmap(bitmap);
								
								if (bitmap != null) {
									ConstUtils.mCache.addBitmapToDiskCache(NameProveActivity.this, headpic, bitmap);
								}
							}
						}
					});
			 }else{
				 proveNameHead.setImageBitmap(bitmap);
			 }
		 }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.prove_name_back:
			finish();
			break;
		case R.id.btn_prove_name_submit:
			proveName = proveNameName.getText().toString();
			proveID = proveNameID.getText().toString();
			if("".equals(proveName)) {
				Toast.makeText(NameProveActivity.this,"请输入姓名",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(proveID)) {
				Toast.makeText(NameProveActivity.this,"请输入身份证号",Toast.LENGTH_SHORT).show();
				break;
			}
			pd = new ProgressDialog(NameProveActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", ""+id));
			params.add(new BasicNameValuePair("realname", proveName));
			params.add(new BasicNameValuePair("idcardno", proveID));
			new Thread(new ConnectPHPToGetJSON(URL_GETPROVENAME,handler,params)).start(); 
			break;
		}
	}
	
	private String URL_GETPROVENAME = ConstUtils.BASEURL + "usernamecertification.php";
	private Handler handler = new Handler(){  
        private int result;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(NameProveActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						Toast.makeText(NameProveActivity.this,"身份认证成功",Toast.LENGTH_SHORT).show();
						finish();
					}else if(result == 1){
						Toast.makeText(NameProveActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
					}else if(result == 2){
						Toast.makeText(NameProveActivity.this,"身份认证失败",Toast.LENGTH_SHORT).show();
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

}
