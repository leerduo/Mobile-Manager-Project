package com.ustc.mobilemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.定义手势识别器
	private GestureDetector detector;
	
	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		
		// setContentView(R.layout.activity_base_setup);
		// 2.实例化手势识别器

		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			/**
			 * 
			 * 手势滑动的监听器
			 * 
			 * 手指在上面滑动的时候回调
			 * 
			 */

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//屏蔽在x轴滑动很慢的情形
				if (Math.abs(velocityX) < 200) {
					Toast.makeText(getApplicationContext(), "滑动的太慢啦!", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				
				//屏蔽斜着滑这种情况
				if (Math.abs((e2.getRawY() - e1.getRawY())) > 100) {
					Toast.makeText(getApplicationContext(), "不能这样滑!", Toast.LENGTH_SHORT).show();
					return true;
				}
				if ((e2.getRawX() - e1.getRawX()) > 200) {
					// 显示上一个页面:从左往右滑动
					showBack();
					return true;
				}
				if ((e1.getRawX() - e2.getRawX()) > 200) {
					// 显示下一个页面:从右往左滑动
					showNext();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}

	public abstract void showBack();

	public abstract void showNext();

	// 下一步的点击事件
	public void next(View view) {

		showNext();

	}

	// 上一步的点击事件
	public void back(View view) {
		showBack();
	}

	// 3.使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		detector.onTouchEvent(event);

		return super.onTouchEvent(event);
	}

}
