package com.mojie.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.DemandListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

public class TaskDetailsActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText etTaskCharge;
	private EditText etTaskDetailsExplain;
	private Button btnTaskDetailsSubmit;
	private TextView tvTaskDetailsLabel;
	private TextView tvTaskDetailsTitle;
	private TextView tvTaskDetailsBudget;
	private TextView tvTaskDetailsDelivery;
	private TextView tvTaskDetailsOverview;
	private TextView tvTaskDetailsFabaofang;
	private TextView tvTaskDetailsMobile;
	private TextView tvTaskDetailsEmail;
	private TextView tvTaskDetailsQq;
	private TextView tvTaskDetailsSubmit;
	private EditText etTaskDate;
	private TextView tvTaskDetailsCity;
	private String city;
	private int taskid;
	private String task_type;
	private String title;
	private String budget;
	private String deliverieddate;
	private String description;
	private final static int DATE_DIALOG = 0;
	private Calendar c = null;
	
	private ProgressDialog pd;
	private SharedPreferences shared;
	private int uid;
	private ListView demandListView;
	private DemandListAdapter listAdapter;
	protected String attachName;
	protected int progress; 
    /* 下载中 */  
    private static final int DOWNLOAD = 1;  
    /* 下载结束 */  
    private static final int DOWNLOAD_FINISH = 2; 
    private static final int DOWNLOAD_CANCEL = 3; 
    private static final int DOWNLOAD_EXIST = 4; 
    
    private File apkFile;
    
    private boolean cancelUpdate = false;
    private Handler mHandler = new Handler()  
    {
//        private File file;
		public void handleMessage(Message msg)  
        { 
			Intent it;
            switch (msg.what)  
            {  
            // 正在下载  
            case DOWNLOAD:
                // 设置进度条位置  
            	progressDialog.setProgress(progress);  
                break;  
            case DOWNLOAD_FINISH:  
                // 安装文件  
            	Toast.makeText(TaskDetailsActivity.this, "附件下载成功",Toast.LENGTH_SHORT).show();
            	Log.v("liuchao", "attach has been download");
            	it = new Intent(Intent.ACTION_VIEW); 
            	it.setDataAndType(Uri.fromFile(apkFile), "image/*"); 
            	startActivity(it);
                break;  
            case DOWNLOAD_CANCEL:
            	finish();
            	cancelUpdate = true;
            	break;
            case DOWNLOAD_EXIST:
            	Toast.makeText(TaskDetailsActivity.this, "文件已存在",Toast.LENGTH_SHORT).show();
            	it = new Intent(Intent.ACTION_VIEW); 
            	it.setDataAndType(Uri.fromFile(apkFile), "image/*"); 
            	startActivity(it);
            	break;
            default:  
                break;  
            }  
        };  
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		
		Intent intent = getIntent();
		taskid = Integer.parseInt(intent.getStringExtra("task_id"));
		task_type = intent.getStringExtra("task_type");
		title = intent.getStringExtra("title");
		budget = intent.getStringExtra("budget");
		deliverieddate = intent.getStringExtra("deliverieddate");
		city = intent.getStringExtra("city");
		description = intent.getStringExtra("description");
		
		rArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_task_details_back);
		backImg.setOnClickListener(this);
		
		tvTaskDetailsLabel = (TextView) findViewById(R.id.tv_task_details_label);
		tvTaskDetailsLabel.setText(task_type);
		tvTaskDetailsTitle = (TextView) findViewById(R.id.tv_task_details_title);
		tvTaskDetailsTitle.setText(title);
		tvTaskDetailsBudget = (TextView) findViewById(R.id.tv_task_details_budget);
		tvTaskDetailsBudget.setText(budget);
		tvTaskDetailsDelivery = (TextView) findViewById(R.id.tv_task_details_delivery);
		String delivery = deliverieddate.substring(0,10);
		tvTaskDetailsDelivery.setText(delivery);
		tvTaskDetailsCity = (TextView) findViewById(R.id.tv_task_details_city);
		tvTaskDetailsCity.setText(city);
		tvTaskDetailsOverview = (TextView) findViewById(R.id.tv_task_details_overview);
		tvTaskDetailsOverview.setText(description);

		tvTaskDetailsFabaofang = (TextView) findViewById(R.id.tv_task_details_fabaofang);
		tvTaskDetailsMobile = (TextView) findViewById(R.id.tv_task_details_mobile);
		tvTaskDetailsEmail = (TextView) findViewById(R.id.tv_task_details_email);
		tvTaskDetailsQq = (TextView) findViewById(R.id.tv_task_details_qq);
		etTaskCharge = (EditText) findViewById(R.id.et_task_charge);
		etTaskDate = (EditText) findViewById(R.id.et_task_date);
		etTaskDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
		etTaskDetailsExplain = (EditText) findViewById(R.id.et_task_details_explain);
		
		btnTaskDetailsSubmit = (Button) findViewById(R.id.btn_task_details_submit);
		btnTaskDetailsSubmit.setOnClickListener(this);
		tvTaskDetailsSubmit = (TextView) findViewById(R.id.tv_task_details_submit);
		tvTaskDetailsSubmit.setOnClickListener(this);
		
		demandListView = (ListView)findViewById(R.id.task_details_demand_listview);
		listAdapter = new DemandListAdapter(TaskDetailsActivity.this, rArray);
		demandListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				attachName = (String)rArray.get(position).get("filename");
				showDownloadDialog(); 
			}
		});
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("taskid", ""+taskid));
		new Thread(new ConnectPHPToGetJSON(URL_TENDERDETAILINFO,dHandler,params)).start(); 
		
	}
	
	private ProgressDialog progressDialog;
	
	private void showDownloadDialog()  
    {  
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("请稍等...");
	    progressDialog.setTitle("下载中");
	    progressDialog.setProgress(0);
	    progressDialog.setMax(100);
	    progressDialog.setCanceledOnTouchOutside(false);
	    progressDialog.setCancelable(false);
	    progressDialog.show();
	    
        // 下载文件  
        downloadAttach();  
    }
	private void downloadAttach()  
    {  
        // 启动新线程下载软件  
        new downloadApkThread().start();  
    }
	
	/* 下载保存路径 */  
    private String mSavePath;  
	private class downloadApkThread extends Thread  
    {  
       

		@Override  
        public void run()  
        {  
            try  
            {  
                // 判断SD卡是否存在，并且是否具有读写权限  
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))  
                {  
                    // 获得存储卡的路径  
                    String sdpath = Environment.getExternalStorageDirectory() + "/";  
                    mSavePath = sdpath + "download";  
                    URL url = new URL(ConstUtils.TASK_ATTACH_URL+attachName);  
                    // 创建连接  
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
                    conn.connect();  
                    // 获取文件大小  
                    int length = conn.getContentLength();  
                    // 创建输入流  
                    InputStream is = conn.getInputStream();  
  
                    File file = new File(mSavePath);  
                    // 判断文件目录是否存在  
                    if (!file.exists())  
                    {  
                        file.mkdir();  
                    }  
//                    File 
                    apkFile = new File(mSavePath, attachName);  
                    if(apkFile.exists()){
                    	mHandler.sendEmptyMessage(DOWNLOAD_EXIST); 
                    }else{
	                    FileOutputStream fos = new FileOutputStream(apkFile);  
	                    int count = 0;  
	                    // 缓存  
	                    byte buf[] = new byte[1024];  
	                    // 写入到文件中  
	                    do  
	                    {  
	                        int numread = is.read(buf);  
	                        count += numread;  
	                        // 计算进度条位置  
	                        progress = (int) (((float) count / length) * 100);  
	                        // 更新进度  
	                        mHandler.sendEmptyMessage(DOWNLOAD);  
	                        if (numread <= 0)  
	                        {  
	                            // 下载完成  
	                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);  
	                            break;  
	                        }  
	                        // 写入文件  
	                        fos.write(buf, 0, numread);  
	                    } while (!cancelUpdate);// 点击取消就停止下载.  
	                    fos.close();  
	                    is.close();  
                    }
                }  
            } catch (MalformedURLException e)  
            {  
                e.printStackTrace();  
            } catch (IOException e)  
            {  
                e.printStackTrace();  
            }  
            // 取消下载对话框显示  
            progressDialog.dismiss();  
        }  
    };
	
	private String URL_TENDERDETAILINFO = ConstUtils.BASEURL + "tenderdetailinfo.php";
	protected int dResult;
	private String username;
	protected String mobile;
	protected String email;
	protected String qq;
	protected int total_num;
	public ArrayList<HashMap<String, String>>rArray;
	
	private Handler dHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(TaskDetailsActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {					
					dResult = ((JSONObject)msg.obj).getInt("result");
					if(dResult == 0){
						username = ((JSONObject)msg.obj).getString("username");
						mobile = ((JSONObject)msg.obj).getString("mobile");
						email = ((JSONObject)msg.obj).getString("email");
						qq = ((JSONObject)msg.obj).getString("qq");
						total_num = ((JSONObject)msg.obj).getInt("total_num");
						
						tvTaskDetailsFabaofang.setText(username);
						tvTaskDetailsMobile.setText(mobile);
						tvTaskDetailsEmail.setText(email);
						tvTaskDetailsQq.setText(qq);
						 
						if( total_num != 0 ){
							JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 String filename = jsonItem.getString("filename");
								 String filetype = jsonItem.getString("filetype");
								 String filesize = jsonItem.getString("filesize");
								 String filepath = jsonItem.getString("filepath");
								 
								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("filename", filename);
								 map.put("filetype", filetype);
								 map.put("filesize", filesize);
								 map.put("filepath", filepath);
								 rArray.add(map);
			                }
							listAdapter.mArray = rArray;
							demandListView.setAdapter(listAdapter);
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(demandListView);
						}
						
					}else{
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.handleMessage(msg);
		};
	};
	
	private String URL_SUBMITTENDER = ConstUtils.BASEURL + "submittender.php";
	private Handler tHandler = new Handler(){  
        private int tResult;
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(TaskDetailsActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
			}else{
				try {
					tResult = ((JSONObject)msg.obj).getInt("result");
					if(tResult == 0){
						Toast.makeText(TaskDetailsActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
						finish();
					}else if(tResult == 1){
						Toast.makeText(TaskDetailsActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
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
	
	private String quote;
	private String taskDate;
	private String taskDescription;	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.btn_task_details_back:
			finish();
			break;
		case R.id.tv_task_details_submit:
		case R.id.btn_task_details_submit:
			quote = etTaskCharge.getText().toString();
//			taskDate = etTaskDate.getText().toString();
			taskDescription = etTaskDetailsExplain.getText().toString();
			
			if("".equals(quote)) {
				Toast.makeText(TaskDetailsActivity.this,"请输入任务报价",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(deliverieddate)) {
				Toast.makeText(TaskDetailsActivity.this,"请输入承诺交付日期",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(taskDescription)) {
				Toast.makeText(TaskDetailsActivity.this,"请输入竞标说明",Toast.LENGTH_LONG).show();
				break;
			}
			pd = new ProgressDialog(TaskDetailsActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("taskid", ""+taskid));
			params.add(new BasicNameValuePair("quote", quote));
			params.add(new BasicNameValuePair("deliverieddate", deliverieddate));
			params.add(new BasicNameValuePair("description", taskDescription));
    		new Thread(new ConnectPHPToGetJSON(URL_SUBMITTENDER,tHandler,params)).start(); 
			break;
		}
	}

	/**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case DATE_DIALOG:
            c = Calendar.getInstance();
            dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
//                    	etFabaoDate.setText(year + "" + (month+1) + "" + dayOfMonth + "");
                    	deliverieddate = new StringBuilder().append(year + "" + format((month+1)) + "" + format(dayOfMonth) + "").toString();
                    	etTaskDate.setText(new StringBuilder().append(year + "年" + format((month+1)) + "月" + format(dayOfMonth) + "日"));
                    }
                }, 
                c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );
            break;
        }
        return dialog;
    }
    
    private String format(int x) {
        if (Integer.toString(x).length() == 1) {
          return "0" + Integer.toString(x);
        } else {
          return Integer.toString(x);
        }
    }
}
