package com.dystu.managerprov2.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dystu.managerprov2.db.BlackNumberDBOpenHelper;
import com.dystu.managerprov2.domain.BlackNumberInfo;

/**
 * 
 * 黑名单数据库的增删改差的业务类
 * 
 * @author
 * 
 */

public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		// 实例化
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 查询黑名单是否存在
	 * 
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {

		boolean result = false;

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from blacknumber where number=?",
				new String[] { number });

		while (cursor.moveToNext()) {
			result = true;
		}

		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询黑名单号码的拦截模式
	 * 
	 * 
	 * @param number
	 * @return
	 */
	public String findMode(String number) {

		String result = null;

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?",
				new String[] { number });

		while (cursor.moveToNext()) {
			result = cursor.getString(0);
		}

		cursor.close();
		db.close();
		return result;
	}


	/**
	 * 查询全部黑名单是否存在
	 * 
	 * 
	 * @param number
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {

		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db
				.rawQuery("select number,mode from blacknumber order by _id desc", null);

		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();

			String number = cursor.getString(0);

			String mode = cursor.getString(1);

			info.setMode(mode);
			info.setNumber(number);
			result.add(info);
		}

		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询部分黑名单是否存在
	 * @param offset 从哪个位置开始获取数据
	 * @param maxnumber 一次最多获取多少条记录
	 * @return
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber) {

		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db
				.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?", new String[]{String.valueOf(maxnumber),String.valueOf(offset)});

		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();

			String number = cursor.getString(0);

			String mode = cursor.getString(1);

			info.setMode(mode);
			info.setNumber(number);
			result.add(info);
		}

		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            黑名单的号码
	 * @param mode
	 *            拦截模式 1.电话拦截2.短信拦截3.全部拦截
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);

		db.insert("blacknumber", null, values);

		db.close();
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * 
	 * @param number
	 *            黑名单的号码
	 * @param newmode
	 *            新的拦截模式
	 */
	public void update(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            黑名单的号码
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}

}
