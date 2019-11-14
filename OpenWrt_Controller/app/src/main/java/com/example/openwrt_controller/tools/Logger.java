package com.example.openwrt_controller.tools;


import android.graphics.PorterDuff;

import com.example.openwrt_controller.classes.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(C.TIMEFORMAT);
    public static String path ;
    public static String filename;
    File file;
    FileOutputStream outputStream;
    public Logger(String syspath){
        path=syspath+"/logger";
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
            //Log.i("msg",String.valueOf(appDir.exists()));
        }
        Date timedate = new Date(System.currentTimeMillis());
        filename=simpleDateFormat.format(timedate).substring(2,10)+ ".log";
        file= new File(appDir, filename);
        if(file.exists()){
            file.delete();
        }
        try{
            //Log.i("msg",file.getAbsolutePath()+file.isFile()+file.isDirectory());
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void writelog(String content) {
        try {
            outputStream = new FileOutputStream(file, true);
            //获取当前时间
            Date timedate = new Date(System.currentTimeMillis());
            outputStream.write(("["+simpleDateFormat.format(timedate)+"]"+"  ").getBytes());
            outputStream.write(content.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void wri_empline(){
        try {
            outputStream = new FileOutputStream(file, true);
            outputStream.write("\n".getBytes());
            outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
