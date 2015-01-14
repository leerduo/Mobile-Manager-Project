package com.dystu.managerprov2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

/**
 * 
 * 短信的工具类
 * @author 
 *
 */
public class SmsUtils {
	/**
	 * 
	 * 备份短信的回调接口
	 * 
	 * @author 
	 *
	 */
	public interface BackUpCallBack{
		/**
		 * 开始备份的时候，需要设置最大的进度
		 * @param max
		 */
		public void beforeBackUp(int max);
		/**
		 * 备份的过程中，增加进度
		 * @param progress 进度
		 */
		public void onBackUp(int progress);
		
	}
	
	
	/**
	 * 
	 * 还原短信的接口
	 * 
	 */
	public interface RestoreSms{
		public void beforeRestore(String max);
		public void onRestore(int progress);
	}
	
	
	
	/**
	 * 备份用户的短信
	 * 
	 * @param context
	 * @param ProgressDialog 进度条
	 * @throws Exception
	 */
	public static void backupSms(Context context,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(context.getFilesDir(),"smsbackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		//把用户的信息一条一条的读出来，按照一定的格式写到文件里
//		<smss>
//			<sms>
//				<body>你好啊</body>
//				<date>2014年12月18日</date>
//				<type>1</type>（发送还是接受）
//				<address>号码</address>
//			</sms>
//		</smss>
		//获取xml文件的序列化器
		XmlSerializer serializer = Xml.newSerializer();
		//初始化序列化器
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);//头
		serializer.startTag(null, "smss");//根
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, null, null, null);
		//开始备份的时候，设置进度条的最大值
		int max = cursor.getCount();
		
		callBack.beforeBackUp(max);
		serializer.attribute(null, "max", max+"");
		
		int process = 0;
		
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			
			
			serializer.startTag(null, "sms");
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			serializer.endTag(null, "sms");
			//备份过程中，增加进度
			process++;
			callBack.onBackUp(process);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
	/**
	 * 还原短信
	 * @param context
	 * @param flag 是否清理原来的短信
	 * @throws Exception 一定是抛出异常而不是捕获异常
	 */
	public static void restoreSms(Context context,boolean flag,RestoreSms restoreSms) throws Exception{
		Uri uri = Uri.parse("content://sms/");
		if (flag) {
			//清除旧的短信
			context.getContentResolver().delete(uri, null, null);
		}
		XmlPullParser parser = Xml.newPullParser();
		
		
		//1.读取sd卡上的xml文件
		
		File file = new File(context.getFilesDir(),"smsbackup.xml");
		FileInputStream fis = new FileInputStream(file);
		parser.setInput(fis, "utf-8");
		int eventType = parser.getEventType();
		
		//2.读取max(属性)
		String attributeValue = parser.getAttributeValue(null, "max");
		restoreSms.beforeRestore(attributeValue);
		
		//3.读取每一条的短信信息body date type address
		
		String body = null;
		String date = null;
		String type = null;
		String address = null;
		
		
		//4.把短信插入到短信的系统应用
		ContentValues values = new ContentValues();
		int progress = 0;
		while (eventType!=XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("body".equals(tagName)) {
					body = parser.nextText();
				}else if ("date".equals(tagName)) {
					date = parser.nextText();
				}else if ("type".equals(tagName)) {
					type = parser.nextText();
				}else if ("address".equals(tagName)) {
					address = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				
				values.put("body", body);
				values.put("date", date);
				values.put("type", type);
				values.put("address", address);
				
				context.getContentResolver().insert(uri, values);
				
				
				break;
				
				
				
			default:
				break;
			}
			eventType = parser.next();
			progress++;
			restoreSms.onRestore(progress);
		}
		
		fis.close();
		
		
		
		
	}

}
