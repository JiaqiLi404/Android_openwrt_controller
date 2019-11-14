package com.example.openwrt_controller.classes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.openwrt_controller.tools.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class RTManger {
    static int iswifiConn = 0;
    static int isRTconn = 0;
    static String wifiname = "OpenWrt";
    static String wifipsw = "lijiaqi120508";
    static String RTusername = "root";
    static String RTpsw = "lijiaqi";
    static  String RTadd="192.168.1.1";



    public static boolean setPWD(String pwd, Logger logger) {
        try {
            jcifs.Config.registerSmbURLHandler();
            SmbFile smbFile = new SmbFile(
                    "smb://" + RTusername + ":" + RTpsw + "@" + RTadd+"/config/network");
            logger.writelog("连接状态："+smbFile.canWrite());
            int length = smbFile.getContentLength();
            String fileNetwork = null;
            String oldPassword = null;
            String newPassword = pwd;
            byte buffer[] = new byte[length];
            SmbFileInputStream in = new SmbFileInputStream(smbFile);
            while ((in.read(buffer)) != -1) fileNetwork = new String(buffer);

            Pattern pattern = Pattern.compile("option password '(.*?)'");
            Matcher matcher = pattern.matcher(fileNetwork);
            //我们假定最后一个密码是l2tp的密码
            while(matcher.find())
            {
                oldPassword = matcher.group(1);
            }

            in.close();
            logger.writelog("旧密码："+oldPassword);
            logger.writelog("新密码："+newPassword);
            if(oldPassword.equals(newPassword)){
                logger.writelog("密码没有变更！");
                return true;
            }
            fileNetwork = fileNetwork.replaceAll(oldPassword, newPassword);
            BufferedOutputStream out = new BufferedOutputStream(new SmbFileOutputStream(smbFile));
            out.write(fileNetwork.getBytes());
            logger.writelog("密码更新完成，准备重启网络……");
            out.close();
            SSH(RTadd,RTusername,RTpsw,22,"/etc/init.d/network restart",logger);
        } catch (Exception e) {
            logger.writelog("路由器连接失败");
            logger.wri_empline();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List SSH(String host,String username,String passwd,int port,String command,Logger logger) throws JSchException, IOException {
        List list=new ArrayList();
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(passwd);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(60 * 1000);
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
        logger.writelog("SSH连接成功");
        byte[] by = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(by, 0, 1024);
                if (i < 0) break;
                list.add(new String(by, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        logger.writelog("网络重启成功");
        channel.disconnect();
        session.disconnect();
        return list;
    }
}
