package com.dystu.managerprov2.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	// 得到位置服务
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 实例化
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// List<String> providers = lm.getAllProviders();
		// for (String l : providers) {
		// System.out.println(l);
		// }

		listener = new MyLocationListener();
		// 注册监听位置服务
		// 给内容提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener);
	}

	class MyLocationListener implements LocationListener {

		/**
		 * 当位置改变的时候回调这个方法
		 * 
		 */
		@Override
		public void onLocationChanged(Location location) {

			// 经度
			String longitude = "jingdu:" + location.getLongitude() + "\n";
			// 纬度
			String latitude = "weidu:" + location.getLatitude()+ "\n";
			// 精度
			String accuracy = "accuracy:" + location.getAccuracy()+ "\n";
			
			
			//发短信给安全号码
			
			
			
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			
			Editor editor = sp.edit();
			
			editor.putString("lastlocation", longitude +latitude + accuracy);
			
			editor.commit();
			
			

			// TextView textView = new TextView(MainActivity.this);
			// textView.setText(longitude + "\n" + latitude + "\n" + accuracy);
			// setContentView(textView);
		}

		/**
		 * 
		 * 当状态发生改变的时候
		 * 
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		/**
		 * 
		 * 
		 * 某一个位置提供者可以使用了回调
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 
		 * 
		 * 某一个位置提供者不可以使用了回调
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 取消监听位置服务
		lm.removeUpdates(listener);
		listener = null;
	}

}
