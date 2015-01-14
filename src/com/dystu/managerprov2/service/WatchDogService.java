package com.dystu.managerprov2.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dystu.managerprov2.EnterPwdActivity;
import com.dystu.managerprov2.dao.AppLockDao;

/**
 * ��ͣ�ļ��ӵ�ǰ�ֻ�ϵͳ���������״̬
 * @author Administrator
 *
 */
public class WatchDogService extends Service {
	public static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	/**
	 * ��ʱֹͣ�����İ���
	 */
	private String tempStopProtectpackname;
	
	private InnerReceiver receiver;
	
	/**
	 * �������İ�������
	 */
	private List<String> protectedPacknames;
	
	private MyObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private  class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectpackname = intent.getStringExtra("packname");
			Log.i(TAG,"���Ź��õ�����Ϣ����ʱ��ֹͣ��ĳ��Ӧ�ó���ı���:"+tempStopProtectpackname);
			
		}
	}
	
	
	
	@Override
	public void onCreate() {
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.dystu.managerprov2.TEMPSTOP");
		registerReceiver(receiver, filter);
		dao = new AppLockDao(this);
		protectedPacknames = dao.findAll();
		//ע��һ�����ݹ۲���
		Uri uri = Uri.parse("content://com.dystu.managerprov2/applockdb");
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true, observer);
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final Intent intent = new Intent(this,EnterPwdActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				flag = true;
				while(flag){
					//Ѳ�� ���ӵ�ǰ���е�Ӧ�ó���  �õ���ǰ����ջ������ǰ������ջ��Ϣ  ��ǰҪ�����ĳ���
					RunningTaskInfo  taskInfo  = am.getRunningTasks(1).get(0);
					String packname = taskInfo.topActivity.getPackageName();
					//if(dao.find(packname)){//��ҪƵ���Ĳ�ѯ�������ݿ� ��Ϊ��ѯ�ڴ档
					if(protectedPacknames.contains(packname)){//��ѯ�ڴ�
						//����Ƿ���Ҫ��ʱֹͣ����
						if(packname.equals(tempStopProtectpackname)){
							//ʲô���鶼������
						}else{
						//����Ҫ��ʱֹͣ����ǰӦ�ó�����Ҫ������ ���ŷŹ�
						intent.putExtra("packname", packname);
						startActivity(intent);
						}
					}else{
						
					}
					try {
						Thread.sleep(20);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		super.onCreate();
	}
	
	
	private class MyObserver extends ContentObserver{

		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG,"�����������ҷ��������ݿ�����ݱ仯�ˡ�");
			protectedPacknames = dao.findAll();
		}
		
	}
	
	public static void actionStart(Context context){
		Intent intent = new Intent(context, WatchDogService.class);
		
		
		
		
	}

	
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		super.onDestroy();
	}

}