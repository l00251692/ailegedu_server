package com.changyu.foryou.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.io.PrintWriter; 
import java.net.URL; 
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List; 
import java.util.Map;

import ytx.org.apache.http.HttpEntity;
import ytx.org.apache.http.HttpResponse;
import ytx.org.apache.http.client.methods.HttpPost;
import ytx.org.apache.http.entity.StringEntity;
import ytx.org.apache.http.impl.client.DefaultHttpClient;
import ytx.org.apache.http.message.BasicHeader;
import ytx.org.apache.http.protocol.HTTP; 
 
/** 
 * Created by lsh on 2017/6/22. 
 */
public class HttpRequest { 
  /** 
   * 向指定URL发送GET方法的请求 
   * 
   * @param url 
   *      发送请求的URL 
   * @param param 
   *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 
   * @return URL 所代表远程资源的响应结果 
   */
  public static String sendGet(String url, String param) { 
    String result = ""; 
    BufferedReader in = null; 
    try { 
      String urlNameString = url + "?" + param; 
      URL realUrl = new URL(urlNameString); 
      // 打开和URL之间的连接 
      URLConnection connection = realUrl.openConnection(); 
      // 设置通用的请求属性 
      connection.setRequestProperty("accept", "*/*"); 
      connection.setRequestProperty("connection", "Keep-Alive"); 
      connection.setRequestProperty("user-agent", 
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); 
      // 建立实际的连接 
      connection.connect(); 
      // 获取所有响应头字段 
      Map<String, List<String>> map = connection.getHeaderFields(); 
      // 遍历所有的响应头字段 
      for (String key : map.keySet()) { 
        System.out.println(key + "--->" + map.get(key)); 
      } 
      // 定义 BufferedReader输入流来读取URL的响应 
      in = new BufferedReader(new InputStreamReader( 
          connection.getInputStream())); 
      String line; 
      while ((line = in.readLine()) != null) { 
        result += line; 
      } 
    } catch (Exception e) { 
      System.out.println("发送GET请求出现异常！" + e); 
      e.printStackTrace(); 
    } 
    // 使用finally块来关闭输入流 
    finally { 
      try { 
        if (in != null) { 
          in.close(); 
        } 
      } catch (Exception e2) { 
        e2.printStackTrace(); 
      } 
    } 
    return result; 
  } 
 
  /** 
   * 向指定 URL 发送POST方法的请求 
   * 
   * @param url 
   *      发送请求的 URL 
   * @param param 
   *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 
   * @return 所代表远程资源的响应结果 
   */
  public static String sendPost(String url, String param) { 
    PrintWriter out = null; 
    BufferedReader in = null; 
    String result = ""; 
    try { 
      URL realUrl = new URL(url); 
      // 打开和URL之间的连接 
      URLConnection conn = realUrl.openConnection(); 
      // 设置通用的请求属性 
      conn.setRequestProperty("accept", "*/*"); 
      conn.setRequestProperty("connection", "Keep-Alive"); 
      conn.setRequestProperty("user-agent", 
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); 
      // 发送POST请求必须设置如下两行 
      conn.setDoOutput(true); 
      conn.setDoInput(true); 
      // 获取URLConnection对象对应的输出流 
      out = new PrintWriter(conn.getOutputStream()); 
      // 发送请求参数 
      out.print(param); 
      // flush输出流的缓冲 
      out.flush(); 
      // 定义BufferedReader输入流来读取URL的响应 
      in = new BufferedReader( 
          new InputStreamReader(conn.getInputStream())); 
      String line; 
      while ((line = in.readLine()) != null) { 
        result += line; 
      } 
    } catch (Exception e) { 
      System.out.println("发送 POST 请求出现异常！"+e); 
      e.printStackTrace(); 
    } 
    //使用finally块来关闭输出流、输入流 
    finally{ 
      try{ 
        if(out!=null){ 
          out.close(); 
        } 
        if(in!=null){ 
          in.close(); 
        } 
      } 
      catch(IOException ex){ 
        ex.printStackTrace(); 
      } 
    } 
    return result; 
  } 
  
  public static String httpPostWithJSON(String url, String json,String outpath,String id)  
          throws Exception {  
      String result = null;  
      // 将JSON进行UTF-8编码,以便传输中文  
      String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);   
      DefaultHttpClient httpClient = new DefaultHttpClient();  
      HttpPost httpPost = new HttpPost(url);  
      httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");    
      StringEntity se = new StringEntity(json);  
      se.setContentType("application/json");  
      se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"UTF-8"));  
      httpPost.setEntity(se);  
      // httpClient.execute(httpPost);  
      HttpResponse response = httpClient.execute(httpPost);  
      if (response != null) {  
          HttpEntity resEntity = response.getEntity();  
          if (resEntity != null) {  
              InputStream instreams = resEntity.getContent();   
             // ResourceBundle systemConfig = ResourceBundle.getBundle("config/system", Locale.getDefault());  
            //  String uploadSysUrl = systemConfig.getString("agentImgUrl")+id+"/";  
            //  File saveFile = new File(uploadSysUrl+id+".jpg");  
              //String uploadSysUrl = "D:\\upload"+"/";  
              File saveFile = new File(outpath+id+".jpg");  
                 // 判断这个文件（saveFile）是否存在  
                 if (!saveFile.getParentFile().exists()) {  
                     // 如果不存在就创建这个文件夹  
                     saveFile.getParentFile().mkdirs();  
                 }  
              saveToImgByInputStream(instreams, outpath, id+".jpg");  
          }  
      }  
      return result;  
  }   
     
  /* @param instreams 二进制流 
  * @param imgPath 图片的保存路径 
  * @param imgName 图片的名称 
  * @return 
  *      1：保存正常 
  *      0：保存失败 
  */  
 public static int saveToImgByInputStream(InputStream instreams,String imgPath,String imgName) throws FileNotFoundException{  
  
     int stateInt = 1;  
     File file=new File(imgPath,imgName);//可以是任何图片格式.jpg,.png等  
     FileOutputStream fos=new FileOutputStream(file);  
     if(instreams != null){  
         try {  
                                 
             byte[] b = new byte[1024];  
             int nRead = 0;  
             while ((nRead = instreams.read(b)) != -1) {  
                 fos.write(b, 0, nRead);  
             }  
                              
         } catch (Exception e) {  
             stateInt = 0;  
             e.printStackTrace();  
         } finally {  
               
             try {  
                 fos.flush();  
                 fos.close();  
          } catch (IOException e) {  
              e.printStackTrace();  
          }   
         }  
     }  
     return stateInt;  
 }        
 public static boolean exists(String imgPath){  
     File saveFile = new File(imgPath);  
     if (!saveFile.getParentFile().exists()) {  
         return false;  
     }else{  
         //如果存在判断这个文件的大小  
         if(saveFile.length()>0){  
             System.out.println("--------------------------------"+saveFile.length());  
             return true;  
         }else{  
             return false;  
         }  
     }  
     
 } 
} 