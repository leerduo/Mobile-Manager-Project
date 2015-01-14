package com.dystu.managerprov2.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dystu.managerprov2.R;
import com.dystu.managerprov2.service.AddressService;
import com.dystu.managerprov2.service.CallSmsService;
import com.dystu.managerprov2.service.WatchDogService;
import com.dystu.managerprov2.ui.SettingClickView;
import com.dystu.managerprov2.ui.SettingItemView;
import com.dystu.managerprov2.utils.ServiceUtils;

public class Yinsibaohu extends Fragment {
	
	
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		siv_update = (SettingItemView)getView(). findViewById(R.id.siv_update);
		siv_show_address = (SettingItemView) getView().findViewById(R.id.siv_show_address);
		siv_callsms_safe = (SettingItemView)getView(). findViewById(R.id.siv_callsms_safe);
		siv_watchdog = (SettingItemView) getView().findViewById(R.id.siv_watchdog);
		
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
		//showAddress = new Intent(Yinsibaohu.this, AddressService.class);
		
	//	AddressService.actionStart(getActivity());//1

		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				getActivity(),
				"com.dystu.managerprov2.service.AddressService");

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

					getActivity().stopService(new Intent(getActivity(), AddressService.class));
				} else {
					// 变为选择的状态
					siv_show_address.setChecked(true);
					getActivity().startService(new Intent(getActivity(), AddressService.class));
				}

			}
		});
		
		//=========================
		
		//callSmsSafeService = new Intent(this, CallSmsService.class);
		
		
		//callSmsSafeService.setClass(getActivity(), CallSmsService.class);
		
		//CallSmsService.actionStart(getActivity());//2

		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// 变为非选中状态
					siv_callsms_safe.setChecked(false);

					getActivity().stopService(new Intent(getActivity(), CallSmsService.class));
				} else {
					// 变为选择的状态
					siv_callsms_safe.setChecked(true);
					getActivity().startService(new Intent(getActivity(), CallSmsService.class));
				}

			}
		});
		
		//==========================
		
		//=================================
		
		
		
		//watchdogIntent = new Intent(this, WatchDogService.class);
		
		//watchdogIntent.setClass(getActivity(), WatchDogService.class);
		
		//WatchDogService.actionStart(getActivity());//3

		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watchdog.isChecked()) {
					// 变为非选中状态
					siv_watchdog.setChecked(false);

					getActivity().stopService(new Intent(getActivity(), WatchDogService.class));
				} else {
					// 变为选择的状态
					siv_watchdog.setChecked(true);
					getActivity().startService(new Intent(getActivity(), WatchDogService.class));
				}

			}
		});
		
		
		boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(
				getActivity(),
				"com.dystu.managerprov2.service.WatchDogService");

		if (isWatchDogServiceRunning) {
			// 变为选择的状态
			siv_watchdog.setChecked(true);
		} else {
			// 变为非选中状态
			siv_watchdog.setChecked(false);
		}
		
		//===============================================

		// 设置号码归属地的背景
		scv_changebg = (SettingClickView) getView().findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);

		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ysbh, container, false);
	}
	
}
