package com.example.gpsandtrack.data;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @title Datas
 * @description:������ѯʱ��������(maxʱ��30��)
 * @author Mersens
 * @time 2016��1��13��
 */
public class Datas {

	private static Datas data = null;
	private List<String> list = null;

	private Datas() {
	   initDatas();
	}

	public static Datas getInstance() {
		if (data == null) {
			synchronized (Datas.class) {
				if (data == null) {
					data = new Datas();
				}
			}
		}
		return data;
	}

	public List<String> getTimerDatas() {
		return new ArrayList<String>(list);
	}

	private void initDatas() {
		list=new ArrayList<String>();
		list.add("��ѡ��");
		for (int i = 1; i < 31; i++) {
			list.add(i + " ��");
		}

	}
}
