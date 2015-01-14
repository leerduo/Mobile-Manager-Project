package com.dystu.managerprov2.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dystu.managerprov2.domain.AppInfo;

public class AppInfoProvider {

	/**
	 * 获取手机里面所有的安装的应用程序信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		//得到包管理器
		PackageManager pm = context.getPackageManager();
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		List<PackageInfo> packinfos = pm.getInstalledPackages(0);
		for(PackageInfo packinfo : packinfos){
			String packname = packinfo.packageName;
			AppInfo appInfo = new AppInfo();
			Drawable icon = packinfo.applicationInfo.loadIcon(pm);
			//String name = packinfo.applicationInfo.loadLabel(pm).toString()+packinfo.applicationInfo.uid;
			String name = packinfo.applicationInfo.loadLabel(pm).toString();
			//应用程序的特征标志。 可以是任意标志的组合
			int flags = packinfo.applicationInfo.flags;//应用交的答题卡
			
			
			int uid = packinfo.applicationInfo.uid;//操作系统分配给应用系统的一个固定的编号，一旦应用程序被装到手机 ID就固定不变了。
			
			appInfo.setUid(uid);
			
			
			if((flags & ApplicationInfo.FLAG_SYSTEM)  == 0){
				//用户应用
				appInfo.setUserApp(true);
			}else{
				//系统应用
				appInfo.setUserApp(false);
			}
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)  == 0){
				//手机内存
				appInfo.setInRom(true);
			}else{
				//外部存储
				appInfo.setInRom(false);
			}
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfo.setPackname(packname);
			appinfos.add(appInfo);
		}
		return appinfos;
	}
}
