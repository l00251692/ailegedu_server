package com.changyu.foryou.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.service.*;
import com.changyu.foryou.tools.PayUtil;
import com.changyu.foryou.tools.StringUtil;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Webhooks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayController {
	@Autowired
	private OrderService orderService;
	@Autowired
    private UserService userService;
    @Autowired
    private PushService pushService;
    @Autowired
    private FoodService foodService;
    
    private static final Logger LOGGER = Logger
			.getLogger(PayController.class);

	@RequestMapping("/webHooksForPayAndRefund")
	public void webHooksForPaySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException{
		request.setCharacterEncoding("UTF8");

		//获取头部所有信息
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			System.out.println(key+" "+value);
		}
		// 获得 http body 内容
		BufferedReader reader = request.getReader();
		StringBuffer buffer = new StringBuffer();
		String string;
		while ((string = reader.readLine()) != null) {
			buffer.append(string);
		}
		reader.close();
		// 解析异步通知数据
		Event event = Webhooks.eventParse(buffer.toString());
		if ("charge.succeeded".equals(event.getType())) {       //支付成功的回调			
			doPaySuccess(buffer.toString());      //事务处理
			response.setStatus(200);
		} else if ("refund.succeeded".equals(event.getType())) {  //退款的回调
			doRefundSuccess(buffer.toString());       //退款事务处理
			response.setStatus(200);
		} else {
			response.setStatus(500);
		}
	}
	
	
	/**
	 * 退款操作
	 * @param buffer
	 * @return
	 */
	private int doRefundSuccess(String buffer) {
		Refund refund = (Refund)Webhooks.parseEvnet(buffer); 
		LOGGER.info(JSON.toJSONString(refund));
		
		Map<String,Object> paramMap=new HashMap<String,Object>();

		String togetherId=refund.getOrderNo();
		paramMap.put("togetherId",togetherId);
		final double price=refund.getAmount()*1.0/100;
		
		Integer flag=orderService.updateOrderStatusRefundSuccess(paramMap);
		final String phone=orderService.getUserPhone(paramMap);        //根据订单号获取用户手机号
		//开启极光推送，通知用户退款成功
		new Thread(() -> { //向超级管理员推送，让其分发订单

            //推送
            pushService.sendPush(phone,"您的一笔金额为"+price+"的订单已经退回到您的账户中，请及时查看。For优。", 5);

        }).start();
		
		return flag;
	}

	/**支付成功**/
	public int doPaySuccess(String buffer){
		Charge charge = (Charge)Webhooks.parseEvnet(buffer); 
		
		//System.out.println(JSON.toJSONString(charge));
		//获得charge对象
		Map<String,Object> paramMap=new HashMap<String,Object>();

		String chargeId=charge.getId();
		paramMap.put("togetherId",charge.getOrderNo());
		paramMap.put("amount",charge.getAmount()*1.0/100);
		paramMap.put("chargeId",chargeId);
		System.out.println(paramMap);
		int flag=orderService.updateOrderStatusAndAmount(paramMap);         //支付完成后更新订单状态以及更新价格 ,以及chargeId
		
		List<Order> orders = orderService.getAllOrdersByTogetherId(charge.getOrderNo()); // 获取该笔订单的消息				
		for(Order order:orders){
			//paramMap.put("foodId", order.getFoodId());
			//paramMap.put("orderCount", order.getOrderCount());
			paramMap.put("campusId",order.getCampusId());
			foodService.changeFoodCount(paramMap); // 增加销量，减少存货
	    }
		
		final Integer campusId=orderService.getCampusIdByTogetherId(paramMap);
		// 开启线程去访问极光推送

		new Thread(() -> { //向超级管理员推送，让其分发订单

            //推送
            //pushService.sendPushByTag("0","一笔新的订单已经到达，请前往选单中查看，并尽早分派配送员进行配送。For优。", 1);

            Map<String, Object> paramterMap=new HashMap<String,Object>();
            paramterMap.put("campusId",campusId);
            List<String> superPhones=userService.getAllSuperAdminPhone(paramterMap);
            for(String phone:superPhones){

                //推送
                pushService.sendPush(phone,"一笔新的订单已经到达，请前往选单中查看，并尽早分派配送员进行配送。for优。", 1);
            }
        }).start();
		return flag;
	}
	
	/**
	 * 获得微信支付参数
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/getPaymentWx")
	public @ResponseBody Map<String, String> getPaymentWx(@RequestParam String order_id,@RequestParam String user_id,@RequestParam String pay_money, HttpServletRequest request){
		//user_id就是openid
		Map<String,String> data = new HashMap<String, String>();
		JSONObject node = new JSONObject();
		System.out.println("getPaymentWx enter");
		
		try{  
            //生成的随机字符串  
            String nonce_str = StringUtil.getRandomStringByLength(32);  
            //商品名称  
            String body = "积木平台";  
            //获取客户端的ip地址  
            String spbill_create_ip = StringUtil.getIpAddr(request);  
            System.out.println("用户IP:" + spbill_create_ip);
              
            //组装参数，用户生成统一下单接口的签名  
            Map<String, String> packageParams = new HashMap<String, String>(); 
            String appId = "wxcc5ecb17be242087";
            String mchId = "1900000109";
            String key = "JIMU"; //微信支付商户密钥
            String notifyUrl = "http://localhost/pay/payNotify";
            
            packageParams.put("appid", appId);  
            packageParams.put("mch_id", mchId);//微信支付商家号
            packageParams.put("nonce_str", nonce_str);  
            packageParams.put("body", body);  
            packageParams.put("out_trade_no", order_id);//商户订单号  
            packageParams.put("total_fee", pay_money);//支付金额，这边需要转成字符串类型，否则后面的签名会失败  
            packageParams.put("spbill_create_ip", spbill_create_ip);//客户端IP  
            packageParams.put("notify_url", notifyUrl);//支付成功后的回调地址  
            packageParams.put("trade_type", "JSAPI");//支付方式  
            packageParams.put("openid", user_id);  
                 
            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串   
          
            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口  
            String mysign = PayUtil.sign(prestr, key, "utf-8").toUpperCase();  //微信支付商户密钥为第二个参数
              
            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去  
            String xml = "<xml>" + "<appid>" + appId + "</appid>"   
                    + "<body><![CDATA[" + body + "]]></body>"   
                    + "<mch_id>" + mchId + "</mch_id>"   
                    + "<nonce_str>" + nonce_str + "</nonce_str>"   
                    + "<notify_url>" + notifyUrl + "</notify_url>"   
                    + "<openid>" + user_id + "</openid>"   
                    + "<out_trade_no>" + order_id + "</out_trade_no>"   
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"   
                    + "<total_fee>" + pay_money + "</total_fee>"  
                    + "<trade_type>" + "JSAPI" + "</trade_type>"   
                    + "<sign>" + mysign + "</sign>"  
                    + "</xml>";  
              
            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);  
  
            //调用统一下单接口，并接受返回的结果  
            String result = PayUtil.httpRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", xml);  
              
            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);  
              
            // 将解析结果存储在HashMap中     
            Map map = PayUtil.doXMLParse(result);  
              
            String return_code = (String) map.get("return_code");//返回状态码  
            //TODO:微信商户申请下来后需要再调试
            //if(return_code=="SUCCESS"||return_code.equals(return_code)){  
        		node.put("signType", "MD5");
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息     
                node.put("nonceStr", nonce_str);  
                //node.put("package", "prepay_id=" + prepay_id);
                node.put("package", "prepay_id=11111");  
                Long timeStamp = System.currentTimeMillis() / 1000;     
                node.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误  
                //拼接签名需要的参数  
                String stringSignTemp = "appId=" + appId + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;     
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法  
                String paySign = PayUtil.sign(stringSignTemp, key, "utf-8").toUpperCase();  
                  
                node.put("paySign", paySign); 
                node.put("appid", appId);  
                
                data.put("State", "Success");
        		data.put("data", node.toString());	
            //}  
              
            

    		return data;
        }catch(Exception e)
		{  
            e.printStackTrace();  
        }  
        return null;	
	}
	
	/** 
     * @Description:微信支付     
     * @return 
     * @throws Exception  
     */  
    @RequestMapping(value="/payNotify")  
    @ResponseBody  
    public void payNotify(HttpServletRequest request,HttpServletResponse response) throws Exception{  
        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));  
        String line = null;  
        StringBuilder sb = new StringBuilder(); 
        String key = "";//微信商户密钥
        while((line = br.readLine()) != null){  
            sb.append(line);  
        }  
        //sb为微信返回的xml  
        String notityXml = sb.toString();  
        String resXml = "";  
        System.out.println("接收到的报文：" + notityXml);  
      
        Map map = PayUtil.doXMLParse(notityXml);  
          
        String returnCode = (String) map.get("return_code");  
        if("SUCCESS".equals(returnCode)){  
            //验证签名是否正确  
            if(PayUtil.verify(PayUtil.createLinkString(map), (String)map.get("sign"), key, "utf-8")){  
                /**此处添加自己的业务逻辑代码start**/  
                  
                  
                /**此处添加自己的业务逻辑代码end**/  
                //通知微信服务器已经支付成功  
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"  
                + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";  
            }  
        }else{  
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
            + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";  
        }  
        System.out.println(resXml);  
        System.out.println("微信支付回调数据结束");  
  
  
        BufferedOutputStream out = new BufferedOutputStream(  
                response.getOutputStream());  
        out.write(resXml.getBytes());  
        out.flush();  
        out.close();  
    }  
}
	
