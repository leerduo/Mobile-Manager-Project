package com.ustc.mobilemanager.engine;

import java.util.ArrayList;
import java.util.List;

import com.ustc.mobilemanager.R;
import com.ustc.mobilemanager.domain.TaskInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

/**
 * 
 * 提供手机里面的进程信息
 * 
 * @author
 * 
 */
public class TaskInfoProvider {

	/**
	 * 获取所有的进程信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfo(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();

		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo processInfo : processInfos) {

			TaskInfo taskInfo = new TaskInfo();

			// 进程名就是包名
			String packname = processInfo.processName;

			taskInfo.setPackname(packname);

			// import android.os.Debug.MemoryInfo导包不能导错了
			MemoryInfo[] memoryInfos = am
					.getProcessMemoryInfo(new int[] { processInfo.pid });
			// 转成byte，方便转化
			long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024l;
			taskInfo.setMemSize(memsize);

			try {
				// PackageInfo packageInfo = pm.getPackageInfo(packname, 0);
				// packageInfo.applicationInfo

				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packname, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);

				String name = applicationInfo.loadLabel(pm).toString();

				taskInfo.setName(name);

				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 用户进程
					taskInfo.setUserTask(true);

				} else {
					// 系统进程

					taskInfo.setUserTask(false);
				}

			} catch (Exception e) {
				e.printStackTrace();
				//系统内核进程   没有名称
				taskInfo.setName(packname);
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.icon));
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}

}
