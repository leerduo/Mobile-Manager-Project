package com.dystu.managerprov2.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dystu.managerprov2.db.AppLockDBOpenHelper;

/**
 * 数据库增删改查的工具类
 *
 */
public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	/**
	 * 构造方法中完成数据库打开帮助类的初始化
	 * @param context
	 */
	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	/**
	 * 添加一条
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Uri uri = Uri.parse("content://com.dystu.managerprov2/applockdb");
		context.getContentResolver().notifyChange(uri, null);
		
	}
	/**
	 * 删除一条
	 * @param packname 包名
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		Uri uri = Uri.parse("content://com.dystu.managerprov2/applockdb");
		context.getContentResolver().notifyChange(uri, null);
	}
	
	
	/**
	 * 查询
	 * @param packname
	 * @return
	 */
	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询所有的要保护的包名
	 * @return
	 */
	public List<String> findAll(){
		List<String> results = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			String packname = cursor.getString(0);
			results.add(packname);
		}
		cursor.close();
		db.close();
		return results;
	}
	
}
