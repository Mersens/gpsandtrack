package com.example.gpsandtrack;

import java.util.List;

import com.example.gpsandtrack.data.Datas;
import com.example.gpsandtrack.utils.SharePreferenceUtil;
import com.example.gpsandtrack.view.HeadView.OnLeftClickListener;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
/**
 * 
 * @title SettingActivity
 * @description 设置轮询查询的时间间隔
 * @author Mersens
 * @time 2016年1月13日
 */
public class SettingActivity extends BaseActivity {
	private Spinner spinner_timernum;
	private List<String> list;
	private ArrayAdapter<String> adapter;
	private int timerNum=0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_setting);
		initViews();
		initEvent();
	}

	private void initViews() {
		spinner_timernum = (Spinner) findViewById(R.id.spinner_timernum);
		setLeftWithTitleViewMethod(R.drawable.ic_menu_back, "设置",
				new OnLeftClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(0, R.anim.bottom_close);
					}
				});
	}

	private void initEvent() {
		list = Datas.getInstance().getTimerDatas();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timerNum=SharePreferenceUtil.getInstance(getApplicationContext()).getTimerNum();
		spinner_timernum.setAdapter(adapter);
		spinner_timernum.setSelection(timerNum, true);
		spinner_timernum
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (position != 0) {
							SharePreferenceUtil.getInstance(
									getApplicationContext()).setTimerNum(position);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

	}

}
