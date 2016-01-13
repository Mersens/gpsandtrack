package com.example.gpsandtrack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.example.gpsandtrack.application.AppConstants;
import com.example.gpsandtrack.view.DatePickerPopWindow;
import com.example.gpsandtrack.view.HeadView.OnRightClickListener;


/**
 * 
 * @title MainActivity
 * @description:地图显示的主页面
 * @author Mersens
 * @time 2016年1月13日
 */
public class MainActivity extends BaseActivity {
	protected static Trace trace = null;
	protected static String entityName = null;
	protected static long serviceId = AppConstants.SERVICE_ID;
	private int traceType = 2;
	protected static LBSTraceClient client = null;
	protected static OnEntityListener entityListener = null;
	protected static OnTrackListener trackListener = null;
	protected static MapView bmapView = null;
	protected static BaiduMap mBaiduMap = null;
	protected static PopupWindow pop;
	protected static LayoutInflater mInflater;
	protected static Context mContext = null;
	private Button btn_mylocation;
	private TraceLocation mLocation = null;
	public static final String ACTION_OK = "ok";
	public static final String ACTION_CANCEL = "cancel";
	private DatePickerPopWindow popWindow = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mInflater = LayoutInflater.from(this);
		registerBoradcastReceiver();
		initViews();
		initEvent();
	}

	private void initViews() {
		// 初始化HeadView
		setRightAndTitleMethod("定位追踪", R.drawable.btn_more,
				new OnRightClickListener() {
					@Override
					public void onClick(View v) {
						if (pop != null && pop.isShowing()) {
							pop.dismiss();
							return;
						} else {
							initPop(v);
						}
					}
				});
		bmapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = bmapView.getMap();
		bmapView.showZoomControls(false);

		btn_mylocation = (Button) findViewById(R.id.btn_mylocation);
	}

	private void initEvent() {
		mContext = getApplicationContext();
		client = new LBSTraceClient(mContext);
		entityName = getImei(mContext);
		trace = new Trace(getApplicationContext(), serviceId, entityName,
				traceType);
		initOnEntityListener();
		initOnTrackListener();
		client.setOnTrackListener(trackListener);
		addEntity();
		btn_mylocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			if (null != mLocation) {
					TrackUploadManager.getInstance(getApplicationContext()).showRealtimeTrack(
							mLocation,false);
				}
			}
		});
	}

	/**
	 * 
	 * @Title: getImei
	 * @Description:获取设备的id
	 * @author Mersens
	 * @param context
	 * @return
	 * @throws
	 */
	protected static String getImei(Context context) {
		String mImei = "NULL";
		try {
			mImei = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
			System.out.println("获取IMEI码失败");
			mImei = "NULL";
		}
		return mImei;
	}

	/**
	 * 
	 * @Title: initOnEntityListener
	 * @Description: entity监听，如果接收到移动位置新坐标，则通知 TrackUploadManager更新地图
	 * @author Mersens
	 * @throws
	 */
	private void initOnEntityListener() {
		entityListener = new OnEntityListener() {

			@Override
			public void onRequestFailedCallback(String arg0) {

			}

			@Override
			public void onAddEntityCallback(String arg0) {
			}

			@Override
			public void onQueryEntityListCallback(String message) {

			}

			@Override
			public void onReceiveLocation(TraceLocation location) {
				// 接收到新坐标
				mLocation = location;
				TrackUploadManager.getInstance(getApplicationContext()).showRealtimeTrack(location,true);
			}

		};
	}

	/**
	 * 
	 * @Title: initOnTrackListener
	 * @Description: Track监听，主要查询某一实体在某一时刻的运行轨迹坐标，查询后TrackQueryManager负责更新地图
	 * @author Mersens
	 * @throws
	 */
	private void initOnTrackListener() {

		trackListener = new OnTrackListener() {

			@Override
			public void onRequestFailedCallback(String arg0) {
			}

			@Override
			public void onQueryHistoryTrackCallback(String arg0) {
				// TODO Auto-generated method stub
				super.onQueryHistoryTrackCallback(arg0);
				//查询成功后的接口回调
				TrackQueryManager.getInstance().showHistoryTrack(arg0);
			}

		};
	}

	/**
	 * 
	 * @Title: addEntity 
	 * @Description: 添加地图上的运动实体(entityName 以手机的id作为实体的唯一标示) 
	 * @author Mersens
	 * @throws
	 */
	protected static void addEntity() {
		String entityName = MainActivity.entityName;
		String columnKey = "";
		client.addEntity(serviceId, entityName, columnKey, entityListener);
	}

	RelativeLayout layout_trackupload, layout_tracquery,layout_setting;

	
	/**
	 * 
	 * @Title: initPop 
	 * @Description: 点击HeadView右侧按钮后弹出菜单
	 * @author Mersens
	 * @param v
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "InflateParams" })
	public void initPop(View v) {
		View view = mInflater.inflate(R.layout.popview_item, null);
		layout_trackupload = (RelativeLayout) view
				.findViewById(R.id.layout_trackupload);
		layout_tracquery = (RelativeLayout) view
				.findViewById(R.id.layout_tracquery);
		layout_setting=(RelativeLayout) view.findViewById(R.id.layout_setting);
		layout_tracquery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TrackUploadManager.getInstance(getApplicationContext()).stopTrace();
				showPop();
				pop.dismiss();
			}
		});

		layout_trackupload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TrackUploadManager.getInstance(getApplicationContext()).startTrace();
				pop.dismiss();
			}
		});
		layout_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				Intent intent = new Intent(MainActivity.this,
						SettingActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.bottom_open, 0);
			}
		});
		pop = new PopupWindow(view);
		pop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		pop.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		pop.setTouchable(true);
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setAnimationStyle(R.style.GrowFromRight);
		pop.showAsDropDown(mHeadView, 0, 2, Gravity.RIGHT);
		view.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
					pop = null;
				}
				return false;
			}
		});
	}

	/**
	 * 
	 * @Title: showPop 
	 * @Description:点击轨迹查询时，弹出日期选择器，查询特定时刻一个实体的运行轨迹。 
	 * @author Mersens
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	private void showPop() {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		popWindow = new DatePickerPopWindow(MainActivity.this,
				df.format(new Date()), 0);
		popWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popWindow.setTouchable(true);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(false);
		popWindow.setAnimationStyle(R.style.GrowFromBottom);
		popWindow.showAtLocation(findViewById(R.id.layout_main),
				Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 定义广播接收者，回传用户选择的日期
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_OK)) {
				String time = intent.getStringExtra("time");
				if (!TextUtils.isEmpty(time)) {
					TrackQueryManager.getInstance().queryHistoryTrack(time);
				}
				popDismiss();
			} else if (action.equals(ACTION_CANCEL)) {
				popDismiss();
			}
		}
	};

	public void popDismiss() {
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		}
	}

	//注册广播
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_OK);
		myIntentFilter.addAction(ACTION_CANCEL);
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		TrackUploadManager.getInstance(getApplicationContext()).startTrace();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TrackUploadManager.getInstance(getApplicationContext()).stopTrace();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}
}
