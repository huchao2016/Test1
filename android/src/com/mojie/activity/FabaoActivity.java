package com.mojie.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.UploadFileListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.network.ConnectPHPToUpLoadFile;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.MyUploadBt;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

public class FabaoActivity extends Activity implements OnClickListener {

	private Context context;
	private ImageView btnFabaoBackImg;
	private TextView tvFabaoLabel;
	private EditText etFabaoTitle;
	private EditText tvFabaoBudget;
	private EditText etFabaoDate;
	private EditText etFabaoOverview;
	private ImageView fabaoUploadImg;
	private TextView tvFabaoFabaofang;
	private TextView tvFabaoMobile;
	private TextView tvFabaoEmail;
	private TextView tvFabaoQq;
	private TextView tvFabaoSave;
	private Button btnFabaoSave;
	private Button btnFabaoSubmit;
	private String tagname_cn;
	private String username;
	private String mobile;
	private String email;
	private String qq;
	private ListView fabaoUploadListView;
	private UploadFileListAdapter uploadFileListAdapter;
	private int type;
	private String filename;
	
	private ProgressDialog pd;
	private int tagsid;
	private String title;
	private String budget;
	private String deliverieddate;
	private String description;
	private SharedPreferences shared;
	private int uid;
	private final static int DATE_DIALOG = 0;
	private Calendar c = null;
	
	private String URL_SUBMITTASK = ConstUtils.BASEURL + "submittask.php";
	protected int result;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(context, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
//						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						result = ((JSONObject)msg.obj).getInt("result");
						if(result == 0){
//							for(int i =  0 ; i < mJSONArray.length(); i++)
//			                {
//								 JSONObject jsonItem = mJSONArray.getJSONObject(i);
//								 String filename = jsonItem.getString("filename");
//								 String filetype = jsonItem.getString("filetype");
//								 String filesize = jsonItem.getString("filesize");
//								 
//								 HashMap<String, String> map = new HashMap<String, String>();
//								 map.put("filename", filename);
//								 map.put("filetype", filetype);
//								 map.put("filesize", filesize);
//								 map.put("title", title);
//								 mArray.add(map);
//			                }
							
							Intent intent = new Intent(context,MyfabaoListActivity.class);
							startActivity(intent);
							finish();
						}else {
							Toast.makeText(FabaoActivity.this,"发包失败",Toast.LENGTH_SHORT).show();
						}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};
	private Spinner spinnerProvince;
	private Spinner spinnerCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fabao);
		context = this;
		
		mArray = new ArrayList<HashMap<String,String>>();
		
		btnFabaoBackImg = (ImageView) findViewById(R.id.btn_fabao_back);
		btnFabaoBackImg.setOnClickListener(this);
		
//		tvFabaoSave = (TextView) findViewById(R.id.tv_fabao_save);
//		tvFabaoSave.setOnClickListener(this);
		
		tvFabaoLabel = (TextView) findViewById(R.id.tv_fabao_label);
		etFabaoTitle = (EditText) findViewById(R.id.et_fabao_title);
		tvFabaoBudget = (EditText) findViewById(R.id.et_fabao_budget);
		spinnerProvince = (Spinner) findViewById(R.id.spinner_fabao_province);
		spinnerCity = (Spinner) findViewById(R.id.spinner_fabao_city);
		
		etFabaoDate = (EditText) findViewById(R.id.et_fabao_date);
		etFabaoDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
		etFabaoOverview = (EditText) findViewById(R.id.et_fabao_overview);
		
		fabaoUploadListView = (ListView)findViewById(R.id.fabao_upload_listview);
		uploadFileListAdapter = new UploadFileListAdapter(FabaoActivity.this, mArray);
		fabaoUploadListView.setAdapter(uploadFileListAdapter);
		fabaoUploadListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				if (mArray.get(position).get("type").equals("0")){
					myUploadBt = new MyUploadBt(FabaoActivity.this, "");
					myUploadBt.show();
//					myUploadBt.uploadFile.setOnClickListener(this);
//					myUploadBt.negativeCancel.setOnClickListener(this);
					myUploadBt.uploadFile.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							myUploadBt.dismiss();
							try {
								startFileBrowser();
							} catch (Exception e) {
								// TODO: handle exception
								Log.v("lishide", "exception == "+e);
								Toast.makeText(FabaoActivity.this,"该手机不支持此功能",Toast.LENGTH_LONG).show();
							}
						}
					});
					
					myUploadBt.uploadPic.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							myUploadBt.dismiss();
							try {
								Intent intent = new Intent(Intent.ACTION_PICK, null);
								intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
								startActivityForResult(intent, 5);
							} catch (Exception e) {
								// TODO: handle exception
								Toast.makeText(FabaoActivity.this,"该手机不支持此功能",Toast.LENGTH_LONG).show();
							}
						}
					});
					myUploadBt.negativeCancel.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							myUploadBt.dismiss();
						}
					});
				}else {
					new AlertDialog.Builder(context)
		            .setTitle("提示")
		            .setMessage("请选择操作：")
		            .setNegativeButton("查看", new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog, int which) {
		                    dialog.dismiss();
		                    
		                    String filename = (String)mArray.get(position).get("filename");
		                    String demandurl = ConstUtils.TASK_ATTACH_URL + filename;
		                    Intent intent = new Intent(context,WebViewActivity.class);
		    				intent.putExtra("title", "浏览");
		    				intent.putExtra("url", demandurl);
		    				startActivity(intent);
		                }
		            })
		            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog, int which) {
		                    mArray.remove(position);
		                    uploadFileListAdapter.mArray = mArray;
							uploadFileListAdapter.notifyDataSetChanged();
							ListViewSetHeightUtil.setListViewHeightBasedOnChildren(fabaoUploadListView);
		                    dialog.dismiss();
		                }
		            })
		            .show();
				}
			}
		});
		
		tvFabaoFabaofang = (TextView) findViewById(R.id.tv_fabao_fabaofang);
		tvFabaoMobile = (TextView) findViewById(R.id.tv_fabao_mobile);
		tvFabaoEmail = (TextView) findViewById(R.id.tv_fabao_email);
		tvFabaoQq = (TextView) findViewById(R.id.tv_fabao_qq);
		
		btnFabaoSave = (Button) findViewById(R.id.btn_fabao_save);
		btnFabaoSave.setOnClickListener(this);
		btnFabaoSubmit = (Button) findViewById(R.id.btn_fabao_submit);
		btnFabaoSubmit.setOnClickListener(this);
		
		Intent intent = getIntent();
		tagsid = Integer.parseInt(intent.getStringExtra("id"));
		tagname_cn = intent.getStringExtra("tagname_cn");
		tvFabaoLabel.setText(tagname_cn);
		
		shared = context.getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		username = shared.getString("username", "");
		mobile = shared.getString("mobile", "");
		email = shared.getString("email", "");
		qq = shared.getString("qq", "");
		
		tvFabaoFabaofang.setText(username);
		tvFabaoMobile.setText(mobile);
		tvFabaoEmail.setText(email);
		tvFabaoQq.setText(qq);
		
		type = 0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", ""+type);
		map.put("filename", filename);
		mArray.add(map);
		
		List<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
		params2.add(new BasicNameValuePair("type", ""+0));
		new Thread(new ConnectPHPToGetJSON(URL_GETPROVINCE,provinceHandler,params2)).start(); 
	}
	
	private List<String> list;
	private String province;
	private String URL_GETPROVINCE = ConstUtils.BASEURL + "citydata.php";
	protected int proResult;
	public ArrayList<HashMap<String, String>>rArray;
	String[] sourceStrArray;
	private Handler provinceHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(FabaoActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {		
					proResult = ((JSONObject)msg.obj).getInt("result");
					if(proResult == 0){
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						list = new ArrayList<String>();  
						for(int i =  0 ; i < mJSONArray.length(); i++)
		                {
							String provinceName = mJSONArray.getString(i);
							list.add(provinceName);
		                }
						
						spinnerProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								province = list.get(position);
								List<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
								params3.add(new BasicNameValuePair("type", ""+1));
								params3.add(new BasicNameValuePair("province", province));
								if(citylist == null){
									citylist = new ArrayList<String>();
								}else{
									citylist.clear();
								}
								new Thread(new ConnectPHPToGetJSON(URL_GETCITY,cityHandler,params3)).start(); 
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								
							}
						});
						
				        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FabaoActivity.this,R.layout.spinner_experience, R.id.text1,list);  
				        spinnerProvince.setAdapter(adapter);
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
	
	private List<String> citylist = null;
	private String city;
	protected int cityResult;
	private String URL_GETCITY = ConstUtils.BASEURL + "citydata.php";
	
	private Handler cityHandler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(FabaoActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {		
					cityResult = ((JSONObject)msg.obj).getInt("result");
					if(cityResult == 0){
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						  
						for(int i =  0 ; i < mJSONArray.length(); i++)
		                {
							String cityName = mJSONArray.getString(i);
							citylist.add(cityName);
		                }
						
						spinnerCity.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								city = citylist.get(position);
 
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								
							}
						});
						
				        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FabaoActivity.this,R.layout.spinner_experience, R.id.text1,citylist);  
				        spinnerCity.setAdapter(adapter);
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
	MyUploadBt myUploadBt;
	private Uri attachuri;
	private String path;
	@Override
	public void onClick(View v) {
		List<BasicNameValuePair> params;
		switch(v.getId()) {
		case R.id.btn_fabao_back:
			finish();
			break;
		case R.id.btn_fabao_save:
//		case R.id.tv_fabao_save:
			title = etFabaoTitle.getText().toString();
			budget = tvFabaoBudget.getText().toString();
			description = etFabaoOverview.getText().toString();
			pd = new ProgressDialog(FabaoActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("tagsid", ""+tagsid));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("budget", budget));
			params.add(new BasicNameValuePair("deliverieddate", deliverieddate));
			params.add(new BasicNameValuePair("city", city));
			params.add(new BasicNameValuePair("description", description));
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("status", ""+0));
			params.add(new BasicNameValuePair("attach_item_num", ""+(mArray.size() - 1)));

			Log.v("lishide", "tagsid  "+tagsid);
			Log.v("lishide", "deliverieddate  "+deliverieddate);
			Log.v("lishide", "num  "+(mArray.size() - 1));
			for(int i = 0; i < (mArray.size() - 1); i++){
				params.add(new BasicNameValuePair("attach_item"+(i+1), mArray.get(i).get("filename")));
				Log.v("lishide", "item  "+mArray.get(i).get("filename"));
			}
    		new Thread(new ConnectPHPToGetJSON(URL_SUBMITTASK,handler,params)).start();
			
			break;
		case R.id.btn_fabao_submit:
			title = etFabaoTitle.getText().toString();
			budget = tvFabaoBudget.getText().toString();
			description = etFabaoOverview.getText().toString();
			if("".equals(title)) {
				Toast.makeText(FabaoActivity.this,"请输入标题",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(budget)) {
				Toast.makeText(FabaoActivity.this,"请输入预算金额",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(deliverieddate)) {
				Toast.makeText(FabaoActivity.this,"请输入交付日期",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(description)) {
				Toast.makeText(FabaoActivity.this,"请输入任务概述",Toast.LENGTH_LONG).show();
				break;
			}
			
			pd = new ProgressDialog(FabaoActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("tagsid", ""+tagsid));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("budget", budget));
			params.add(new BasicNameValuePair("deliverieddate", deliverieddate));
			params.add(new BasicNameValuePair("city", city));
			params.add(new BasicNameValuePair("description", description));
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("status", ""+1));
			params.add(new BasicNameValuePair("attach_item_num", ""+(mArray.size() - 1)));

			Log.v("lishide", "city  "+city);
			Log.v("lishide", "tagsid  "+tagsid);
			Log.v("lishide", "deliverieddate  "+deliverieddate);
			Log.v("lishide", "num  "+(mArray.size() - 1));
			for(int i = 0; i < (mArray.size() - 1); i++){
				params.add(new BasicNameValuePair("attach_item"+(i+1), mArray.get(i).get("filename")));
				Log.v("lishide", "item  "+mArray.get(i).get("filename"));
			}
    		new Thread(new ConnectPHPToGetJSON(URL_SUBMITTASK,handler,params)).start();
			
			break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 4) {
				attachuri = data.getData();
				Log.v("liuchao", "uri = "+ attachuri.getPath());
				new Thread(new ConnectPHPToUpLoadFile(attachuri.getPath(), upLoadFileHander,ConstUtils.upLoadServerUri_attach)).start();
				pd = new ProgressDialog(FabaoActivity.this);
				pd.setMessage("请稍后...");
				pd.setCanceledOnTouchOutside(false);
				pd.setCancelable(false);
				pd.show();
			}else if (requestCode == 5) {
				attachuri = data.getData();
				Log.v("liuchao", "uri = "+ attachuri.getPath());
				
				 String[] proj = {MediaStore.Images.Media.DATA};
		         //好像是android多媒体数据库的封装接口，具体的看Android文档
		         Cursor cursor = managedQuery(attachuri, proj, null, null, null); 
		         //按我个人理解 这个是获得用户选择的图片的索引值
		         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		         //将光标移至开头 ，这个很重要，不小心很容易引起越界
		         cursor.moveToFirst();
		         //最后根据索引值获取图片路径
		         path = cursor.getString(column_index);

				Log.v("lishide", "path=="+path);
				new Thread(new ConnectPHPToUpLoadFile(path, upLoadPicHander,ConstUtils.upLoadServerUri_attach)).start();
				pd = new ProgressDialog(FabaoActivity.this);
				pd.setMessage("请稍后...");
				pd.setCanceledOnTouchOutside(false);
				pd.setCancelable(false);
				pd.show();
			}
		}
	}
	
	private Handler upLoadFileHander = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case 0:
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", ""+1);
				map.put("filename", attachuri.getPath().substring(attachuri.getPath().lastIndexOf("/") + 1) );
				mArray.add(uploadFileListAdapter.getCount()-1,map);
				uploadFileListAdapter.mArray = mArray;
				uploadFileListAdapter.notifyDataSetChanged();
				ListViewSetHeightUtil.setListViewHeightBasedOnChildren(fabaoUploadListView);
				Toast.makeText(FabaoActivity.this, "文件上传成功",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(FabaoActivity.this, "文件上传失败",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
	private Handler upLoadPicHander = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case 0:
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", ""+1);
				map.put("filename", path.substring(path.lastIndexOf("/") + 1) );
				mArray.add(uploadFileListAdapter.getCount()-1,map);
				uploadFileListAdapter.mArray = mArray;
				uploadFileListAdapter.notifyDataSetChanged();
				ListViewSetHeightUtil.setListViewHeightBasedOnChildren(fabaoUploadListView);
				Toast.makeText(FabaoActivity.this, "文件上传成功",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(FabaoActivity.this, "文件上传失败",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
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
                    	etFabaoDate.setText(new StringBuilder().append(year + "年" + format((month+1)) + "月" + format(dayOfMonth) + "日"));
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
    
    private void startFileBrowser()
   	{
   		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
   		intent.setType("file/*");
   		try
   		{
   			startActivityForResult(intent, 4);
   		}
   		catch (Exception e)
   		{
   			e.printStackTrace();

   			if (android.os.Build.BRAND.equals("samsung"))
   			{
   				Intent samsungIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
   				samsungIntent.setAction("com.sec.android.app.myfiles.PICK_DATA");
   				samsungIntent.putExtra("CONTENT_TYPE", "*/*");
   				try
   				{
   					startActivityForResult(samsungIntent, 4);
   					return;
   				}
   				catch (Exception e_samsung)
   				{
   					e.printStackTrace();
   				}
   			}
   			
   			// 提示安装文件管理器
   			Toast.makeText(context, "请安装文件管理器", Toast.LENGTH_SHORT).show();;
   		}
   	}
}
