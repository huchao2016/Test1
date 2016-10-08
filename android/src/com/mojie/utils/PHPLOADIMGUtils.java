package com.mojie.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class PHPLOADIMGUtils {
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(5);

	public static void onLoadImage(final String bitmapUrl,final OnLoadImageListener onLoadImageListener) {
			final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				onLoadImageListener.OnLoadImage((Bitmap) msg.obj, bitmapUrl);
			}
		};

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL imageUrl = new URL(bitmapUrl);
					System.out.println(Thread.currentThread().getName()
							+ "线程被调用了。");
					HttpURLConnection conn = (HttpURLConnection) imageUrl
							.openConnection();
					InputStream inputStream = conn.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					Message msg = new Message();
					msg.obj = bitmap;
					handler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + "线程结束。");
			}
		});

	}

	public interface OnLoadImageListener {
		public void OnLoadImage(Bitmap bitmap, String bitmapPath);
	}
}

