package com.repackage.bigStep;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;

/**
 * Created by Shinelon on 2017/7/4.
 */
public class Handler  {

    StartsActivity st;
    AndroidDriver ad;



    /**
     * 处理窗口跳转
     * 如果当前窗口不是待处理窗口，也就是产生了窗口跳转，则点击返回
     * 如果无效则直接启动待处理窗口
     */

    public Handler(){

    }

    /***
     * 判断当前窗口是否属于测试APP
     * @param activity，context
     * @return 属于返回true
     */
    private boolean isCurrentApp(Activity context,String activity){
        ComponentName cn = getRunningTask(context);
        String pkg = cn.getPackageName();
        String act = cn.getClassName();
        Log.d("Handler","pkg:"+pkg+" act:"+act);
        return true;

    }

    /**
     * Return a list of the tasks that are currently running.
     * @param context context
     * @return ComponentName Return only the top one.
     */
    private ComponentName getRunningTask(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningTasks(1).get(0).topActivity;
    }

}
