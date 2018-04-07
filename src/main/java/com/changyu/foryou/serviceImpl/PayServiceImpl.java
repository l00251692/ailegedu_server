package com.changyu.foryou.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.changyu.foryou.service.PayService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.PayUtil;
import com.changyu.foryou.tools.StringUtil;

@Service("payService")
public class PayServiceImpl implements PayService {

	public  String refund(String order_id,String fee){
			
		String nonceStr = StringUtil.getRandomStringByLength(32);
        Map<String, String> packageParams = new HashMap<String, String>(); 
        packageParams.put("appid", Constants.appId);
        packageParams.put("mch_id", Constants.mchId);//微信支付分配的商户号
        packageParams.put("nonce_str", nonceStr);//随机字符串，不长于32位
        packageParams.put("out_refund_no", order_id);//商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
        packageParams.put("out_trade_no", order_id);//商户侧传给微信的订单号32位
        packageParams.put("refund_fee", fee);
        packageParams.put("total_fee", fee);
        //packageParams.put("transaction_id", payLog.getTransactionId());//微信生成的订单号，在支付通知中有返回
        
        String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串   
        
        //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口  
        String sign = PayUtil.sign(prestr, Constants.mchKey, "utf-8").toUpperCase();  //微信支付商户密钥为第二个参数
         
        String refundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        String xmlParam="<xml>"+
                "<appid>"+Constants.appId+"</appid>"+
                "<mch_id>"+Constants.mchId+"</mch_id>"+
                "<nonce_str>"+nonceStr+"</nonce_str>"+
                //"<op_user_id>"+Constants.mchId+"</op_user_id>"+
                "<out_refund_no>"+order_id+"</out_refund_no>"+
                "<out_trade_no>"+order_id+"</out_trade_no>"+
                "<refund_fee>"+fee+"</refund_fee>"+
                "<total_fee>"+fee+"</total_fee>"+
                //"<transaction_id>"+payLog.getTransactionId()+"</transaction_id>"+
                "<sign>"+sign+"</sign>"+
                "</xml>";
        String resultStr = PayUtil.refundpost(refundUrl, xmlParam);  
		
		return resultStr;
		
	}
}
