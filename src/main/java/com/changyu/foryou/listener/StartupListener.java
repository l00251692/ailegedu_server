package com.changyu.foryou.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.DSHOrder;
import com.changyu.foryou.model.FoodComment;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.service.DelayService;
import com.changyu.foryou.service.FoodService;
import com.changyu.foryou.service.OrderService;
import com.changyu.foryou.service.PayService;
import com.changyu.foryou.service.DelayService.OnDelayedListener;
import com.changyu.foryou.service.RedisService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.PayUtil;
import com.changyu.foryou.tools.ThreadPoolUtil;

@Service  
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {  
      
    private static final Logger log = Logger.getLogger(StartupListener.class);  
  
    @Autowired  
    DelayService delayService;  
    @Autowired  
    RedisService redisService;
    @Autowired
    OrderService orderService;   
    @Autowired
    private FoodService foodService;
    @Autowired
	private PayService payService;
      
      
    @Override  
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        log.info(">>>>>>>>>>>>系统启动完成，onApplicationEvent()");  
/*        if (evt.getApplicationContext().getParent() == null) {  
        	System.out.println("getApplicationContext().getParent()  null");
            return;  
        }  */
        //自动收货  
        delayService.start(new OnDelayedListener(){
            @Override  
            public void onDelayedArrived(final DSHOrder order) {  
                //异步来做  
                ThreadPoolUtil.execute(new Runnable(){  
                    public void run(){  
                        long orderId = order.getOrderId();
                        System.out.println("onDelayedArrived:orderId=" + String.valueOf(orderId));
                        switch(order.getStatus())
                        {
                        	//订单提交但是还未支付到时间后将订单状态改为取消
                        	case Constants.ORDER_CREATE:
                        	{
                        		Map<String, Object> paramMap = new HashMap<String, Object>();
                    			paramMap.put("orderId",orderId);
                    			Order order = orderService.getOrderByIdWx(paramMap);
                    			if (order == null)
                    			{
                    				log.error("查找订单失败");
                    				break;
                    			}
                    			order.setStatus(Constants.ORDER_CANCEL);
                    			JSONArray records = JSON.parseArray(order.getRecords());
                    			JSONObject record = new JSONObject();
                    			record.put("status", Constants.ORDER_CANCEL);
                    			record.put("time", new Date());
                    			
                    			records.add(record);
                    			order.setRecords(records.toJSONString());
                    			
                    			int flag = orderService.updateOrder(order);

                        		break;
                        	}
                        	//支付成功但是商家未接单，启动退款流程
                        	case Constants.ORDER_PAY_SUCCESS:
                        	{
                        		//启动退款流程，退款金额单位为分
								Map<String, Object> paramMap = new HashMap<String, Object>();
								paramMap.put("orderId",orderId);
								Order order = orderService.getOrderByIdWx(paramMap);
								if (order == null)
                    			{
                    				log.error("查找订单失败");
                    				break;
                    			}
                    			order.setStatus(Constants.ORDER_CANCEL);
                    			JSONArray records = JSON.parseArray(order.getRecords());
                    			JSONObject record = new JSONObject();
                    			record.put("status", Constants.ORDER_CANCEL);
                    			record.put("time", new Date());
                    			
                    			records.add(record);
                    			order.setRecords(records.toJSONString());
                    			
                    			int flag = orderService.updateOrder(order);
								String resultStr = payService.refund(String.valueOf(orderId), String.valueOf(order.getPayPrice()*100));
								try {
									Map map =  PayUtil.doXMLParse(resultStr);
									String returnCode = map.get("return_code").toString();
						            if(returnCode.equals("SUCCESS")){
						                  String resultCode = map.get("result_code").toString();
						                  if(resultCode.equals("SUCCESS")){
												order.setStatus(Constants.ORDER_REFUND_SUCCESS);
										
												JSONArray recordes2 = JSON.parseArray(order.getRecords());
												JSONObject record2 = new JSONObject();
												record2.put("status",Constants.ORDER_REFUND_SUCCESS);
												record2.put("time", new Date());
												recordes2.add(record);
										  		order.setRecords(recordes2.toString());
										  			
										  		int flag2 = orderService.updateOrder(order);
						                  }   
						            }
								    
								} 
								catch (Exception e) 
								{
									e.printStackTrace();
								}
                		
								break;
                        	}
                        	//商家接单，超过规定时间后自动确认收货
                        	case Constants.ORDER_RECEIVE:
                        	{
                        		Map<String, Object> paramMap = new HashMap<String, Object>();
                    			paramMap.put("orderId",orderId);
                    			Order order = orderService.getOrderByIdWx(paramMap);
                    			if (order == null)
                    			{
                    				log.error("查找订单失败");
                    				break;
                    			}
                    			order.setStatus(Constants.ORDER_SUCCESS);
                    			JSONArray records = JSON.parseArray(order.getRecords());
                    			JSONObject record = new JSONObject();
                    			record.put("status", Constants.ORDER_SUCCESS);
                    			record.put("time", new Date());
                    			
                    			records.add(record);
                    			order.setRecords(records.toJSONString());
                    			
                    			int flag = orderService.updateOrder(order);
                        		break;
                        	}
                        	//用户确认收货后超过规定时间自动好评
                        	case Constants.ORDER_SUCCESS:
                        	{
                        		Map<String, Object> paramMap = new HashMap<String, Object>();
                    			paramMap.put("orderId",orderId);
                    			Order order = orderService.getOrderByIdWx(paramMap);
                    			if (order == null)
                    			{
                    				log.error("查找订单失败");
                    				break;
                    			}
                        		FoodComment foodComment = new FoodComment();
                                foodComment.setOrderId(orderId);
                                foodComment.setCampusId(order.getCampusId());
                                foodComment.setUserId(order.getUserId());
                                foodComment.setService((short)5);
                                foodComment.setQuality((short)5);
                                foodComment.setComment("");         
                                foodComment.setDate(Calendar.getInstance().getTime());
                                foodComment.setTag((short) 1);
                                
                                Integer flag = foodService.insertFoodComment(foodComment);
                                
                                order.setStatus(Constants.ORDER_REVIEWED);
                    			JSONArray records = JSON.parseArray(order.getRecords());
                    			JSONObject record = new JSONObject();
                    			record.put("status", Constants.ORDER_REVIEWED);
                    			record.put("time", new Date());
                    			
                    			records.add(record);
                    			order.setRecords(records.toJSONString());
                    			
                    			int flag2 = orderService.updateOrder(order);
                        		break;
                        	}
                        }
                         
                        //从redis删除  
                        redisService.delete(Constants.REDISPREFIX + orderId);  
                        log.info("自动确认收货，删除redis："+orderId);  
                    }  
                });  
            }  
        });  
        //查找需要入队的订单  
        ThreadPoolUtil.execute(new Runnable(){
            @Override  
            public void run() {  
                log.info("查找需要入队的订单");  
                //扫描redis，找到所有可能的orderId  
                List<String> keys = redisService.scan(Constants.REDISPREFIX, 10000);
                if(keys == null || keys.size() <= 0){  
                    return;  
                }  
                log.info("需要入队的订单keys："+keys);  
                //写到DelayQueue  
                for(String key : keys){  
                    DSHOrder order = redisService.get(key);  
                    log.info("读redis，key："+key);  
                    if(order != null){  
                        delayService.add(order);      
                        log.info("订单自动入队："+order.getOrderId());  
                    }  
                }   
            }  
        });   
    }  
}