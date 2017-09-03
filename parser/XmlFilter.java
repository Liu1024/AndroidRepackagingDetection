package com.repackage.parser;

/**
 * Created by Shinelon on 2017/8/16.
 */
public class XmlFilter {
    //处理XML中不允许出现的字符
    public static String filter(String xmlStr) {
        StringBuilder sb = new StringBuilder();
        char[] chs = xmlStr.toCharArray();
        //System.out.println("filter before=" +chs.length);
        for(char ch : chs) {
            if((ch >= 0x00 &&ch <= 0x08)
             ||(ch >= 0x0b &&ch <= 0x0c)
            ||(ch >= 0x0e &&ch <= 0x1f)
                    ||(ch=='&')
                    ||(ch=='#')) {
                //eat...
            } else {
                sb.append(ch);
            }
        }
        //System.out.println("filter after=" +sb.length());
        return sb.toString();
    }
}
