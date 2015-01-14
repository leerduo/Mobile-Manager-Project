package com.dystu.managerprov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dystu.managerprov2.fragment.Anquanfanghu;
import com.dystu.managerprov2.fragment.Changyonggongneng;
import com.dystu.managerprov2.fragment.Ruanjianguanli;
import com.dystu.managerprov2.fragment.Yinsibaohu;

public class MainActivity extends FragmentActivity {
	
	
	
	private Button[] mTabs;
	
	private Changyonggongneng cygnFragment;
	
	private Ruanjianguanli rjglFragment;
	
	private Anquanfanghu aqfhFragment;
	
	private Yinsibaohu ysbhFragment;
	
	
	private Fragment[] fragments;
	
	private int index;
	
	 private RelativeLayout[] tab_containers;
	
	 private int currentTabIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 initView();
		cygnFragment = new Changyonggongneng();
		rjglFragment = new Ruanjianguanli();
		aqfhFragment = new Anquanfanghu();
		ysbhFragment = new Yinsibaohu();
	    fragments = new Fragment[] { cygnFragment, rjglFragment,
	        		aqfhFragment, ysbhFragment };
	 // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, cygnFragment)
                .add(R.id.fragment_container, rjglFragment)
                .hide(rjglFragment).show(cygnFragment).commit();
        
       
		
	}

	/**
	 * 
	 * 初始化底部菜单的状态
	 * 
	 */
	private void initView() {
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		mTabs[2] = (Button) findViewById(R.id.btn_find_list);
		mTabs[3] = (Button) findViewById(R.id.btn_setting);

		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		
	}
	
	/**
     * button点击事件
     * 
     * @param view
     */
    public void onTabClicked(View view) {
    	switch (view.getId()) {
		case R.id.btn_conversation:
			index = 0;
			break;
		case R.id.btn_address_list:
			index = 1;
			break;
		case R.id.btn_find_list:
			index = 2;
			break;
		case R.id.btn_setting:
			index = 3;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
    }
  
   
    
}
