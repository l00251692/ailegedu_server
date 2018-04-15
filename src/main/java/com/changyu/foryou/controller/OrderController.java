package com.changyu.foryou.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.BigOrder;
import com.changyu.foryou.model.Campus;
import com.changyu.foryou.model.CartGood;
import com.changyu.foryou.model.DSHOrder;
import com.changyu.foryou.model.DeliverChildOrder;
import com.changyu.foryou.model.DeliverOrder;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.model.PCOrder;
import com.changyu.foryou.model.Preferential;
import com.changyu.foryou.model.Receiver;
import com.changyu.foryou.model.SmallOrder;
import com.changyu.foryou.model.SuperAdminOrder;
import com.changyu.foryou.model.TogetherOrder;
import com.changyu.foryou.model.TradeInfo;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.payment.ChargeInterface;
import com.changyu.foryou.service.CampusService;
import com.changyu.foryou.service.DelayService;
import com.changyu.foryou.service.FoodService;
import com.changyu.foryou.service.OrderService;
import com.changyu.foryou.service.PayService;
import com.changyu.foryou.service.PreferentialService;
import com.changyu.foryou.service.PushService;
import com.changyu.foryou.service.ReceiverService;
import com.changyu.foryou.service.RedisService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.CalendarTool;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.PayUtil;
import com.changyu.foryou.tools.ThreadPoolUtil;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;

//import net.sf.json.JSONObject;

/**
 * 处理订单控制器
 * 
 * @author 殿下
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	private OrderService orderService;
	private UserService userService;
	private FoodService foodService;
	private ReceiverService receiverService;
	private CampusService  campusService;
	private PushService pushService;
	
	@Resource  
    private WebSocketPushHandler webSocketHandler;  
	
	@Autowired
	private PayService payService;
	
	@Autowired  
    private DelayService delayService;  
    @Autowired  
    private RedisService redisServie;  
	
	@Autowired()
	@Qualifier("preferentialService")
	private PreferentialService preferentialService;

	@Autowired
	public void setReceiverService(ReceiverService receiverService) {
		this.receiverService = receiverService;
	}

	@Autowired
	public void setCampusService(CampusService campusService) {
		this.campusService = campusService;
	}

	public PushService getPushService() {
		return pushService;
	}

	@Autowired
	public void setPushService(PushService pushService) {
		this.pushService = pushService;
	}

	@Autowired
	public void setUserServce(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setFoodService(FoodService foodService) {
		this.foodService = foodService;
	}

	protected static final Logger LOG = LoggerFactory
			.getLogger(OrderController.class);

	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * 生成购物车订单
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	/*@RequestMapping("/createOrder")
	public @ResponseBody Map<String, Object> createOrder(
			@RequestParam Integer campusId, @RequestParam String phoneId,
			@RequestParam Long foodId, @RequestParam Integer foodCount){
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Order order = new Order(campusId, phoneId, foodId, foodCount);
			Long orderId=order.getOrderId();
			List<Order> oldOrders = orderService.selectOrder(order);

			// 待优化。。。。。。。将delete和insert改为一次操作 delete by ljt相互订单不影响
			if (oldOrders.size() != 0) {
				order.setOrderCount(foodCount
						+ oldOrders.get(0).getOrderCount());
				orderService.deleteCartGood(order);
			}

			int flag = orderService.insertSelectiveOrder(order);

			if (flag != -1 && flag != 0) {
				map.put("orderId", orderId);
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "生成订单成功");
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "生成订单失败");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "生成订单失败");
		}

		return map;
	}*/

	/**
	 * 生成购物车订单
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/createOrderWx")
	public @ResponseBody Map<String, String> createOrderWx(
			@RequestParam String user_id,  @RequestParam String seller_id, @RequestParam String goods,@RequestParam String totalPackingFee){
		Map<String,String> map = new HashMap<String, String>();
		JSONObject node = new JSONObject();
		
		float packingFee = 0.0f;
		float orderPrice = 0.0f;
		float payPrice = 0.0f;
		
		System.out.println("createOrderWx:" + user_id + "," + seller_id + "," + goods);

		try {
			Order order = new Order(seller_id, user_id, goods,Float.valueOf(totalPackingFee));
			Long orderId=order.getOrderId();
			JSONArray records = new JSONArray();
			JSONObject record = new JSONObject();
			record.put("status", 1);
			record.put("time", order.getCreateTime());
			records.add(record);
			order.setRecords(records.toJSONString());
			
			JSONArray goodsArray = new JSONArray();
			JSONArray goodsTmp = JSON.parseArray(goods);
			
			for (int i = 0; i < goodsTmp.size(); i ++)
			{
				JSONObject good = new JSONObject();
				good.put("goods_name",goodsTmp.getJSONObject(i).getString("goods_name"));
				good.put("num",goodsTmp.getJSONObject(i).getString("num"));
				good.put("select_property",goodsTmp.getJSONObject(i).getString("select_property"));
				good.put("price_sum",String.valueOf(goodsTmp.getJSONObject(i).getFloatValue("price") * goodsTmp.getJSONObject(i).getFloatValue("num")));
				goodsArray.add(good);
				packingFee = packingFee + goodsTmp.getJSONObject(i).getFloatValue("packing_fee");
				orderPrice = orderPrice + goodsTmp.getJSONObject(i).getFloatValue("price") * goodsTmp.getJSONObject(i).getFloatValue("num");
			}
			
			Map<String, Object> paramMap3 = new HashMap<String, Object>();
			paramMap3.put("campusId",  order.getCampusId());
			Campus campus = campusService.getCampusById(paramMap3);
			
			order.setDeliveryFee(campus.getDelivery_fee());
			order.setOrderPrice(orderPrice);
			
			
			List<Preferential> list = preferentialService.getPreferential(paramMap3);
			float cutMoney = 0.0f;
			for(Preferential pref: list)
			{
				if((orderPrice >= pref.getNeedNumber())&& pref.getDiscountNum() > cutMoney)
				{
					cutMoney = pref.getDiscountNum();
				}
			}
			order.setCutMoney(cutMoney);
			order.setPayPrice(orderPrice + campus.getDelivery_fee() - cutMoney);
			order.setCouponMoney(0.0f);
			order.setPackingFee(packingFee);
					
			int flag = orderService.insertSelectiveOrder(order);
			if (flag != -1 && flag != 0)
			{
				//把订单插入到待支付的队列和redis  
				try{
					ThreadPoolUtil.execute(new Runnable(){  
			            @Override  
			            public void run() {  
			                //1 插入到待支付队列  
			                DSHOrder dshOrder = new DSHOrder(orderId,Constants.ORDER_CREATE, Constants.PAYDELAYTIME);  
			                delayService.add(dshOrder);  
			                System.out.println("DSHOrder add delayQueue");
			        
			                //2插入到redis  
			                redisServie.add(Constants.REDISPREFIX+orderId, dshOrder, Constants.REDISSAVETIME);  
			                System.out.println("DSHOrder add redis");
			            }  
			        }); 
				}
				catch(Exception e)
				{
					System.out.println("catch exception" + e);
				}
				
				System.out.println("CreateOrderWx ok");				
				node.put("quasi_order_id", String.valueOf(orderId));
				map.put("State", "Success");
				map.put("data", node.toString());	
				return map;
			} else 
			{
				map.put("State", "False");
				map.put("data", node.toString());	
				return map;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("State", "False");
		map.put("data", null);	

		return map;
	}
	
	/**
	 * 获得准订单信息
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/getQuasiOrderInfoWx")
	public @ResponseBody Map<String, String> getQuasiOrderInfoWx(@RequestParam String quasi_order_id, @RequestParam String user_id){
		Map<String,String> data = new HashMap<String, String>();
		JSONObject node = new JSONObject();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		System.out.println("getQuasiOrderInfoWx enter:" + quasi_order_id);
		
		paramMap.put("orderId",Long.parseLong(quasi_order_id));
		Order order = orderService.getOrderByIdWx(paramMap);
		if (order == null)
		{
			data.put("State", "False");
			data.put("data", "未查询到订单信息");	

			return data;
		}
		
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("userId",user_id);
		
		Receiver receiver = receiverService.getReceiverDefault(paramMap2);
		
		if (receiver != null)
		{
			node.put("receiver_addr_id", receiver.getAddressId());
			node.put("receiver", receiver.getName());
			node.put("receiver_phone", receiver.getPhone());
			node.put("receiver_addr", receiver.getAddress());
		}
		
		JSONArray goods = new JSONArray();
		JSONArray goodsTmp = JSON.parseArray(order.getGoods());
		
		for (int i = 0; i < goodsTmp.size(); i ++)
		{
			JSONObject good = new JSONObject();
			good.put("goods_name",goodsTmp.getJSONObject(i).getString("goods_name"));
			good.put("num",goodsTmp.getJSONObject(i).getString("num"));
			good.put("select_property",goodsTmp.getJSONObject(i).getString("select_property"));
			good.put("price_sum",String.valueOf(goodsTmp.getJSONObject(i).getFloatValue("price") * goodsTmp.getJSONObject(i).getFloatValue("num")));
			goods.add(good);
		}
		
		Map<String, Object> paramMap3 = new HashMap<String, Object>();
		paramMap3.put("campusId",  order.getCampusId());
		Campus campus = campusService.getCampusById(paramMap3);
		
		node.put("seller_name", campus.getCampusName());
			
		node.put("goods", goods);
		node.put("packing_fee", String.valueOf(order.getPackingFee()));
		node.put("delivery_fee", campus.getDelivery_fee().toString());
		
		node.put("cut_money", String.valueOf(order.getCutMoney())); //优惠多少
		node.put("coupon_money", String.valueOf(order.getCouponMoney())); //优惠多少
		node.put("cut_money_total", String.valueOf(order.getCutMoney()+ order.getCutMoney())); //总优惠
		node.put("pay_price", String.valueOf(order.getPayPrice())); 
		node.put("order_price", String.valueOf(order.getOrderPrice()));

		data.put("State", "Success");
		data.put("data", node.toString());	
		
		System.out.println("getQuasiOrderInfoWx rtn:" + node.toString());

		return data;
	}
	
	/**
	 * 订单更新收货地址
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/updateOrderAddrWx")
	public @ResponseBody Map<String, Object> updateOrderAddrWx(
			@RequestParam String user_id,  @RequestParam String quasi_order_id, @RequestParam String addr_id){
		Map<String,Object> map = new HashMap<String, Object>();
		JSONObject node = new JSONObject();
		
		System.out.println("updateOrderAddrWx:" + user_id + "," + quasi_order_id + "," + addr_id);

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",Long.parseLong(quasi_order_id));
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				map.put("State", "Fail");
				map.put("data", "获取订单信息失败");	

				return map;
			}
			
			order.setAddrId(addr_id);
			int flag = orderService.updateOrder(order);
			if (flag != -1 && flag != 0)
			{
				Map<String, Object> paramMap2 = new HashMap<String, Object>();
				paramMap2.put("userId",user_id);
				paramMap2.put("addressId",addr_id);
				
				Receiver receiver = receiverService.selectByPrimaryKey(paramMap2);
				
				if (receiver != null)
				{
					node.put("receiver_addr_id", addr_id);
					node.put("receiver", receiver.getName());
					node.put("receiver_phone", receiver.getPhone());
					node.put("receiver_addr", receiver.getAddress());
				}
				
				map.put("State", "Success");
				map.put("data", node);	

				return map;
			} else 
			{
				map.put("State", "False");
				map.put("data", null);	
				return map;
			}
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		map.put("State", "False");
		map.put("data", null);	

		return map;
	}
	
	/**
	 * 生成购物车订单
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/submitOrder")
	public @ResponseBody Map<String, String> SubmitOrderWx(
			@RequestParam String user_id,  @RequestParam String quasi_order_id, @RequestParam String addr_id,
			@RequestParam String remark){
		Map<String,String> map = new HashMap<String, String>();
		JSONObject node = new JSONObject();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",Long.parseLong(quasi_order_id));
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				map.put("State", "False");
				map.put("data", "生成订单失败");	

				return map;
			}
			
			order.setMessage(remark);
			order.setStatus((short)1);
			order.setAddrId(addr_id);
			
			int flag = orderService.updateOrder(order);
			if (flag != -1 && flag != 0)
			{
				node.put("order_id", quasi_order_id);
				map.put("State", "Success");
				map.put("data", node.toString());
				return map;
			} else 
			{
				map.put("State", "False");
				map.put("data", null);	
				return map;
			}
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		map.put("State", "False");
		map.put("data", null);	

		return map;
	}
	
	@RequestMapping("/cancelOrderWx")
	public @ResponseBody Map<String, String> cancelOrderWx(
			@RequestParam String user_id,  @RequestParam String order_id){
		Map<String,String> result = new HashMap<String, String>();
		JSONObject node = new JSONObject();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",Long.parseLong(order_id));
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put("State", "False");
				result.put("data", "生成订单失败");	

				return result;
			}

			order.setStatus(Constants.ORDER_CANCEL);
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",5);
			record.put("time", new Date());
			recordes.add(record);
			order.setRecords(recordes.toString());
			
			
			int flag = orderService.updateOrder(order);
			if (flag != -1 && flag != 0)
			{
				//启动退款流程，退款金额单位为分
				String resultStr = payService.refund(order_id, String.valueOf(order.getPayPrice()*100));
				try {
		              Map map =  PayUtil.doXMLParse(resultStr);
		              String returnCode = map.get("return_code").toString();
		              if(returnCode.equals("SUCCESS")){
		                  String resultCode = map.get("result_code").toString();
		                  if(resultCode.equals("SUCCESS")){
		                	//返回订单取消成功
		                	order.setStatus(Constants.ORDER_REFUND_SUCCESS);
  		          			
  		          			JSONArray recordes2 = JSON.parseArray(order.getRecords());
  		          			JSONObject record2 = new JSONObject();
  		          			record2.put("status",Constants.ORDER_REFUND_SUCCESS);
  		          			record2.put("time", new Date());
  		          			recordes2.add(record2);
  		          			order.setRecords(recordes2.toString());
  		          			
  		          			int flag2 = orderService.updateOrder(order);
		      				node.put("order_id", order_id);
		      				result.put("State", "Success");
		      				result.put("data", node.toString());
		      				return result;
		                  }   
		              }
		          } 
		          catch (Exception e) 
		          {
		              e.printStackTrace();
		
		          }
				
			} 
			else 
			{
				result.put("State", "False");
				result.put("data", null);	
				return result;
			}
				
		} catch (Exception e) {
			System.out.println(e);
		}
		
		result.put("State", "Fail");
		result.put("data", null);	

		return result;
	}
	
	/**
	 * 获取下达的所有订单
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getShopOrdersWx")
	public @ResponseBody Map<String, Object> getShopOrdersWx(
			@RequestParam String campus_id, @RequestParam Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("campusId", campus_id);
			
			paramMap.put("limit", 5);
			paramMap.put("offset", page * 5);//默认一次5条
	
			List<Order> myOrdersList =orderService.getCampusOrders(paramMap);
			System.out.println("getShopOrdersWx:" + campus_id + ",size:" + String.valueOf(myOrdersList.size()));
			
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("campusId",  campus_id);
			Campus campus = campusService.getCampusById(paramMap2);
			
			JSONArray orderArray = new JSONArray();
			
			for(Order order1:myOrdersList)
			{
				JSONObject order = new JSONObject();
				
				order.put("add_time", order1.getCreateTime());
				order.put("seller_id", order1.getCampusId());
				order.put("seller_name", campus.getCampusName());
				order.put("pic_url", campus.getPic_url());
				order.put("order_id", order1.getOrderId());
				if(order1.getStatus() == 8)//8:订单完成且已评论
				{
					order.put("state", 4);
					order.put("is_reviews", String.valueOf(1));
				}
				else
				{
					order.put("state", order1.getStatus());
					order.put("is_reviews", String.valueOf(0));
				}
				
				order.put("pay_price", order1.getPayPrice());	
				orderArray.add(order);
				
			}
			JSONObject data = new JSONObject();
			data.put("list",orderArray);
			data.put("count",String.valueOf(myOrdersList.size()));
			data.put("page",String.valueOf(page));

			map.put("State", "Success");
			map.put("data", data);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取下达的所有订单
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getOrdersMineWx")
	public @ResponseBody Map<String, Object> getOrdersMineWx(
			@RequestParam String user_id, @RequestParam Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId", user_id);
			
			System.out.println("page:" + String.valueOf(page) + "user_id:" + user_id);
			
			paramMap.put("limit", 5);
			paramMap.put("offset", page * 5);//默认一次5条
	
			List<Order> myOrdersList =orderService.getOrdersMine(paramMap);
			
			
			JSONArray orderArray = new JSONArray();
			
			
			for(Order order1:myOrdersList)
			{
				JSONObject order = new JSONObject();
				
				Map<String, Object> paramMap2 = new HashMap<String, Object>();
				paramMap2.put("campusId",  order1.getCampusId());
				Campus campus = campusService.getCampusById(paramMap2);
				
				order.put("add_time", order1.getCreateTime());
				order.put("seller_id", order1.getCampusId());
				order.put("seller_name", campus.getCampusName());
				order.put("pic_url", campus.getPic_url());
				order.put("order_id", order1.getOrderId());
				if(order1.getStatus() == 8)//8:订单完成且已评论
				{
					order.put("state", 4);
					order.put("is_reviews", String.valueOf(1));
				}
				else
				{
					order.put("state", order1.getStatus());
					order.put("is_reviews", String.valueOf(0));
				}
				
				order.put("pay_price", order1.getPayPrice());	
				orderArray.add(order);
				
			}
			JSONObject data = new JSONObject();
			data.put("list",orderArray);
			data.put("count",String.valueOf(myOrdersList.size()));
			data.put("page",String.valueOf(page));

			map.put("State", "Success");
			map.put("data", data);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取订单具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getOrdersInfoWx")
	public @ResponseBody Map<String, Object> getOrderInfoWx(
			@RequestParam String  user_id, @RequestParam String order_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			System.out.println("getOrdersInfoWx enter:" + user_id +"," + order_id);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			
			if (order == null)
			{
				map.put("State", "False");
				map.put("data", "查询订单详细信息失败");	
				return map;
			}
		
			JSONObject rtnOrder = new JSONObject();
			
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("campusId",  order.getCampusId());
			Campus campus = campusService.getCampusById(paramMap2);
			
			JSONArray flowarray = new JSONArray();
			
			JSONArray records = JSON.parseArray(order.getRecords());
			for (int i = 0; i < records.size(); i++)
			{
				JSONObject flow = new JSONObject(); //订单处理流程信息
				flow.put("time", records.getJSONObject(i).getDate("time"));
				flow.put("state", records.getJSONObject(i).getString("status"));
				flowarray.add(flow);
			}
			
			rtnOrder.put("add_time", order.getCreateTime());
			rtnOrder.put("flow", flowarray);
			
			DSHOrder dshOrder = redisServie.get(Constants.REDISPREFIX  + order_id);
			
			if(dshOrder != null && (order.getStatus() == Constants.ORDER_CREATE || order.getStatus() == Constants.ORDER_PAY_SUCCESS))
			{
				rtnOrder.put("left_time", dshOrder.getDelay(TimeUnit.SECONDS) + Constants.COUNTDELAY);//剩余多少秒
			}
			else
			{
				rtnOrder.put("left_time", 0);
			}

			//当前步骤剩余时间计时器，s为单位，小程序会启动定时器，到时间后重新获得订单状态
			rtnOrder.put("seller_name", campus.getCampusName());
			rtnOrder.put("is_reviews", String.valueOf(0));
			rtnOrder.put("state", order.getStatus());
			rtnOrder.put("deliver", campus.getDeliver());
			rtnOrder.put("goods", JSON.parseArray(order.getGoods()));
			rtnOrder.put("packing_fee", order.getPackingFee());
			rtnOrder.put("delivery_fee", order.getDeliveryFee());
			
			rtnOrder.put("cut_money", order.getCutMoney());
			rtnOrder.put("coupon_money", order.getCouponMoney());
			rtnOrder.put("order_price", order.getOrderPrice());
			rtnOrder.put("pay_price", order.getPayPrice());
			
			Map<String, Object> paramMap3 = new HashMap<String, Object>();
			paramMap3.put("userId",user_id);
			paramMap3.put("addressId",order.getAddrId());
			Receiver reveiver = receiverService.selectByPrimaryKey(paramMap3);
			
			System.out.println("receiver:" + user_id + "," + order.getAddrId());
			
			if (reveiver != null)
			{
				rtnOrder.put("receiver", reveiver.getName());
				rtnOrder.put("receiver_phone", reveiver.getPhone());
				rtnOrder.put("receiver_addr", reveiver.getAddress());
			}
			
			rtnOrder.put("remark", order.getMessage());
			rtnOrder.put("order_id", order.getOrderId());
			rtnOrder.put("seller_phone", campus.getCustomService());
			rtnOrder.put("localphone", "18551410942");

			map.put("State", "Success");
			map.put("data", rtnOrder);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取下达的所有订单
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getOrderInMine")
	public @ResponseBody Map<String, Object> getOrderInMine(
			@RequestParam String phoneId, Short status, Integer page,
			Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TogetherOrder> togetherOrdersList = new ArrayList<TogetherOrder>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phoneId", phoneId);
			paramMap.put("status", status);
			
			List<Integer> count=new ArrayList<Integer>();
			for(int i=1;i<=5;i++){
				paramMap.put("status", i);
				List<String> togetherId=orderService.getTogetherId(paramMap);
				count.add(togetherId.size());
			}
			
			map.put("waitPay",count.get(0));
			map.put("hasPaid",count.get(1));
			map.put("delivering",count.get(2));
			map.put("waitComment",count.get(3));
			map.put("done", count.get(4));
			
			if (limit != null && page != null) {
				paramMap.put("limit", limit);
				paramMap.put("offset", (page - 1) * limit);
			}
			paramMap.put("status", status);
			List<String> togetherIds = orderService.getTogetherId(paramMap);
			
			//System.out.println(JSON.toJSONString(togetherIds));
			
			if (togetherIds.size() != 0) {
				
				for (String togetherId : togetherIds) {
					TogetherOrder togetherOrder = new TogetherOrder(); // 一单
					togetherOrder.setTogetherId(togetherId);
					paramMap.put("togetherId", togetherId);
					List<SmallOrder> orderList = orderService
							.getOrderListInMine(paramMap); // 一单里面的小订单				
					togetherOrder.setSmallOrders(orderList);
					togetherOrder.setPayWay(orderList.get(0).getPayWay());
					togetherOrder.setTotalPrice(orderList.get(0).getTotalPrice());
					Short totalStatus=0;
					if(orderList.get(0).getStatus()!=4)
					{
						totalStatus=orderList.get(0).getStatus();
					}
					else
					{
						totalStatus=5;
						for (int i = 0; i < orderList.size(); i++) {
							if (orderList.get(i).getIsRemarked() == 0) {
								totalStatus = 4;
							}
						}
					}
					togetherOrder.setStatus(totalStatus);
					togetherOrder.setTogetherDate(orderList.get(0)
							.getTogetherDate());
					togetherOrdersList.add(togetherOrder);
				}
				
				//根据phoneId获取各个状态订单数量

				/*for (int i = 1; i <= 5; i++) {
					MiniOrder order = new MiniOrder();
					order.setStatus(Short.parseShort(i+""));
					if(i==1||i==2||i==3){
						paramMap.put("status", i);
					}
					else if(i==4){
						paramMap.put("status", 4);
						paramMap.put("isRemarked", 0);
					}
					else if(i==5){
						paramMap.put("status", 4);
						paramMap.put("isRemarked", 1);
					}
					order.setOrderCount(orderService.getMiniOrderByPhone(paramMap));
					statusList.add(order);
				}*/
				/*map.put("statusList", statusList);*/

				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取订单成功");
				map.put("orderList", JSON.parse(JSON
						.toJSONStringWithDateFormat(togetherOrdersList,
								"yyyy-MM-dd")));
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "暂无订单");
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取订单失败");
		}
		return map;
	}

	/**
	 * 弃用 获取下达的代发货订单
	 * 
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/getOrderInMineWait2Deliver")
	public @ResponseBody Map<String, Object> getOrderInMineWait2Deliver(
			@RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<SmallOrder> orderList = orderService
					.getOrderListInMineWait2Deliver(phoneId);
			if (orderList.size() != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取代发货订单成功");
				map.put("orderList", orderList);
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "暂无代发货订单");
			}

		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取代发货订单失败");
		}
		return map;
	}

	/**
	 * 弃用 获取下达的正在配送订单
	 * 
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/getOrderInMineDeliver")
	public @ResponseBody Map<String, Object> getOrderInMineDeliver(
			@RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<SmallOrder> orderList = orderService
					.getOrderListInMineDeliver(phoneId);
			if (orderList.size() != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取正在配送订单成功");
				map.put("orderList", orderList);
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "暂无订单配送中");
			}

		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取配送订单失败");
		}
		return map;
	}

	/**
	 * 弃用 获取完成订单
	 * 
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/getOrderInMineFinish")
	public @ResponseBody Map<String, Object> getOrderInMineFinish(
			@RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<SmallOrder> orderList = orderService
					.getOrderListInMineFinish(phoneId);
			if (orderList.size() != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取完成订单成功");
				map.put("orderList", orderList);
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "暂无完成订单");
			}

		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取完成订单失败");
		}
		return map;
	}

	/**
	 * 获取购物车里面的收藏订单（未完成订单）
	 * 
	 * @param phoneId
	 *            手机号
	 * @return
	 */
	@RequestMapping("/getUserOrder")
	public @ResponseBody Map<String, Object> getUserOrder(
			@RequestParam String phoneId, @RequestParam Integer campusId,
			Integer limit, Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phoneId", phoneId);
			paramMap.put("campusId", campusId);

			if (limit != null && page != null) {
				paramMap.put("limit", limit);
				paramMap.put("offset", (page - 1) * limit);
			}
			List<CartGood> orderList = orderService.getOrderList(paramMap);
			if (orderList != null && orderList.size() != 0) {
				for (CartGood cartGood : orderList) {
					if (cartGood.getIsDiscount() != 0) {
						cartGood.setDiscountPrice(cartGood.getDiscountPrice()); // 获取折扣价
					}
					paramMap.put("foodId", cartGood.getFoodId());
					paramMap.put("campusId", campusId);
				}
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取购物车订单成功");
				map.put("orderList", orderList);
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "购物车里暂时还没有订单哦，亲");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取购物车订单失败");
		}

		return map;
	}

	/**
	 * 删除购物车中的某一订单
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping("/deleteUserOrder")
	public @ResponseBody Map<String, Object> deleteUserOrder(
			@RequestParam Long orderId, @RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			int flag = orderService.deleteUserOrder(orderId, phoneId);
			if (flag != -1 && flag != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "删除订单成功");
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "删除订单失败");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "删除订单失败");
		}

		return map;
	}

	/**
	 * 删除多条订单，订单号用逗号隔开
	 * 
	 * @param orderId
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/deleteAllUserOrder")
	public @ResponseBody Map<String, Object> deleteAllUserOrder(
			@RequestParam String orderId, @RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			int flag = 0;
			String[] orderIdStrings = orderId.split(",");
			for (String oneOrderId : orderIdStrings) {
				if (oneOrderId != null && !oneOrderId.trim().equals("")) {
					flag = orderService.deleteUserOrder(
							Long.valueOf(oneOrderId), phoneId);

					if (flag == -1 || flag == 0) {
						map.put(Constants.STATUS, Constants.FAILURE);
						map.put(Constants.MESSAGE, "删除订单失败");
						break;
					}
				}
			}

			if (flag != -1 && flag != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "删除订单成功");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "删除订单失败");
		}

		return map;
	}

	/**
	 * 编辑订单的个数
	 * 
	 * @param orderId
	 *            订单号
	 * @param orderCount
	 *            订单数
	 * @return
	 */
	@RequestMapping("/editUserOrder")
	public @ResponseBody Map<String, Object> editUserOrder(
			@RequestParam Long orderId, @RequestParam String phoneId,
			@RequestParam Integer orderCount) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			int flag = orderService.editUserOrder(orderId, phoneId, orderCount);
			if (flag != 0 && flag != -1) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "编辑订单成功");
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "编辑订单失败");
			}

		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "编辑订单失败");
		}
		return map;
	}

	/**
	 * 获取成功订单
	 * 
	 * @param phoneId
	 *            用户手机号
	 * @return
	 */
	@RequestMapping("/getUserSuccessOrder")
	public @ResponseBody Map<String, Object> getUserSuccessOrder(
			@RequestParam String phoneId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<Order> orderList = orderService.getOrderSuccessList(phoneId);
			if (orderList != null && orderList.size() != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取订单成功");
				map.put("orderList", orderList);
			} else {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "暂时还没有订单哦，亲");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取订单失败");
		}

		return map;
	}

	/**
	 * 改变订单至下单状态
	 * 
	 * @param phoneId
	 * @param orderId
	 * @param message
	 * 留言
	 * @param reserveTime
	 * 预约送达时间
	 * @return
	 */
	/*
	@RequestMapping("/orderToBuy")
	public @ResponseBody Map<String, Object> changeOrderStatus2Buy(
			@RequestParam String phoneId, @RequestParam String orderId,
			@RequestParam String rank, String reserveTime, String message,@RequestParam Short payWay,
			@RequestParam Float totalPrice ,Integer preferentialsId,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			Calendar calendar = Calendar.getInstance();

			String[] orderString = orderId.split(",");     //传过来的orderId是一个以逗号隔开d字符串
			int flag = 0;
			String togetherId = phoneId + String.valueOf(new Date().getTime());

			paramMap.put("orderId",orderString[0]);
			paramMap.put("phoneId",phoneId);
			Campus campus=campusService.getCampus(paramMap);   //根据订单获取该校区的详细情况
			Integer campusId=campus.getCampusId();
			//判断该校区是否正在营业
			if(campus.getStatus()==0){
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, campus.getCloseReason());
				return map;
			}

			Calendar runOpenTime=Calendar.getInstance();
			runOpenTime.setTime(campus.getOpenTime());
			Calendar runCloseTime=Calendar.getInstance();
			runCloseTime.setTime(campus.getCloseTime());
			//判断是否超出校区营业时间

			int openHour=runOpenTime.get(Calendar.HOUR_OF_DAY);     
			int openMinute=runOpenTime.get(Calendar.MINUTE);
			int closeHour=runCloseTime.get(Calendar.HOUR_OF_DAY);
			int closeMinute=runCloseTime.get(Calendar.MINUTE);

			if(calendar.get(Calendar.HOUR_OF_DAY)>closeHour
					||(calendar.get(Calendar.HOUR_OF_DAY)==closeHour&&calendar.get(Calendar.MINUTE)>closeMinute)
					||calendar.get(Calendar.HOUR_OF_DAY)<openHour
					||(calendar.get(Calendar.HOUR_OF_DAY)==openHour&&calendar.get(Calendar.MINUTE)<runOpenTime.get(openMinute))
					){ 
				StringBuffer message2=new StringBuffer();
				message2.append("fou优该校区的营业时间为"+openHour+":");

				if(openMinute<10){
					message2.append("0"+openMinute);
				}else{
					message2.append(openMinute);
				}
				message2.append("--"+closeHour+":");
				if(openMinute<10){
					message2.append("0"+closeMinute);
				}else{
					message2.append(closeMinute);
				}

				map.put(Constants.STATUS,Constants.FAILURE); 
				map.put(Constants.MESSAGE,message2.toString()); 
				return map; 
			}
			DecimalFormat df = new DecimalFormat("####.0");
			
			Float serverPrice=orderService.getPriceDiscounted(orderString,campusId,phoneId);
			if(Math.abs(serverPrice-totalPrice)>=1){            //判断客户端价格和服务器端价格是否一致
				map.put(Constants.STATUS,Constants.FAILURE); 
				map.put(Constants.MESSAGE,"价格有误"); 
				System.out.println(serverPrice);
				return map;
			}
			//写入单价操作
			for (String id : orderString) {
				float price= 0f;
				paramMap.put("orderId",id);
				SmallOrder smallOrder=orderService.getOrderById(paramMap);
				if(smallOrder.getIsDiscount()==1)
				{
					price = (smallOrder.getDiscountPrice()*smallOrder.getOrderCount());
				}
				else
				{
					price = smallOrder.getPrice()*smallOrder.getOrderCount();
				}		
				flag = orderService.changeOrderStatus2Buy(phoneId, id,
						togetherId, rank, reserveTime, message,payWay,price,totalPrice);	
				
				Order order = orderService.selectOneOrder(phoneId, id); // 获取该笔订单的消息				
				paramMap.put("foodId", order.getFoodId());
				paramMap.put("orderCount", order.getOrderCount());
				paramMap.put("campusId",campus.getCampusId());
				foodService.changeFoodCount(paramMap); // 增加销量，减少存货
				if (flag == 0 || flag == -1) {
					break;
				}
			}

			String channel;
			if(payWay==1){
				channel="alipay";
			}else{
				channel="wx";
			}
			
			if (flag != -1 && flag != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "下单成功，即将开始配送！");
				String clientIp=getIpAddr(request);
				Charge charge=ChargeInterface.charge(channel,togetherId,(Float.parseFloat(df.format(totalPrice))),clientIp);
				map.put("charge", charge); //支付
				map.put("totalPrice",df.format(totalPrice));
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "下单失败，请重新开始下单");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "下单失败，请重新开始下单");
		}

		return map;
	}
	*/

	/**
	 * 改变订单至配送状态
	 * 
	 * @param adminPhone
	 * @param togetherId
	 * @return
	 */
	@Deprecated
	@RequestMapping("/orderToDeliver")
	public @ResponseBody Map<String, Object> changeOrderStatus2Deliver(
			@RequestParam String adminPhone,
			@RequestParam final String togetherId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			int flag = orderService.changeOrderStatus2Deliver(adminPhone,
					togetherId);

			if (flag != -1 && flag != 0) {

				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "正在配送中！");

				// 开启线程访问服务器进行推送

				new Thread(new Runnable() {

					 public void run() { //推送
						String userPhone=userService.getUserPhone(togetherId);
						System.out.println(userPhone);
						pushService.sendPush(userPhone,
								"您有一笔订单正在配送中,请稍候。感谢您对For优的支持", 1);

					} }).start();

			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "点击配送失败，请重试");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "点击配送失败，请重试！");
		}
		return map;
	}

	/**
	 * 修改订单状态至完成
	 * 
	 * @param adminPhone
	 * @param togetherId
	 * @return
	 */
	@Deprecated
	@RequestMapping("/orderToFinish")
	public @ResponseBody Map<String, Object> changeOrderStatus2Finish(
			@RequestParam String adminPhone, @RequestParam String togetherId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int flag = orderService.changeOrderStatus2Finish(adminPhone,
					togetherId);
			if (flag != -1 && flag != 0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "订单完成，谢谢您的惠顾！");

				final String userPhone = userService.getUserPhone(togetherId);


				new Thread(new Runnable() {

					public void run() { //推送
						pushService.sendPush(userPhone,
								"您有一笔订单已完成交易,赶快去评价吧！For优欢迎您下次惠顾", 1);

					} }).start();


			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "点击完成订单失败，请重试");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "点击完成订单失败，请重试");
		}
		return map;
	}

	/**
	 * 超级管理员获取待发货单信息
	 * 
	 * @param isSelected
	 * @return
	 */
	@RequestMapping("/superAdminGetOrder")
	public @ResponseBody Map<String, Object> superAdminGetOrder(
			@RequestParam Integer isSelected, @RequestParam Integer campusId,
			Integer limit, Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("isSelected", isSelected);
		requestMap.put("campusId", campusId);
		requestMap.put("tag", 1);		//订单有效
        //System.out.println(isSelected);
		if (page != null && limit != null) {
			requestMap.put("limit", limit);
			requestMap.put("offset", (page - 1) * limit);
		} 
		
		try {
			List<SuperAdminOrder> orders = orderService
					.superAdminGetOrder(requestMap);
			
			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE, "获取订单成功！");
			map.put("orderList", JSONArray.parse(JSON
					.toJSONStringWithDateFormat(orders, "yyyy-MM-dd")));
		} catch (Exception e) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取订单失败！");
		}

		return map;
	}

	/**
	 * 给某一订单设置配送员
	 * 
	 * @param togetherId
	 * @param adminPhone
	 * @return
	 */
	@RequestMapping("/setDeliverAdmin")
	public @ResponseBody Map<String, Object> setDeliverAdmin(
			@RequestParam String togetherId,
			@RequestParam final String adminPhone) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int flag1 = orderService.setDeliverAdmin(togetherId, adminPhone); // 设置配送员
			//修改大订单状态为3（配送中）
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("togetherId", togetherId);
			paramMap.put("status", 3);
			int flag2 = orderService.modifyOrderStatus(paramMap);
			if (flag1 != -1 && flag1 != 0 && flag2!=-1 && flag2!=0) {
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "设置配送员成功！");

				new Thread(new Runnable() {

					public void run() {
						// 推送
						pushService.sendPush(adminPhone,
								"For优提醒您，一笔新订单已达到，请及时配送，辛苦您了。", 1);

					}
				}).start();

			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "设置配送员失败！");
			}
		} catch (Exception e) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "设置配送员失败！");
		}

		return map;
	}

	/**
	 * 
	 * 配送员获取配送订单
	 * 
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/DeliverAdminGetOrder")
	public @ResponseBody Map<String, Object> deliverGetOrder(
			@RequestParam String phoneId, @RequestParam Integer campusId,
			Integer limit, Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("phoneId", phoneId);
		requestMap.put("campusId", campusId);
		if (page != null && limit != null) {
			requestMap.put("offset", (page - 1) * limit);
			requestMap.put("limit", limit);
		}
		try {
			// 获取一笔订单列表
			List<DeliverOrder> deliverOrders = orderService
					.deliverGetOrder(requestMap);
			if(deliverOrders.size()<=0){
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "订单列表为空，请检查phoneId和campusId是否正确。如正确，则当前没有要配送的订单");
			}else{
				for (DeliverOrder deliverOrder : deliverOrders) {
					//String togetherId = deliverOrder.getTogetherId();
					String togetherId =  "1";
					// 获取订单食品集
					List<DeliverChildOrder> deliverChildOrders = orderService
							.getDeliverChildOrders(togetherId);
					Float priceFloat = 0f;
					priceFloat = orderService.getTotalPriceByTogetherId(togetherId);
//					// 获取该笔订单总价
//					for (DeliverChildOrder deliverChildOrder : deliverChildOrders) {
//						if (deliverChildOrder.getIsDiscount() == 0) {
//							priceFloat += deliverChildOrder.getPrice()
//									* deliverChildOrder.getOrderCount();
//						} else {
//							priceFloat += deliverChildOrder.getDiscountPrice()
//									* deliverChildOrder.getOrderCount();
//						}
//					}
					//deliverOrder.setTotalPrice(priceFloat);
					//deliverOrder.setOrderList(deliverChildOrders);
				}

				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "获取配送订单成功！");
				map.put("orderList", JSONArray.parse(JSON
						.toJSONStringWithDateFormat(deliverOrders, "yyyy-MM-dd")));
			}
		} catch (Exception e) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取配送订单失败！");
		}

		return map;
	}

	/**
	 * pc端获取简单的订单列表
	 * 
	 * @param status
	 * @param limit
	 * @param offset
	 * @param search
	 * @return
	 */
	@RequestMapping("/getPCSimpleOrder")
	public @ResponseBody Map<String, Object> getPcOrders(Short status,@RequestParam String campusId,
			Integer limit, Integer offset, String search) {
		Map<String, Object> map = new HashMap<String, Object>();
		
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("campusId", campusId);
        paramMap.put("limit", limit);
        paramMap.put("offset", offset);
        paramMap.put("search", search);
        paramMap.put("status", status);
        System.out.println("limit:" + limit);
        System.out.println("offset:" + offset);
        System.out.println("search:" + search);
        System.out.println("status:" + status);
		
		List<PCOrder> listsresult = new ArrayList<>();
	
		List<DeliverOrder> lists = orderService.getPCSimpleOrders(paramMap);
		for (DeliverOrder order: lists)
		{
			JSONObject obj = new JSONObject();
			obj.put("togetherId", order.getOrderId());
			obj.put("phone", order.getPhone());
			obj.put("name", order.getName());//食品名称
			obj.put("address", order.getAddress());
			obj.put("price", order.getPayPrice());
			obj.put("adminName", order.getUserName());
			obj.put("orderCount", 10 );
			
			obj.put("isDiscount", "0");
			obj.put("discountPrice", "0");
			obj.put("foodPrice", order.getOrderPrice());
			obj.put("receiverPhone", order.getPhone());
			obj.put("togetherDate", order.getCreateTime());
			obj.put("adminPhone", "111");
			listsresult.add(JSON.parseObject(JSON.toJSONString(obj),PCOrder.class));
		}
		if (status == 4)
		{
			//状态4：已完成，8：已完成且完成评论
			paramMap.put("status", 8);
			List<DeliverOrder> lists2 = orderService.getPCSimpleOrders(paramMap);
			for (DeliverOrder order: lists2)
			{
				JSONObject obj = new JSONObject();
				obj.put("togetherId", order.getOrderId());
				obj.put("phone", order.getPhone());
				obj.put("name", order.getName());//食品名称
				obj.put("address", order.getAddress());
				obj.put("price", order.getPayPrice());
				obj.put("adminName", order.getUserName());
				obj.put("orderCount", 10 );
				
				obj.put("isDiscount", "0");
				obj.put("discountPrice", "0");
				obj.put("foodPrice", order.getOrderPrice());
				obj.put("receiverPhone", order.getPhone());
				obj.put("togetherDate", order.getCreateTime());
				obj.put("adminPhone", "111");
				listsresult.add(JSON.parseObject(JSON.toJSONString(obj),PCOrder.class));
			}
		}
		//DecimalFormat df = new DecimalFormat("####.00");

		/*for (PCOrder order : lists) {
			// 如果是完成订单，直接显示交易价格，否则计算应收取的价格
			if (order.getPrice() == null) {
				if (order.getIsDiscount() == 0) {
					order.setPrice(Float.parseFloat(df.format(order
							.getFoodPrice() * order.getOrderCount())));
				} else {
					order.setPrice(Float.parseFloat(df.format(order
							.getDiscountPrice() * order.getOrderCount())));
				}
				//先设置个价格
				order.setPrice(0.0f);
			}
		}*/

		JSONArray jsonArray = JSONArray.parseArray(JSON
				.toJSONStringWithDateFormat(listsresult, "yyyy-MM-dd"));

		long totalCount = orderService.getPCSimpleOrdersCount(paramMap);
		map.put("rows", jsonArray);
		map.put("total", totalCount);
		return map;
	}

	/**
	 * 设置无效订单
	 * 
	 * @param togetherId
	 * @return
	 */
	@RequestMapping(value = "/setOrderInvalid", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> setOrderInvalid(
			@RequestParam String togetherId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("togetherId", togetherId);

			int flag = orderService.setOrderInvalid(parameterMap);
			if (flag != -1) {
				resultMap.put(Constants.STATUS, Constants.SUCCESS);
				resultMap.put(Constants.MESSAGE, "订单已取消");
			} else {
				resultMap.put(Constants.STATUS, Constants.FAILURE);
				resultMap.put(Constants.MESSAGE, "订单取消失败，请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put(Constants.STATUS, Constants.FAILURE);
			resultMap.put(Constants.MESSAGE, "订单取消失败，请重试");
		}

		return resultMap;
	}

	/**
	 * 获取某日订单所有订单详情
	 * 
	 * @param date
	 * @return
	 */
	@RequestMapping("/getOrdersByDate")
	@ResponseBody
	public Map<String, Object> getOrdersByDate(String date,
			@RequestParam String campusId, Integer limit, Integer page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		DecimalFormat df = new DecimalFormat("####.00");

		try {
			if (date.equals("") || date.equals("null"))
			{
				date = null;
			}
			else
			{
				date = date.replace("年", "-").replace("月", "-").replace("日", "");
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("createTime", date);
			paramMap.put("campusId", campusId);

			if (page != null && limit != null) {
				paramMap.put("limit", limit);
				paramMap.put("offset", (page - 1) * limit);
			}

			System.out.println("getOrdersByDate:" + date);
			System.out.println("campusId:" +campusId);

			List<DeliverOrder> deliverOrders = orderService.selectOrdersByDate(paramMap);
			Float totalPrice = 0f;
			System.out.println("deliverOrders size:" + String.valueOf(deliverOrders.size()));
			for (DeliverOrder deliverOrder : deliverOrders)
			{
				totalPrice = totalPrice + deliverOrder.getPayPrice();
				//JSONArray goods = new JSONArray();
				JSONArray goodsTmp = JSON.parseArray(deliverOrder.getGoods());
				List<DeliverChildOrder> goods  = new ArrayList<>();
				
				for (int i = 0; i < goodsTmp.size(); i ++)
				{
					JSONObject good = new JSONObject();
					good.put("foodName",goodsTmp.getJSONObject(i).getString("goods_name"));
					good.put("orderCount",goodsTmp.getJSONObject(i).getString("num"));
					//good.put("select_property",goodsTmp.getJSONObject(i).getString("select_property"));
					good.put("price",String.valueOf(goodsTmp.getJSONObject(i).getFloatValue("price")));
					good.put("totalPrice",String.valueOf(goodsTmp.getJSONObject(i).getFloatValue("price") * goodsTmp.getJSONObject(i).getFloatValue("num")));
					//goods.add((DeliverChildOrder)JSONObject.toBean(good, DeliverChildOrder.class));
					goods.add(JSON.parseObject(JSON.toJSONString(good),DeliverChildOrder.class));

				}
				deliverOrder.setOrderList(goods);
			}
			
			
			//for (DeliverOrder deliverOrder : deliverOrders) {
				//String togetherId = deliverOrder.getTogetherId();
				//System.out.println(JSON.toJSON(deliverOrder.getTogetherDate()));
				// 获取订单食品集
				/*paramMap.put("togetherId", togetherId);
				List<DeliverChildOrder> deliverChildOrders = orderService
						.getAllChildOrders(paramMap);
				Float priceFloat = 0f;*/
				
				/*// 获取该笔订单总价
				for (DeliverChildOrder deliverChildOrder : deliverChildOrders) {
					if (deliverChildOrder.getIsDiscount() == 0) {
						priceFloat += deliverChildOrder.getPrice()
								* deliverChildOrder.getOrderCount();
					} else {
						priceFloat += deliverChildOrder.getDiscountPrice()
								* deliverChildOrder.getOrderCount();
					}
					priceFloat+=deliverChildOrder.getPrice();
				}
				totalPrice += priceFloat;*/
				//deliverOrder.setTotalPrice(Float.parseFloat(df.format(priceFloat)));
				//deliverOrder.setOrderList(goods);
			//}

			resultMap.put("total_price", df.format(totalPrice));
			resultMap.put("counts", deliverOrders.size());
			resultMap.put("orderList", JSONArray.parse(JSON.toJSONStringWithDateFormat(deliverOrders,
							"yyyy-MM-dd HH:mm:ss")));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 根据togetherId获取大订单信息
	 * 
	 * @param togetherId
	 */
	@RequestMapping("getBigOrderById")
	@ResponseBody
	public Map<String, Object> getBigOrderById(@RequestParam String togetherId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		Short status = 0;
		paramMap.put("togetherId", togetherId);
		BigOrder bigOrder = new BigOrder();
		bigOrder.setTogetherId(togetherId);
		List<SmallOrder> orders = orderService.getOrdersById(paramMap);
		
		String campusId=orders.get(0).getCampusId();
		
		String campusName=campusService.getCampusName(campusId);
		
		if (orders.size() > 0 && orders!= null) {
			if (orders.get(0).getStatus() != 4) {
				status = orders.get(0).getStatus();
			} else {
				status = 5;
				for (int i = 0; i < orders.size(); i++) {
					if (orders.get(i).getIsRemarked() == 0) {
						status = 4;
					}
				}

			}
			Receiver receiver = receiverService.getReceiver(paramMap);
			receiver.setAddress(campusName+receiver.getAddress());
			Date date = orders.get(0).getTogetherDate();
			bigOrder.setDate(date);
			//System.out.println(orders.get(0).getPayWay());
			bigOrder.setPayWay(orders.get(0).getPayWay());
			
			Float sum=orders.get(0).getTotalPrice();
			if(sum!=null)
			{
				DecimalFormat df = new DecimalFormat("0.0");
				bigOrder.setTotalPrice(df.format(sum));
			}
			bigOrder.setOrders(orders);
			bigOrder.setReceiver(receiver);
			bigOrder.setStatus(status);
			resultMap.put("BigOrder",
					JSONArray.parse(JSON.toJSONStringWithDateFormat(bigOrder,
							"yyyy-MM-dd HH:mm:ss")));
			resultMap.put(Constants.STATUS, Constants.SUCCESS);
			resultMap.put(Constants.MESSAGE, "获取大订单信息成功");
		} else {
			resultMap.put(Constants.STATUS, Constants.FAILURE);
			resultMap.put(Constants.MESSAGE, "没有这个大订单");
		}

		return resultMap;
	}

	/**
	 * 修改订单状态
	 * @param orderId
	 * @param status
	 * @param togetherId
	 * @return
	 */
	@RequestMapping("/modifyOrderStatus")
	public @ResponseBody Map<String, Object> modifyOrderStatus(	@RequestParam final String togetherId, @RequestParam Short status, Long orderId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		
		try {		
			requestMap.put("togetherId", togetherId);
			requestMap.put("status", status);	
			final String userPhone=orderService.getUserPhone(requestMap);          //获取用户手机号
			//final String adminPhone=orderService.getAdminPhone(requestMap);        //获取配送员手机号
			Integer flag = null;
			switch(status){
			case 0:
				//购物车
				//flag = orderService.modifyOrderStatus(requestMap);
				break;
			case 1:
				//待付款
				//flag = orderService.modifyOrderStatus(requestMap);
				break;
			case 2:
				//待确认
				flag = orderService.modifyOrderStatus(requestMap);
				break;
			case 3:
				//配送中
				flag = orderService.modifyOrderStatus(requestMap);
				new Thread(() -> { //推送

                    pushService.sendPush(userPhone,
                            "您有一笔订单正在配送中,请稍候。感谢您对For优的支持", 1);

                }).start();
				break;
			case 4:
				//待评价
				flag = orderService.modifyOrderStatus(requestMap);
				new Thread(() -> { //推送

                    pushService.sendPush(userPhone,
                            "您有一笔订单已经完成,赶快去评价吧。感谢您对For优的支持", 1);

                }).start();
				break;
			case 5:
				//小订单已完成
				requestMap.put("orderId", orderId);
				requestMap.put("isRemarked", 1);
				requestMap.put("status", 4);
				flag = orderService.modifyOrderStatus(requestMap);
				break;
			default:
				break;
			}
			resultMap.put(Constants.STATUS, Constants.SUCCESS);
			resultMap.put(Constants.MESSAGE, "更改状态成功");
			resultMap.put("flag", flag);
		} catch (Exception e) {
			resultMap.put(Constants.STATUS, Constants.FAILURE);
			resultMap.put(Constants.MESSAGE, "更改状态失败");
		}
		
		return resultMap;
	}


	/**
	 * 删除订单（status=4）
	 *@param togetherId
	 */
	@RequestMapping("/deleteOrder")
	public @ResponseBody Map<String, Object> deleteOrder(@RequestParam String togetherId)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("togetherId", togetherId);
		List<SmallOrder> orders=orderService.getOrdersById(paramMap);
		if(orders.size() > 0)
		{
			orderService.deleteOrder(paramMap);
			resultMap.put(Constants.STATUS, Constants.SUCCESS);
			resultMap.put(Constants.MESSAGE, "删除订单成功");
		} else {
			resultMap.put(Constants.STATUS, Constants.FAILURE);
			resultMap.put(Constants.MESSAGE, "订单不存在,删除订单失败");
		}

		return resultMap;
	}

	/**
	 * 商品详情处立即购买
	 * @param campusId
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @return
	 */
	/*
	@RequestMapping("/purchaseImmediately")
	public @ResponseBody Map<String, Object> purchaseImmediately(
			@RequestParam Integer campusId, @RequestParam String phoneId,
			@RequestParam Long foodId, @RequestParam Integer foodCount)
			{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			Order order = new Order(campusId, phoneId, foodId, foodCount);
			order.setStatus((short)7);
			paramMap.put("orderId",order.getOrderId());
			int flag = orderService.insertSelectiveOrder(order);

			if (flag == -1 || flag == 0) {
				resultMap.put(Constants.STATUS, Constants.FAILURE);
				resultMap.put(Constants.MESSAGE, "生成订单失败");
			}

			SmallOrder smallOrder=orderService.getOrderById(paramMap);
			resultMap.put("order", smallOrder);
			resultMap.put(Constants.STATUS, Constants.SUCCESS);
			resultMap.put(Constants.MESSAGE, "订单详情：");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put(Constants.STATUS, Constants.FAILURE);
			resultMap.put(Constants.MESSAGE, "生成订单失败");			
		}
		return resultMap;

			}
	*/
	/**
	 * 根据id获取满减信息
	 * @param preferentialId
	 * @return
	 */
	@RequestMapping("/getPreferentialById")
	public @ResponseBody Map<String, Object> getPreferentialById(@RequestParam Integer preferentialId)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Preferential preferential=orderService.getPreferentialById(preferentialId);
		resultMap.put("preferential",preferential);
		resultMap.put(Constants.STATUS,Constants.SUCCESS);
		resultMap.put(Constants.MESSAGE,"获取成功");
		return resultMap;
	}
	
	/**
	 * 获取满减信息
	 * @return
	 */
	@RequestMapping("/getPreferentials")
	public @ResponseBody Map<String, Object> getPreferentialList(@RequestParam Integer campusId)
	{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("campusId", campusId);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Preferential> preferentials=orderService.getPreferential(paramMap);
		resultMap.put("preferential",preferentials);
		resultMap.put(Constants.STATUS,Constants.SUCCESS);
		resultMap.put(Constants.MESSAGE,"获取成功");
		return resultMap;
	}
	
	//获取客户端ip
	public String getIpAddr(HttpServletRequest request){
	    String ip = request.getHeader("x-forwarded-for");
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
	
	/**
	 * 取消订单并退款
	 * @param togetherId
	 * @return
	 */
	@RequestMapping("/cancelOrderWithRefund")
	public @ResponseBody Map<String,Object> cancelOrderWithRefund(@RequestParam String togetherId){
		Map<String,Object> map=new HashMap<String,Object>();
		
		try {
			Map<String,Object> paramMap=new HashMap<String,Object>();
			paramMap.put("togetherId", togetherId);
			
			Integer flag=orderService.cancelOrderWithRefund(paramMap);
			if(flag!=-1){
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE,"取消订单成功，退款受理中，请耐心等待");
			}else{
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE,"取消订单失败，请重试");
			}
		} catch (Exception e) {
			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE,"取消订单失败，请重试");
		}
		
		return map;
	}
	
	/**
	 * 获取退款订单
	 * @param campusId
	 * @return
	 */
	@RequestMapping("/getRefundOrder")
	public @ResponseBody JSONArray getRefundOrder(Integer campusId,Integer type){
		Map<String,Object> paramMap=new HashMap<String,Object>();
				
		if(type==1){
			paramMap.put("status",10);
		}else if(type==0){
			paramMap.put("status", 9);
			paramMap.put("campusId",campusId);
		}
		
	    List<SuperAdminOrder> refundOrders=orderService.getPCOrders(paramMap);
		System.out.println(refundOrders);
	    return JSONArray.parseArray(JSON.toJSONStringWithDateFormat(refundOrders, "yyyy-MM-dd"));
	}
	
	/**
	 * 获取退款订单
	 * @param campusId
	 * @return
	 */
	@RequestMapping("/getInvalideOrder")
	public @ResponseBody JSONArray getInvalidOrder(Integer campusId){
		Map<String,Object> paramMap=new HashMap<String,Object>();
				
		paramMap.put("campusId",campusId);
		
	    List<SuperAdminOrder> refundOrders=orderService.getPCInvalidOrders(paramMap);  //PC获取无效订单
	    return JSONArray.parseArray(JSON.toJSONStringWithDateFormat(refundOrders, "yyyy-MM-dd"));
	}
	
	/**
	 * 取消退款
	 * @return
	 */
	@RequestMapping("/cancelRefund")
	public @ResponseBody Map<String,Object> cancelRefund(@RequestParam String togetherId){
		Map<String,Object> resultMap=new HashMap<>();
		
		try {
			Map<String,Object> paramMap=new HashMap<>();
			paramMap.put("togetherId",togetherId);
			
			int flag=orderService.updateCancelRefund(paramMap);
			
			if(flag==-1){
				resultMap.put(Constants.STATUS,Constants.FAILURE);
				resultMap.put(Constants.MESSAGE,"取消失败");
			}else{
				resultMap.put(Constants.STATUS, Constants.SUCCESS);
				resultMap.put(Constants.MESSAGE,"取消成功");
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			resultMap.put(Constants.STATUS,Constants.FAILURE);
			resultMap.put(Constants.MESSAGE,"取消失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 总校区管理员确认退款
	 * @param togetherId
	 * @param totalPrice
	 * @return
	 */
	@RequestMapping("/refund")
	public @ResponseBody Map<String,Object> refund(@RequestParam String togetherId,Float totalPrice){
		Map<String,Object> resultMap=new HashMap<>();
		
		try {
		    Map<String,Object> paramMap=new HashMap<String,Object>();
		    paramMap.put("togetherId", togetherId);
		    
		    Float price=orderService.getTotalPriceByTogetherId(togetherId);
		    if(Math.abs(price-totalPrice)>0.1){
		    	resultMap.put(Constants.STATUS,Constants.FAILURE);
				resultMap.put(Constants.MESSAGE,"退款失败");
		    }else{
		    	 String chargeId=orderService.getChargeId(paramMap);
		    	 if(chargeId!=null){
		    		 Refund refund=ChargeInterface.Refund(chargeId, price);
		    		 if(refund!=null){
		    			 resultMap.put("refund", refund);
				    	 resultMap.put(Constants.STATUS,Constants.SUCCESS);
				    	 resultMap.put(Constants.MESSAGE,"操作成功");
		    		 }else{
		    			 resultMap.put(Constants.STATUS,Constants.FAILURE);
				    	 resultMap.put(Constants.MESSAGE,"操作失败");
		    		 }
		    	 }else{
		    		 resultMap.put(Constants.STATUS,Constants.FAILURE);
			    	 resultMap.put(Constants.MESSAGE,"操作失败");
		    	 }	
		    }
		} catch (Exception e) {
			e.getStackTrace();
			resultMap.put(Constants.STATUS,Constants.FAILURE);
			resultMap.put(Constants.MESSAGE,"退款失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 校区管理员确认退款
	 * @param togetherId
s	 * @return
	 */
	@RequestMapping("/confirmRefund")
	public @ResponseBody Map<String,Object> confirmRefund(String togetherId){
		Map<String,Object> resultMap=new HashMap<>();
		
		try {
		    Map<String,Object> paramMap=new HashMap<String,Object>();
		    paramMap.put("togetherId", togetherId);
		    int flag=orderService.updateRefundStatus(paramMap);   //将状态置为10
		    if(flag!=-1){
		    	resultMap.put(Constants.STATUS,Constants.SUCCESS);
		        resultMap.put(Constants.MESSAGE,"修改成功");
		    }else{
		    	resultMap.put(Constants.STATUS,Constants.FAILURE);
		    	resultMap.put(Constants.MESSAGE, "修改失败");
		    }
		} catch (Exception e) {
			e.getStackTrace();
			resultMap.put(Constants.STATUS,Constants.FAILURE);
			resultMap.put(Constants.MESSAGE,"修改失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取销量和销售额
	 * @param campusId
	 * @return
	 */
	@RequestMapping("getSalesInfoByCampusId")
	@ResponseBody
	public JSONArray getSalesByDate(@RequestParam Integer campusId, String month){
		Map<String,Object> paramMap=new HashMap<String,Object>();
		
		Date dateStart;
		Date dateEnd;
		List<TradeInfo> tradeList = new ArrayList<TradeInfo>();
		
		paramMap.put("campusId", campusId);
		//System.out.println("month="+month);
		//如果month不为空，说明切换了月份；但是注意month为本月的时候，此时是下面的情况
		if(month!=null&&!month.equals(null)&&!month.equals("")&&!CalendarTool.checkIsThisMonth(month)){
			Map<String,Date> dateMap = CalendarTool.getFirstAndLastDayOfMonth(month);
			dateStart = dateMap.get("monthStart");
			dateEnd = dateMap.get("monthEnd");
			paramMap.put("dateStart", dateStart);
			paramMap.put("dateEnd", dateEnd);
			TradeInfo monthInfo = new TradeInfo();
			monthInfo.setInfoDateType("当月");
			monthInfo.setOrderCount(orderService.getSalesInfoByCampusId(paramMap));//获取指定时间段和指定校区的订单总数
			monthInfo.setTradeVolume(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段和指定校区的订单交易额
			paramMap.put("payWay", 1);	//payWay:0,没有; 1,支付宝; 2,微信
			monthInfo.setTradeVolumeAliPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区和指定支付方式的订单交易额
			paramMap.put("payWay", 2);
			monthInfo.setTradeVolumeWeChatPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区、指定支付方式的订单交易额
			tradeList.add(monthInfo);
			return (JSONArray)JSON.toJSON(tradeList);
		}
		
		//当天订单数、销售额-
		dateStart = CalendarTool.getTodayStart();
		dateEnd  = CalendarTool.getTodayEnd();
		paramMap.put("dateStart", dateStart);
		paramMap.put("dateEnd", dateEnd);
		TradeInfo todayInfo = new TradeInfo();
		todayInfo.setInfoDateType("今天");
		todayInfo.setOrderCount(orderService.getSalesInfoByCampusId(paramMap));	//获取指定时间段和指定校区的订单总数
		todayInfo.setTradeVolume(orderService.getTradeVolumeByCampusId(paramMap));	//获取指定时间段和指定校区的订单交易额
		paramMap.put("payWay", 1);	//payWay:0,没有; 1,支付宝; 2,微信
		todayInfo.setTradeVolumeAliPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区、指定支付方式的订单交易额
		paramMap.put("payWay", 2);
		todayInfo.setTradeVolumeWeChatPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区、指定支付方式的订单交易额
		tradeList.add(todayInfo);
		paramMap.remove("payWay");
		
		//本周订单数、销售额
		dateStart = CalendarTool.getMondayOfThisWeek();
		dateEnd  = CalendarTool.getSundayOfThisWeek();
		paramMap.put("dateStart", dateStart);
		paramMap.put("dateEnd", dateEnd);
		TradeInfo weekInfo = new TradeInfo();
		weekInfo.setInfoDateType("本周");
		weekInfo.setOrderCount(orderService.getSalesInfoByCampusId(paramMap));//获取指定时间段和指定校区的订单总数
		weekInfo.setTradeVolume(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段和指定校区的订单交易额
		paramMap.put("payWay", 1);	//payWay:0,没有; 1,支付宝; 2,微信
		weekInfo.setTradeVolumeAliPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区和指定支付方式的订单交易额
		paramMap.put("payWay", 2);
		weekInfo.setTradeVolumeWeChatPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区、指定支付方式的订单交易额
		tradeList.add(weekInfo);
		paramMap.remove("payWay");
		
		//本月订单数、销售额
		dateStart = CalendarTool.getFirstDayOfThisMonth();
		dateEnd = CalendarTool.getLastDayOfThisMonth();
		paramMap.put("dateStart", dateStart);
		paramMap.put("dateEnd", dateEnd);
		TradeInfo monthInfo = new TradeInfo();
		monthInfo.setInfoDateType("本月");
		monthInfo.setOrderCount(orderService.getSalesInfoByCampusId(paramMap));//获取指定时间段和指定校区的订单总数
		monthInfo.setTradeVolume(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段和指定校区的订单交易额
		paramMap.put("payWay", 1);	//payWay:0,没有; 1,支付宝; 2,微信
		monthInfo.setTradeVolumeAliPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区和指定支付方式的订单交易额
		paramMap.put("payWay", 2);
		monthInfo.setTradeVolumeWeChatPay(orderService.getTradeVolumeByCampusId(paramMap));//获取指定时间段、指定校区、指定支付方式的订单交易额
		tradeList.add(monthInfo);
		
		return (JSONArray)JSON.toJSON(tradeList);
	}
	
	@RequestMapping("/deleteOrderTrue")
	@ResponseBody
	public Map<String,Object> deleteOrderTrue(@RequestParam String togetherId){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		try {
			Map<String,Object> paramMap=new HashMap<String,Object>();
			paramMap.put("togetherId",togetherId);
			int flag=orderService.deleteOrderTrue(paramMap);
			if(flag!=-1){
		    	resultMap.put(Constants.STATUS,Constants.SUCCESS);
		        resultMap.put(Constants.MESSAGE,"删除成功");
		    }else{
		    	resultMap.put(Constants.STATUS,Constants.FAILURE);
		    	resultMap.put(Constants.MESSAGE, "删除失败");
		    }
		} catch (Exception e) {
			e.getStackTrace();
			resultMap.put(Constants.STATUS,Constants.FAILURE);
	    	resultMap.put(Constants.MESSAGE, "删除失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 商家接单
	 * 
	 * @return
	 */
	@RequestMapping("/setRecvOrderWx")
	public @ResponseBody Map<String, Object> setRecvOrderWx(
			@RequestParam String  user_id, @RequestParam String order_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId",order_id);
		Order order = orderService.getOrderByIdWx(paramMap);
		if (order == null)
		{
			map.put("State", "False");
			map.put("data", "查询订单详细信息失败");	
			return map;
		}
		
		JSONArray recores = JSON.parseArray(order.getRecords());
		JSONObject record = new JSONObject();
		record.put("status",3);
		record.put("time", new Date());
		recores.add(record);
		
		paramMap.put("records",recores.toJSONString());
		paramMap.put("status",Constants.ORDER_RECEIVE);
		
		int flage = orderService.updateOrderStatusWx(paramMap);
		if(flage != -1 && flage !=0)
		{

			//从delay队列删除，从redis删除  
	        ThreadPoolUtil.execute(new Runnable(){  
	            public void run(){  
	                //从delay队列删除  
	                delayService.remove(Long.parseLong(order_id));  
	                //从redis删除  
	                redisServie.delete(Constants.REDISPREFIX+order_id); 
	                
	                //重新添加代用户收货的延迟队列
	                DSHOrder dshOrder = new DSHOrder(Long.parseLong(order_id),Constants.ORDER_RECEIVE, Constants.WAITCONFIRM);  
	                delayService.add(dshOrder);  
	        
	                //2插入到redis  
	                redisServie.add(Constants.REDISPREFIX+order_id, dshOrder, Constants.REDISSAVETIME);  
	            }  
	        });
			map.put("State", "Success");
			map.put("data", null);	
		}
		else
		{
			map.put("State", "False");
			map.put("data", null);	
		}
		
		return map;
	}
	
	/**
	 * 商家接单
	 * 
	 * @return
	 */
	@RequestMapping("/setRejectOrderWx")
	public @ResponseBody Map<String, Object> setRejectOrderWx(
			@RequestParam String  user_id, @RequestParam String order_id) {
		Map<String, Object> result = new HashMap<String, Object>();

		System.out.println("setRejectOrderWx enter:" + user_id +"," + order_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId",order_id);
		Order order = orderService.getOrderByIdWx(paramMap);
		if (order == null)
		{
			result.put("State", "False");
			result.put("data", "查询订单详细信息失败");	
			return result;
		}
		
		JSONArray recores = JSON.parseArray(order.getRecords());
		JSONObject record = new JSONObject();
		record.put("status",Constants.ORDER_REJECT);
		record.put("time", new Date());
		recores.add(record);
		
		paramMap.put("records",recores.toJSONString());
		paramMap.put("status",9);
		
		int flage = orderService.updateOrderStatusWx(paramMap);
		if(flage != -1 && flage !=0)
		{
			//启动退款流程，退款金额单位为分
			String resultStr = payService.refund(order_id, String.valueOf(order.getPayPrice()*100));
			try {
	              Map map =  PayUtil.doXMLParse(resultStr);
	              String returnCode = map.get("return_code").toString();
	              if(returnCode.equals("SUCCESS")){
	                  String resultCode = map.get("result_code").toString();
	                  if(resultCode.equals("SUCCESS")){
	                	//返回订单取消成功
                	    order.setStatus(Constants.ORDER_REFUND_SUCCESS);
	          			
	          			JSONArray recordes2 = JSON.parseArray(order.getRecords());
	          			JSONObject record2 = new JSONObject();
	          			record2.put("status",Constants.ORDER_REFUND_SUCCESS);
	          			record2.put("time", new Date());
	          			recordes2.add(record2);
	          			order.setRecords(recordes2.toString());
	          			
	          			int flag2 = orderService.updateOrder(order);
	                	result.put("State", "Success");
	          			result.put("data", null);
	      				return result;
	                  }   
	              }
	          } 
	          catch (Exception e) 
	          {
	              e.printStackTrace();
	
	          }
				
		}
		else
		{
			result.put("State", "False");
			result.put("data", null);	
		}
		
		return result;
	}
	
	
	/**
	 * 用户确认收货
	 * 
	 * @return
	 */
	@RequestMapping("/setOrderReceiveWx")
	public @ResponseBody Map<String, Object> setOrderReceiveWx( @RequestParam String order_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		System.out.println("setOrderReceiveWx enter:" +"," + order_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId",order_id);
		Order order = orderService.getOrderByIdWx(paramMap);
		if (order == null)
		{
			map.put("State", "Fail");
			map.put("data", "查询订单详细信息失败");	
			return map;
		}
		
		JSONArray recores = JSON.parseArray(order.getRecords());
		JSONObject record = new JSONObject();
		record.put("status",4);
		record.put("time", new Date());
		recores.add(record);
		
		paramMap.put("records",recores.toJSONString());
		paramMap.put("status",4);
		
		int flage = orderService.updateOrderStatusWx(paramMap);
		if(flage != -1 && flage !=0)
		{
			//从delay队列删除，从redis删除  
	        ThreadPoolUtil.execute(new Runnable(){  
	            public void run(){  
	                //从delay队列删除  
	                delayService.remove(Long.parseLong(order_id));  
	                //从redis删除  
	                redisServie.delete(Constants.REDISPREFIX+order_id); 
	                
	                //重新添加代用户收货的延迟队列
	                DSHOrder dshOrder = new DSHOrder(Long.parseLong(order_id),Constants.ORDER_SUCCESS, Constants.WAITREVIEW);  
	                delayService.add(dshOrder);  
	        
	                //2插入到redis  
	                redisServie.add(Constants.REDISPREFIX+order_id, dshOrder, Constants.REDISSAVETIME);  
	            }  
	        });
			map.put("State", "Success");
			map.put("data", null);	
		}
		else
		{
			map.put("State", "Fail");
			map.put("data", null);	
		}
		
		return map;
	}
	
}
