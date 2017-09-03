package com.repackage.ssdeep;

/**
 * Created by Shinelon on 2017/8/20.
 */
public class StartFuzzy {
    private static String s1;
    private static String s2;
    public static void main(String args[]){
        s1="384:uyySKoK0Kyx44dCE2KoKUtOxKRKHx4hmK5+tbg7lcX2DiNk3vgRytxBg7lcX2DiP:jFnwFe6GuOYm3X/MtHRos+syPe";
        s2="384:uyyJKoK0KyxqXdCEdKoKUljhmK5+tbg7lcX2DiNk3vgRytgZ2bECQKQIQo9OeGhD:2FneFOurUyPU";
        for(int i=0;i<10;i++){
        ssdeep test=new ssdeep();
        test.Compare(new SpamSumSignature(s1), new SpamSumSignature(s2));
        }
    }
}
