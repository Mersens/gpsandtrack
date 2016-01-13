package com.example.gpsandtrack;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.TraceLocation;
import com.example.gpsandtrack.R;
import com.example.gpsandtrack.utils.SharePreferenceUtil;

/**
 * 
 * @title TrackUploadManager
 * @description:ʵ���˶��켣�����ߣ������˶��켣�Ļ���
 * @author Mersens
 * @time 2016��1��13��
 */
public class TrackUploadManager {
	public static TrackUploadManager manager;
	protected static OnStartTraceListener startTraceListener = null;
	protected static OnStopTraceListener stopTraceListener = null;
	private int gatherInterval = 0;
	private static BitmapDescriptor realtimeBitmap;
	protected static OverlayOptions overlay;
	private static PolylineOptions polyline = null;
	private static List<LatLng> pointList = new ArrayList<LatLng>();
	protected boolean isTraceStart = false;
	protected RefreshThread refreshThread = null;
	protected static MapStatusUpdate msUpdate = null;
	protected static boolean isInUploadFragment = true;
	private Context context;

	// ˽�й��캯������ֹ�û�������ֻ��ͨ��getInstance()������ȡʵ��
	private TrackUploadManager(Context context) {
		this.context = context;
		init();

	}

	/**
	 * 
	 * @Title: getInstance
	 * @Description:TrackUploadManager ����ģʽ˫��У������ȷ��ʵ����Ψһ��
	 * @author Mersens
	 * @return
	 * @throws
	 */
	public static TrackUploadManager getInstance(Context context) {
		if (manager == null) {
			synchronized (TrackUploadManager.class) {
				if (manager == null) {
					manager = new TrackUploadManager(context);
				}
			}
		}
		return manager;
	}

	public void init() {
		initListener();
		setInterval();
		setRequestType();
	}

	private void initListener() {
		initOnStartTraceListener();
		initOnStopTraceListener();
	}

	private void initOnStartTraceListener() {
		startTraceListener = new OnStartTraceListener() {

			public void onTraceCallback(int arg0, String arg1) {

				if (0 == arg0 || 10006 == arg0 || 10008 == arg0) {
					isTraceStart = true;
					startRefreshThread(true);
				}
			}

			public void onTracePushCallback(byte arg0, String arg1) {
			}

		};
	}

	private void initOnStopTraceListener() {
		// ��ʼ��stopTraceListener
		stopTraceListener = new OnStopTraceListener() {
			// �켣����ֹͣ�ɹ�
			public void onStopTraceSuccess() {
				// TODO Auto-generated method stub
				isTraceStart = false;
				startRefreshThread(false);
			}

			public void onStopTraceFailed(int arg0, String arg1) {
			}
		};
	}

	// ������ѯ��ʽ���ض�ʱ������������
	protected class RefreshThread extends Thread {
		protected boolean refresh = true;

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (refresh) {
				// ��ѯʵʱ�켣
				queryRealtimeTrack();
				try {
					gatherInterval = SharePreferenceUtil.getInstance(
							context.getApplicationContext()).getTimerNum();
					if (gatherInterval == 0) {
						gatherInterval = 5;// Ĭ��5�����ѯ���
					}
					Thread.sleep(gatherInterval * 1000);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	private void setInterval() {
		// �������
		int packInterval = 30;
		MainActivity.client.setInterval(gatherInterval, packInterval);
	}

	public void startTrace() {
		MainActivity.client.startTrace(MainActivity.trace, startTraceListener);
	}

	public void stopTrace() {
		MainActivity.client.stopTrace(MainActivity.trace, stopTraceListener);
	}

	private void setRequestType() {
		int type = 0;
		MainActivity.client.setProtocolType(type);
	}

	private void queryRealtimeTrack() {
		MainActivity.client.queryRealtimeLoc(MainActivity.serviceId,
				MainActivity.entityListener);
	}

	protected void startRefreshThread(boolean isStart) {
		if (null == refreshThread) {
			refreshThread = new RefreshThread();
		}
		refreshThread.refresh = isStart;
		if (isStart) {
			if (!refreshThread.isAlive()) {
				refreshThread.start();
			}
		} else {
			refreshThread = null;

		}
	}

	/**
	 * 
	 * @Title: showRealtimeTrack
	 * @Description: ��ʾʱ�̵�λ��
	 * @author Mersens
	 * @param location
	 * @throws
	 */
	protected void showRealtimeTrack(TraceLocation location,boolean isAdd) {

		if (null == refreshThread || !refreshThread.refresh) {
			return;
		}

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		if (Math.abs(latitude - 0.0) < 0.000001
				&& Math.abs(longitude - 0.0) < 0.000001) {
		} else {

			LatLng latLng = new LatLng(latitude, longitude);

			if(isAdd){
				pointList.add(latLng);
			}
			if (isInUploadFragment) {
				// ����ʵʱ��
				drawRealtimePoint(latLng);
			}

		}

	}

	/**
	 * ����ʵʱ��
	 * 
	 * @param points
	 */
	private void drawRealtimePoint(LatLng point) {
		MainActivity.mBaiduMap.clear();
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18)
				.build();
		msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

		realtimeBitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);

		overlay = new MarkerOptions().position(point).icon(realtimeBitmap)
				.zIndex(9).draggable(true);

		if (pointList.size() >= 2 && pointList.size() <= 10000) {
			polyline = new PolylineOptions().width(10).color(Color.BLUE)
					.points(pointList);
		}

		addMarker();

	}

	/**
	 * ��ӵ�ͼ������
	 */
	protected static void addMarker() {

		if (null != msUpdate) {
			MainActivity.mBaiduMap.setMapStatus(msUpdate);
		}

		if (null != polyline) {
			MainActivity.mBaiduMap.addOverlay(polyline);
		}

		if (null != overlay) {
			MainActivity.mBaiduMap.addOverlay(overlay);
		}
	}
}
