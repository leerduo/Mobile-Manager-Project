package com.ustc.mobilemanager.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.ustc.mobilemanager.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsService extends Service {

	public static final String TAG = "CallSmsService";
	private InnerSmsReceiver receiver;

	private BlackNumberDao dao;

	private TelephonyManager tm;

	private MyListener listener;

	@Override
	public void onCreate() {

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();

		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");

		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

		registerReceiver(receiver, filter);

		dao = new BlackNumberDao(this);

		super.onCreate();
	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					// 挂断电话
					Log.i(TAG, "挂断电话");

					// 观察呼叫记录数据库内容的变化
					// 呼叫记录的uri路径
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,
							new CallLogObserver(incomingNumber, new Handler()));

					endCall();// 在另外一个进程里面运行的远程服务的方法，方法调用后，呼叫记录可能还没有生成，导致清除不掉呼叫记录
					// 删除呼叫记录
					// 另外一个应用程序的私有的联系人数据库

					// deleteCallLog(incomingNumber);//本方法和endCall()不在一个进程中运行

				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;

		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.i(TAG, "数据库的内容变化了，产生了呼叫记录");
			// 取消注册
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 利用内容提供者去删除通话记录
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		// 内容提供者解析器
		ContentResolver resolver = getContentResolver();
		// 呼叫记录的uri路径
		Uri uri = Uri.parse("content://call_log/calls");

		// CallLog.CONTENT_URI 直接使用常量也可以

		resolver.delete(uri, "number=?", new String[] { incomingNumber });
		
		//可以利用query把通话记录的号码导入到黑名单中

	}

	public void endCall() {

		// IBinder b = ServiceManager.getService(TELEPHONY_SERVICE);
		// ---->ServiceManager得不到，怎么办？？？反射

		// 加载ServiceManager的字节码
		try {
			Class clazz = CallSmsService.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

			ITelephony.Stub.asInterface(iBinder).endCall();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "短信来啦！");
			// 检查发件人是否是黑名单号码，并且设置了短信拦截或者全部拦截

			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage
						.createFromPdu((byte[]) object);
				// 得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if ("2".equals(mode) || "3".equals(sender)) {
					// 拦截短信
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
			}

		}

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

}
