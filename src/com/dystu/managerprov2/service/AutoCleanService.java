package com.dystu.managerprov2.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author ËøÆÁÇåÀí½ø³Ì
 *
 */

public class AutoCleanService extends Service {
	public static final String TAG = "AutoCleanService";
	
	private ScreenOffReceiver receiver;
	
	private ActivityManager am;

	@Override
	public void onCreate() {
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		receiver = new ScreenOffReceiver();
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "ÆÁÄ»ËøÆÁÀ²!");
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				am.killBackgroundProcesses(info.processName);
			}
		}
		
	}

}
