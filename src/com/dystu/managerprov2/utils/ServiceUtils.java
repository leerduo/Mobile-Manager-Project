package com.dystu.managerprov2.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 
 * 校验某个服务是否还活着
 * 
 * @author
 * 
 */

public class ServiceUtils {
	/**
	 * 
	 * 校验某个服务是否还活着
	 * 
	 * @param service
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String service) {
		// 校验服务是否还活着

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 返回数目
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// 正在运行的服务的名字
			String name = runningServiceInfo.service.getClassName();
			if (service.equals(name)) {
				return true;
			}
		}

		return false;

	}

}
