package com.mojie.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.network.ConnectPHPToUpLoadFile;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.MyMoreBt;
import com.mojie.view.XListViewChat.IXListViewChatListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatActivity extends Activity implements IXListViewChatListener,OnClickListener {

	public static boolean isForeground = false;
	
	ArrayList<HashMap<String, Object>> chatList = null;
	String[] from = { "image", "text", "time" };
	int[] to = { R.id.chatlist_image_me, R.id.chatlist_text_me,
			R.id.tv_chat_me_time, R.id.chatlist_image_other,
			R.id.chatlist_text_other, R.id.tv_chat_other_time };
	int[] layout = { R.layout.chat_listitem_me, R.layout.chat_listitem_other };
	String userQQ = null;
	/**
	 * 这里两个布局文件使用了同一个id，测试一下是否管用 TT事实证明这回产生id的匹配异常！所以还是要分开。。
	 * 
	 * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲 先暂时使用一个头像
	 */

	public final static int OTHER = 1;
	public final static int ME = 0;

	// protected ListView chatListView=null;
	private com.mojie.view.XListViewChat chatListView;
	protected Button chatSendButton = null;
	protected EditText editText = null;
	private ImageView btnChatBack;
	private ImageButton chatBottomMore;
	private ProgressDialog pd;
	public int start_pos;
	public int list_num;
	protected MyChatAdapter adapter = null;
	private int cid;
	private SharedPreferences shared;
	private int myuid;
	private int touid;
	private int midwindows_height;

	private ImageView btnChatShang;

	private String URL_READNUM = ConstUtils.BASEURL + "updatamsgreadnum.php";
	protected int readnumResult;
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler readnumHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					readnumResult = ((JSONObject) msg.obj).getInt("result");
					if (readnumResult == 0) {
						
						} else if (result == 1) {
							
						}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			finish();
			super.handleMessage(msg);
		};

	};
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ArrayList<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
			params3.add(new BasicNameValuePair("cid", "" + cid));
			params3.add(new BasicNameValuePair("uid", "" + myuid));
			new Thread(new ConnectPHPToGetJSON(URL_READNUM, readnumHandler, params3)).start();
		}
		return true;
	}
	
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
            	Toast.makeText(ChatActivity.this, "附件下载成功",Toast.LENGTH_LONG).show();
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
            	Toast.makeText(ChatActivity.this, "文件已存在",Toast.LENGTH_SHORT).show();
            	it = new Intent(Intent.ACTION_VIEW); 
            	it.setDataAndType(Uri.fromFile(apkFile), "image/*"); 
            	startActivity(it);
            	break;
            default:  
                break;  
            }  
        };  
    };
	
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

	protected String attachName;

	private String fromuid;

	private String username;

	private String headpic;

	private TextView tvChatShang;  
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
                    URL url = new URL(ConstUtils.COMMUNICATION_ATTACH_URL+attachName);  
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_chat);
		shared = getSharedPreferences("userInfo", 0);
		myuid = shared.getInt("id", 0);

		Intent intent = getIntent();
		cid = Integer.parseInt(intent.getStringExtra("id"));
		touid = Integer.parseInt(intent.getStringExtra("touid"));
		fromuid = intent.getStringExtra("fromuid");
		username = intent.getStringExtra("username");
		headpic = intent.getStringExtra("headpic");
		chatList = new ArrayList<HashMap<String, Object>>();
		// addTextToList("333333", OTHER);
		// addTextToList("444444^_^", OTHER);

		btnChatBack = (ImageView) findViewById(R.id.btn_chat_back);
		btnChatBack.setOnClickListener(this);
		
		btnChatShang = (ImageView) findViewById(R.id.ibtn_chat_people);
		btnChatShang.setOnClickListener(this);
		tvChatShang = (TextView) findViewById(R.id.tv_chat_shang);
		tvChatShang.setOnClickListener(this);
		
		chatBottomMore = (ImageButton) findViewById(R.id.chat_bottom_more);
		chatBottomMore.setOnClickListener(this);

		chatSendButton = (Button) findViewById(R.id.chat_bottom_sendbutton);
		editText = (EditText) findViewById(R.id.chat_bottom_edittext);
		
		chatListView = (com.mojie.view.XListViewChat) findViewById(R.id.chat_list);
		chatListView.setPullLoadEnable(true);
		chatListView.setRefreshTime();
		chatListView.setXListViewListener(this, 1);
		chatListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;

				if(chatList.get(position).get("contenttype").toString().equals("1")){
					
					String txtString = chatList.get(position).get(from[1]).toString();
		
					attachName = txtString.substring(0);
					showDownloadDialog();
					
				}
			}
		});

		adapter = new MyChatAdapter(this, chatList, layout, from, to);

		chatSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = null;
				String createddate = getTime();
				String username = "123";
				String headpic = shared.getString("headpic", "");
				int contenttype = 0;
				/**
				 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
				 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
				 * ，并且不能发送空消息。
				 */
				content = (editText.getText() + "").toString();
				if (content.length() == 0)
					return;
				editText.setText("");
				ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
				params2.add(new BasicNameValuePair("cid", "" + cid));
				params2.add(new BasicNameValuePair("uid", "" + myuid));
				params2.add(new BasicNameValuePair("contenttype", ""
						+ contenttype));
				params2.add(new BasicNameValuePair("content", content));
				params2.add(new BasicNameValuePair("touid", "" + touid));
				new Thread(new ConnectPHPToGetJSON(URL_GETSENDMSG, sHandler,
						params2)).start();
				addTextToList(content, createddate, username, headpic,
						contenttype, ME);
				
				/**
				 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
				 */
				// adapter.notifyDataSetChanged();
				// chatListView.setSelection(chatList.size()-1);
			}
		});

		// chatListView.setAdapter(adapter);
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 - 50 - 93;
		
		start_pos = 0;
		list_num = 50;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("cid", "" + cid));
		params.add(new BasicNameValuePair("start_pos", "" + start_pos));
		params.add(new BasicNameValuePair("list_num", "" + list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETCHAT, handler, params))
				.start();
		pd = new ProgressDialog(ChatActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		registerMessageReceiver();
	}

	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String str = format.format(date);
		return str;
	}

	private String URL_GETCHAT = ConstUtils.BASEURL + "communicationinfo.php";
	protected int result;
	protected int total_num;
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					total_num = ((JSONObject) msg.obj).getInt("total_num");
					if (total_num != 0) {
						JSONArray mJSONArray = ((JSONObject) msg.obj)
								.getJSONArray("list");
						result = ((JSONObject) msg.obj).getInt("result");
						if (result == 0) {
							for (int i = 0; i < mJSONArray.length(); i++) {
								JSONObject jsonItem = mJSONArray
										.getJSONObject(i);
								int uid = jsonItem.getInt("uid");
								String username = jsonItem
										.getString("username");
								String headpic = jsonItem.getString("headpic");
								int contenttype = jsonItem
										.getInt("contenttype");
								String content = jsonItem.getString("content");
								String createddate = jsonItem
										.getString("createddate");

								if (uid == myuid) {
									addTextToListHead(content, createddate,
											username, headpic, contenttype, ME);
								} else {
									addTextToListHead(content, createddate,
											username, headpic, contenttype,
											OTHER);
								}

							}
							start_pos += mJSONArray.length();
							if (loadmoreFlg == true) {
								adapter.chatList = chatList;
								adapter.notifyDataSetChanged();
							} else {
								chatListView.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							}
							
							setListPosition();
						}
					} else {
						Toast.makeText(ChatActivity.this, "没有数据",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};

	private String URL_GETSENDMSG = ConstUtils.BASEURL + "sendmsg.php";
	protected int sResult;
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler sHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					sResult = ((JSONObject) msg.obj).getInt("result");
					if (sResult == 0) {
						if(total_num == 0){
							adapter.chatList = chatList;
							chatListView.setAdapter(adapter);
						}
						adapter.notifyDataSetChanged();
						
						setListPosition();
					} else {
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};

	protected void addTextToList(String text, String createddate,
			String username, String headpic, int contenttype, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("headpic", headpic);
		map.put("text", text);
		map.put("time", createddate);
		map.put("contenttype", contenttype);
		chatList.add(map);
	}

	protected void addTextToListHead(String text, String createddate,
			String username, String headpic, int contenttype, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("headpic", headpic);
		map.put("text", text);
		map.put("time", createddate);
		map.put("contenttype", contenttype);
		chatList.add(0, map);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mMessageReceiver);
	}
	private class MyChatAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String, Object>> chatList = null;
		int[] layout;
		String[] from;
		int[] to;
		private String headpic;
		private ImageLoader imageLoader;
		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
			imageLoader = ImageLoader.getInstance();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder {
//			public com.mojie.view.RoundedWebImageView imageView = null;
			public TextView textView = null;
			public TextView time = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder = new ViewHolder();
			int who = (Integer) chatList.get(position).get("person");

			convertView = LayoutInflater.from(context).inflate(
					layout[who == ME ? 0 : 1], null);
 
			holder.textView = (TextView) convertView
					.findViewById(to[who * 3 + 1]);
			holder.time = (TextView) convertView.findViewById(to[who * 3 + 2]);

			System.out.println(holder);
			System.out.println("WHYWHYWHYWHYW");
			
//			holder.imageView.setBackgroundResource((Integer) chatList.get(
//					position).get(from[0]));
			
			if(chatList.get(position).get("contenttype").toString().equals("1")){
				holder.textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
				holder.textView.getPaint().setAntiAlias(true);//抗锯齿
				 //设置斜体    
				String txtString = "附件：\n"+chatList.get(position).get(from[1]).toString();
				SpannableString sp =  new  SpannableString(txtString);     
			    sp.setSpan( new  StyleSpan(android.graphics.Typeface.BOLD_ITALIC),  0 ,  txtString.length() , Spannable.SPAN_EXCLUSIVE_INCLUSIVE);                
				holder.textView.setText(sp);
				
			}else{
				holder.textView.setText(chatList.get(position).get(from[1]).toString());
			}
			
			holder.time.setText(chatList.get(position).get(from[2]).toString());
			com.mojie.view.RoundedWebImageView imageView = (com.mojie.view.RoundedWebImageView) convertView
					.findViewById(to[who * 3 + 0]);
			headpic = ConstUtils.IMGURL + (String)chatList.get(position).get("headpic");
			imageLoader.displayImage(headpic, imageView, ConstUtils.options);
			return convertView;
		}

	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	boolean loadmoreFlg = false;
	private int chatListViewH;

	// int tmp = 1;
	@Override
	public void onLoadMore(int id) {
		if (start_pos < total_num) {
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("cid", "" + cid));
			params.add(new BasicNameValuePair("start_pos", "" + start_pos));
			params.add(new BasicNameValuePair("list_num", "" + list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETCHAT, handler, params))
					.start();
		} else {
			chatListView.setPullLoadEnable(false);
		}
	}

	// 获取屏幕高度
	private int getDisplayMetricsHeight() {
		int i = getWindowManager().getDefaultDisplay().getWidth();
		int j = getWindowManager().getDefaultDisplay().getHeight();
		return Math.max(i, j);
	}
	
	private void setListPosition(){
		chatListViewH = ListViewSetHeightUtil.getXListViewHeight(chatListView);
		chatListView.setSelection(chatList.size()-1);
		if(midwindows_height < chatListViewH){
			chatListView.setStackFromBottom(true);
		}else{
			chatListView.setStackFromBottom(false);
		}
	}
	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}
	
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.mojie.activity.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String title = intent.getStringExtra(KEY_TITLE);
//              String extras = intent.getStringExtra(KEY_EXTRAS);
//              StringBuilder showMsg = new StringBuilder();
//              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//              if (!ExampleUtil.isEmpty(extras)) {
//            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//              }
              
				try {
					JSONObject jsonObject = new JSONObject(messge);
					if(cid == jsonObject.getInt("cid")){
						ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			  		    params.add(new BasicNameValuePair("cid", "" + cid));
			  		    new Thread(new ConnectPHPToGetJSON(URL_GETNEWCHAT, newChathandler, params)).start();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}
	
	private String URL_GETNEWCHAT = ConstUtils.BASEURL + "getnewestcommunicationinfo.php";
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler newChathandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					result = ((JSONObject) msg.obj).getInt("result");
					if (result == 0) {
						int uid = ((JSONObject) msg.obj).getInt("uid");
						String username = ((JSONObject) msg.obj).getString("username");
						String headpic = ((JSONObject) msg.obj).getString("headpic");
						int contenttype = ((JSONObject) msg.obj).getInt("contenttype");
						String content = ((JSONObject) msg.obj).getString("content");
						String createddate = ((JSONObject) msg.obj).getString("createddate");

						addTextToList(content, createddate,username, headpic, contenttype,OTHER);

						start_pos += 1;
				
						adapter.chatList = chatList;
						adapter.notifyDataSetChanged();
						Log.v("liuchao", "Put New Msg to List");
						setListPosition();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};

	};
	
	MyMoreBt myMoreBt;

	private String timeString;

	private String filename;

	private String cutnameString;

	private Uri attachuri;
	private String path;
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_chat_back:
			ArrayList<BasicNameValuePair> params3 = new ArrayList<BasicNameValuePair>();
			params3.add(new BasicNameValuePair("cid", "" + cid));
			params3.add(new BasicNameValuePair("uid", "" + myuid));
			new Thread(new ConnectPHPToGetJSON(URL_READNUM, readnumHandler, params3)).start();
			break;
		case R.id.tv_chat_shang:
//		case R.id.ibtn_chat_people:
			if (touid == 0) {
				Toast.makeText(ChatActivity.this, "暂未指定专家,无法打赏。",Toast.LENGTH_LONG).show();
			} else {
				intent = new Intent(ChatActivity.this,MyresumePreviewActivity2.class);
				intent.putExtra("flgActivity", ""+1);
				intent.putExtra("cid", ""+cid);
				intent.putExtra("uid", ""+touid);
				intent.putExtra("username", username);
				intent.putExtra("headpic", headpic);
				startActivity(intent);
			}
			break;
		case R.id.chat_bottom_more:
			
			myMoreBt = new MyMoreBt(ChatActivity.this, "");
			myMoreBt.show();
			myMoreBt.morePic.setOnClickListener(this);
			myMoreBt.moreFile.setOnClickListener(this);
			myMoreBt.negativeCancel.setOnClickListener(this);
			break;
//		case R.id.more_camera_bt:
//			myMoreBt.dismiss();
//			Date date = new Date(System.currentTimeMillis());
//			SimpleDateFormat dateFormat = new SimpleDateFormat(
//					"'IMG'_yyyyMMddHHmmss");
//			timeString = dateFormat.format(date);
//			createSDCardDir();
//			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
//					Environment.getExternalStorageDirectory() + "/DCIM/Camera",
//					timeString + ".jpg")));
//			startActivityForResult(intent, 1);
//
//			break;
//		case R.id.more_photo_bt:
//			myMoreBt.dismiss();
//
//			intent = new Intent(Intent.ACTION_PICK, null);
//			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//					"image/*");
//			startActivityForResult(intent, 2);
//			break;
		case R.id.more_file_bt:
			myMoreBt.dismiss();
			try {
				startFileBrowser();
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(ChatActivity.this,"该手机不支持此功能",Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.more_pic_bt:
			myMoreBt.dismiss();
			try {
				intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 5);
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(ChatActivity.this,"该手机不支持此功能",Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.more_cancel_bt:
			myMoreBt.dismiss();
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
				new Thread(new ConnectPHPToUpLoadFile(attachuri.getPath(), upLoadFileHander,ConstUtils.upLoadServerUri_communication)).start();
				pd = new ProgressDialog(ChatActivity.this);
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
				new Thread(new ConnectPHPToUpLoadFile(path, upLoadPicHander,ConstUtils.upLoadServerUri_communication)).start();
				pd = new ProgressDialog(ChatActivity.this);
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
					Toast.makeText(ChatActivity.this, "文件上传成功",Toast.LENGTH_SHORT).show();
					
					String content = attachuri.getPath().substring(attachuri.getPath().lastIndexOf("/") + 1);
					String createddate = getTime();
					String username = "123";
					String headpic = shared.getString("headpic", "");
					int contenttype = 1;
					ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
					params2.add(new BasicNameValuePair("cid", "" + cid));
					params2.add(new BasicNameValuePair("uid", "" + myuid));
					params2.add(new BasicNameValuePair("contenttype", ""+ contenttype));
					params2.add(new BasicNameValuePair("content",content));
					params2.add(new BasicNameValuePair("touid", "" + touid));
					new Thread(new ConnectPHPToGetJSON(URL_GETSENDMSG, sHandler, params2)).start();
					addTextToList(content, createddate, username, headpic, contenttype, ME);
					break;
				case 1:
					Toast.makeText(ChatActivity.this, "文件上传失败",Toast.LENGTH_SHORT).show();
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
					Toast.makeText(ChatActivity.this, "文件上传成功",Toast.LENGTH_SHORT).show();
					
					String content = path.substring(path.lastIndexOf("/") + 1);
					String createddate = getTime();
					String username = "123";
					String headpic = shared.getString("headpic", "");
					int contenttype = 1;
					ArrayList<BasicNameValuePair> params2 = new ArrayList<BasicNameValuePair>();
					params2.add(new BasicNameValuePair("cid", "" + cid));
					params2.add(new BasicNameValuePair("uid", "" + myuid));
					params2.add(new BasicNameValuePair("contenttype", ""+ contenttype));
					params2.add(new BasicNameValuePair("content",content));
					params2.add(new BasicNameValuePair("touid", "" + touid));
					new Thread(new ConnectPHPToGetJSON(URL_GETSENDMSG, sHandler, params2)).start();
					addTextToList(content, createddate, username, headpic, contenttype, ME);
					break;
				case 1:
					Toast.makeText(ChatActivity.this, "文件上传失败",Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			};
		};
		
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
					Toast.makeText(ChatActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();;
				}
			}
}