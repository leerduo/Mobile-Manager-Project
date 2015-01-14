package com.dystu.managerprov2;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dystu.managerprov2.dao.BlackNumberDao;
import com.dystu.managerprov2.domain.BlackNumberInfo;

public class CallSmsSaveActivity extends Activity {

	private ListView listView;

	private List<BlackNumberInfo> infos;

	private BlackNumberDao dao;

	private CallSmsSafeAdapter safeAdapter;

	private LinearLayout ll_loading;

	private int offset = 0;
	private int maxnumber = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_call_sms_save);
		listView = (ListView) findViewById(R.id.list);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDao(this);
		
		final List<BlackNumberInfo> findAll = dao.findAll();
		//final int size = findAll.size();
		//System.out.println("size:" + size);

		fillData();

		// listview注册一个滚动事件的监听器
		listView.setOnScrollListener(new OnScrollListener() {
			/**
			 * 当滚动的状态发生变化的时候
			 * 
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 空闲状态
				case OnScrollListener.SCROLL_STATE_IDLE:
					System.out.println("SCROLL_STATE_IDLE");

					// 判断当前listview滚动的位置
					// 获取ListView最后一个可见条目在集合里面的位置
					int lastVisiblePosition = listView.getLastVisiblePosition();

					System.out.println(lastVisiblePosition);

					if (lastVisiblePosition == (infos.size() - 1)) {
						System.out.println("列表被移动到了最后一个位置，加载更多数据。。。");
						offset += maxnumber;
						
						if (offset >= findAll.size()) {
							Toast.makeText(getApplicationContext(), "亲，数据已经加载完毕啦!", 0).show();
							ll_loading.setVisibility(View.INVISIBLE);
						}else {
							fillData();
						}
					//	System.out.println("offset:" + offset);
					//	System.out.println("列表的长度："+ infos.size());
						
						
						
						
						
					}

					break;
				// 手指触摸滑动
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					System.out.println("SCROLL_STATE_TOUCH_SCROLL");
					break;
				// 惯性滑行状态
				case OnScrollListener.SCROLL_STATE_FLING:
					System.out.println("SCROLL_STATE_FLING");
					break;
				default:
					break;
				}
			}

			/**
			 * 
			 * 滚动的时候调用的方法
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		// 更新的方法（没有做好）
		// listView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// final int position, long id) {
		// AlertDialog.Builder builder = new Builder(
		// CallSmsSaveActivity.this);
		// final AlertDialog dialog = builder.create();
		//
		// view = View.inflate(CallSmsSaveActivity.this,
		// R.layout.dialog_add_blacknumber, null);
		//
		// cancel = (Button) view.findViewById(R.id.cancel);
		// ok = (Button) view.findViewById(R.id.ok);
		// cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		// cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
		// et_blacknumber = (EditText) view
		// .findViewById(R.id.et_blacknumber);
		//
		// dialog.setView(view, 0, 0, 0, 0);
		//
		// dialog.show();
		//
		// cancel.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// dialog.dismiss();
		// }
		// });
		// ok.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// String blacknumber = infos.get(position).getNumber();
		// et_blacknumber.setText(blacknumber);
		// if (TextUtils.isEmpty(blacknumber)) {
		// Toast.makeText(getApplicationContext(),
		// "黑名单号码不能为空!", 0).show();
		// return;
		// }
		// String mode;
		// if (cb_phone.isChecked() && cb_sms.isChecked()) {
		// // 全部拦截
		// mode = "3";
		//
		// } else if (cb_phone.isChecked()) {
		// // 电话拦截
		// mode = "1";
		//
		// } else if (cb_sms.isChecked()) {
		// // 短信拦截
		// mode = "2";
		// } else {
		//
		// Toast.makeText(getApplicationContext(), "请选择拦截模式!",
		// 0).show();
		// return;
		// }
		// // 数据被更新
		// dao.update(blacknumber, mode);
		// // 更新ListView的内容
		//
		// BlackNumberInfo info = new BlackNumberInfo();
		//
		// info.setMode(mode);
		//
		// info.setNumber(blacknumber);
		//
		// infos.add(0, info);
		//
		// // 通知适配器数据更新了
		//
		// safeAdapter.notifyDataSetChanged();
		//
		// dialog.dismiss();
		// }
		// });
		//
		// }
		// });
	}

	private void fillData() {
		List<BlackNumberInfo> all = dao.findAll();
		final int size = all.size();
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				if (infos == null) {
					infos = dao.findPart(offset, maxnumber);
				}else {
					infos.addAll(dao.findPart(offset, maxnumber));
				}
				//这样写的问题？旧的数据还会存在吗？。。。所以加了上面的判断
				//infos = dao.findPart(offset, maxnumber);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (safeAdapter == null) {
							safeAdapter = new CallSmsSafeAdapter();
							listView.setAdapter(safeAdapter);
							
						}else {
							safeAdapter.notifyDataSetChanged();
						}
						
						
						//每次加载新的数据，旧的数据就会跑到第一页的第一个item，蛋疼
						//safeAdapter = new CallSmsSafeAdapter();
						//listView.setAdapter(safeAdapter);

					}
				});
			};
		}.start();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;

			ViewHolder viewHolder;

			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_callsms, null);

				viewHolder = new ViewHolder();

				viewHolder.tv_black_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				viewHolder.tv_black_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);

				viewHolder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				view.setTag(viewHolder);

			} else {
				view = convertView;

				viewHolder = (ViewHolder) view.getTag();

			}

			viewHolder.tv_black_number.setText(infos.get(position).getNumber());

			String mode = infos.get(position).getMode();

			if ("1".equals(mode)) {
				viewHolder.tv_black_mode.setText("电话拦截");

			} else if ("2".equals(mode)) {
				viewHolder.tv_black_mode.setText("短信拦截");
			} else {
				viewHolder.tv_black_mode.setText("全部拦截");
			}
			// viewHolder.iv_delete.setTag(position);
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsSaveActivity.this);
					builder.setTitle("提示");
					builder.setMessage("确定删除这条记录吗?");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									System.out.println("删除");

									// 删除数据库的内容
									dao.delete(infos.get(position).getNumber());

									// 更新界面
									infos.remove(position);

									safeAdapter.notifyDataSetChanged();

								}
							});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
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

	static class ViewHolder {
		TextView tv_black_number;
		TextView tv_black_mode;
		ImageView iv_delete;
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

				infos.add(0, info);

				// 通知适配器数据更新了

				safeAdapter.notifyDataSetChanged();

				dialog.dismiss();
			}
		});

	}
	
	public static void actionStart(Context context){
		Intent intent = new Intent(context, CallSmsSaveActivity.class);
		
		context.startActivity(intent);
		
		
	}

}
