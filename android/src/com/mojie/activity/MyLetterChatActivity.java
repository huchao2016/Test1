package com.mojie.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;
import com.mojie.utils.ListViewSetHeightUtil;
import com.mojie.view.XListViewChat.IXListViewChatListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyLetterChatActivity extends Activity implements IXListViewChatListener {

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
	private int pmsid;
	private SharedPreferences shared;
	private int myuid;
	private int midwindows_height;
	private ImageView chatPeople;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		shared = getSharedPreferences("userInfo", 0);
		myuid = shared.getInt("id", 0);

		Intent intent = getIntent();
		pmsid = Integer.parseInt(intent.getStringExtra("pmsid"));

		chatList = new ArrayList<HashMap<String, Object>>();
		// addTextToList("333333", OTHER);
		// addTextToList("444444^_^", OTHER);

		btnChatBack = (ImageView) findViewById(R.id.btn_chat_back);
		btnChatBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		chatPeople = (ImageView) findViewById(R.id.ibtn_chat_people);
		chatPeople.setVisibility(View.INVISIBLE);
		chatBottomMore = (ImageButton) findViewById(R.id.chat_bottom_more);
		chatBottomMore.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				MyMoreBt mMoreBt;
//				mMoreBt = new MyMoreBt(MyLetterChatActivity.this, "");
//				mMoreBt.show();
			}
		});

		chatSendButton = (Button) findViewById(R.id.chat_bottom_sendbutton);
		editText = (EditText) findViewById(R.id.chat_bottom_edittext);
		chatListView = (com.mojie.view.XListViewChat) findViewById(R.id.chat_list);
		chatListView.setPullLoadEnable(true);
		chatListView.setRefreshTime();
		chatListView.setXListViewListener(this, 1);

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
				params2.add(new BasicNameValuePair("pmsid", "" + pmsid));
				params2.add(new BasicNameValuePair("uid", "" + myuid));
				params2.add(new BasicNameValuePair("contenttype", ""
						+ contenttype));
				params2.add(new BasicNameValuePair("content", content));
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
		params.add(new BasicNameValuePair("pmsid", "" + pmsid));
		params.add(new BasicNameValuePair("start_pos", "" + start_pos));
		params.add(new BasicNameValuePair("list_num", "" + list_num));
		new Thread(new ConnectPHPToGetJSON(URL_GETCHAT, handler, params))
				.start();
		pd = new ProgressDialog(MyLetterChatActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String str = format.format(date);
		return str;
	}

	private String URL_GETCHAT = ConstUtils.BASEURL + "pmsinfo.php";
	protected int result;
	protected int total_num;
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyLetterChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
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
							}
							setListPosition();
						}
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

	private String URL_GETSENDMSG = ConstUtils.BASEURL + "sendpmsmsg.php";
	protected int sResult;
	// public ArrayList<HashMap<String, String>>chatList;
	private Handler sHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(MyLetterChatActivity.this, "网络连接失败", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					sResult = ((JSONObject) msg.obj).getInt("result");
					if (sResult == 0) {
						if (total_num == 0) {
							adapter.chatList = chatList;
							chatListView.setAdapter(adapter);
						}
						adapter.notifyDataSetChanged();
						chatListView.setSelection(chatList.size() - 1);
						Toast.makeText(MyLetterChatActivity.this, "发送成功",
								Toast.LENGTH_SHORT).show();
						setListPosition();
					} else {
						Toast.makeText(MyLetterChatActivity.this, "发送失败",
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

	protected void addTextToList(String text, String createddate,
			String username, String headpic, int contenttype, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("headpic", headpic);
		map.put("text", text);
		map.put("time", createddate);
		chatList.add(map);
	}

	protected void addTextToListHead(String text, String createddate,
			String username, String headpic, int contenttype, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("headpic", headpic);
		map.put("text", text);
		map.put("time", createddate);
		chatList.add(0, map);
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
			holder.textView.setText(chatList.get(position).get(from[1])
					.toString());
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
			params.add(new BasicNameValuePair("pmsid", "" + pmsid));
			params.add(new BasicNameValuePair("start_pos", "" + start_pos));
			params.add(new BasicNameValuePair("list_num", "" + list_num));
			new Thread(new ConnectPHPToGetJSON(URL_GETCHAT, handler, params))
					.start();
		} else {
			chatListView.setPullLoadEnable(false);
		}
		// Log.v("lishide", ""+tmp);
		// tmp++;
		// addTextToListHead("222222", ME);
		// addTextToListHead("111111", ME);
		// adapter.chatList = chatList;
		// adapter.notifyDataSetChanged();
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
	
}