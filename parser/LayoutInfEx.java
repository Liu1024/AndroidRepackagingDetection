package com.repackage.parser;

import com.repackage.entity.UiNode;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shinelon on 2017/5/10
 *
 * 布局信息融合模块：
 * （1）将布局xml文件保存下来
 * （2）整合成WholeLayoutTree并保存
 *
 *
 */
public class LayoutInfEx {

    private String myLayoutStr;
    private Document myLayoutXML;
    private ArrayList<Document> wholeLayoutTree;
    private Document myWLT;
    private String myCoreWLT;
    private Document newPage;
    private ArrayList myElementList;
    private String triggerNodeBounds;
    private String triggerNodeInfo;
    private String triggerNodeWinID;
    private String triggerNodePreWinID;

    private String triggerNodeWinIDTrans;
    private String triggerNodeWinIndexTrans;
    private long startMili;
    private long endMili;
    public long totalMili;
    public String apkName;
    public String findPackage;
    public boolean isWeb;
    public int totalTranNum;


    public LayoutInfEx(){
        this.myCoreWLT="";
        this.apkName="default";
        this.myLayoutStr=null;
        this.myLayoutXML=null;
        this.myElementList=new ArrayList();
        this.wholeLayoutTree=new ArrayList<Document>();
        this.myWLT=null;
        this.triggerNodeInfo=null;
        this.totalMili=0;
        this.findPackage="null";
        this.isWeb=false;
        this.totalTranNum=0;
    }
    public void setMyLayoutStr(String layoutStr){
        this.myLayoutStr=layoutStr;
    }
    public String getMyLayoutStr(){
        return this.myLayoutStr;
    }
    public void setMyLayoutXML(Document layoutXML){
        this.myLayoutXML=layoutXML;
    }
    public Document getMyLayoutXML(){
        return this.myLayoutXML;
    }



    public void saveDocument()  {//最后再保存
        try {
            this.startMili = System.currentTimeMillis();
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = null;
            Integer i;
            System.out.println("正在保存全局布局树文件：" + this.apkName + "WLT.xml...");
            //  2017/7/10 在WLT中保存界面的MD5值有问题，因为一旦页面有一点变化，会造成MD5完全变化，这样会让WLT相似检测有很大误差，本来两个界面只有一点变化，因为MD5完全不同而使得相似度大幅降低
            //  2017/7/11 目前的想法是：1 在录入WLT时取消界面的WinID属性，改为index属性，表示该页面出现的顺序，在transitionNode中也同样替换为indexTransition属性
            writer = new XMLWriter(new FileOutputStream(this.apkName + "WLT.xml"), format);
            writer.write(this.myWLT);
            writer.close();
            this.endMili = System.currentTimeMillis();
            this.totalMili += (this.endMili - this.startMili);
            //System.out.println("拼接和保存WLT耗时为："+(this.totalMili)/1000+"秒");
        }catch (Exception e){
            //e.printStackTrace();
            System.out.println("saveDocument:"+e);
        }
    }


    public void makeWLT (String pageSource, UiNode triggerNode) {
        try {
            this.startMili = System.currentTimeMillis();
            this.triggerNodePreWinID = triggerNode.getPreWindowID();
            this.triggerNodeWinID = triggerNode.getWindowID();
            this.triggerNodeWinIDTrans = triggerNode.getWindowsIDTransition();
            this.triggerNodeWinIndexTrans = triggerNode.getWinIndexTransition();
            this.triggerNodeInfo = triggerNode.getInfo();
            this.triggerNodeBounds = triggerNode.getBounds();
            Document myPageSource = null;

                myPageSource = DocumentHelper.parseText(pageSource);

            this.newPage = myPageSource;
            insertWinID(this.newPage, this.triggerNodeWinID);
            insertWinIndex(this.newPage, triggerNode.getWinIndex());
            //makeItCore(this.newPage);
            if (triggerNode.getInfo().equals("homeNode")) {
                //该页面是主页面
                //System.out.println("获取主页面完成");
                this.myWLT = this.newPage;
            } else {
                //该页面是由触发节点产生的

                this.totalTranNum++;
                if (triggerNode.getInfo().equals("transitionNew")) {
                    //跳转到了新的界面，将页面信息和转换节点信息都加入到WLT中
                    //查找该节点并插入
                    //System.out.println("获取新的界面，正在拼接WLT...");

                } else if (triggerNode.getInfo().equals("transitionPre")) {
                    //跳转到了曾经的界面，只将转换节点信息加入到WLT中
                    this.newPage = null;
                    //System.out.println("获取旧的界面，正在拼接WLT但不重复添加界面...");
                } else if (triggerNode.getInfo().equals("transitionWeb")) {
                    //跳转到Web界面，只将转换节点信息加入WLT
                    //System.out.println("获取新的界面，但新界面是Web界面，正在拼接WLT但不添加界面信息...");
                    this.newPage = null;
                } else if (triggerNode.getInfo().equals("transitionOtherAPP")) {
                    //跳转到其他APP，只将转换节点信息加入WLT
                    //System.out.println("获取新的界面，但新界面是其他APP界面，正在拼接WLT但不添加界面信息...");
                    this.newPage = null;
                }

                Element hierarchy = this.myWLT.getRootElement();
                findAndInsert(hierarchy);
                this.myWLT = hierarchy.getDocument();
                //System.out.println("拼接WLT完成");

            }

            this.endMili = System.currentTimeMillis();
            this.totalMili += (this.endMili - this.startMili);
        }catch (Exception e){
            System.out.println("makeWLT:"+e);
        }
    }


    public void TextToFile(final String strFilename, final String strBuffer) throws IOException
    {

            // 创建文件对象
            File fileText = new File(strFilename);
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(fileText);

            // 写文件
            fileWriter.write(strBuffer);
            // 关闭
            fileWriter.close();

    }

    private void insertWinID(Document myPageSource, String windowsID){//插入winID到每一个节点
        //System.out.println("插入windowsID结点...");
        try {
            Element hierarchy = myPageSource.getRootElement();
            this.insertWinIDForAll(hierarchy, windowsID);
            //System.out.println("插入完成");
        }catch (Exception e){
            System.out.println("insertWinID:"+e);
        }
    }

    private void insertWinIDForAll(Element node,String ID) {
        try {
            node.addAttribute("windowsID", ID);
            final List<Element> listElement = node.elements();// 所有一级子节点的list

            for (final Element e : listElement) {// 遍历所有一级子节点
                insertWinIDForAll(e, ID);// 递归
            }
        }catch (Exception e){
            System.out.println("insertWinIDForAll:"+e);
        }
    }

    private void insertWinIndex(Document myPageSource, Integer windowsIndex){//插入winIndex到第一个节点
        try {
            //System.out.println("插入windowsIndex结点...");
            Element hierarchy = myPageSource.getRootElement();
            hierarchy.addAttribute("windowsIndex", windowsIndex + "");
            //System.out.println("插入完成");
        }catch (Exception e){
            System.out.println("insertWinIndex:"+e);
        }
    }


    public void saveCoreWLT(){
        //将wlt存储为字符串
        try {
            //System.out.println("开始保存核心WLT...");
            Element hierarchy = this.myWLT.getRootElement();
            this.saveCoreValue(hierarchy);

            this.TextToFile(this.apkName + "coreWLT.txt", this.myCoreWLT);

        }catch (Exception e){
            System.out.println("saveCoreWLT:"+e);
        }
        //System.out.println("核心WLT保存完成");
    }

    private void saveCoreValue(Element node)throws Exception{
        //除了干扰属性，其他属性都存入WLT中

        if((!node.getName().equals("transitionNew"))&&(!node.getName().equals("transitionPre"))){
            this.myCoreWLT+=node.getName();
            final List<Attribute> listAttr = node.attributes();// 当前节点的所有属性
            for (final Attribute attr : listAttr) {// 遍历当前节点的所有属性
                final String name = attr.getName();// 属性名称
                final String value=attr.getValue();//属性值
                if((!name.equals("windowsID"))&&(!name.equals("windowsIDTransition"))&&(!name.equals("text"))&&(!name.equals("package"))&&(!name.equals("bounds"))&&(!name.equals("resource-id"))&&(!name.equals("index"))&&(!name.equals("content-desc"))){
                    this.myCoreWLT+=(name+value);
                }
            }
        }
        // 递归遍历当前节点所有的子节点
        final List<Element> listElement = node.elements();// 所有一级子节点的list
        for (final Element e : listElement) {// 遍历所有一级子节点
            saveCoreValue(e);// 递归
        }
    }


    private void findAndInsert(Element node) throws Exception {
            final List<Attribute> listAttr = node.attributes();// 当前节点的所有属性
            String bValue="init",wValue="init";
            for (final Attribute attr : listAttr) {// 遍历当前节点的所有属性
                final String name = attr.getName();// 属性名称
                final String value = attr.getValue();// 属性的值
                // 2017/7/17 在获取WLT时，不记录transition结点信息，改为：将跳转信息添加到具体的跳转控件中，如：添加一个transition属性并记录
                if(name.equals("bounds")){
                    bValue=value;
                }
                if(name.equals("windowsID")){
                    wValue=value;
                }
            }
        if((wValue.equals(this.triggerNodePreWinID))&&(bValue.equals(this.triggerNodeBounds))){
            //System.out.println("正在插入跳转节点信息...");
            //System.out.println("插入位置：" + bValue);
            node.addAttribute(this.triggerNodeInfo,this.triggerNodeWinIndexTrans);
            Element transition=node.addElement(this.triggerNodeInfo);
            transition.addAttribute("windowsIDTransition",this.triggerNodeWinIDTrans);
            transition.addAttribute("windowsIndexTransition",this.triggerNodeWinIndexTrans);
            //System.out.println("插入跳转节点信息完成");
            if(this.newPage!=null) {
                //System.out.println("正在插入新页面信息...");
                //先将根节点插入
                Element rootNode=this.newPage.getRootElement();
                transition.add(rootNode);
                //再将剩余节点插入
                List<Element> newPageNodes = rootNode.elements();
                for (Element element : newPageNodes) {
                    rootNode.add(element.detach());//将新页面下的节点添加到跳转节点下
                }
                //System.out.println("插入新页面信息完成");
            }
            //return;
            //  2017/6/15 这样写不会直接跳出递归,影响效率，但下面的方法似乎会造成不拼接WLT的问题？
            //throw new Exception(){
            throw new StopMsgException();

            // };
        }
            // 递归遍历当前节点所有的子节点
            final List<Element> listElement = node.elements();// 所有一级子节点的list
            for (final Element e : listElement) {// 遍历所有一级子节点
                findAndInsert(e);// 递归
            }


    }

    public void getPackageByPS(String forPackage) {
        try{
        Document myPageSource= DocumentHelper.parseText(forPackage);
        Element root=myPageSource.getRootElement();
            findPackage(root);
        }catch (StopMsgException e){
            return;
        }catch (Exception e){
            System.out.println("getPackageByPS:"+e);
            this.findPackage="null";
        }

    }

    private void findPackage(Element node) throws Exception{
        final List<Attribute> listAttr = node.attributes();// 当前节点的所有属性
        for (final Attribute attr : listAttr) {// 遍历当前节点的所有属性
            final String name = attr.getName();// 属性名称
            final String value = attr.getValue();// 属性的值
            //System.out.println("属性名称：" + name + "---->属性值：" + value);
            if(name.equals("package")){
                this.findPackage= value;
                throw new StopMsgException();
            }
        }
        // 递归遍历当前节点所有的子节点
        final List<Element> listElement = node.elements();// 所有一级子节点的list
        for (final Element e : listElement) {// 遍历所有一级子节点
            findPackage(e);// 递归
        }
        this.findPackage="null";
    }

    public void isWebViewByPS(String forWebView)  {
        try{
        Document myPageSource= DocumentHelper.parseText(forWebView);
        Element root=myPageSource.getRootElement();
            findWebView(root);
        }catch (StopMsgException e){
            return;
        }
        catch (Exception e){
            System.out.println("isWebViewByPS:"+e);
            this.isWeb=true;
        }

    }
    private void findWebView(Element node) throws Exception{
        //System.out.println("nodeName:"+node.getName());
        if((node.getName().contains("webkit"))||(node.getName().contains("WebView"))||(node.getName().contains("web"))||(node.getName().contains("Web"))){
            this.isWeb=true;
            throw new StopMsgException();
        }
        // 递归遍历当前节点所有的子节点
        else{
            final List<Element> listElement = node.elements();// 所有一级子节点的list
            for (final Element e : listElement) {// 遍历所有一级子节点
                findWebView(e);// 递归
            }
            this.isWeb=false;
        }

    }

    static class StopMsgException extends RuntimeException{

    }

}
