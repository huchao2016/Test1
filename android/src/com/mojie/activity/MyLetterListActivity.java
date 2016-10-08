package com.mojie.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.adapter.MyLetterReceivedListAdapter;
import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.XListView;
import com.mojie.view.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyLetterListActivity extends Activity implements IXListViewListener,OnClickListener {
	ArrayList<HashMap<String, Object>> chatList = null;
	String[] from = { "image", "text", "time" };
	int[] to = { R.id.chatlist_image_me, R.id.chatlist_text_me,
			R.id.tv_chat_me_time, R.id.chatlist_image_other,
			R.id.chatlist_text_other, R.id.tv_chat_other_time };
	int[] layout = { R.layout.chat_listitem_me, R.layout.chat_listitem_other };
	
	public final static int OTHER = 1;
	public final static int ME = 0;
	
	private Context context;
	private MyLetterReceivedListAdapter listAdapter;
	private ProgressDialog pd;
	private com.mojie.view.XListView myletterReceivedListView;
	public int start_pos;
	public int list_num;
	private ImageView backImg;
	private Button btnMyletterReceived;
	private Button btnMyletterFeedback;
	
	private SharedPreferences shared;
	private int uid;
	private LinearLayout feedbackBottomLinear;
	private ImageButton chatBottomMore;
	private Button chatSendButton;
	private EditText editText;
	private ListView myletterFeedbackListview;
	private MyChatAdapter feedbackListAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myletter);
		context = this;
		shared = context.getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);

		mArray = new ArrayList<HashMap<String,String>>();
		
		backImg = (ImageView) findViewById(R.id.btn_myletter_back);
		btnMyletterReceived = (Button) findViewById(R.id.btn_myletter_received);
		btnMyletterFeedback = (Button) findViewById(R.id.btn_myletter_feedback);
		
		backImg.setOnClickListener(this);
		btnMyletterReceived.setOnClickListener(this);
		btnMyletterFeedback.setOnClickListener(this);
		
		myletterFeedbackListview = (ListView)findViewById(R.id.myletter_feedback_listview);
		chatList = new ArrayList<HashMap<String, Object>>();
		addTextToList("模界团队：\n聊聊您的想法和建议，我们会不断改进。", OTHER);
		feedbackListAdapter = new MyChatAdapter(this, chatList, layout, from, to);
		myletterFeedbackListview.setAdapter(feedbackListAdapter);
		
		myletterReceivedListView = (com.mojie.view.XListView)findViewById(R.id.myletter_received_listview);
		myletterReceivedListView.setPullLoadEnable(true);
		myletterReceivedListView.setRefreshTime();
		myletterReceivedListView.setXListViewListener(this,1);
		listAdapter = new MyLetterReceivedListAdapter(MyLetterListActivity.this, mArray);
		myletterReceivedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				position = position - 1;
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,MyLetterChatActivity.class);
				intent.putExtra("pmsid", (String)mArray.get(position).get("id"));
				startActivity(intent);
			}
		});
		
		feedbackBottomLinear = (LinearLayout) findViewById(R.id.feedback_bottom_linear);
		chatBottomMore = (ImageButton) findViewById(R.id.chat_bottom_more);
		chatBottomMore.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				MyMoreBt mMoreBt;
//				mMoreBt = new MyMoreBt(MyLetterListActivity.this, "");
//				mMoreBt.show();
			}
		});

		editText = (EditText) findViewById(R.id.chat_bottom_edittext);
		chatSendButton = (Button) findViewById(R.id.chat_bottom_sendbutton);
		chatSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = null;
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
				params2.add(new BasicNameValuePair("fromuid", ""+uid));
				params2.add(new BasicNameValuePair("touid", "0"));
				params2.add(new BasicNameValuePair("content", content));
				new Thread(new ConnectPHPToGetJSON(URL_CREATEPMS, fHandler,
						params2)).start();
				addTextToList(content, ME);
			}
		});
		setStatus(1);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadmoreFlg = false;
		int windows_height = getDisplayMetricsHeight();
		midwindows_height = windows_height - 60 -30 - 93;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		listAdapter.mArray = mArray;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+uid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETPMSLIST,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	protected void addTextToList(String text,int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("image", who == ME ? R.drawable.logo : R.drawable.logo);
		map.put("text", text);
		chatList.add(map);
	}
	
	private String URL_GETPMSLIST = ConstUtils.BASEURL + "getpmslistinfo.php";
	protected int result;
	protected int total_num;
	public ArrayList<HashMap<String, String>>mArray;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null){//获取数据失败
				Toast.makeText(MyLetterListActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					total_num = ((JSONObject)msg.obj).getInt("total_num");
					if (total_num != 0) {
						JSONArray mJSONArray = ((JSONObject)msg.obj).getJSONArray("list");
						result = ((JSONObject)msg.obj).getInt("result");
						if(result == 0){
							for(int i =  0 ; i < mJSONArray.length(); i++)
			                {
								JSONObject jsonItem = mJSONArray.getJSONObject(i);
								 int id = jsonItem.getInt("id");
								 int fromuid = jsonItem.getInt("fromuid");
								 int touid = jsonItem.getInt("touid");
								 String title = jsonItem.getString("title");
								 String content = jsonItem.getString("content");
								 String createddate = jsonItem.getString("createddate");
								 int qa_num = jsonItem.getInt("qa_num");
								 String username = jsonItem.getString("username");
								 String headpic = jsonItem.getString("headpic");

								 HashMap<String, String> map = new HashMap<String, String>();
								 map.put("id", ""+id);
								 map.put("fromuid", ""+fromuid);
								 map.put("touid", ""+touid);
								 map.put("title", title);
								 map.put("content", content);
								 map.put("createddate", createddate);
								 map.put("qa_num", ""+qa_num);
								 map.put("username", username);
								 map.put("headpic", headpic);
								 mArray.add(map);
			                }
							start_pos += mJSONArray.length();
							if(loadmoreFlg == true){
								listAdapter.mArray = mArray;
								listAdapter.notifyDataSetChanged();
							}else{
								myletterReceivedListView.setAdapter(listAdapter);
							}
							if (mArray.size() == total_num) {
								myletterReceivedListView.setPullLoadEnable(false);
							}
//							setListPosition(myletterReceivedListView,mArray);
						}
					}else {
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		};
	};

	private String URL_CREATEPMS = ConstUtils.BASEURL + "createpms.php";
	private Handler fHandler = new Handler() {
		private int fResult;
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyLetterListActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
					try {
						fResult = ((JSONObject) msg.obj).getInt("result");
						if (fResult == 0) {
							Toast.makeText(MyLetterListActivity.this, "反馈成功",
									Toast.LENGTH_SHORT).show();
							setStatus(1);
						} else if (fResult == 1) {
							Toast.makeText(MyLetterListActivity.this, "反馈失败",
									Toast.LENGTH_SHORT).show();
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
	
	private class MyChatAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String, Object>> chatList = null;
		int[] layout;
		String[] from;
		int[] to;

		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
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
			public com.mojie.view.RoundedWebImageView imageView = null;
			public TextView textView = null;
			public TextView time = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			int who = (Integer) chatList.get(position).get("person");

			convertView = LayoutInflater.from(context).inflate(
					layout[who == ME ? 0 : 1], null);
			holder = new ViewHolder();
			holder.imageView = (com.mojie.view.RoundedWebImageView) convertView
					.findViewById(to[who * 3 + 0]);
			holder.textView = (TextView) convertView
					.findViewById(to[who * 3 + 1]);
			holder.time = (TextView) convertView.findViewById(to[who * 3 + 2]);

			System.out.println(holder);
			System.out.println("WHYWHYWHYWHYW");
			System.out.println(holder.imageView);
			holder.imageView.setBackgroundResource((Integer) chatList.get(
					position).get(from[0]));
			holder.textView.setText(chatList.get(position).get(from[1])
					.toString());
			holder.time.setVisibility(View.GONE);;

			return convertView;
		}

	}
	
	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadmoreFlg = true;
		start_pos = 0;
		list_num = 50;
		mArray.clear();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", ""+uid));
		params.add(new BasicNameValuePair("start_pos", ""+start_pos));
		params.add(new BasicNameValuePair("list_num", ""+list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETPMSLIST,handler,params)).start(); 
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	boolean loadmoreFlg = false;
	private int midwindows_height;
	
	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if(start_pos < total_num){
			loadmoreFlg = true;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", ""+uid));
			params.add(new BasicNameValuePair("start_pos", ""+start_pos));
			params.add(new BasicNameValuePair("list_num", ""+list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETPMSLIST,handler,params)).start(); 
		}else{
			myletterReceivedListView.setPullLoadEnable(false);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_myletter_back:
			finish();
			break;
		case R.id.btn_myletter_received:
			setStatus(1);
			break;
		case R.id.btn_myletter_feedback:
			setStatus(2);
			break;
		}
	}
	
	private void setStatus(int status) {
		if (status == 1) {
		
		btnMyletterReceived.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.letter_received_selected));
		btnMyletterFeedback.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.letter_feedback_unselected));
			
		myletterReceivedListView.setVisibility(View.VISIBLE);
		myletterFeedbackListview.setVisibility(View.GONE);
		feedbackBottomLinear.setVisibility(View.GONE);
		} else if (status == 2) {
			btnMyletterFeedback.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.letter_feedback_selected));
			btnMyletterReceived.setBackgroundDrawable(this.getResources()
					.getDrawable(R.drawable.letter_received_unselected));
			
			myletterReceivedListView.setVisibility(View.INVISIBLE);
			myletterFeedbackListview.setVisibility(View.VISIBLE);
			feedbackBottomLinear.setVisibility(View.VISIBLE);
		}

	}
	
	// 获取屏幕高度
	private int getDisplayMetricsHeight() {
		int i = this.getWindowManager().getDefaultDisplay().getWidth();
		int j = this.getWindowManager().getDefaultDisplay().getHeight();
		return Math.max(i, j);
	}
	
	private void setListPosition(XListView xListView,ArrayList<HashMap<String, String>> alist){
		int ListViewH = ListViewSetHeightUtil.getXListViewHeight(xListView);
		xListView.setSelection(0);
		if(midwindows_height < ListViewH){
			xListView.setPullLoadEnable(true);
		}else{
			xListView.setPullLoadEnable(false);
		}
	}
}
