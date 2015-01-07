package com.ustc.mobilemanager.test;

import java.util.List;
import java.util.Random;

import com.ustc.mobilemanager.db.BlackNumberDBOpenHelper;
import com.ustc.mobilemanager.db.dao.BlackNumberDao;
import com.ustc.mobilemanager.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {

	/**
	 * 测试创建数据库的方法
	 * 
	 */
	public void testCreateDB() {

		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				getContext());

		helper.getWritableDatabase();

	}

	public void testAdd() {

		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 13512345;
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber + i),
					String.valueOf(random.nextInt(3) + 1));
		}

	}

	public void testUpdate() {

		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");

	}

	public void testDelete() {

		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");

	}

	public void testFind() {

		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean find = dao.find("110");
		assertEquals(true, find);

	}

	public void testFindAll() {

		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> findAll = dao.findAll();

		for (BlackNumberInfo blackNumberInfo : findAll) {
			System.out.println(blackNumberInfo.toString());
		}

	}

}
