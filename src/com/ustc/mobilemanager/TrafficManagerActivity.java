package com.ustc.mobilemanager;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.Window;
import android.widget.SlidingDrawer;


/**
 * 
 *proc/uid_stat/uid/tcp_snd
 * 
 * @author Administrator
 *
 */

public class TrafficManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		
		PackageManager pm = getPackageManager();
		
		List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
		
		for (ApplicationInfo applicationInfo : applicationInfos) {
			int uid = applicationInfo.uid;
			
			long tx = TrafficStats.getUidTxBytes(uid);//上传
			
			long rx = TrafficStats.getUidRxBytes(uid);//下载
			//方法返回值为-1 代表的是应用程序没有产生流量或者操作系统不支持流量统计
			
		}
		
		TrafficStats.getMobileTxBytes();//获取手机3g/2g网络上传的总流量
		TrafficStats.getMobileTxBytes();//手机2g/3g下载的总流量
		
		
		TrafficStats.getTotalTxBytes();//获取上传的总流量
		TrafficStats.getTotalRxBytes();//获取下载的总流量
		
		
		setContentView(R.layout.activity_traffic_manager);
		
		
	}
}
