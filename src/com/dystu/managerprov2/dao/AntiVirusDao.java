package com.dystu.managerprov2.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 病毒数据库查询的业务类
 * 
 * @author 
 *
 */

public class AntiVirusDao {

	/**
	 * 查询一个md5是否在数据库中存在
	 * @param md5
	 * @return
	 */
	public static  boolean isVirus(String md5){
		
		String path = "/data/data/com.dystu.managerprov2/files/antivirus.db";
		
		boolean result = false;
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
}
