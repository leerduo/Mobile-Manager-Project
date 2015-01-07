package com.ustc.mobilemanager;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.mobilemanager.adapter.CallSmsSafeAdapter;
import com.ustc.mobilemanager.db.dao.BlackNumberDao;
import com.ustc.mobilemanager.domain.BlackNumberInfo;

/**
 * 
 * 测试的类
 * 
 * @author Administrator
 *
 */

public class CopyOfCallSmsSaveActivity extends Activity {

	private ListView listView;

	private List<BlackNumberInfo> infos;

	private BlackNumberDao dao;

	private CallSmsSafeAdapter safeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_call_sms_save);

		dao = new BlackNumberDao(this);

		safeAdapter = new CallSmsSafeAdapter(this);

		infos = dao.findAll();

		listView = (ListView) findViewById(R.id.list);

		listView.setAdapter(safeAdapter);
	}


	private EditText et_blacknumber;

	private CheckBox cb_phone, cb_sms;

	private Button cancel, ok;

	public void addBlackNumber(View view) {

		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();

		view = View.inflate(this, R.layout.dialog_add_blacknumber, null);

		cancel = (Button) view.findViewById(R.id.cancel);
		ok = (Button) view.findViewById(R.id.ok);
		cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
		et_blacknumber = (EditText) view.findViewById(R.id.et_blacknumber);

		dialog.setView(view, 0, 0, 0, 0);

		dialog.show();

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(blacknumber)) {
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空!", 0)
							.show();
					return;
				}
				String mode;
				if (cb_phone.isChecked() && cb_sms.isChecked()) {
					// 全部拦截
					mode = "3";

				} else if (cb_phone.isChecked()) {
					// 电话拦截
					mode = "1";

				} else if (cb_sms.isChecked()) {
					// 短信拦截
					mode = "2";
				} else {

					Toast.makeText(getApplicationContext(), "请选择拦截模式!", 0)
							.show();
					return;
				}
				// 数据被添加
				dao.add(blacknumber, mode);
				// 更新ListView的内容

				BlackNumberInfo info = new BlackNumberInfo();

				info.setMode(mode);

			info.setNumber(blacknumber);

				

				// 通知适配器数据更新了
				
				//让数据显示在列表开始的位置
				safeAdapter.infos.add(0, info);

				safeAdapter.notifyDataSetChanged();

				dialog.dismiss();
			}
		});

	}

}
