package com.ustc.mobilemanager.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.ustc.mobilemanager.R;
import com.ustc.mobilemanager.utils.SystemInfoUtils;
import com.ustc.mobilemanager.widget.MyWidget;

public class UpdateWidgetService extends Service {

	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;

	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	/**
	 * Widget管理器
	 */
	private AppWidgetManager awm;

	@Override
	public void onCreate() {

		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();

		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver,
				new IntentFilter(Intent.ACTION_SCREEN_OFF));

		awm = AppWidgetManager.getInstance(this);
		startTimer();
		super.onCreate();
	}

	private void startTimer() {
		if (timer == null && task == null) {
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {

					Log.i(TAG, "更新widget");
					// 设置更新的组件
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, MyWidget.class);
					// A class that describes a view hierarchy that can be
					// displayed
					// in another process. The hierarchy is inflated from a
					// layout
					// resource file, and this class provides some basic
					// operations
					// for modifying the content of the inflated hierarchy

					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(
							R.id.process_count,
							"正在运行进程:"
									+ SystemInfoUtils
											.getRunningProcessCount(getApplicationContext())
									+ "个");
					long size = SystemInfoUtils
							.getAvailMem(getApplicationContext());
					views.setTextViewText(
							R.id.process_memory,
							"可用内存:"
									+ Formatter.formatFileSize(
											getApplicationContext(), size));
					// 描述一个动作,这个动作是由另外一个应用程序执行的
					// 自定义一个广播事件，杀死后台的进程

					Intent intent = new Intent();
					intent.setAction("com.ustc.mobilemanager.KILLALL");

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

					awm.updateAppWidget(provider, views);

				}
			};
			timer.schedule(task, 0, 3000);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(onReceiver);
		unregisterReceiver(offReceiver);
		onReceiver = null;
		offReceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			task = null;
			timer = null;
		}
	}

	private class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕锁屏啦!停止Timer的更新");
			stopTimer();
		}

	}

	private class ScreenOnReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕解锁啦!开始Timer的更新");
			startTimer();
		}

	}

}
