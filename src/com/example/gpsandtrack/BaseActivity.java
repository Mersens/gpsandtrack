package com.example.gpsandtrack;


import com.example.gpsandtrack.application.CustomApplcation;
import com.example.gpsandtrack.view.HeadView;
import com.example.gpsandtrack.view.HeadView.HeaderStyle;
import com.example.gpsandtrack.view.HeadView.OnLeftClickListener;
import com.example.gpsandtrack.view.HeadView.OnRightClickListener;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * 
 * @title BaseActivity
 * @description:BaseActivity
 * @author Mersens
 * @time 2016年1月11日
 */
public class BaseActivity extends FragmentActivity{
	private int mScreenWidth;
	private int mScreenHeight;
	private  Toast mToast;
	private Activity activity;
	protected HeadView mHeadView;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		//设置禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		activity=this;
		CustomApplcation.getInstance().addActivity(activity);
		//设备屏幕的高度和宽度
		DisplayMetrics metrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenHeight=metrics.heightPixels;
		mScreenWidth=metrics.widthPixels;
	}

	/**
	 * @author Mersens
	 * setDefaultViewMethod--默认显示左侧按钮，标题和右侧按钮
	 * @param leftsrcid
	 * @param title
	 * @param rightsrcid
	 * @param onleftclicklistener
	 * @param onrightclicklistener
	 */
	public void setDefaultViewMethod(int leftsrcid,String title,int rightsrcid, OnLeftClickListener onleftclicklistener,OnRightClickListener onrightclicklistener) {
		mHeadView=(HeadView) findViewById(R.id.common_actionbar);
		mHeadView.init(HeaderStyle.DEFAULT);
		mHeadView.setDefaultViewMethod(leftsrcid,title,rightsrcid,onleftclicklistener,onrightclicklistener);
	}
	
	/**
	 * @author Mersens
	 * setRightAndTitleMethod--显示右侧按钮和标题
	 * @param title
	 * @param rightsrcid
	 * @param onRightClickListener
	 */
	public void setRightAndTitleMethod(String title,int rightsrcid, OnRightClickListener onRightClickListener){
		mHeadView=(HeadView) findViewById(R.id.common_actionbar);
		mHeadView.init(HeaderStyle.RIGHTANDTITLE);
		mHeadView.setRightAndTitleMethod(title, rightsrcid, onRightClickListener);
	}
	
	
	/**
	 * @author Mersens
	 * setLeftWithTitleViewMethod--显示左侧按钮和标题
	 * @param leftsrcid
	 * @param title
	 * @param onleftclicklistener
	 */
	public void setLeftWithTitleViewMethod(int leftsrcid,String title, OnLeftClickListener onleftclicklistener){
		mHeadView=(HeadView) findViewById(R.id.common_actionbar);
		mHeadView.init(HeaderStyle.LEFTANDTITLE);
		mHeadView.setLeftWithTitleViewMethod( leftsrcid, title,  onleftclicklistener);
	}
	
	/**
	 * @author Mersens
	 * setOnlyTileViewMethod--只显示标题
	 * @param title
	 */
	public void setOnlyTileViewMethod(String title) {
		mHeadView=(HeadView) findViewById(R.id.common_actionbar);
		mHeadView.init(HeaderStyle.ONLYTITLE);
		mHeadView.setOnlyTileViewMethod(title);
	}

	/**
	 * @author Mersens
	 * setLeftViewMethod--只显示左侧按钮
	 * @param leftsrcid
	 * @param onleftclicklistener
	 */
	public void setLeftViewMethod(int leftsrcid,OnLeftClickListener onleftclicklistener) {
		mHeadView=(HeadView) findViewById(R.id.common_actionbar);
		mHeadView.init(HeaderStyle.LEFT);
		mHeadView.setLeftViewMethod( leftsrcid, onleftclicklistener);
	}
	
	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int srcid) {
		if (mToast == null) {
			mToast = Toast.makeText(activity, srcid, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(srcid);
		}
		mToast.show();
	}
	

	public int getScreenWidth(){
		return mScreenWidth;
	}
	

	public int getScreenHeight(){
		return mScreenHeight;
	}
	

}
