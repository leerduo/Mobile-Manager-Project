package com.ustc.mobilemanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ustc.mobilemanager.R;

/**
 * 
 * 自定义组合控件（两个TextView 一个ImageView）
 * 
 * @author
 * 
 */

public class SettingClickView extends RelativeLayout {

	private TextView tv_desc;
	private TextView tv_title;

	private String desc_on;

	private String desc_off;

	private void initView(Context context) {
		// 把一个布局文件转化为----》View并且加载在SettingItemView类中
		View view = View.inflate(context, R.layout.setting_click_view,
				SettingClickView.this);

		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 
	 * 带有两个参数的构造方法，布局文件使用的时候调用
	 * 
	 * @param context
	 * @param attrs
	 */

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

		// String value = attrs.getAttributeValue(0);
		// System.out.println(value);
		// String value1 = attrs.getAttributeValue(1);
		// System.out.println(value1);
		// String value2 = attrs.getAttributeValue(2);
		// System.out.println(value2);
		// String value3 = attrs.getAttributeValue(3);
		// System.out.println(value3);
		// String value4 = attrs.getAttributeValue(4);
		// System.out.println(value4);
		// String value5 = attrs.getAttributeValue(5);
		// System.out.println(value5);

		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.ustc.mobilemanager",
				"title");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.ustc.mobilemanager",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.ustc.mobilemanager",
				"desc_off");

		tv_title.setText(title);
		setDesc(desc_off);

	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 
	 * 
	 * 设置组合控件的状态
	 */
	public void setChecked(boolean checked) {
		if (checked) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}

	}

	/**
	 * 组合控件的描述信息
	 * 
	 */
	public void setDesc(String text) {

		tv_desc.setText(text);
	}

	/**
	 * 组合控件的标题
	 * 
	 */
	public void setTitle(String title) {

		tv_title.setText(title);
	}

}
