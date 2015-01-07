package com.ustc.mobilemanager;

import com.ustc.mobilemanager.service.AutoCleanService;
import com.ustc.mobilemanager.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {

	private CheckBox cb_show_system;
	private SharedPreferences sp;
	private CheckBox cb_auto_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				// 锁屏的广播事件是一个特殊的广播事件，在清单文件配置广播接受者是不会生效的
				// 锁屏的广播接受者必须在代码中注册（写在服务里）
				Intent intent = new Intent(TaskSettingActivity.this,
						AutoCleanService.class);
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}

			}
		});
		cb_show_system
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sp.edit();
						editor.putBoolean("showsystem", isChecked);
						editor.commit();
					}
				});
		// 倒计时的API
		CountDownTimer cdt = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				System.out.println(millisUntilFinished);
			}

			@Override
			public void onFinish() {
				System.out.println("finish");
			}
		};
		cdt.start();
	}

	@Override
	protected void onStart() {

		boolean running = ServiceUtils.isServiceRunning(this,
				"com.ustc.mobilemanager.service.AutoCleanService");

		cb_auto_clean.setChecked(running);

		super.onStart();
	}

}
