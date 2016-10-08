package com.mojie.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.activity.RegisterActivity;
import com.mojie.network.ConnectPHPToGetJSON;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MsgCheckUtil {
	private Handler myHandlers;
	
	public  int DEFAULT_CODE_LENGTH = 4;
	public  String DEFAULT_CONTENTS_HEAD = "您的验证码：";
	public  String DEFAULT_CONTENTS_FOOT = "【模具世界】";
	public  String URL_MSGPLAT = "http://sh2.ipyy.com/smsJson.aspx";
	
	public  String MSG_ACCOUNT = "hpwcs10";
	public  String MSG_PSW = "hpwcs10";
	public  String MSG_SENDTIME = "";
	public  String MSG_ACTION = "send";
	public  String MSG_EXTNO = "";
	
	private int codeLength = DEFAULT_CODE_LENGTH;
	private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9' };
	private Random random = new Random();
	private String mCode;
	private String mMsgContent;
	private String mobile;
	
	public MsgCheckUtil(Handler myHandlers,String mobile){
		this.myHandlers = myHandlers;
		this.mobile = mobile;
	}
	
	public  String URL_CONFIG = ConstUtils.BASEURL + "getMsgAccount.php";
	private Handler cfgHandlers = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Message msgtemp = new Message();
				msgtemp.obj = "";
				myHandlers.sendMessage(msgtemp);
				Log.v("liuchao", "短信发送失败");
			} else {
				try {
					int result = ((JSONObject) msg.obj).getInt("result");
					if(result == 0){
						MSG_ACCOUNT = ((JSONObject) msg.obj).getString("SMSAccount");
						MSG_PSW = ((JSONObject) msg.obj).getString("SMSPassword");
						Log.v("liuchao", "MSG_ACCOUNT:"+MSG_ACCOUNT+"MSG_PSW:"+MSG_PSW);
						
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("userid", "Mojie"));
						params.add(new BasicNameValuePair("account", MSG_ACCOUNT));
						params.add(new BasicNameValuePair("password", MSG_PSW));
						params.add(new BasicNameValuePair("mobile", mobile));
						params.add(new BasicNameValuePair("content", mMsgContent));
						params.add(new BasicNameValuePair("sendTime", MSG_SENDTIME));
						params.add(new BasicNameValuePair("action", MSG_ACTION));
						params.add(new BasicNameValuePair("extno", MSG_EXTNO));
						new Thread(new ConnectPHPToGetJSON(URL_MSGPLAT, msgHandlers, params)).start();
					}else{
						Message msgtemp = new Message();
						msgtemp.obj = "";
						myHandlers.sendMessage(msgtemp);
						Log.v("liuchao", "短信发送失败");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};
	};
	
	private Handler msgHandlers = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Message msgtemp = new Message();
				msgtemp.obj = "";
				myHandlers.sendMessage(msgtemp);
				Log.v("liuchao", "短信发送失败");
			} else {
				try {
					String returnstatus = ((JSONObject) msg.obj).getString("returnstatus");
					String message = ((JSONObject) msg.obj).getString("message");
					String remainpoint = ((JSONObject) msg.obj).getString("remainpoint");
					String taskID = ((JSONObject) msg.obj).getString("taskID");
					String successCounts = ((JSONObject) msg.obj).getString("successCounts");
					
					Log.v("liuchao", "message:"+message+"remainpoint:"+remainpoint+"taskID:"+taskID+"successCounts:"+successCounts);
					
					if(returnstatus.equals("Success")){
						Log.v("liuchao", "短信发送成功");
						
						Message msgtemp = new Message();
						msgtemp.obj = mCode;
						myHandlers.sendMessage(msgtemp);
					}else{
						Message msgtemp = new Message();
						msgtemp.obj = "";
						myHandlers.sendMessage(msgtemp);
						
						Log.v("liuchao", "短信发送失败");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			super.handleMessage(msg);
		};

	};
	
	public void sendMsgReq(){
		makeContents();
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("userid", "Mojie"));
		new Thread(new ConnectPHPToGetJSON(URL_CONFIG, cfgHandlers, params)).start();
		
	}
	
	private void makeContents(){
		mCode = createCode();
		mMsgContent = DEFAULT_CONTENTS_HEAD+mCode+DEFAULT_CONTENTS_FOOT;
	}
	
	// 验证码
	private String createCode() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			buffer.append(CHARS[random.nextInt(CHARS.length)]);
		}
		return buffer.toString();
	}
}
