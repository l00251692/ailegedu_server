package com.changyu.foryou.serviceImpl;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

import com.changyu.foryou.service.PushService;
import com.changyu.foryou.tools.JpushInterface;
import com.cloopen.rest.sdk.CCPRestSDK;

@Service("pushService")
public class PushServiceImpl implements PushService{
	private static final String appKey = "6b618bf4a73419c8e351240e";
	private static final String masterSecret = "a80ba2c3934895be10ae9a75";
	
	private  static final Logger log = LoggerFactory.getLogger(PushServiceImpl.class);

	//通过别名推送
	public  void sendPush(String phone, String message, Integer count) {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, count);   //count表示离线消息保持时间

		PushPayload payload = JpushInterface.buildPushObject_android_and_ios_alias_alert(message,phone);

		try {
			PushResult result = jpushClient.sendPush(payload);
			log.info("Got result - " + result);

		} catch (APIConnectionException e) {
			log.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			log.error("Error response from JPush server. Should review and fix it. ", e);
			log.info("HTTP Status: " + e.getStatus());
			log.info("Error Code: " + e.getErrorCode());
			log.info("Error Message: " + e.getErrorMessage());
			log.info("Msg ID: " + e.getMsgId());
		}
	}

	//推送通过tag
	public void sendPushByTag(String tag, String message, int count) {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, count);   //count表示离线消息保持时间

		PushPayload payload = JpushInterface.buildPushObject_android_and_ios_tag_alert(message,tag);

		try {
			PushResult result = jpushClient.sendPush(payload);
			log.info("Got result - " + result);

		} catch (APIConnectionException e) {
			log.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			log.error("Error response from JPush server. Should review and fix it. ", e);
			log.info("HTTP Status: " + e.getStatus());
			log.info("Error Code: " + e.getErrorCode());
			log.info("Error Message: " + e.getErrorMessage());
			log.info("Msg ID: " + e.getMsgId());
		}
	}
	
	public Boolean sendPhoneCall(String phone)
	{
		
		HashMap<String, Object> result = null;
		
		if (phone == null)
		{
			return false;
		}

		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("aaf98f894526e89a014530c51a73072b", "0646f9ca871743df84f6b181f475734d");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8a216da86150f043016164f06b4c04fc");// 初始化应用ID
		//type=1，则播放默认语音文件,0是自定义语音文件 
		System.out.println("拨打商户电话:" + phone);
		result = restAPI.landingCall(phone, "order.wav", "", "400880088", "3", "", "", "", "", "", "", "");

		System.out.println("SDKTestLandingCall result=" + result);
		
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			/*HashMap<String,Object> data = (HashMap<String, Object>)result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}*/
			return true;
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			return false;
		}
		
	}
}
