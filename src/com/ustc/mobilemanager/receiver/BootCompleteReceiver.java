package com.ustc.mobilemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	private SharedPreferences sp;
	
	private TelephonyManager tm;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//读取之前保存的sim卡信息
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String saveSim = sp.getString("sim", "");
		
		//读取当前的sim卡信息
		String realSim = tm.getSimSerialNumber();
		
		//比较是否一样
		if (saveSim.equals(realSim)) {
			//sim没有变更，还是同一个sim卡
			Toast.makeText(context, "sim没有发生变更", Toast.LENGTH_LONG).show();
		}else {
			//sim发生变更,发一个短信给安全号码
			Log.i(TAG, "sim发生变更");
			Toast.makeText(context, "sim发生变更", Toast.LENGTH_LONG).show();
		}
		
		
	}

}
