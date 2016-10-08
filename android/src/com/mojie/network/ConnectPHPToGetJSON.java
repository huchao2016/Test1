package com.mojie.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectPHPToGetJSON implements Runnable {
	Handler myHandler;
	String URL;
	List<BasicNameValuePair> params;
	JSONObject jsonObject;
	int connectTimeOut ;
	int readTimeOut ;
	
	public ConnectPHPToGetJSON(String URL, Handler myHandler,
			List<BasicNameValuePair> params) {

		this.myHandler = myHandler;
		this.URL = URL;
		this.params = params;
		jsonObject = null;
		connectTimeOut = 10000;//默认10秒
		readTimeOut = 10000;//默认10秒
	}

	public void setConnectTimeOut(int time){
		connectTimeOut = time;
	}
	
	public void setReadTimeOut(int time){
		readTimeOut = time;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		HttpClient httpClient = new DefaultHttpClient();
		// 设置链接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeOut);
		// 设置读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,connectTimeOut);

		HttpPost httpRequst = new HttpPost(URL);

		try {
			// 发送请求
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 得到响应
			HttpResponse response = httpClient.execute(httpRequst);

			// 返回值如果为200的话则证明成功的得到了数据
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				//将得到的数据进行解析
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					builder.append(s);
				}
				Log.v("liuchao", "JSON " + builder.toString());
				jsonObject = new JSONObject(builder.toString());

			} else {
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Message message = new Message();
		message.obj = jsonObject;

		myHandler.sendMessage(message);
	}
}
