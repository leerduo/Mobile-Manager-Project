package com.ustc.mobilemanager.receiver;

import com.ustc.mobilemanager.R;
import com.ustc.mobilemanager.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";

	private SharedPreferences sp;
	
	//设备策略服务
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 写接受短信的代码

		Object[] objs = (Object[]) intent.getExtras().get("pdus");

		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		dpm = (DevicePolicyManager)context.getSystemService(context.DEVICE_POLICY_SERVICE);

		for (Object b : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			// 发送者
			String sender = sms.getOriginatingAddress();

			String safeNumber = sp.getString("safenumber", "");
			// 短信内容
			String body = sms.getMessageBody();

			if (sender.contains(safeNumber)) {
				// Log.i(TAG, sender);
				// Toast.makeText(context, sender, 0).show();

				if ("#*location*#".equals(body)) {
					// 得到手机的GPS
					Log.i(TAG, "得到手机的GPS");
					//启动服务
					Intent i = new Intent(context,GPSService.class);
					context.startService(i);
					
					SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastlocation)) {
						//位置没有得到
						
						SmsManager.getDefault().sendTextMessage(sender, null, "正在获取位置中...", null, null);
						
					}else {
						//得到位置了
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
						
					}
					
					// 把这个广播终止掉
					abortBroadcast();
				} else if ("#*alarm*#".equals(body)) {
					
					MediaPlayer player = MediaPlayer.create(context,R.raw.danxiaogui);
					
					player.setLooping(false);
					player.start();
					
					Log.i(TAG, "播放报警音乐");
					abortBroadcast();
				} else if ("#*wipedata*#".equals(body)) {
					Log.i(TAG, "远程销毁数据");
					//危险！！！
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					Log.i(TAG, "远程锁屏");
					ComponentName who = new ComponentName(context,MyAdmin.class);
			    	if (dpm.isAdminActive(who)) {
						dpm.lockNow();//锁屏
						dpm.resetPassword("123", 0);//设置屏蔽密码
						//dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除sd卡的数据
						//dpm.wipeData(0);//恢复出厂设置
					}else {
						Toast.makeText(context, "还没有开启管理的权限!", 1).show();
						return;
					}
					abortBroadcast();
				}
			}
		}

	}

}
