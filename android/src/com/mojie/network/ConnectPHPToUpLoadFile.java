package com.mojie.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.net.URL;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.mojie.utils.ConstUtils;

import android.os.Handler;
import android.util.Log;

public class ConnectPHPToUpLoadFile implements Runnable{
	private Handler myHandler;
	private String srcPath;
	private String interfaceUrl;
	public ConnectPHPToUpLoadFile(String srcPath, Handler myHandlers) {

		this.myHandler = myHandlers;
		this.srcPath = srcPath;
		
		interfaceUrl = ConstUtils.upLoadServerUri;
	}

	public ConnectPHPToUpLoadFile(String srcPath, Handler myHandlers,String interfaceUrl) {

		this.myHandler = myHandlers;
		this.srcPath = srcPath;
		
		this.interfaceUrl = interfaceUrl;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		uploadFile();
	}
	
	private void uploadFile()  
	  {  
	    String end = "\r\n";  
	    String twoHyphens = "--";  
	    String boundary = "******";  
	    try  
	    {  
	      URL url = new URL(interfaceUrl);  
	      HttpURLConnection httpURLConnection = (HttpURLConnection) url  
	          .openConnection();  
	      // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃  
	      // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。  
	     // httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
	      // 允许输入输出流  
	      httpURLConnection.setDoInput(true);  
	      httpURLConnection.setDoOutput(true);  
	      httpURLConnection.setUseCaches(false);  
	      // 使用POST方法  
	      httpURLConnection.setRequestMethod("POST");  
	      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
	      httpURLConnection.setRequestProperty("Charset", "UTF-8");  
	      httpURLConnection.setRequestProperty("Content-Type",  
	          "multipart/form-data;boundary=" + boundary);  
	  
	      DataOutputStream dos = new DataOutputStream(  
	          httpURLConnection.getOutputStream());  
	      dos.writeBytes(twoHyphens + boundary + end);  
	      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""  
	          + srcPath.substring(srcPath.lastIndexOf("/") + 1)  
	          + "\""  
	          + end);  
	      dos.writeBytes(end);  
	  
	      FileInputStream fis = new FileInputStream(srcPath);  
	      byte[] buffer = new byte[8192]; // 8k  
	      int count = 0;  
	      // 读取文件  
	      while ((count = fis.read(buffer)) != -1)  
	      {  
	        dos.write(buffer, 0, count);  
	      }  
	      fis.close();  
	  
	      dos.writeBytes(end);  
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
	      dos.flush();  
	  
	      InputStream is = httpURLConnection.getInputStream();  
	      InputStreamReader isr = new InputStreamReader(is, "utf-8");  
	      BufferedReader br = new BufferedReader(isr);  
	      String resultStr = br.readLine();  
	      
	      JSONObject jsonObject = new JSONObject(resultStr);
	      int result = jsonObject.getInt("result");
	      
	      Log.v("liuchao", "upload:"+resultStr);
	      if(result == 0){
	    	  myHandler.sendEmptyMessage(0); 
	      }else{
	    	  myHandler.sendEmptyMessage(1);
	      }

	      Log.v("liuchao", "picname:"+srcPath.substring(srcPath.lastIndexOf("/") + 1));
	      dos.close();  
	      is.close();  
	  
	    } catch (Exception e)  
	    {  
	      e.printStackTrace();  
	    //  setTitle(e.getMessage());  
	      myHandler.sendEmptyMessage(1);
	    }  
	  }
	
}
