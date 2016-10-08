package com.mojie.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern; 
public class InfoDetailsActivity extends Activity {

	private TextView tv_info_details_title;
	private ImageView backImg;
	private TextView infoName;
	private TextView infoDate;
	private TextView infoSee;
	private TextView infoContent;
	private int id;
	private String title;
	private String description;
	private String clicks;
	private String createddate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_details);
		
		Intent intent = getIntent();
		id = Integer.parseInt(intent.getStringExtra("id"));
		title = intent.getStringExtra("title");
		description = intent.getStringExtra("description");
		clicks = intent.getStringExtra("clicks");
		createddate = intent.getStringExtra("createddate");
		
		backImg = (ImageView) findViewById(R.id.btn_info_details_back);
		backImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		tv_info_details_title = (TextView) findViewById(R.id.tv_info_details_title);
		infoName = (TextView) findViewById(R.id.tv_info_details_name);
		infoDate = (TextView) findViewById(R.id.tv_info_details_date);
		infoSee = (TextView) findViewById(R.id.tv_info_details_see);
		infoContent = (TextView) findViewById(R.id.tv_info_details_content);
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+id));
		new Thread(new ConnectPHPToGetJSON(URL_GETNEWSCONTENT,handler,params)).start();
		
		infoName.setText(title);
		infoDate.setText(createddate);
		infoSee.setText(clicks+"人看过");
	}

	private String URL_GETNEWSCONTENT = ConstUtils.BASEURL + "getnewscontent.php";
	protected int result;
	protected String content;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(InfoDetailsActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
						content = ((JSONObject)msg.obj).getString("content");
						
						String a =content; //含html标签的字符串	      
				        java.util.regex.Pattern p_html; 
				        java.util.regex.Matcher m_html; 
				        try {	        
				        	String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式       
				             p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
				             m_html = p_html.matcher(a); 
				             a = m_html.replaceAll(""); //过滤html标签 
				             a = a.replaceAll("&nbsp;", " ");
				             infoContent.setText(a);
				        } 
				        catch(Exception e)  { 
				        	System.err.println("过滤html标签出错 " + e.getMessage()); 
				        } 
				        
					}else {
						Toast.makeText(InfoDetailsActivity.this,"获取资讯内容失败",Toast.LENGTH_SHORT).show();
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
