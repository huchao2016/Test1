package com.mojie.utils;

import com.mojie.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import android.os.Environment;

public class ConstUtils {
	public static String BASEURL = "http://www.mouldworld.com.cn/mobileapp/";

	public static String IMGURL = BASEURL + "img/";
	public static String upLoadServerUri = BASEURL + "up_file.php";

	public static String TASK_ATTACH_URL = BASEURL + "taskattach/";
	public static String upLoadServerUri_attach = BASEURL + "up_file_taskattach.php";

	public static String COMMUNICATION_ATTACH_URL = BASEURL + "communicationattach/";
	public static String upLoadServerUri_communication = BASEURL + "up_file_communicationattach.php";

	public static WebImageCache mCache;
	public static DisplayImageOptions options;

	static {
		mCache = new WebImageCache();
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 构建完成
	}

	public static String NEWS_BASE_URL = "http://www.mouldworld.com.cn/news/Show/id/";
	public static String ABOUT_BASE_URL = "http://www.mouldworld.com.cn/";
}