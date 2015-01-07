package com.ustc.mobilemanager.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StringUtils {
	
	/**
	 * 
	 * @param is 输入流
	 * @return  返回的字符串
	 * @throws Exception
	 */

	public static String readFromStream(InputStream is) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = is.read(buff)) != -1) {
			baos.write(buff, 0, len);
		}
		is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}

}
