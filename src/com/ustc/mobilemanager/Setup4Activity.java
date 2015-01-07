package com.ustc.mobilemanager;

import com.ustc.mobilemanager.receiver.MyAdmin;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

	private SharedPreferences sp;
	
	private CheckBox cb_protecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_protecting = (CheckBox) findViewById(R.id.cb_protecting);
		
		boolean protecting = sp.getBoolean("protecting", false);
		
		if (protecting) {
			//手机防盗已经开启
			cb_protecting.setText("手机防盗已经开启");
			cb_protecting.setChecked(true);
		}else {
			//手机防盗没有开启
			cb_protecting.setText("手机防盗没有开启");
			cb_protecting.setChecked(false);
		}
		cb_protecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					cb_protecting.setText("手机防盗已经开启");
				}else {
					cb_protecting.setText("手机防盗没有开启");
				}
				
				//保存选择的状态
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
		
		
	}

	@Override
	public void showBack() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

	
	public void ok(View view) {
		Editor edit = sp.edit();
		edit.putBoolean("configed", true);
		edit.commit();
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}
	public void lock(View view){
		
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    	//我要激活谁
    	ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
    	
    	intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
    	
    	//提示用户开启管理员的权限
    	intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"开启可以一键锁屏!");
    	startActivity(intent);
	}

	@Override
	public void showNext() {
		
		
	}
}
