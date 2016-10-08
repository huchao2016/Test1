package com.mojie.view;

import com.mojie.activity.R;
import com.mojie.activity.R.id;
import com.mojie.activity.R.layout;
import com.mojie.activity.R.style;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class MyDialogBt {

	private Dialog mDialog;
	public Button registerCamera;
	public Button registerPhoto;
	public Button negativeCancel;

	public MyDialogBt(Context context, String message) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_view_btn, null);

		mDialog = new Dialog(context, R.style.dialog);
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(false);
		
		registerCamera = (Button) view.findViewById(R.id.register_camera_bt);
		registerPhoto = (Button) view.findViewById(R.id.register_photo_bt);
		negativeCancel = (Button) view.findViewById(R.id.register_cancel_bt);
		
//		registerCamera.setOnClickListener(this);
//		registerPhoto.setOnClickListener(this);
//		negativeCancel.setOnClickListener(this);
	}
	
	public void show() {
		mDialog.show();
	}
	
	public void dismiss() {
		mDialog.dismiss();
	}

//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		Intent intent;
//		switch(v.getId()) {
//		case R.id.register_camera_bt:
//			
//			break;
//		case R.id.register_photo_bt:
//			
//			break;
//		case R.id.register_cancel_bt:
//			mDialog.dismiss();
//			break;
//		}
//	}

}