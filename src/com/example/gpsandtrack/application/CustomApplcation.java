package com.example.gpsandtrack.application;

import java.util.LinkedList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.app.Application;

public class CustomApplcation extends Application {
	public static CustomApplcation mInstance;
	// ����list��������ÿһ��activity�ǹؼ�
	private List<Activity> mList = new LinkedList<Activity>();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
	}
	
	public static CustomApplcation getInstance() {
		if(mInstance==null){
			synchronized (CustomApplcation.class) {
				if(mInstance==null){
					mInstance=new CustomApplcation();	
				}
			}
		}
		return mInstance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	// �ر�ÿһ��list�ڵ�activity
	//��˵���ǰ�׿�������ŵ��˳���ʽ  O.o
	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
}
