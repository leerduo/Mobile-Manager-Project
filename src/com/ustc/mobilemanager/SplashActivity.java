package com.ustc.mobilemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.mobilemanager.utils.StringUtils;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int ERROR = 2;
	private TextView tv_splash_version;
	private TextView tv_update_info;

	private String description;
	private String apkurl;

	private RelativeLayout rootLayout;

	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		rootLayout = (RelativeLayout) findViewById(R.id.rl_root);
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		// 设置版本号
		tv_splash_version.setText("版本号:" + getVersion());
		boolean update = sp.getBoolean("update", false);

		// 拷贝数据库
		copyDB("antivirus.db");
		copyDB("address.db");

		installShortcut();

		if (update) {
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已经关闭
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(3000);
		rootLayout.startAnimation(alphaAnimation);
	}

	/**
	 * 创建快捷方式
	 * 
	 */
	private void installShortcut() {

		boolean shortcut = sp.getBoolean("shortcut", false);

		if (shortcut) {
			return;
		}
		Editor editor = sp.edit();

		// 发送广播的Intent
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式，要包含三个重要的信息：1.名称；2.图标；3.干什么事情
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机管家");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.icon));

		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
		shortcutIntent.setClassName(getPackageName(),
				"com.ustc.mobilemanager.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		Toast.makeText(this, "已创建手机管家快捷方式", 0).show();
		editor.putBoolean("shortcut", true);
		editor.commit();

	}

	/**
	 * 拷贝号码归属地数据库 把数据库拷贝到data/data/包名/files/address.db
	 */
	private void copyDB(String fileName) {
		// 第一次拷贝之后 后面启动不再需要拷贝
		try {

			File file = new File(getFilesDir(), fileName);

			if (file.exists() && file.length() > 0) {
				// 不需要拷贝了
				Log.i(TAG, "不需要拷贝了");
			} else {
				InputStream is = getAssets().open(fileName);

				FileOutputStream fos = new FileOutputStream(file);

				byte[] buffer = new byte[1024];

				int len = 0;

				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 显示升级的对话框
			case SHOW_UPDATE_DIALOG:
				Log.i(TAG, "显示升级的对话框");
				// 按下F3 进入方法
				showUpdateDialog();
				break;
			// 进入主界面
			case ENTER_HOME:
				enterHome();
				break;
			// 错误处理,进入主界面
			case ERROR:
				enterHome();
				Toast.makeText(SplashActivity.this, "错误，进入主界面",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 
	 * 检查是否有新版本 如果有就升级
	 * 
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {

				// Return a new Message instance from the global pool. Allows us
				// to avoid allocating new objects in many cases.
				Message msg = Message.obtain();

				long start = System.currentTimeMillis();

				// URL http://192.168.1.31:8080/updateinfo.html
				try {

					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();

					conn.setRequestMethod("GET");

					conn.setConnectTimeout(4000);

					conn.setReadTimeout(4000);

					int responseCode = conn.getResponseCode();

					if (responseCode == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转换为String
						String result = StringUtils.readFromStream(is);
						Log.i(TAG, "联网成功了:" + result);

						// json解析
						JSONObject obj = new JSONObject(result);
						// 得到服务器的版本信息
						String version = (String) obj.get("version");
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// 校验是否有新版本
						if (getVersion().equals(version)) {
							// 版本一致，没有新版本，进入到主界面

							msg.what = ENTER_HOME;

						} else {
							// 有新版本，弹出升级对话框
							msg.what = SHOW_UPDATE_DIALOG;
						}

					}

				} catch (Exception e) {
					msg.what = ERROR;
					e.printStackTrace();
				} finally {

					long end = System.currentTimeMillis();
					// 花了多少时间
					long dTime = end - start;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					handler.sendMessage(msg);
				}

			};
		}.start();

	}

	private void enterHome() {

		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 弹出升级对话框
	 * 
	 */
	public void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		// builder.setCancelable(false);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// 进入主界面,
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setTitle("升级提醒");
		builder.setMessage(description);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk 并做替换安装
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd存在
					// 面向组件编程
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilemanager2.0.apk",
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(),
											"下载失败", Toast.LENGTH_SHORT).show();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_update_info.setVisibility(View.VISIBLE);
									// 当前下载的百分比
									int progress = (int) (current * 100 / count);
									tv_update_info.setText("下载进度:" + progress
											+ "%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installAPK(t);
								}

								/**
								 * 安装APK
								 * 
								 * @param t
								 */
								private void installAPK(File t) {
									Intent intent = new Intent();
									// action category data可以定位窗口
									// extra和data可以传递数据
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}

							});

				} else {
					Toast.makeText(getApplicationContext(), "SD卡不存在",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();// 进入主界面
			}
		});
		builder.show();
	}

	/**
	 * 返回应用程序的版本号
	 * 
	 * @return
	 */
	private String getVersion() {
		// 用来管理手机的APK(包管理器)
		PackageManager pm = getPackageManager();
		try {
			// 得到指定APK的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
