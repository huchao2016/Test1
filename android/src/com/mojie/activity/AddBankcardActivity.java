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
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddBankcardActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText etAddBankcardNum;
	private EditText etAddBankcardName;
	private Spinner bankcardTypeSpinner;
	private Button addBankcardSubmit;
	private String account;
	private String banktype;
	private int cardtype = 0;
	private SharedPreferences shared;
	protected int id;
	private ProgressDialog pd;
	//sehishhdifhso
	
	private String URL_ADDBANKCARD = ConstUtils.BASEURL + "adduserbank.php";
	protected int result;
	private Handler handler = new Handler(){  
		@Override  
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null){//获取数据失败
				Toast.makeText(AddBankcardActivity.this, "网络连接失败",Toast.LENGTH_LONG).show();
			}else{
				try {
					result = ((JSONObject)msg.obj).getInt("result");
					if(result == 0){
//						Toast.makeText(AddBankcardActivity.this,"添加银行卡成功",Toast.LENGTH_SHORT).show();
						finish();
					}else {
						Toast.makeText(AddBankcardActivity.this,"添加银行卡失败",Toast.LENGTH_SHORT).show();
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bankcard);
		
		shared = getSharedPreferences("userInfo", 0);
		id = shared.getInt("id", 0);
		
		etAddBankcardNum = (EditText)findViewById(R.id.et_add_bankcard_num);
		etAddBankcardName = (EditText)findViewById(R.id.et_add_bankcard_name);
		
		backImg = (ImageView) findViewById(R.id.btn_add_bankcard_back);
		backImg.setOnClickListener(this);
		
		addBankcardSubmit = (Button) findViewById(R.id.btn_add_bankcard_submit);
		addBankcardSubmit.setOnClickListener(this);
		
		bankcardTypeSpinner = (Spinner) findViewById(R.id.spinner_add_bankcard_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getApplicationContext(), R.array.bankcard_type, R.layout.spinner_experience);
		bankcardTypeSpinner.setAdapter(adapter);
		bankcardTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				cardtype = position;
				Log.v("lishide", "cardtype =="+cardtype);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_add_bankcard_back:
			finish();
			break;
		case R.id.btn_add_bankcard_submit:
			account = etAddBankcardNum.getText().toString();
			banktype = etAddBankcardName.getText().toString();
			if("".equals(account)) {
				Toast.makeText(AddBankcardActivity.this,"请输入银行卡号",Toast.LENGTH_LONG).show();
				break;
			}else if("".equals(banktype)) {
				Toast.makeText(AddBankcardActivity.this,"请输入银行卡开户行",Toast.LENGTH_LONG).show();
				break;
			} else {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        		params.add(new BasicNameValuePair("uid", ""+id));
        		params.add(new BasicNameValuePair("account", account));
        		params.add(new BasicNameValuePair("cardtype", ""+cardtype));
        		params.add(new BasicNameValuePair("banktype", banktype));
        		Log.v("lishide", "submit>>uid =="+id);
        		Log.v("lishide", "submit>>account =="+account);
        		Log.v("lishide", "submit>>cardtype =="+cardtype);
        		Log.v("lishide", "submit>>banktype =="+banktype);
        		
        		
        		new Thread(new ConnectPHPToGetJSON(URL_ADDBANKCARD,handler,params)).start(); 
        		pd = new ProgressDialog(AddBankcardActivity.this);
    			pd.setMessage("请稍后…");
    			pd.show();
			}
			break;
		}
	}

}
