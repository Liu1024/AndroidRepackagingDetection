package com.repackage.preprocess;

import java.util.ArrayList;

/**
 * Created by Shinelon on 2017/5/10.
 *
 * 预处理模块通过反编译目标应用程序 APK 文件，解析
 其中的 AndroidManifest.xml 文件获得目标应用的包名和所有
 Activity 名等信息，保存至数据库。

 */
public class PreProcess {
    private String appName;
    private String deviceName;
    private String platformVersion;
    private String appPackage;
    private String appActivity;
    //private ArrayList<String> Parameter;
    private ArrayList<ArrayList> wholeParameter;

    public PreProcess(){

        this.wholeParameter=new ArrayList<ArrayList>();
    }

    private void startReversal(){
        // 2017/5/17 反编译apps目录中的apk并存储相应信息，将五个信息全部保存在arraylist中，供UserSim调用，暂时写死
        this.appName="taobao.apk";
        this.deviceName="85UABM98KG2P";
        this.platformVersion="5.1.0";
        this.appPackage="com.taobao.taobao";
        this.appActivity="com.taobao.tao.welcome.Welcome";
        this.resetPara(wholeParameter.get(0));
    }

    private void resetPara(ArrayList parameter){
        parameter.add(this.appName);
        parameter.add(this.deviceName);
        parameter.add(this.platformVersion);
        parameter.add(this.appPackage);
        parameter.add(this.appActivity);
    }
    public ArrayList<ArrayList> getSettingPara(){
        this.startReversal();
        return this.wholeParameter;
    }


}
