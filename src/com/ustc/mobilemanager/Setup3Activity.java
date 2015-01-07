package com.ustc.mobilemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

	private EditText number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup3);
		number = (EditText) findViewById(R.id.number);
		number.setText(sp.getString("safenumber", null));
	}

	@Override
	public void showBack() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

	@Override
	public void showNext() {
		//应该保存下安全号码
		
		String phonenumber = number.getText().toString().trim();
		if (TextUtils.isEmpty(phonenumber)) {
			Toast.makeText(this, "安全号码未设置，请先设置安全号码.", Toast.LENGTH_LONG).show();
			return;
			
		}
		//应该保存下安全号码
		Editor editor = sp.edit();
		editor.putString("safenumber", phonenumber);
		editor.commit();
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	/**
	 * 选择联系人的按钮的点击事件
	 * 
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent = new Intent(Setup3Activity.this,
				SelectContactActivity.class);
		// 希望返回给本类一个电话号码,所以使用下面的方法
		startActivityForResult(intent, 0);
		// 要求finish()或者startActivity(intent)方面后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}
		// 电话号码的“-”去掉
		String phone = data.getStringExtra("phone").replace("-", "");

		number.setText(phone);

	}

}
