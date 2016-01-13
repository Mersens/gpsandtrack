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
 * @description:Track�˶��켣�����ߣ������˶��켣�Ļ���
 * @author Mersens
 * @time 2016��1��13��
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
    
    //˽�й��캯������ֹ�û�������ֻ��ͨ��getInstance()������ȡʵ��
	private TrackQueryManager(){
		
	}
	/**
	 * 
	 * @Title: getInstance 
	 * @Description: TrackQueryManager����ģʽ˫��У������ȷ��ʵ����Ψһ��
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
    * @Description: ��ѯ��ʷ�켣
    * @author Mersens
    * @param time
    * @throws
    */
    protected void queryHistoryTrack(String time) {
    	String str[]=time.split("-");
        String st = str[0] + "��" + str[1] + "��" + str[2] + "��0ʱ0��0��";
        String et = str[0] + "��" + str[1] + "��" + str[2] + "��23ʱ59��59��";
        startTime = Integer.parseInt(DateUtils.getTimeToStamp(st));
        endTime = Integer.parseInt(DateUtils.getTimeToStamp(et));
        // entity��ʶ
        String entityName = MainActivity.entityName;
        // �Ƿ񷵻ؾ���Ľ����0 : ��1 : �ǣ�
        int simpleReturn = 0;
        // ��ʼʱ��
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // ��ҳ��С
        int pageSize = 1000;
        // ��ҳ����
        int pageIndex = 1;

        MainActivity.client.queryHistoryTrack(MainActivity.serviceId, entityName, simpleReturn, startTime, endTime,
                pageSize,
                pageIndex,
                MainActivity.trackListener);
    }
    /**
     * 
     * @Title: showHistoryTrack 
     * @Description: ��ʾ��ʷ�켣
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
            // ������ʷ�켣
            drawHistoryTrack(latLngList, historyTrackData.distance);

        }

    }
    
    /**
     * 
     * @Title: drawHistoryTrack 
     * @Description: ������ʷ�켣 
     * @author Mersens
     * @param points
     * @param distance
     * @throws
     */
    private void drawHistoryTrack(final List<LatLng> points, final double distance) {
        // �����¸�����ǰ�����֮ǰ�ĸ�����
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
     * @Description: ��ӵ�ͼͼ�㸲����
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
