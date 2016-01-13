package com.example.gpsandtrack;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.gpsandtrack.R;
import com.example.gpsandtrack.entity.TrackBean;
import com.example.gpsandtrack.utils.DateUtils;
import com.example.gpsandtrack.utils.GsonUtils;

/**
 * 
 * @title TrackQueryManager
 * @description:Track运动轨迹管理者，负责运动轨迹的绘制
 * @author Mersens
 * @time 2016年1月13日
 */
public class TrackQueryManager {
	public static TrackQueryManager manager;
    private int startTime = 0;
    private int endTime = 0;
    private static BitmapDescriptor bmStart;
    private static BitmapDescriptor bmEnd;
    private static MarkerOptions startMarker = null;
    private static MarkerOptions endMarker = null;
    private static PolylineOptions polyline = null;
    private MapStatusUpdate msUpdate = null;
    
    //私有构造函数，禁止用户创建，只能通过getInstance()方法获取实例
	private TrackQueryManager(){
		
	}
	/**
	 * 
	 * @Title: getInstance 
	 * @Description: TrackQueryManager单例模式双重校验锁，确保实例的唯一性
	 * @author Mersens
	 * @return manager
	 * @throws
	 */
	public static TrackQueryManager getInstance() {
		if (manager == null) {
			synchronized (TrackQueryManager.class) {
				if (manager == null) {
					manager = new TrackQueryManager();
				}
			}
		}
		return manager;
	}
   /**
    * 
    * @Title: queryHistoryTrack 
    * @Description: 查询历史轨迹
    * @author Mersens
    * @param time
    * @throws
    */
    protected void queryHistoryTrack(String time) {
    	String str[]=time.split("-");
        String st = str[0] + "年" + str[1] + "月" + str[2] + "日0时0分0秒";
        String et = str[0] + "年" + str[1] + "月" + str[2] + "日23时59分59秒";
        startTime = Integer.parseInt(DateUtils.getTimeToStamp(st));
        endTime = Integer.parseInt(DateUtils.getTimeToStamp(et));
        // entity标识
        String entityName = MainActivity.entityName;
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;

        MainActivity.client.queryHistoryTrack(MainActivity.serviceId, entityName, simpleReturn, startTime, endTime,
                pageSize,
                pageIndex,
                MainActivity.trackListener);
    }
    /**
     * 
     * @Title: showHistoryTrack 
     * @Description: 显示历史轨迹
     * @author Mersens
     * @param historyTrack
     * @throws
     */
    protected void showHistoryTrack(String historyTrack) {

        TrackBean historyTrackData = GsonUtils.parseJson(historyTrack,
                TrackBean.class);

        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }
            // 绘制历史轨迹
            drawHistoryTrack(latLngList, historyTrackData.distance);

        }

    }
    
    /**
     * 
     * @Title: drawHistoryTrack 
     * @Description: 绘制历史轨迹 
     * @author Mersens
     * @param points
     * @param distance
     * @throws
     */
    private void drawHistoryTrack(final List<LatLng> points, final double distance) {
        // 绘制新覆盖物前，清空之前的覆盖物
        MainActivity.mBaiduMap.clear();

        if (points == null || points.size() == 0) {
            resetMarker();
        } else if (points.size() > 1) {
            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);

            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            polyline = new PolylineOptions().width(10)
                    .color(Color.BLUE).points(points);

            addMarker();


        }

    }

    /**
     * 
     * @Title: addMarker 
     * @Description: 添加地图图层覆盖物
     * @author Mersens
     * @throws
     */
    protected void addMarker() {

        if (null != msUpdate) {
            MainActivity.mBaiduMap.setMapStatus(msUpdate);
        }

        if (null != startMarker) {
            MainActivity.mBaiduMap.addOverlay(startMarker);
        }

        if (null != endMarker) {
            MainActivity.mBaiduMap.addOverlay(endMarker);
        }

        if (null != polyline) {
            MainActivity.mBaiduMap.addOverlay(polyline);
        }

    }

    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
    }
}
