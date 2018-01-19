package com.changyu.foryou.tools;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

public class StringUtil {
	
	/** 
     * StringUtils工具类方法 
     * 获取一定长度的随机字符串，范围0-9，a-z 
     * @param length：指定字符串长度 
     * @return 一定长度的随机字符串 
     */  
    public static String getRandomStringByLength(int length) {  
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < length; i++) {  
            int number = random.nextInt(base.length());  
            sb.append(base.charAt(number));  
        }  
        return sb.toString();  
       }  
    /** 
     * IpUtils工具类方法 
     * 获取真实的ip地址 
     * @param request 
     * @return 
     */  
    public static String getIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if(ip != null && !ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)){  
             //多次反向代理后会有多个ip值，第一个ip才是真实ip  
            int index = ip.indexOf(",");  
            if(index != -1){  
                return ip.substring(0,index);  
            }else{  
                return ip;  
            }  
        }  
        ip = request.getHeader("X-Real-IP");  
        if(ip != null && !ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)){  
           return ip;  
        }  
        return request.getRemoteAddr();  
    }   

}
