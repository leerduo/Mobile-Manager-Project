package com.dystu.managerprov2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dystu.managerprov2.R;

/**
 * 
 * 自定义组合控件（两个TextView 一个CheckBox）
 * 
 * @author
 * 
 */

public class SettingItemView extends RelativeLayout {

	private CheckBox cb_status;

	private TextView tv_desc;
	private TextView tv_title;

	private String desc_on;

	private String desc_off;

	private void initView(Context context) {
		// 把一个布局文件转化为----》View并且加载在SettingItemView类中
		View view = View.inflate(context, R.layout.setting_item_view,
				SettingItemView.this);

		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
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

	public SettingItemView(Context context, AttributeSet attrs) {
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
				"http://schemas.android.com/apk/res/com.dystu.managerprov2",
				"title1");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.dystu.managerprov2",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.dystu.managerprov2",
				"desc_off");

		tv_title.setText(title);
		setDesc(desc_off);

	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 
	 * 校验组合控件是否选中
	 * 
	 */

	public boolean isChecked() {
		return cb_status.isChecked();
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
		cb_status.setChecked(checked);

	}

	/**
	 * 组合控件的描述信息
	 * 
	 */
	public void setDesc(String text) {

		tv_desc.setText(text);
	}
}
