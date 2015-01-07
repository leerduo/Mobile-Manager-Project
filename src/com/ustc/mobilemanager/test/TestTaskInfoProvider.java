package com.ustc.mobilemanager.test;

import java.util.List;

import com.ustc.mobilemanager.domain.TaskInfo;
import com.ustc.mobilemanager.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class TestTaskInfoProvider extends AndroidTestCase {

	public void testGetTaskInfo() throws Exception {

		List<TaskInfo> infos = TaskInfoProvider.getTaskInfo(getContext());

		for (TaskInfo taskInfo : infos) {
			System.out.println(taskInfo.toString());
		}

	}

}
