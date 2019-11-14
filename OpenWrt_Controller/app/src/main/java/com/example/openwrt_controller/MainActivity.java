package com.example.openwrt_controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.openwrt_controller.classes.RTManger;
import com.example.openwrt_controller.tools.Logger;
import com.example.openwrt_controller.classes.C;
import com.example.openwrt_controller.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {
    int got = 0, bu = 0, fin = 0, need = 0,ala=0;
    String lasttime;
    String sxpsw;
    String endday;
    Logger logger;
    EditText ed_sxmm;
    EditText ed_endday;
    LinearLayout ll_sta_bac;
    TextView tv_lasttime;
    TextView tv_nowstatus;
    TextView mm_status;
    TextView wifi_status;
    TextView rt_status;
    TextView net_status;
    private MyReceiver myReceiver;
    Context con = this;
    String min, hour;
    int ihour,imin;
    //获取当前时间
    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
    //Date timedate = new Date(System.currentTimeMillis());
    final int reqcode = 158;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        getPer(this, C.permissions_io);
        logger = new Logger(getBaseContext().getExternalCacheDir().toString());
        logger.writelog(C.LOG_START);
        tv_lasttime = (TextView) findViewById(R.id.last_updatetime);
        tv_nowstatus = (TextView) findViewById(R.id.now_status);
        mm_status = (TextView) findViewById(R.id.sm_status);
        wifi_status = (TextView) findViewById(R.id.wifi_status);
        rt_status = (TextView) findViewById(R.id.rt_status);
        net_status = (TextView) findViewById(R.id.net_status);
        ed_sxmm = (EditText) findViewById(R.id.sxmm);
        ed_endday = (EditText) findViewById(R.id.endday);
        ll_sta_bac = (LinearLayout) findViewById(R.id.sta_bac);
        ll_sta_bac.setBackgroundColor(Color.rgb(255, 69, 0));
    }

    //获得权限
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permsRequestCode == reqcode) {
            if (permissions.length > 0 && permissions[0].equals(C.permissions_sms[0])) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && need == 1) {
                    logger.writelog(C.PER_SUCCESS(Manifest.permission.SEND_SMS));
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(C.SX_SENDTO, null, C.SX_CONTEXT, null, null);
                    need = 0;
                    logger.writelog(C.LOG_SENDSM);
                } else {
                    for (int i = 0; i < permissions.length; i++)
                        logger.writelog(C.PER_DENIED(permissions[i]));
                }
            }
            if (permissions.length > 0 && permissions[0].equals(C.permissions_net[0])) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logger.writelog(C.PER_SUCCESS(Manifest.permission.ACCESS_NETWORK_STATE));
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (RTManger.setPWD(sxpsw, logger)) {
                                fin = 1;
                            }
                        }
                    });
                    t.start();
                    int time = 0;
                    while (fin == 0 && time <= 10) {
                        try {
                            Thread.sleep(1000);
                            time++;
                        } catch (Exception e) {
                        }
                    }
                    if (fin == 1) {
                        ll_sta_bac.setBackgroundColor(Color.rgb(60, 179, 113));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(C.TIMEFORMAT);// HH:mm:ss
                        Date timedate = new Date(System.currentTimeMillis());
                        lasttime = simpleDateFormat.format(timedate);
                        tv_lasttime.setText(lasttime);
                        tv_nowstatus.setText("自动更新中");
                        wifi_status.setText(" ");
                        rt_status.setText(" ");
                        net_status.setText(" ");
                        logger.writelog("路由器密码更新完成");
                        logger.wri_empline();
                        Toast toast=Toast.makeText(this,"路由器密码更新完成",Toast.LENGTH_LONG);
                        toast.show();
                        fin = 0;
                    }
                } else {
                    for (int i = 0; i < permissions.length; i++)
                        logger.writelog(C.PER_DENIED(permissions[i]));
                }
            }
        }
    }

    //获得权限
    void getPer(Activity a, String[] pers) {
        try {
            for (int i = 0; i < pers.length; i++)
                logger.writelog(C.PER_REGET(pers[i]));
        } catch (Exception e) {
        }
        ActivityCompat.requestPermissions(a, pers, reqcode);
    }

    //获得密码
    public void getSXMM(View view) {
        ll_sta_bac.setBackgroundColor(Color.rgb(0, 191, 255));
        need = 1;
        registetRecriver(1);
        //发送短信
        if (this.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (need == 1) {
                logger.writelog(C.PER_SUCCESS(Manifest.permission.SEND_SMS));
                SmsManager smsManager = SmsManager.getDefault();
                need = 0;
                smsManager.sendTextMessage(C.SX_SENDTO, null, C.SX_CONTEXT, null, null);
                logger.writelog(C.LOG_SENDSM);
            }
        } else {
            getPer(this, C.permissions_sms);
            logger.writelog(C.PER_DENIED(Manifest.permission.SEND_SMS));
        }
        logger.wri_empline();
    }

    //注册广播
    private void registetRecriver(int i) {
        logger.writelog("广播形式："+i);
        try {
            unregisterReceiver(myReceiver);
        }catch(Exception e){}
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        switch (i){
            case 3:ala=1;filter.addAction(C.ALARMACTION);logger.writelog(C.BOARDCAST_ADD(C.ALARMACTION));
            case 1:filter.addAction(C.SMSRECEIVER);logger.writelog(C.BOARDCAST_ADD(C.SMSRECEIVER));registerReceiver(myReceiver, filter);break;
            case 2:ala=1;filter.addAction(C.ALARMACTION);logger.writelog(C.BOARDCAST_ADD(C.ALARMACTION));registerReceiver(myReceiver, filter);break;
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(C.SMSRECEIVER) && intent.getExtras() != null) {
                Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");
                String format = intent.getStringExtra("format");
                process_sms(pdusObj, format);
                if (got == 1 && bu == 3) {
                    View view = new View(context);
                    setRT(view);
                    bu = 0;
                }
            } else if (intent.getAction().equals(C.ALARMACTION)) {
                if(ala==1){
                    ala=0;
                    return;
                }
                logger.writelog("收到了广播：" + intent.getAction());
                String h, m;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
                Date timedate = new Date(System.currentTimeMillis());
                h = simpleDateFormat.format(timedate);
                m = h.substring(14, 16);
                h = h.substring(11, 13);
                if (Math.abs(Integer.parseInt(m) - imin) <= 3 && Integer.parseInt(h)==ihour) {
                    logger.writelog("截止时间到了，开始重新发送短信");
                    View view = new View(context);
                    oneClick(view);
                }else {
                    logger.writelog("收到了无效的广播,现在时间："+h+":"+m+"   应该的广播时间："+ihour+":"+imin);
                    logger.wri_empline();
                }
                return;
            }
            return;
        }

    }

    //处理收到的短信
    private void process_sms(Object[] pdusObj, String format) {
        SmsMessage[] messages = new SmsMessage[pdusObj.length];
        logger.writelog(C.MS_RECE(pdusObj.length));
        for (int i = 0; i < pdusObj.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
            if (messages[i].getOriginatingAddress().equals(C.SX_RECEFROM)) {
                sxpsw = Tools.getcontext(messages[i].getMessageBody(), C.MS_MM_FINDER);
                if (!sxpsw.equals("-1")) {
                    mm_status.setText(" ");
                    logger.writelog(C.SXMS_RECE_SUCC);
                    endday = Tools.getcontext(messages[i].getMessageBody(), C.MS_ENDDAY_FINDER);
                    ed_sxmm.setText(sxpsw);
                    ed_endday.setText(endday);

                    min = endday.substring(14, 16);
                    hour = endday.substring(11, 13);
                    Log.d("&&&&&&&&", endday);
                    Log.d("&&&&&&&&", hour);
                    Log.d("&&&&&&&&", min);
                    ihour = Integer.parseInt(hour);
                    imin = Integer.parseInt(min) + 1;
                    if (imin >= 60) {
                        ihour += 1;
                        imin -= 60;
                        if (ihour >= 24)
                            ihour = 0;
                    }
                    got = 1;
                } else
                    logger.writelog(C.SXMS_RECE_FAIL);
            }
        }
    }

    public void setRT(View view) {
        bu = 0;
        sxpsw = ed_sxmm.getText().toString();
        hour=ed_endday.getText().toString().substring(11,13);
        min=ed_endday.getText().toString().substring(14,16);
        ihour=Integer.parseInt(hour);
        imin=Integer.parseInt(min);
        setTask(ihour, imin, false, con);
        logger.writelog("正在设置下次更新时间：" + ihour + ":" + imin);
        registetRecriver(2);
        this.getPer(this, C.permissions_net);
    }

    public void oneClick(View view) {
        bu = 3;
        getSXMM(view);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();

    }

    public void setTask(int hour, int min, boolean isEveryday, Context context) {
        Intent intent = new Intent(C.ALARMACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        alarmManager.getNextAlarmClock();
        logger.writelog("广播有：" + pendingIntent.toString());
        alarmManager.cancel(pendingIntent);
        logger.writelog("取消了广播");
        logger.writelog("广播有：" + pendingIntent.toString());
//TODO:
        if (isEveryday) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 12 * 60 * 60 * 1000,
                    pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        }
        logger.writelog("设置后，广播有：" + pendingIntent.toString());
    }
}
