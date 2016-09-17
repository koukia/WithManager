package com.example.kohki.withmanager;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class Util {
    /***************************
     * スリープ処理
     *
     * @param ms スリープ時間
     ***************************/
    public static void sleep(int ms){
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***************************
     * IPアドレスをテキストに整形
     *
     * @param ip1 IPアドレスの1オクテット目
     * @param ip2 IPアドレスの2オクテット目
     * @param ip3 IPアドレスの3オクテット目
     * @param ip4 IPアドレスの4オクテット目
     ***************************/
    public static String getIpAddressText(int ip1, int ip2, int ip3, int ip4){
        return String.format(Locale.JAPANESE, "%d.%d.%d.%d", ip1, ip2, ip3, ip4);
    }
    /***************************
     * Notification.Builderから、使用しているAndroidバージョンに適した方法でNotificationを取得する。
     *
     * @param builder Notificationを取得したいNotification.Builder
     * @return 取得したNotificationを返す。
     ***************************/
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Notification getNotification(Notification.Builder builder){
        // Androidバージョンによって生成方法を分ける、getNotification()メソッドはAPI16から非推奨
        if(VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
            return builder.build();
        else
            return builder.getNotification();
    }

}

