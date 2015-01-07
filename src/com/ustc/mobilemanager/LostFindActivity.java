package com.ustc.mobilemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	
	private ImageView iv_protecting;
	
	private TextView tv_safenumber;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 判断一下，是否做过设置向导，如果没有做过，就跳转到设置向导页面去设置，否则就留在当前的页面
		boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			// 就在手机防盗页面
			setContentView(R.layout.activity_lost_find);
			
			tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
			iv_protecting  = (ImageView) findViewById(R.id.iv_protecting);
			
			//设置安全号码
			String safenumber = sp.getString("safenumber", "");
			if (safenumber != null) {
				tv_safenumber.setText(safenumber);
			}
			
			//设置防盗保护的状态
			boolean protecting = sp.getBoolean("protecting", false);
			if (protecting) {
				iv_protecting.setImageResource(R.drawable.strongbox_app_lock_ic_locked);
			}else {
				iv_protecting.setImageResource(R.drawable.strongbox_app_lock_ic_unlock);
			}
		} else {
			// 还没有做过设置向导
			Intent intent = new Intent(LostFindActivity.this,
					Setup1Activity.class);
			startActivity(intent);
			finish();
			// 要求finish()或者startActivity(intent)方面后面执行
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		}

	}

	public void back(View view) {
		Intent intent = new Intent(LostFindActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
		//要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	/**
	 * 重新进入防盗设置页面(TextView的点击事件)
	 * 
	 * @param view
	 */
	public void reEnterSetup(View view) {
		Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}
}
