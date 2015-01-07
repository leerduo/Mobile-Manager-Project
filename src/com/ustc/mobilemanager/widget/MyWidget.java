package com.ustc.mobilemanager.widget;

import com.ustc.mobilemanager.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 最短的更新时间为半小时
 * 
 * @author 
 *
 */
public class MyWidget extends AppWidgetProvider {
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("onReceive");
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("onUpdate");
		//修改从设置那里杀死UpdateWidgetService的BUG
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		System.out.println("onDisabled");
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
	
	
	
	
}
