package com.ustc.mobilemanager;

import com.ustc.mobilemanager.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	protected static final String TAG = "HomeActivity";


	private GridView list_home;
	
	
	private MyAdapter adapter;
	
	
	private SharedPreferences sp;
	
	private static String[] names = 
		{
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
		};
	
	private static int[] ids = {
		
		R.drawable.shoujifangdao,R.drawable.tongxunweishi,R.drawable.ruanjianguanli,
		R.drawable.jinchengguanli,R.drawable.liuliangtongji,R.drawable.shoujishadu,
		R.drawable.huancunqingli,R.drawable.gaojigongju,R.drawable.shezhizhongxin
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0://进入手机防盗界面
					showLostFindDialog();
					break;
				case 1://进入黑名单拦截界面
					Intent intent1 = new Intent(HomeActivity.this, CallSmsSaveActivity.class);
					startActivity(intent1);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 2://进入软件管理界面
					Intent intent2 = new Intent(HomeActivity.this, AppManagerActivity.class);
					startActivity(intent2);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 3://进入进程管理界面
					Intent intent3 = new Intent(HomeActivity.this, TaskManagerActivity.class);
					startActivity(intent3);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 4://进入流量管理界面
					Intent intent4 = new Intent(HomeActivity.this, TrafficManagerActivity.class);
					startActivity(intent4);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 5://进入杀毒界面
					Intent intent5 = new Intent(HomeActivity.this, AntiVirusActivity.class);
					startActivity(intent5);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 6://进入缓存清理界面
					Intent intent6 = new Intent(HomeActivity.this, CleanCacheActivity.class);
					startActivity(intent6);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 7://进入高级工具
					Intent intent7 = new Intent(HomeActivity.this, AToolsActivity.class);
					startActivity(intent7);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					
				break;
					
				case 8://进入设置中心
					Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;

				default:
					break;
				}
			}
		});
	}
	
	protected void showLostFindDialog() {
		//判断是否设置过密码
		if (isSetupPwd()) {
			//已经设置密码了,弹出的是输入对话框
			showEnterDialog();
		}else {
			//没有设置过密码,弹出的是设置密码的对话框
			showSetupPwdDialog();
		}
	}
	private EditText password;
	private EditText confirm_password;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;
	
	/**
	 * 
	 * 设置密码对话框
	 */
	private void showSetupPwdDialog() {
		
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		//自定义一个布局文件
		View view = View.inflate(this, R.layout.dialog_setup_dialog, null);
		
		password = (EditText) view.findViewById(R.id.password);
		confirm_password = (EditText) view.findViewById(R.id.confirm_password);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//取消掉对话框
				dialog.dismiss();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//取出密码
				String password1 = password.getText().toString().trim();
				String password2 = confirm_password.getText().toString().trim();
				if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
					Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
					return;
				}
				//判断是否一致  
				if (password1.equals(password2)) {
					//一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password1));//保存加密后的密码
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					//finish();
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
				}else {
					Toast.makeText(HomeActivity.this, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		builder.setView(view);
		dialog = builder.show();
		
	}

	/**
	 * 
	 * 输入密码对话框
	 * 
	 */
	private void showEnterDialog() {
		
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		//自定义一个布局文件
		View view = View.inflate(this, R.layout.dialog_enter_dialog, null);
		
		password = (EditText) view.findViewById(R.id.password);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//取消掉对话框
				dialog.dismiss();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password1 = password.getText().toString().trim();
				String savedPassword = sp.getString("password", null);//取出加密后的密码
				if (TextUtils.isEmpty(password1)) {
					Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
					return;
				}
				if (MD5Utils.md5Password(password1).equals(savedPassword)) {
					//输入的密码是之前设置的密码,如果不相同则提示用户并返回
					//把对话框消掉,进入主页面
					dialog.dismiss();
					Log.i(TAG, "把对话框消掉,进入手机防盗页面");
					
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					//finish();
					//要求finish()或者startActivity(intent)方面后面执行
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					
				}else {
					Toast.makeText(HomeActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		builder.setView(view);
		dialog = builder.show();
		//dialog.setView(view, 0, 0, 0, 0);
		//dialog.show();
	}

	/**
	 * 判断是否设置过密码
	 * 
	 * @return
	 */
	private boolean isSetupPwd(){
		String password = sp.getString("password", null);
		
//		if (TextUtils.isEmpty(password)) {
//			return false;
//		}else {
//			return true;
//		}
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			
			
			tv_item.setText(names[position]);
			
			iv_item.setImageResource(ids[position]);
			
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
		
	}
	
}
