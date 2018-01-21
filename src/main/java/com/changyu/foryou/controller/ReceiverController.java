package com.changyu.foryou.controller;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Campus;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.model.Receiver;
import com.changyu.foryou.service.ReceiverService;
import com.changyu.foryou.tools.Constants;


/**
 * 这是收货人的控制器，主要处理收货人的信息
 * @author 殿下
 *
 */
@Controller
@RequestMapping("/receiver")
public class ReceiverController {
	@Autowired
	private ReceiverService receiverService;

	/**
	 * 添加收貨人信息
	 * @param phoneId 用戶手机，id
	 * @param phone  收货人手机号
	 * @param name   收货人名字
	 * @param address  收货人地址
	 * @param campusId 校区号
	 * @return
	 */
	@RequestMapping("/addUserAddrWx")
	public @ResponseBody Map<String, Object>addUserAddrWx(@RequestParam String user_id,@RequestParam String receiver,@RequestParam String phone,
			@RequestParam String detail,@RequestParam String gps,@RequestParam String addr,@RequestParam Integer city_id,
			@RequestParam String city_name,@RequestParam Integer district_id,@RequestParam String district_name){
		Map<String, Object> map=new HashMap<String ,Object>();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId",user_id);
		paramMap.put("name",receiver);
		paramMap.put("phone",phone);
		paramMap.put("address",addr);
		paramMap.put("detail",detail);
		
		System.out.println("addUserAddrWx:" + gps);
		String gpstmp[] = gps.split(",");
		paramMap.put("longitude",gpstmp[0]);
		paramMap.put("latitude",gpstmp[1]);
		
		
		paramMap.put("cityId",city_id);
		paramMap.put("cityName",city_name);
		paramMap.put("districtId",district_id);
		paramMap.put("districtName",district_name);
		
		//通过时间生成该记录的序列号，和userId一起唯一表志收货人信息
		Calendar calendar=Calendar.getInstance();
		paramMap.put("addressId", String.valueOf(calendar.getTimeInMillis()));
		
		System.out.println("addUserAddrW:" + paramMap.toString());
		
		try 
		{
			if(receiverService.insertSelective(paramMap)!=-1)
			{
				map.put("State", "Success");
				map.put("data", null);	

				return map;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}


	/**
	 * 根据用户id获取用户存下来的收货人信息
	 * @param phoneId
	 * @return
	 */
	@RequestMapping("/selectReceiver")
	public @ResponseBody Map<String, Object> relectReceiver(@RequestParam String phoneId){
		Map<String, Object> map=new HashMap<String ,Object>();
		try {
			List<Receiver> receivers=receiverService.selectByPhoneId(phoneId);
			map.put("receivers", receivers);
			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE, "获取成功");
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取失败！");
		}
		return map;
	}

	/**
	 * 更改收货人信息
	 * @param phoneId  用户id
	 * @param rank    收货人序列，主键
	 * @param address  收货人地址
	 * @param name    收货人姓名
	 * @param phone   收货人手机号
	 * @param campusId 校区
	 * @return
	 */
	@RequestMapping("/updateUserAddrWx")
	public @ResponseBody Map<String, Object> updateUserAddrWx(@RequestParam String user_id,@RequestParam String receiver,@RequestParam String phone,
			@RequestParam String detail,@RequestParam String gps,@RequestParam String addr_id,@RequestParam String addr,@RequestParam Integer city_id,
			@RequestParam String city_name,@RequestParam Integer district_id,@RequestParam String district_name){
		Map<String, Object> map=new HashMap<String ,Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId",user_id);
			paramMap.put("name",receiver);
			paramMap.put("phone",phone);
			paramMap.put("address",addr);
			paramMap.put("detail",detail);
			
			System.out.println("addUserAddrWx:" + gps);
			String gpstmp[] = gps.split(",");
			paramMap.put("longitude",gpstmp[0]);
			paramMap.put("latitude",gpstmp[1]);
			
			paramMap.put("cityId",city_id);
			paramMap.put("cityName",city_name);
			paramMap.put("districtId",district_id);
			paramMap.put("districtName",district_name);
			paramMap.put("addressId", addr_id);
			
			if(receiverService.updateByPrimaryKeySelective(paramMap)!=-1)
			{
				map.put("State", "Success");
				map.put("data", null);	

				return map;
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}

	/**
	 * 设置默认收货地址
	 * @param phoneId  用户id
	 * @param rank   收货人序列号
	 * @return
	 */
	@RequestMapping("/setDefaultAddress")
	public @ResponseBody Map<String, Object> setDefaultAddress(@RequestParam String phoneId,@RequestParam String rank){
		Map<String, Object> map=new HashMap<String ,Object>();
		try{
			receiverService.setReceiverTag(phoneId);   //将原先的默认收货地址改成非默认
			if(receiverService.setDefaultAddress(phoneId, rank)!=-1){
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "设置默认收货地址成功");
			}else{
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "设置默认收货地址失败");
			}
		}catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "设置默认收货地址失败！");
		}

		map.put("State", "False");
		map.put("data", null);	
		return map;
	}

	
	/**
	 * 删除某个收货人地址
	 * @param phoneId
	 * @param rank
	 * @return
	 */	
	@RequestMapping("/deleteUserAddrWx")
	public @ResponseBody Map<String,Object> deleteUserAddrWx(@RequestParam String user_id,@RequestParam String addr_id){
		Map<String, Object> map=new HashMap<String ,Object>();
		try {
      
			int flag=receiverService.deleteByPrimaryKey(user_id, addr_id);
			if(flag!=-1&&flag!=0){
				map.put("State", "Success");
				map.put("data", "删除成功");
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		
		map.put("State", "False");
		map.put("data", null);
		return map;
	}
	
	/**
	 * 获得用户的所有地址信息
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/getUserAddressWx")
	public @ResponseBody Map<String, String> getUserAddressWx(@RequestParam String user_id){
		Map<String,String> data = new HashMap<String, String>();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId",user_id);
		List<Receiver> reveiverList = receiverService.getReceiverList(paramMap);
		
		JSONArray jsonarr = new JSONArray();
		for(Receiver revceiver : reveiverList)
		{
			JSONObject tmp = new JSONObject();
			tmp.put("addr_id", revceiver.getAddressId());
			tmp.put("receiver", revceiver.getName());
			tmp.put("phone", revceiver.getPhone());
			tmp.put("addr", revceiver.getAddress());
			tmp.put("detail", revceiver.getDetail());
			jsonarr.add(tmp);
			
		}
		
		data.put("State", "Success");
		data.put("data", jsonarr.toString());	

		return data;
	}
}
