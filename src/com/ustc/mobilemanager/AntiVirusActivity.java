package com.ustc.mobilemanager;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ustc.mobilemanager.db.dao.AntiVirusDao;

public class AntiVirusActivity extends Activity {

	private static final String TAG = "AntiVirusActivity";
	protected static final int SCANNING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_container;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case SCANNING:

				ScanInfo scanInfo = (ScanInfo) msg.obj;

				tv_scan_status.setText("正在扫描:" + scanInfo.name);
				
				TextView child = new TextView(getApplicationContext());
				
				if (scanInfo.isVirus) {
					child.setTextColor(Color.RED);
					child.setText("发现病毒:" + scanInfo.name);
				}else {
					child.setTextColor(Color.BLACK);
					child.setText("扫描安全:" + scanInfo.name);
				}
				
				ll_container.addView(child,0);

				break;
				
			case FINISH:
				tv_scan_status.setText("扫描完毕");
				iv_scan.clearAnimation();//停止动画
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		/*
		 * progressBar1.setMax(100); new Thread(new Runnable() {
		 * 
		 * @Override public void run() { for (int i = 0; i < 100; i++) { try {
		 * Thread.sleep(100); progressBar1.setProgress(i); } catch (Exception e)
		 * { e.printStackTrace(); } }
		 * 
		 * } }).start();
		 */
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.setAnimation(ra);

		scanVirus();

	}

	private void scanVirus() {
		pm = getPackageManager();
		tv_scan_status.setText("正在初始化杀毒引擎...");
		// 耗时操作,需要放在子线程中
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				progressBar1.setMax(infos.size());
				int progress = 0;
				for (PackageInfo info : infos) {
					// String dataDir = info.applicationInfo.dataDir;
					// apk文件的完整路径
					String sourceDir = info.applicationInfo.sourceDir;
					// Log.i(TAG, "dataDir:" + dataDir);
					Log.i(TAG, "sourceDir:" + sourceDir);
					String md5 = getFileMd5(sourceDir);
					System.out.println(info.applicationInfo.loadLabel(pm)
							+ "---->" + "md5:" + md5);
					// 查询md5信息，是否在病毒数据库中存在

					ScanInfo scanInfo = new ScanInfo();

					scanInfo.name = info.applicationInfo.loadLabel(pm)
							.toString();
					scanInfo.packname = info.packageName;

					if (AntiVirusDao.isVirus(md5)) {
						// 发现病毒
						scanInfo.isVirus = true;

					} else {
						// 扫描完全
						scanInfo.isVirus = false;

					}
					Message message = Message.obtain();

					message.obj = scanInfo;

					message.what = SCANNING;

					handler.sendMessage(message);

					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					progressBar1.setProgress(progress);
				}
				
				
				Message message = Message.obtain();

				message.what = FINISH;

				handler.sendMessage(message);
				

			}
		}).start();

	}

	/**
	 * 扫描信息的内部类
	 * 
	 * @author
	 *
	 */
	class ScanInfo {

		String packname;

		String name;

		boolean isVirus;

	}

	/**
	 * 获取文件的MD5值
	 * 
	 * @param path
	 *            文件的全路径名称
	 * @return
	 */
	private String getFileMd5(String path) {

		File file = new File(path);

		// 得到一个信息摘要器
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			// 把每一个byte做一个与运算oxff(11111111)
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
