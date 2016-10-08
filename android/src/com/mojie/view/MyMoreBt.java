package com.mojie.view;

import com.mojie.activity.R;
import com.mojie.activity.R.id;
import com.mojie.activity.R.layout;
import com.mojie.activity.R.style;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyMoreBt {

	private Dialog moreDialog;
	public Button morePic;
	public Button moreFile;
	public Button negativeCancel;

	public MyMoreBt(Context context, String message) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_view_btn_more, null);

		moreDialog = new Dialog(context, R.style.dialog);
		moreDialog.setContentView(view);
		moreDialog.setCanceledOnTouchOutside(false);
		
		morePic = (Button) view.findViewById(R.id.more_pic_bt);
		moreFile = (Button) view.findViewById(R.id.more_file_bt);
		moreFile.setVisibility(View.GONE);
		negativeCancel = (Button) view.findViewById(R.id.more_cancel_bt);
		
//		moreCamera.setOnClickListener(this);
//		morePhoto.setOnClickListener(this);
//		moreFile.setOnClickListener(this);
//		negativeCancel.setOnClickListener(this);
	}
	
	public void show() {
		moreDialog.show();
	}
	
	public void dismiss() {
		moreDialog.dismiss();
	}

}