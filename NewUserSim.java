package com.repackage;

import com.repackage.constant.Action;
import com.repackage.entity.Config;
import com.repackage.entity.UiNode;
import com.repackage.parser.AndroidXmlParser;
import com.repackage.parser.ConfigProvider;
import com.repackage.parser.LayoutInfEx;
import com.repackage.parser.XmlFilter;
import com.repackage.parser.XmlParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

/**
 * Created by Shinelon on 2017/8/17.
 */



public class NewUserSim {
    private AndroidDriver<AndroidElement> driver;
    private String apkName=null;
    private String appName=null;
    private String deviceName=null;
    private String platformVersion=null;
    private String appPackage=null;
    private String appActivity=null;
    private LayoutInfEx layoutInfEx;
    Stack<UiNode> taskStack;
    protected Config config;
    protected XmlParser parser;
    String homePageSource;
    protected String configFile;
    UiNode homeNode;
    private ArrayList<String> wholeWindowsID;
    private HashMap windowsIndexMap;//代表界面ID和界面序号的键值对，界面ID为主键
    private Integer windowsIndex;//代表界面的序号，其中主界面序号为0，新界面按照第一次被遍历的顺序记录序号，外部APP的序号默认都为-1（实际上最终的index值即为遍历到的总界面数，不包括外部APP界面，但包括Web界面）
    private HashMap winTransition;//代表界面序号和界面OPFlows的键值对，界面序号为主键
    private HashMap OPFlow;//代表界面序号和触发该界面的按钮的xpth，界面序号为主键
    private String xpath;
    private long startMili;
    private long endMili;
    private long delayMili;
    private static final int MAX_DEPTH=0;//最大遍历深度，0表示不限深度，单位层
    private static final int MAX_TIME=0;//最大遍历时间，0表示不限时,单位分钟
    private static final int DELAY_FIRST_START=5000;//应用初次启动时等待时间。单位毫秒，最佳值5000ms，因为第一次启动，比较慢。一些app有广告，所以需要的时间更长
    private static final int DELAY_NORMAL=1000;//每次点击事件后等待时间，单位毫秒，最佳值1000ms，因为有可能在点击后，界面会发生好几次跳转，等界面稳定后才继续后续操作

    public NewUserSim(){
        this.startMili=0;
        this.endMili=0;
        this.delayMili=0;
        this.layoutInfEx = new LayoutInfEx();
        this.deviceName="85UABM98KG2P";
        this.platformVersion="5.1.0";
        this.apkName="wechat";
        if(apkName.equals("toutiao")){
            this.appName="toutiao633.apk";
            this.appPackage="com.ss.android.article.news";
            this.appActivity="com.ss.android.article.news.MainActivityBadge0";
            this.layoutInfEx.apkName="toutiao633";
        }
        if(apkName.equals("zuiyou")){
            this.appName="zuiyou3673.apk";
            this.appPackage="cn.xiaochuankeji.tieba";
            this.appActivity="cn.xiaochuankeji.tieba.ui.base.SplashActivity";
            this.layoutInfEx.apkName="zuiyou";
        }
        if(apkName.equals("taobao")){
            this.appName="taobao.apk";
            this.appPackage="com.taobao.taobao";
            this.appActivity="com.taobao.tao.welcome.Welcome";
            this.layoutInfEx.apkName="taobao";
        }
        if(apkName.equals("wechat")){
            this.appName="wechat6.5.8encryptedtencent.apk";
            this.appPackage="com.tencent.mm";
            this.appActivity="com.tencent.mm.ui.LauncherUI";
            this.layoutInfEx.apkName="wechat6.5.8encryptedtencent";
        }
        if(apkName.equals("hupu")){
            this.appName="hupu.apk";
            this.appPackage="com.hupu.games";
            this.appActivity="com.hupu.games.home.activity.HupuHomeActivity";
            this.layoutInfEx.apkName="hupu";
        }
        if(apkName.equals("qq")){
            this.appName="qq715.apk";
            this.appPackage="com.tencent.mobileqq";
            this.appActivity="com.tencent.mobileqq.activity.SplashActivity";
            this.layoutInfEx.apkName="qq715";
        }
        if(apkName.equals("qqi")){
            this.appName="qqi521.apk";
            this.appPackage="com.tencent.mobileqqi";
            this.appActivity="com.tencent.mobileqq.activity.SplashActivity";
            this.layoutInfEx.apkName="qqi521";
        }
        if(apkName.equals("qqlite")){
            this.appName="qqlite360.apk";
            this.appPackage="com.tencent.qqlite";
            this.appActivity="com.tencent.mobileqq.activity.SplashActivity";
            this.layoutInfEx.apkName="qqlite360";
        }
        this.configFile="config/android.xml";
        this.config=new ConfigProvider().getConfig(this.configFile);
        this.taskStack=null;
        this.parser = new AndroidXmlParser(config);
        this.homeNode=new UiNode();
        this.wholeWindowsID=new ArrayList();
        this.windowsIndexMap=new HashMap();
        this.windowsIndex=0;
        this.winTransition=new HashMap();
        this.OPFlow=new HashMap();
        this.xpath=null;
    }

    @Before
    public void setUp() throws Exception {
        File classpathRoot = new File(System.getProperty("user.dir"));
        //app的目录
        File appDir = new File(classpathRoot, "/src/main/java/apps/");
        //app的名字，对应你apps目录下的文件
        File app = new File(appDir, this.appName);
        //创建Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        //设置要调试的模拟器的名字
        capabilities.setCapability("deviceName", this.deviceName);
        //设置模拟器的系统版本
        capabilities.setCapability("platformVersion", this.platformVersion);
        //设置app的路径
        capabilities.setCapability("app", app.getAbsolutePath());
        //设置app的包名
        capabilities.setCapability("appPackage", this.appPackage);
        //设置app的启动activity
        capabilities.setCapability("appActivity", this.appActivity);
        capabilities.setCapability("unicodeKeyboard", "True");
        capabilities.setCapability("resetKeyboard", "True");
        capabilities.setCapability("noReset",true);
        if((this.apkName.equals("qq"))||(this.apkName.equals("qqi"))){
            capabilities.setCapability("appWaitActivity","com.tencent.mobileqq.activity.LoginActivity");
            //capabilities.setCapability("appWaitActivity","com.tencent.mobileqq.activity.RegisterGuideActivity");
        }
        if(this.apkName.equals("qqlite")){
            capabilities.setCapability("appWaitActivity","com.tencent.mobileqq.activity.RegisterGuideActivity");
        }
        //启动driver
        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @After
    public void tearDown() throws Exception {
        //测试完毕，关闭driver，不关闭将会导致会话还存在，下次启动就会报错
        driver.quit();
    }

    @Test
    public void startSim() throws Exception {
        TimeUnit.MILLISECONDS.sleep(this.DELAY_FIRST_START);//初次启动
        this.startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
        this.homePageSource= XmlFilter.filter(driver.getPageSource());
        //保存当前窗口ID
        String homeWinID=parser.getCurrentWindowID(this.homePageSource);
        this.wholeWindowsID.add(homeWinID);
        //保存WLT
        homeNode.setWindowID(homeWinID);
        homeNode.setInfo("homeNode");
        homeNode.setWindowsIDTransition("null");
        homeNode.setBounds("null");
        homeNode.setWinIndex(0);
        this.windowsIndexMap.put(homeWinID,0);
        this.winTransition.put(0,"0");
        //保存主页面
        this.layoutInfEx.makeWLT(this.homePageSource,homeNode);
        this.taskStack=this.getTaskStack(this.homePageSource,1);
        /*System.out.println("获取完成");
        System.out.println("当前可点击控件数为："+this.taskStack.size());*/
        System.out.println("########################### 开始执行探索性遍历测试 ###########################");
        this.dfsSearch(this.taskStack,this.MAX_DEPTH,this.MAX_TIME);
        this.layoutInfEx.saveDocument();
        this.layoutInfEx.saveCoreWLT();
        this.endMili=System.currentTimeMillis();
        System.out.println("总耗时为："+(endMili-startMili+delayMili)/60000+"分钟");
        System.out.println("拼接和保存WLT耗时为："+(this.layoutInfEx.totalMili)/1000+"秒");
        System.out.println("遍历APP耗时为："+((endMili-startMili)-(this.layoutInfEx.totalMili))/60000+"分钟");
        System.out.println("总遍历界面数："+(this.windowsIndex+1));
        System.out.println("总界面跳转数:"+this.layoutInfEx.totalTranNum);
    }



    private void dfsSearch(Stack<UiNode> taskStack, int max_depth,int max_time){
        UiNode curTask,preTask=null,transitionNode=new UiNode();
        String curPageSource,curWindow;
        WebElement element;
        Stack<UiNode> children,existsTaskStack;
        int newTaskCount;
        while(!taskStack.isEmpty()){
            System.out.println("任务栈不为空，获取栈顶任务");
            long curMili=System.currentTimeMillis();
            if((max_time!=0)&&((curMili-this.startMili)/60000)>max_time){//超时直接结束遍历
                break;
            }
            curTask=taskStack.peek();
            try{
                //System.out.println("有1个任务出栈，节点信息为：[" + curTask.getInfo() + "]。还有"+ (taskStack.size()-1) + "个节点任务待运行" );
                //获取当前窗口ID
                curPageSource = XmlFilter.filter(driver.getPageSource());
                curWindow = parser.getCurrentWindowID(curPageSource);
                /*System.out.println("当前窗口ID："+curWindow+"当前任务窗口ID:"+curTask.getWindowID());
                System.out.println("当前窗口布局："+curPageSource);
                System.out.println("当前节点任务所属界面是否就是当前界面 -> " + curTask.getWindowID().equals(curWindow));*/
                if (curWindow.equals(curTask.getWindowID())){
                    System.out.println("任务属于当前界面，开始点击按钮");
                    xpath = curTask.getId().split("-")[3];
                    element = driver.findElement(By.xpath(xpath));
                    if (curTask.getAction().equals(Action.CLICK)) {
                        //System.out.println(Action.CLICK + " -> " + "[info = " + curTask.getInfo() + "], [depth = " + curTask.getDepth() + "]" + curTask.getId());
                        element.click();
                        preTask=taskStack.pop();
                        try{
                            TimeUnit.MILLISECONDS.sleep(this.DELAY_NORMAL);
                        }catch (Exception e){
                            this.delayMili-=this.DELAY_NORMAL;
                        }
                        this.delayMili-=this.DELAY_NORMAL;
                    }
                    else{
                        //其他操作暂时没处理，直接执行下一个任务
                        //System.out.println("其他操作暂时没处理，直接执行下一个任务");
                        preTask=taskStack.pop();
                    }
                }
                else{
                    transitionNode.setBounds(preTask.getBounds());
                    if(!this.wholeWindowsID.contains(curWindow)){
                        //新界面
                        this.layoutInfEx.getPackageByPS(curPageSource);
                        String curPackage=this.layoutInfEx.findPackage;
                        this.layoutInfEx.isWebViewByPS(curPageSource);
                        Boolean isWebView=this.layoutInfEx.isWeb;
                        if((isWebView==true)){
                            //保留OPFlows
                            System.out.println("跳转至Web界面，现在执行下一个任务");
                            this.windowsIndex++;
                            this.windowsIndexMap.put(curWindow,this.windowsIndex);
                            String preWinTran= (String) this.winTransition.get(this.windowsIndexMap.get(preTask.getWindowID()));
                            String thisWinTran=preWinTran+"+"+this.windowsIndex;
                            this.winTransition.put(this.windowsIndex,thisWinTran);
                            this.OPFlow.put(this.windowsIndex,xpath);
                            // 将web信息添加至WLT中，Web界面也算是一个新的界面，但只保留序号，不保留界面信息
                            transitionNode.setInfo("transitionWeb");
                            transitionNode.setWinIndex(this.windowsIndex);
                            transitionNode.setWinIndexTransition(this.windowsIndexMap.get(preTask.getWindowID())+"TO"+this.windowsIndex);
                            transitionNode.setPreWindowID(preTask.getWindowID());
                            transitionNode.setWindowID(curWindow);
                            transitionNode.setWindowsIDTransition(preTask.getWindowID()+" "+curWindow);
                            layoutInfEx.makeWLT(curPageSource,transitionNode);
                            //标记新界面
                            this.wholeWindowsID.add(curWindow);
                            //返回目标界面
                            this.backWithOPFlow(curTask.getWindowID());
                            String windowTemp = parser.getCurrentWindowID(XmlFilter.filter(driver.getPageSource()));
                            if(!windowTemp.equals(curTask.getWindowID())){
                                this.removeDeadNode(curTask.getWindowID());
                            }
                            else{
                                //System.out.println("成功回到目标界面");
                            }
                        }else if(!curPackage.equals(this.appPackage)){
                            // 将外界信息添加到WLT中，仅保留跳转信息
                            System.out.println("跳转至外部APP，现在执行下一个任务");
                            transitionNode.setInfo("transitionOtherAPP"+curPackage);//保存跳转的APP的包名
                            transitionNode.setWinIndex(-1);
                            transitionNode.setWinIndexTransition(this.windowsIndexMap.get(preTask.getWindowID())+"TO"+(-1));
                            transitionNode.setPreWindowID(preTask.getWindowID());
                            transitionNode.setWindowID(curWindow);
                            transitionNode.setWindowsIDTransition(preTask.getWindowID()+" "+curWindow);
                            layoutInfEx.makeWLT(curPageSource,transitionNode);
                            //返回目标界面
                            this.backWithOPFlow(curTask.getWindowID());
                            String windowTemp = parser.getCurrentWindowID(XmlFilter.filter(driver.getPageSource()));
                            if(!windowTemp.equals(curTask.getWindowID())){
                                this.removeDeadNode(curTask.getWindowID());
                            }
                            else{
                                //System.out.println("成功回到目标界面");
                            }
                        }else if((null!=preTask)&&(max_depth!=0)&&((preTask.getDepth()+1)>max_depth)){//超过遍历最大深度
                                System.out.println("超过最大遍历深度，现在执行下一个任务");
                                this.backWithOPFlow(curTask.getWindowID());
                                String windowTemp = parser.getCurrentWindowID(XmlFilter.filter(driver.getPageSource()));
                                if(!windowTemp.equals(curTask.getWindowID())){
                                    this.removeDeadNode(curTask.getWindowID());
                                }
                                else{
                                    //System.out.println("成功回到目标界面");
                                }
                        }else{
                            //符合全部前提条件，进行新界面的遍历

                            //保存OPFlows
                            this.windowsIndex++;
                            System.out.println("获取新界面："+this.windowsIndex);
                            this.windowsIndexMap.put(curWindow,this.windowsIndex);
                            String preWinTran= (String) this.winTransition.get(this.windowsIndexMap.get(preTask.getWindowID()));
                            String thisWinTran=preWinTran+"+"+this.windowsIndex;
                            this.winTransition.put(this.windowsIndex,thisWinTran);
                            this.OPFlow.put(this.windowsIndex,xpath);
                                    //应该是这样存：将上一个窗口和本窗口，以及跳转结点保存下来，也就是保留相邻的跳转关系
                                    //winTransition，保存<newWinIndex，indexFrom0TonewWinIndex>
                                    // OPFlow保存<newWinIndex，xpath>
                                    //每一个窗口只会在key位置出现一次
                            //保存WLT
                            transitionNode.setInfo("transitionNew");
                            transitionNode.setWinIndex(this.windowsIndex);
                            transitionNode.setWinIndexTransition(this.windowsIndexMap.get(preTask.getWindowID())+"TO"+this.windowsIndex);
                            transitionNode.setPreWindowID(preTask.getWindowID());
                            transitionNode.setWindowID(curWindow);
                            transitionNode.setWindowsIDTransition(preTask.getWindowID()+" "+curWindow);
                            layoutInfEx.makeWLT(curPageSource,transitionNode);
                            //标记新界面
                            this.wholeWindowsID.add(curWindow);
                            //新任务入栈
                            children = getTaskStack(curPageSource, preTask.getDepth() + 1);
                            newTaskCount = null != children ? children.size() : 0;
                            //System.out.println(newTaskCount + "个新任务准备入栈......");
                            // 如果有新的节点任务生成,把当前节点任务先压栈,新生成的节点任务出栈
                            if (null != children && children.size() > 0) {
                                //System.out.println(children.size() + "个新任务允许入栈......");
                                taskStack.addAll(children);
                                //System.out.println("任务栈已更新, " + children.size() + "个新任务允许入栈, 现在还有" + taskStack.size() + "个任务待运行");
                                // 更新任务栈后,新任务出栈
                            }
                            if (newTaskCount == 0 || children.size() == 0 ) {
                                //System.out.println("界面虽然发生跳转,但没有新任务加入");
                            }
                        }
                    }
                    else{
                        //旧界面
                        //记录跳转信息至WLT中
                        transitionNode.setInfo("transitionPre");
                        transitionNode.setWinIndex((Integer) this.windowsIndexMap.get(curWindow));
                        transitionNode.setWinIndexTransition(this.windowsIndexMap.get(preTask.getWindowID())+"TO"+this.windowsIndexMap.get(curWindow));
                        transitionNode.setPreWindowID(preTask.getWindowID());
                        transitionNode.setWindowID(curWindow);
                        transitionNode.setWindowsIDTransition(preTask.getWindowID()+" "+curWindow);
                        //判断上一个窗口和本窗口有无变化，没有变化则说明：窗口没有发生跳转，不保存跳转信息
                        if(!preTask.getWindowID().equals(curWindow)){
                            layoutInfEx.makeWLT(curPageSource,transitionNode);
                        }
                        //获取旧界面的未完成任务
                        existsTaskStack = searchByWindowID(curWindow, taskStack);
                        if(null==existsTaskStack){
                            //旧界面任务已完成
                            System.out.println("跳转至老界面，但老界面任务已全部完成，回到目标任务所在界面");
                            this.backWithOPFlow(curTask.getWindowID());
                            String psTemp=XmlFilter.filter(driver.getPageSource());
                            String windowTemp = parser.getCurrentWindowID(psTemp);
                            if(!windowTemp.equals(curTask.getWindowID())){
                                this.removeDeadNode(curTask.getWindowID());
                                //  2017/8/31 这个的话，每次在OPFlow寻找过程中产生异常的话，都走不到这一步，需要在backWithOP中抛出返回失败的异常，并在处理这个异常中执行removeDeadNode操作
                            }
                            else{
                                /*System.out.println("成功回到目标界面");
                                System.out.println("当前窗口ID："+windowTemp+"当前任务窗口ID:"+curTask.getWindowID());
                                System.out.println("当前窗口布局："+psTemp);*/
                            }
                        }else{
                            //旧界面任务未完成
                            System.out.println("跳转至老界面,获取该界面剩余任务：" + existsTaskStack.size()+"个");
                            resetTaskStack(taskStack, existsTaskStack);
                            //System.out.println("任务栈已更新,现在还有" + taskStack.size() + "个任务待运行");
                        }
                    }
                }
            }catch (NoSuchElementException e) {
                System.out.println("ERROR：任务 -> [info = " + curTask.getInfo() + "], NoSuchElementException, 弹出下一个节点任务" );
                taskStack.pop();
                continue;
            } catch (org.openqa.selenium.ElementNotVisibleException e) {
                System.out.println("ERROR：任务 -> [info = " + curTask.getInfo() + "], ElementNotVisibleException, 弹出下一个节点任务 " );
                taskStack.pop();
                continue;
            } catch (org.openqa.selenium.NoSuchSessionException e) {
                System.out.println("ERROR：会话丢失,退出 >> 任务 -> [info = " + curTask.getInfo() + "], NoSuchSessionException, 弹出下一个节点任务" );
                taskStack.pop();
                continue;
            } catch (org.openqa.selenium.SessionNotCreatedException e) {
                System.out.println("ERROR：会话未创建,退出 >> 任务 -> [info = " + curTask.getInfo() + "], SessionNotCreatedException, 弹出下一个节点任务" );
                taskStack.pop();
                continue;
            } catch (org.openqa.selenium.NotFoundException e) {
                System.out.println("ERROR：任务 -> [info = " + curTask.getInfo() + "], NotFoundException, 弹出下一个节点任务 " );
                taskStack.pop();
                continue;
            }catch (Exception e) {
                System.out.println("ERROR：任务 -> [info = " + curTask.getInfo() + "], 发生未知异常, 弹出下一个节点任务 " );
                taskStack.pop();
                continue;
            }

        }
    }





    private void removeDeadNode(String windowID) {
        try {
            System.out.println("开始移除目标任务及相关任务...");
            int j = this.taskStack.size();
            for (int i = 0; i < j; i++) {
                if (this.taskStack.get(i).getWindowID().equals(windowID)) {
                    this.taskStack.remove(i);
                }
            }
            System.out.println("移除成功");
        }catch (Exception e){
            System.out.println("移除失败，原因："+e);
        }
    }


    private void backWithOPFlow(String targetWindow) throws Exception {
        int j= (int) this.windowsIndexMap.get(targetWindow);
        System.out.println("back to Window："+j+"...");
        String xpathBack="";
        WebElement elementBack;
        //System.out.println("启动主页面");
        driver.startActivity(this.appPackage,this.appActivity);
        try {
            TimeUnit.MILLISECONDS.sleep(this.DELAY_FIRST_START);
        } catch (InterruptedException e) {
            System.out.println("UserSim-ERROR:"+e);
            this.delayMili-=this.DELAY_NORMAL;
        }
        this.delayMili-=this.DELAY_FIRST_START;
        if(j!=0){
            String winTran= (String) this.winTransition.get(j);
            System.out.println("OPFlow:"+winTran);
            String[] winIndex=winTran.split("\\+");
            for(int k=1;k<winIndex.length;k++){
                System.out.println("index:"+winIndex[k]);
                xpathBack= (String) this.OPFlow.get(Integer.valueOf(winIndex[k]));
                //System.out.println("xpath:"+xpathBack);
                elementBack = driver.findElement(By.xpath(xpathBack));
                elementBack.click();
                System.out.println("点击成功");
                try {
                    TimeUnit.MILLISECONDS.sleep(this.DELAY_NORMAL);
                } catch (InterruptedException e) {
                    System.out.println("UserSim-ERROR:"+e);
                    this.delayMili-=this.DELAY_NORMAL;
                }
                this.delayMili-=this.DELAY_NORMAL;
            }
        }
    }

    /**
     * 根据窗口ID在任务栈中搜索节点,并返回结果
     *
     * @param windowID
     * @param nodeStack
     * @return
     */
    protected Stack<UiNode> searchByWindowID(String windowID, Stack<UiNode> nodeStack) {
        try {
            Stack<UiNode> searchStack = new Stack<>();
            for (int i = 0; i < nodeStack.size(); i++) {
                if (nodeStack.get(i).getWindowID().equals(windowID)) {
                    searchStack.push(nodeStack.get(i));
                }
            }
            searchStack = searchStack.size() > 0 ? searchStack : null;
            return searchStack;
        }catch (Exception e){
            System.out.println("searchByWindowID:"+e);
            return null;
        }
    }

    /**
     * 重置任务栈
     *
     * @param taskStack
     * @param existsTaskStack
     */
    protected void resetTaskStack(Stack<UiNode> taskStack, Stack<UiNode> existsTaskStack) {
        try{
            taskStack.removeAll(existsTaskStack);
            taskStack.addAll(existsTaskStack);
        }catch (Exception e){
            System.out.println("resetTaskStack:"+e);
        }

    }


    public Stack<UiNode> getTaskStack(String source,int depth){
        List<UiNode> tempForReverse;
        Stack<UiNode> taskStack=null;
        try {
            taskStack = getTaskStackByXml(source, depth);
            if (taskStack != null && config.getReverse() == 1) {
                tempForReverse = new ArrayList<>();
                for (int i = taskStack.size() - 1; i >= 0; i--) {
                    tempForReverse.add(taskStack.get(i));
                }
                taskStack.clear();
                taskStack.addAll(tempForReverse);
            }
        }catch (Exception e){
            System.out.println("getTaskStack:"+e);
        }
        return taskStack;
    }

    /**
     * 从xml获取任务栈
     *
     * @param pageSource
     * @param depth
     * @return
     */
    protected Stack<UiNode> getTaskStackByXml(String pageSource, int depth) {
        //System.out.println("depth:"+depth);
        Stack<UiNode> taskStack;
        taskStack = parser.getNodesFromWindow(pageSource, depth);
        return taskStack;
    }


}

