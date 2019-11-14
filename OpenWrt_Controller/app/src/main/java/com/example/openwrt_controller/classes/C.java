package com.example.openwrt_controller.classes;

import android.Manifest;

public class C {
    public static String[] permissions = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE,  // 1
            Manifest.permission.ACCESS_WIFI_STATE,     // 2
            Manifest.permission.CHANGE_NETWORK_STATE,  // 3
            Manifest.permission.CHANGE_WIFI_STATE,     // 4
            Manifest.permission.CHANGE_CONFIGURATION,  // 5
            Manifest.permission.RECEIVE_SMS,           // 6
            Manifest.permission.READ_SMS,               // 7
            Manifest.permission.SEND_SMS,               // 8
            Manifest.permission.READ_EXTERNAL_STORAGE,  // 9
            Manifest.permission.WRITE_EXTERNAL_STORAGE,  // 10
            Manifest.permission.ACCESS_NOTIFICATION_POLICY // 11
    };
    public static String[] permissions_net = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE,  // 1
            Manifest.permission.ACCESS_WIFI_STATE,     // 2
            Manifest.permission.CHANGE_NETWORK_STATE,  // 3
            Manifest.permission.CHANGE_WIFI_STATE,     // 4
            Manifest.permission.CHANGE_CONFIGURATION,  // 5
            Manifest.permission.INTERNET
    };
    public static String[] permissions_sms = new String[]{
            Manifest.permission.RECEIVE_SMS,           // 1
            Manifest.permission.READ_SMS,               // 2
            Manifest.permission.SEND_SMS,               // 3
    };
    public static String[] permissions_io = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,  // 1
            Manifest.permission.WRITE_EXTERNAL_STORAGE,  // 2
    };
    public static String[] permissions_notifi = new String[]{
        Manifest.permission.ACCESS_NOTIFICATION_POLICY // 1
    };
    public static String LOG_START="开始记录";
    public static String LOG_SENDSM="短信应已发送";
    public static String MS_RECE(int i){
        return "收到了 "+String.valueOf(i)+" 条短信";
    }
    public static String SXMS_RECE_SUCC= "收到并解析了 1 条闪讯短信";
    public static String SXMS_RECE_FAIL= "1 条闪讯短信解析失败";
    public static String PER_SUCCESS(String per){
        return "权限成功申请："+per;
    }
    public static String PER_DENIED(String per){
        return "权限申请失败："+per;
    }
    public static String PER_REGET(String per){
        return "重新申请权限："+per;
    }
    public static String BOARDCAST_ADD(String bd){
        return "广播注册成功："+bd;
    }
    public static String SMSRECEIVER="android.provider.Telephony.SMS_RECEIVED";
    public static String ALARMACTION="ALARM_ACTION";
    public static String TIMEFORMAT="yyyy-MM-dd HH:mm:ss";
    public static String MS_MM_FINDER="密码是：(.*),密码";
    public static String MS_ENDDAY_FINDER="密码在(.*)以前有效";
    public static String SX_SENDTO="1065930052";
    public static String SX_RECEFROM="106593005";
    public static String SX_CONTEXT="MM";

}
