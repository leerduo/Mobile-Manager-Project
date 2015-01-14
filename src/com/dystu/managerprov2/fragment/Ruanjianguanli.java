package com.dystu.managerprov2.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dystu.managerprov2.R;
import com.dystu.managerprov2.dao.AppLockDao;
import com.dystu.managerprov2.domain.AppInfo;
import com.dystu.managerprov2.engine.AppInfoProvider;
import com.dystu.managerprov2.utils.DensityUtil;
import com.dystu.managerprov2.widget.CircleImageView;



public class Ruanjianguanli extends Fragment implements OnClickListener{
	
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_appmanager;
	private LinearLayout ll_loading;
	private List<AppInfo> appInfos;
	private TextView tv_status;

	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private LinearLayout ll_uninstall;

	/**
	 * 被点击的条目
	 */
	private AppInfo appInfo;

	/**
	 * 用户程序集合
	 */
	private List<AppInfo> userAppInfos;

	/**
	 * 系统程序集合
	 */
	private List<AppInfo> systemAppInfos;

	private PopupWindow popupWindow;
	
	private AppLockDao dao;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.GONE);
			lv_appmanager.setAdapter(new AppInfoAdapter());
		};
	};

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

		dao = new AppLockDao(getActivity());
		tv_avail_rom = (TextView) getView().findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView)getView(). findViewById(R.id.tv_avail_sd);
		tv_avail_sd.setText("SD卡可用："
				+ getTotalSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		tv_avail_rom.setText("内存可用："
				+ getTotalSpace(Environment.getDataDirectory()
						.getAbsolutePath()));
		lv_appmanager = (ListView)getView(). findViewById(R.id.lv_app_manager);
		tv_status = (TextView) getView().findViewById(R.id.tv_status);

		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_appmanager.getItemAtPosition(position);
				if (obj != null) {
					dismissPopupWindow();
					appInfo = (AppInfo) obj;
					View contentView = View.inflate(getActivity(),
							R.layout.popup_app_item, null);
					ll_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_uninstall);
					ll_start = (LinearLayout) contentView
							.findViewById(R.id.ll_start);
					ll_share = (LinearLayout) contentView
							.findViewById(R.id.ll_share);
					ll_share.setOnClickListener(Ruanjianguanli.this);
					ll_start.setOnClickListener(Ruanjianguanli.this);
					ll_uninstall.setOnClickListener(Ruanjianguanli.this);

					// 播放动画有一个前提 就是窗体必须有背景
					popupWindow = new PopupWindow(contentView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					// ☆ 注意： 必须要设置背景
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					// 根据手机手机的分辨率 把60dip 转化成 不同的值 px
					int px = DensityUtil.dip2px(getActivity(), 60);
					System.out.println(px);
					popupWindow.showAtLocation(parent, Gravity.TOP
							+ Gravity.LEFT, px, location[1]);
					AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
					aa.setDuration(200);
					ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
							1.0f, Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(200);
					AnimationSet set = new AnimationSet(false);
					set.addAnimation(sa);
					set.addAnimation(aa);
					contentView.startAnimation(set);
				}

			}
		});
		
		/**
		 * 程序锁长点击事件
		 */
		lv_appmanager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position == 0) {// 显示一个textview告诉用户有多少个用户应用
					return true;
				} else if (position == (userAppInfos.size() + 1)) {
					return true;
				} else if (position <= userAppInfos.size()) {
					// 用户程序。
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {
					// 系统程序
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				System.out.println("长点击" + appInfo.toString());
				ViewHolder holder = (ViewHolder) view.getTag();
				//判断条目是否存在在程序锁数据库里面
				if (dao.find(appInfo.getPackname())) {
					//被锁定的程序，解除锁定，更新界面为打开的锁图片
					dao.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.strongbox_app_lock_ic_unlock);
				}else {
					//锁定程序，更新界面为关闭的锁
					dao.add(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.strongbox_app_lock_ic_locked);
				}
				
				
				
				
				
				//为true表示事件到这里就处理完毕了
				return true;
			}
		});
		
		
		// tv_status.setPadding(0, 0, 0, 0);
		lv_appmanager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序(" + systemAppInfos.size() + ")");
					} else {
						tv_status.setText("用户程序(" + userAppInfos.size() + ")");
					}
				}
			}
		});

		ll_loading = (LinearLayout)getView(). findViewById(R.id.ll_loading);

		fillData();

	
	}
	
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(getActivity());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appinfo : appInfos) {
					if (appinfo.isUserApp()) {
						userAppInfos.add(appinfo);
					} else {
						systemAppInfos.add(appinfo);
					}
				}

				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	
	/**
	 * 获取某个路径可用的空间
	 * 
	 * @param path
	 * @return
	 */
	public String getTotalSpace(String path) {
		StatFs stat = new StatFs(path);
		long count = stat.getAvailableBlocks();
		long size = stat.getBlockSize();
		return Formatter.formatFileSize(getActivity(), count * size);
	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	private class AppInfoAdapter extends BaseAdapter {
		

		@Override
		public int getCount() {
			// 多了两个textview的item 所以 +1 +1
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		// 控制每个位置显示的内容。
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;

			if (position == 0) {// 显示一个textview告诉用户有多少个用户应用
				TextView tv = new TextView(getActivity());
				tv.setText("用户程序  (" + userAppInfos.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getActivity());
				tv.setText("系统程序  (" + systemAppInfos.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {
				// 用户程序。
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				// 系统程序
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getActivity(),
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv = (CircleImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);
				view.setTag(holder);
			}

			holder.iv.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("手机内存" + "--->uid:" + appInfo.getUid()) ;
			} else {
				holder.tv_location.setText("外部存储" + "--->uid:" + appInfo.getUid());
			}
			
//			holder.iv_status.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					
//				}
//			});
			
			if (dao.find(appInfo.getPackname())) {
				holder.iv_status.setImageResource(R.drawable.strongbox_app_lock_ic_locked);
			}else {
				
				holder.iv_status.setImageResource(R.drawable.strongbox_app_lock_ic_unlock);
			}
			
			return view;
		}

		@Override
		public Object getItem(int position) {
			AppInfo appInfo;
			if (position == 0) {// 显示一个textview告诉用户有多少个用户应用
				return null;
			} else if (position == (userAppInfos.size() + 1)) {
				return null;
			} else if (position <= userAppInfos.size()) {
				// 用户程序。
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				// 系统程序
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	
		
	}
	
	static class ViewHolder {
		CircleImageView iv;
		TextView tv_name;
		TextView tv_location;
		ImageView iv_status;
	}
	
	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_share:
			Log.i(TAG, "分享：" + appInfo.getName());
			shareApplication();
			break;
		case R.id.ll_start:
			Log.i(TAG, "启动：" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_uninstall:
			Log.i(TAG, "卸载：" + appInfo.getName());
			uninstall();
			break;
		}
	}

	/**
	 * 开启应用
	 */
	private void startApplication() {
		Intent intent = new Intent();
		String packname = appInfo.getPackname();
		PackageManager pm = getActivity().getPackageManager();
		
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		//查询出来了手机上所有具有启动能力的Activity
//		List<ResolveInfo> queryIntentActivities = pm.queryIntentActivities(intent, 0);
		
		
		
		try {
			PackageInfo packinfo = pm.getPackageInfo(packname,
					PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = packinfo.activities;
			if (activityInfos != null && activityInfos.length > 0) {
				ActivityInfo activityinfo = activityInfos[0];
				intent.setClassName(packname, activityinfo.name);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "哎呀，这个应用程序没界面", 0).show();
			}
		} catch (NameNotFoundException e) { 
			e.printStackTrace();
			Toast.makeText(getActivity(), "没法开这个应用。", 0).show();
		}
	}

	/**
	 * 分享应用程序
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		// <action android:name="android.intent.action.SEND" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:mimeType="text/plain" />
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"推荐你使用一个软件，软件的名称叫："
						+ appInfo.getName()
						+ "，下载地址：https://play.google.com/store/apps/details?id="
						+ appInfo.getPackname());
		startActivity(intent);
	}

	private void uninstall() {
		if (appInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + appInfo.getPackname()));
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(getActivity(), "系统应用需要有root权限后才能卸载", 0).show();
		}
	}

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rjgl, container, false);
	}


	
	
}
