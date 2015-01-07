package com.ustc.mobilemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.ustc.mobilemanager.service.AddressService;
import com.ustc.mobilemanager.service.CallSmsService;
import com.ustc.mobilemanager.service.WatchDogService;
import com.ustc.mobilemanager.ui.SettingClickView;
import com.ustc.mobilemanager.ui.SettingItemView;
import com.ustc.mobilemanager.utils.ServiceUtils;

public class SettingActivity extends Activity {

	// 设置是否开启自动更新
	private SettingItemView siv_update;
	// 设置是否开启显示号码归属地
	private SettingItemView siv_show_address;
	//黑名单拦截的设置
	private SettingItemView siv_callsms_safe;
	//程序锁看门狗
	private SettingItemView siv_watchdog;
	
	private Intent callSmsSafeService;
	//程序锁看门狗
	private Intent watchdogIntent;

	private SharedPreferences sp;

	private Intent showAddress;

	// 设置归属地显示框背景
	private SettingClickView scv_changebg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
			// siv_update.setDesc("自动升级已经开启");
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
			// siv_update.setDesc("自动升级已经关闭");
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();

				// 判断是否选中
				// 已经选中了
				if (siv_update.isChecked()) {

					siv_update.setChecked(false);
					// siv_update.setDesc("自动升级已经关闭");
					editor.putBoolean("update", false);
				} else {
					// 没有选中
					siv_update.setChecked(true);
					// siv_update.setDesc("自动升级已经开启");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		showAddress = new Intent(this, AddressService.class);

		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.ustc.mobilemanager.service.AddressService");

		if (isServiceRunning) {
			// 变为选择的状态
			siv_show_address.setChecked(true);
		} else {
			// 变为非选中状态
			siv_show_address.setChecked(false);
		}

		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// 变为非选中状态
					siv_show_address.setChecked(false);

					stopService(showAddress);
				} else {
					// 变为选择的状态
					siv_show_address.setChecked(true);
					startService(showAddress);
				}

			}
		});
		
		//=========================
		
		callSmsSafeService = new Intent(this, CallSmsService.class);

		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// 变为非选中状态
					siv_callsms_safe.setChecked(false);

					stopService(callSmsSafeService);
				} else {
					// 变为选择的状态
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeService);
				}

			}
		});
		
		//==========================
		
		//=================================
		
		
		
		watchdogIntent = new Intent(this, WatchDogService.class);

		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watchdog.isChecked()) {
					// 变为非选中状态
					siv_watchdog.setChecked(false);

					stopService(watchdogIntent);
				} else {
					// 变为选择的状态
					siv_watchdog.setChecked(true);
					startService(watchdogIntent);
				}

			}
		});
		
		
		boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.ustc.mobilemanager.service.WatchDogService");

		if (isWatchDogServiceRunning) {
			// 变为选择的状态
			siv_watchdog.setChecked(true);
		} else {
			// 变为非选中状态
			siv_watchdog.setChecked(false);
		}
		
		//===============================================

		// 设置号码归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);

		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// 保存选择参数
								Editor editor = sp.edit();
								editor.putInt("which", which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// 取消对话框
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("cancel", null);
				builder.show();

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		showAddress = new Intent(this, AddressService.class);

		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.ustc.mobilemanager.service.AddressService");

		if (isServiceRunning) {
			// 变为选择的状态
			siv_show_address.setChecked(true);
		} else {
			// 变为非选中状态
			siv_show_address.setChecked(false);
		}
		
		//=========================
		
		boolean isCallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.ustc.mobilemanager.service.CallSmsService");

		if (isCallSmsServiceRunning) {
			// 变为选择的状态
			siv_callsms_safe.setChecked(true);
		} else {
			// 变为非选中状态
			siv_callsms_safe.setChecked(false);
		}

		//============================

		
		boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.ustc.mobilemanager.service.WatchDogService");

		if (isWatchDogServiceRunning) {
			// 变为选择的状态
			siv_watchdog.setChecked(true);
		} else {
			// 变为非选中状态
			siv_watchdog.setChecked(false);
		}
		
		//===================================
		
		
	}

}
