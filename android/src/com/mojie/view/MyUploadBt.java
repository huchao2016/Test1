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

public class MyUploadBt {

	private Dialog uploadDialog;
	public Button uploadFile;
	public Button negativeCancel;
	public Button uploadPic;

	public MyUploadBt(Context context, String message) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_view_btn_upload, null);

		uploadDialog = new Dialog(context, R.style.dialog);
		uploadDialog.setContentView(view);
		uploadDialog.setCanceledOnTouchOutside(false);
		
		uploadFile = (Button) view.findViewById(R.id.upload_file_bt);
		uploadFile.setVisibility(View.GONE);
		uploadPic = (Button) view.findViewById(R.id.upload_pic_bt);
		negativeCancel = (Button) view.findViewById(R.id.upload_cancel_bt);
		
	}
	
	public void show() {
		uploadDialog.show();
	}
	
	public void dismiss() {
		uploadDialog.dismiss();
	}

}