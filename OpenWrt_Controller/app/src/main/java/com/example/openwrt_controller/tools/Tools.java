package com.example.openwrt_controller.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static String getcontext(String context,String find){
        String ans="-1";
        Pattern pattern = Pattern.compile(find);
        Matcher matcher = pattern.matcher(context);
        while(matcher.find())
        {
            ans = matcher.group(1);
        }
        return ans;
    }

}
