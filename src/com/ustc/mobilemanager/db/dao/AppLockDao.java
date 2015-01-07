package com.ustc.mobilemanager.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.ustc.mobilemanager.db.AppLockDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * ���ݿ���ɾ�Ĳ�Ĺ�����
 *
 */
public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	/**
	 * ���췽����������ݿ�򿪰�����ĳ�ʼ��
	 * @param context
	 */
	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	/**
	 * ����һ��
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Uri uri = Uri.parse("content://com.ustc.mobilemanager/applockdb");
		context.getContentResolver().notifyChange(uri, null);
		
	}
	/**
	 * ɾ��һ��
	 * @param packname ����
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		Uri uri = Uri.parse("content://com.ustc.mobilemanager/applockdb");
		context.getContentResolver().notifyChange(uri, null);
	}
	
	
	/**
	 * ��ѯ
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
	 * ��ѯ���е�Ҫ�����İ���
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