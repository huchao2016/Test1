package com.mojie.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mojie.network.ConnectPHPToGetJSON;
import com.mojie.utils.ConstUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProjectExperienceAddActivity extends Activity implements OnClickListener{

	private ImageView btnAddExperienceBack;
	private EditText etProjectTitle;
	private EditText etProjectIntroduce;
	private EditText etProjectPeople;
	private EditText etProjectMobile;
	private Button btnAddExperienceSave;
	private EditText etProjectStartDate;
	private EditText etProjectFinishDate;
	private SharedPreferences shared;
	private int uid;
//	private String resumeid;
	private ProgressDialog pd;
	private String title;
	private String description;
	private String begindate;
	private String enddate;
	private String provedby;
	private String provertel;
	private String resume_id;
	private final static int DATE_DIALOG_START = 0;
	private final static int DATE_DIALOG_FINISH = 1;
	private Calendar c = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_project_experience);
		
		shared = getSharedPreferences("userInfo", 0);
		uid = shared.getInt("id", 0);
		resume_id = shared.getString("resume_id", "");
		Log.v("lishide", "exper   resumeid "+resume_id);
//		Intent intent = getIntent();
//		resumeid = intent.getStringExtra("resumeid");
		
		btnAddExperienceBack = (ImageView) findViewById(R.id.btn_add_experience_back);
		btnAddExperienceBack.setOnClickListener(this);
		
		etProjectTitle = (EditText) findViewById(R.id.et_project_title);
		etProjectIntroduce = (EditText) findViewById(R.id.et_project_introduce);
		etProjectPeople = (EditText) findViewById(R.id.et_project_people);
		etProjectMobile = (EditText) findViewById(R.id.et_project_mobile);
		
		etProjectStartDate = (EditText) findViewById(R.id.et_project_start_date);
		etProjectStartDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showDialog(DATE_DIALOG_START);
            }
        });
		etProjectFinishDate = (EditText) findViewById(R.id.et_project_finish_date);
		etProjectFinishDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showDialog(DATE_DIALOG_FINISH);
            }
        });
		btnAddExperienceSave = (Button) findViewById(R.id.btn_add_experience_save);
		btnAddExperienceSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_add_experience_back:
			finish();
			break;
		case R.id.btn_add_experience_save:
			title = etProjectTitle.getText().toString();
//			begindate = etProjectStartDate.getText().toString();
//			enddate = etProjectFinishDate.getText().toString();
			description = etProjectIntroduce.getText().toString();
			provedby = etProjectPeople.getText().toString();
			provertel = etProjectMobile.getText().toString();
			if("".equals(title)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(begindate)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入项目开始时间",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(enddate)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入项目完成时间",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(description)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入项目简介",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(provedby)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入证明人姓名",Toast.LENGTH_SHORT).show();
				break;
			}else if("".equals(provertel)) {
				Toast.makeText(ProjectExperienceAddActivity.this,"请输入证明人电话",Toast.LENGTH_SHORT).show();
				break;
			}
			
			pd = new ProgressDialog(ProjectExperienceAddActivity.this);
			pd.setMessage("请稍后…");
			pd.show();
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("uid", ""+uid));
			params.add(new BasicNameValuePair("resumeid", ""+resume_id));
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("begindate", begindate));
			params.add(new BasicNameValuePair("enddate", enddate));
			params.add(new BasicNameValuePair("description", description));
			params.add(new BasicNameValuePair("provedby", provedby));
			params.add(new BasicNameValuePair("provertel", provertel));
    		new Thread(new ConnectPHPToGetJSON(URL_ADDEXPERIENCE,handler,params)).start();
			break;
		}
	}
	
	private String URL_ADDEXPERIENCE = ConstUtils.BASEURL + "addresumeexperiencelist.php";
	private Handler handler = new Handler() {
		private int result;
		public void handleMessage(android.os.Message msg) {
			if (msg.obj == null) {// 获取数据失败
				Toast.makeText(ProjectExperienceAddActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
			} else {
					try {
						result = ((JSONObject) msg.obj).getInt("result");
						if (result == 0) {
							finish();
						} else if (result == 1) {
							Toast.makeText(ProjectExperienceAddActivity.this, "添加项目经验失败",
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
	
	/**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case DATE_DIALOG_START:
            c = Calendar.getInstance();
            dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
//                    	etFabaoDate.setText(year + "" + (month+1) + "" + dayOfMonth + "");
                    	begindate = new StringBuilder().append(year + "" + format((month+1)) + "" + format(dayOfMonth) + "").toString();
                    	etProjectStartDate.setText(new StringBuilder().append(year + "年" + format((month+1)) + "月" + format(dayOfMonth) + "日"));
                    }
                }, 
                c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );
            break;
        case DATE_DIALOG_FINISH:
        	c = Calendar.getInstance();
        	dialog = new DatePickerDialog(
        			this,
        			new DatePickerDialog.OnDateSetListener() {
        				public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
//                    	etFabaoDate.setText(year + "" + (month+1) + "" + dayOfMonth + "");
        					enddate = new StringBuilder().append(year + "" + format((month+1)) + "" + format(dayOfMonth) + "").toString();
        					etProjectFinishDate.setText(new StringBuilder().append(year + "年" + format((month+1)) + "月" + format(dayOfMonth) + "日"));
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
