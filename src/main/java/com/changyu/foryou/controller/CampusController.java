package com.changyu.foryou.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Campus;
import com.changyu.foryou.model.CampusAdmin;
import com.changyu.foryou.model.City;
import com.changyu.foryou.model.CityWithCampus;
import com.changyu.foryou.model.Food;
import com.changyu.foryou.model.FoodCategory;
import com.changyu.foryou.service.CampusService;
import com.changyu.foryou.service.FoodService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.Md5;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.callback.PrivateKeyCallback.IssuerSerialNumRequest;

@Controller
@RequestMapping("/campus")
public class CampusController {
    private CampusService campusService;
    
    private FoodService foodService;
    

    @Autowired
    public void setCampusService(CampusService campusService) {
        this.campusService = campusService;
    }

    
    @Autowired
    public void setFoodService(FoodService foodService) {
        this.foodService = foodService;
    }
    
    /**
     * 获取校区
     *
     * @return
     */
    @RequestMapping("getAllCampus")
    public @ResponseBody
    JSONArray getAllCampus() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Campus> campus = campusService.getAllCampus(paramMap);
        JSONArray array = JSON.parseArray(JSON.toJSONStringWithDateFormat(
                campus, "HH:mm:ss"));//yyyy-MM-dd HH:mm:ss
        return array;
    }

    /**
     * 获取相应的校区和城市
     *
     * @return
     */
    @RequestMapping("/getCampusAndCity")
    public @ResponseBody
    Map<String, Object> getCampusByCity() {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();

            List<CityWithCampus> campus = campusService.getCampusWithCity(paramMap);

            resultMap.put(Constants.STATUS, Constants.SUCCESS);
            resultMap.put(Constants.MESSAGE, "获取校区成功");
            resultMap.put("campus", campus);

        } catch (Exception e) {
            e.getStackTrace();
            resultMap.put(Constants.STATUS, Constants.FAILURE);
            resultMap.put(Constants.MESSAGE, "获取校区失败！");
        }
        return resultMap;
    }

    /**
     * @param campusName 根据校区名获取校区Id
     */

    @RequestMapping("/getIdByName")
    public @ResponseBody
    Map<String, Object> getIdByName(@RequestParam String campusName) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("campusName", campusName.trim());
            Integer campusId = campusService.getIdByName(paramMap);
            map.put(Constants.STATUS, Constants.SUCCESS);
            map.put(Constants.MESSAGE, "获取校区Id成功！");
            map.put("campusId", campusId);

        } catch (Exception e) {
            e.getStackTrace();
            map.put(Constants.STATUS, Constants.FAILURE);
            map.put(Constants.MESSAGE, "获取校区Id失败！");
        }

        return map;
    }

    //一键关店、开店

    /**
     * @param campusId
     * @param closeReason 关店原因
     * @param status      关店传0，开店传1。
     * @return
     */
    @RequestMapping("/closeCampus")
    public @ResponseBody
    Map<String, Object> closeCampus(@RequestParam Integer campusId, String closeReason, @RequestParam Short status) {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        Map<String, Object> responseMap = new HashMap<String, Object>();
        try {
            requestMap.put("campusId", campusId);
            requestMap.put("closeReason", closeReason);
            requestMap.put("status", status);
            Integer flag = campusService.closeCampus(requestMap);

            if (status == 1) {
                responseMap.put("isOpened", flag);
                responseMap.put(Constants.STATUS, Constants.SUCCESS);
                responseMap.put(Constants.MESSAGE, "开店成功！");
            } else if (status == 0) {
                responseMap.put("isClosed", flag);
                responseMap.put(Constants.STATUS, Constants.SUCCESS);
                responseMap.put(Constants.MESSAGE, "关店成功！");
            }
        } catch (Exception e) {
            e.getStackTrace();
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "操作失败！");
        }

        return responseMap;
    }


    /**
     * @param campusId 校区id
     */
    @RequestMapping("/getCampusById")
    public @ResponseBody
    Map<String, Object> getCampusById(@RequestParam Integer campusId) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("campusId", campusId);
            Campus campus = campusService.getCampusById(paramMap);
            map.put(Constants.STATUS, Constants.SUCCESS);
            map.put(Constants.MESSAGE, "获取校区成功！");
            map.put("campus", JSON.toJSON(campus));

        } catch (Exception e) {
            e.getStackTrace();
            map.put(Constants.STATUS, Constants.FAILURE);
            map.put(Constants.MESSAGE, "获取校区失败！");
        }

        return map;
    }

    @RequestMapping("getCampusIdByAdmin")
    public @ResponseBody
    Map<String, Object> getCampusIdByAdmin(@RequestParam String campusAdminName) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> requestMap = new HashMap<String, Object>();

        requestMap.put("campusAdmin", campusAdminName);

        CampusAdmin campusAdminInfo = campusService.getCampusIdByAdmin(requestMap);

        responseMap.put("CampusAdmin", campusAdminInfo);

        return responseMap;
    }

    /**
     * 返回校区管理员
     *
     * @return
     */
    @RequestMapping("getAllCampusAdmin")
    public @ResponseBody
    JSONArray getAllCampusAdmin() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("type", 0);

        List<CampusAdmin> campusAdmins = campusService.getAllCampusAdmin(paramMap);
        JSONArray array = JSON.parseArray(JSON.toJSONStringWithDateFormat(campusAdmins, "yyyy-MM-dd"));

        return array;
    }

    /**
     * 修改校区管理员
     *
     * @param campusId
     * @param campusAdminName
     * @return
     */
    @RequestMapping("/updateCampusAdmin")
    public @ResponseBody
    Map<String, Object> updateCampusAdmin(@RequestParam String campusName, @RequestParam String campusAdminName) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("campusName", campusName);

        paramMap.put("campusId", campusService.getIdByName(tempMap));
        paramMap.put("campusAdmin", campusAdminName);

        Integer result = campusService.updateCampusAdmin(paramMap);

        if (result == 0 || result == -1) {
            //没更新成功
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "修改校区的管理员失败！");
        } else {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "修改校区的管理员成功！");
        }

        return responseMap;
    }


    /**
     * 删除校区的某校区管理员
     *
     * @param campusId
     * @param campusAdminName
     * @return
     */
    @RequestMapping("/deleteCampusAdmin")
    public @ResponseBody
    Map<String, Object> deleteCampusAdmin(@RequestParam Integer campusId, @RequestParam String campusAdminName) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("campusId", campusId);
        paramMap.put("campusAdmin", campusAdminName);

        Integer result = campusService.deleteCampusAdmin(paramMap);
        if (result > 0) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "删除成功");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "删除失败");
        }
        return responseMap;
    }

    /**
     * 添加校区，默认添加8个分类
     *
     * @param campusName
     * @param status
     * @param customService
     * @param openTime
     * @param closeTime
     * @return
     * @throws ParseException
     */
    @RequestMapping("addCampus")
    public @ResponseBody
    Map<String, Object> addCampus(@RequestParam String campusName, @RequestParam String cityName, @RequestParam String openTime, @RequestParam String closeTime, @RequestParam Short status, @RequestParam String customService) throws ParseException {
        Map<String, Object> responseMap;
        Map<String, Object> paramMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date openTimeDate = sdf.parse(openTime);
        Date closeTimeDate = sdf.parse(closeTime);

        paramMap.put("campusId", null);
        paramMap.put("campusName", campusName);
        paramMap.put("cityId", campusService.getCityByName(cityName).getCityId());
        paramMap.put("openTime", openTimeDate);
        paramMap.put("closeTime", closeTimeDate);
        paramMap.put("status", status);                    //默认开启校区
        paramMap.put("customService", customService);

        responseMap = campusService.addCampus(paramMap);
        return responseMap;
    }

    /**
     * 添加校区管理员
     *
     * @param campusName
     * @param campusName
     * @param campusAdminName
     * @return
     */
    @RequestMapping("addCampusAdmin")
    public @ResponseBody
    Map<String, Object> addCampusAdmin(@RequestParam String campusName, @RequestParam String campusAdminName, @RequestParam String password) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();

        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("campusName", campusName);

        paramMap.put("campusId", campusService.getIdByName(tempMap));
        paramMap.put("campusName", campusName);
        paramMap.put("campusAdmin", campusAdminName);
        paramMap.put("password", Md5.GetMD5Code(password));
        paramMap.put("type", 0);            //只能添加校区管理员，总校区管理员只能从数据库添加，更符合逻辑

        Integer result = campusService.addCampusAdmin(paramMap);

        if (result != 0 && result != -1) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "添加校区管理员成功，请及时将账户分派给相应人员并提醒他/她修改密码");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "添加校区管理员失败");
        }
        return responseMap;
    }

    /**
     * 获取全部城市
     *
     * @return
     */
    @RequestMapping("getAllCity")
    @ResponseBody
    public List<City> getAllCity() {

        return campusService.getAllCity();
    }

    @RequestMapping("addCity")
    @ResponseBody
    public Map<String, Object> addCity(@RequestParam String cityName) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("cityName", cityName);

        if (campusService.getCityByName(cityName) == null) {
            Integer result = campusService.addCity(paramMap);
            if (result != -1 && result != 0) {
                responseMap.put(Constants.STATUS, Constants.SUCCESS);
                responseMap.put(Constants.MESSAGE, "添加城市成功！");
            } else {
                responseMap.put(Constants.STATUS, Constants.FAILURE);
                responseMap.put(Constants.MESSAGE, "添加城市失败！");
            }
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "该城市已存在");
        }

        return responseMap;
    }

    @RequestMapping("updateCampus")
    @ResponseBody
    public Map<String, Object> updateCampus(@RequestParam String campusId, @RequestParam String campusName, @RequestParam String cityName, @RequestParam String openTime, @RequestParam String closeTime, @RequestParam Short status, @RequestParam String customService) {
        //管理端这些值都要传过来，传之前判空
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("campusId", campusId);
        paramMap.put("campusName", campusName);
        paramMap.put("cityId", campusService.getCityByName(cityName).getCityId());
        paramMap.put("openTime", openTime);
        paramMap.put("closeTime", closeTime);
        paramMap.put("status", status);
        paramMap.put("customService", customService);

        Integer result = campusService.updateCampus(paramMap);

        if (result != 0 && result != -1) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "更新校区成功！");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "更新校区失败！");
        }

        return responseMap;
    }


    @RequestMapping("/updateCampusAdminPassword")
    public @ResponseBody
    Map<String, Object> updateCampusAdminPassword(String campusAdmin, String oldPassword, String newPassword) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("campusAdmin", campusAdmin);    //campusAdmin唯一

            String password = campusService.getOldPassword(paramMap);

            if (password.equals(Md5.GetMD5Code(oldPassword))) {
                paramMap.put("newPassword", Md5.GetMD5Code(newPassword));

                int flag = campusService.updateCampusAdminPassword(paramMap);
                if (flag != -1) {
                    resultMap.put(Constants.STATUS, Constants.SUCCESS);
                    resultMap.put(Constants.MESSAGE, "修改密码成功");
                } else {
                    resultMap.put(Constants.STATUS, Constants.FAILURE);
                    resultMap.put(Constants.MESSAGE, "修改密码失败");
                }
            } else {
                resultMap.put(Constants.STATUS, Constants.FAILURE);
                resultMap.put(Constants.MESSAGE, "原密码输入错误请重新输入");
            }
        } catch (Exception e) {
            e.getStackTrace();
            resultMap.put(Constants.STATUS, Constants.FAILURE);
            resultMap.put(Constants.MESSAGE, "修改密码失败");
        }

        return resultMap;
    }
    
    /**
	 * 小程序获得所有商家列表
	 * add by ljt
	 */
	
	@RequestMapping("/getAllCampusWx")
    public @ResponseBody Map<String,String> getAllCampusWx() {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Campus> campuslist = campusService.getAllCampus(paramMap);
						
		JSONArray jsonarray = new JSONArray(); 
				
		for (Campus campus: campuslist)
		{
			JSONObject node = new JSONObject();  
			node.put("seller_id", String.valueOf(campus.getCampusId()));
			node.put("seller_name", campus.getCampusName());
			node.put("sales", String.valueOf(campus.getSales()));
			node.put("min_price", String.valueOf(campus.getMin_price()));
			node.put("reach_time", String.valueOf(campus.getReach_time()));
			node.put("distance", "98000");//设置店铺与买家地址的距离，先写死
			node.put("distanceFormat", "10000");//设置店铺与买家地址的距离，先写死
			node.put("pic_url", campus.getPic_url());
			jsonarray.add(node);
		}
		System.out.println(jsonarray.toString());
		Map<String,String> data = new HashMap<String, String>();
		data.put("State", "Success");
		data.put("data", jsonarray.toString());				
		return data;
		
	}	
	
	/**
	 * 获得商家信息
	 * add by ljt
	 */
	
	@RequestMapping("/getCampusByIdWx")
    public @ResponseBody Map<String,String> getCampusByIdWx(@RequestParam String seller_id,@RequestParam String longitude,@RequestParam String latitude) {
		
		System.out.println("enter:");
				
		Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("campusId", seller_id);
        Campus campus = campusService.getCampusById(paramMap);
					
		JSONObject node = new JSONObject();  
		node.put("seller_id", String.valueOf(campus.getCampusId()));
		node.put("seller_name", campus.getCampusName());
		node.put("sales", String.valueOf(campus.getSales()));
		node.put("min_price", String.valueOf(campus.getMin_price()));
		node.put("reach_time", String.valueOf(campus.getReach_time()));
		node.put("distance", "98000");//设置店铺与买家地址的距离，先写死
		node.put("distanceFormat", "10000");//设置店铺与买家地址的距离，先写死
		node.put("pic_url", campus.getPic_url());
		node.put("promotion", "");//优惠活动数组
		node.put("delivery_fee", "2");//配送费2元
					
		//获取店铺的商品分类和商品信息
		List<FoodCategory> categoryList = foodService.getFirstCategory(paramMap);
		
		List<Food> foods = new ArrayList<Food>();
        foods = foodService.getAllFoods(paramMap);
        
        JSONArray foodCategory = new JSONArray();
        	
		for(FoodCategory category: categoryList)
		{
			JSONObject categorytmp = new JSONObject();
			categorytmp.put("menu_id", String.valueOf(category.getCategoryId()));
			categorytmp.put("menu_name", category.getCategory());
			JSONArray  food = new JSONArray ();
			for(int i = 0 ;i < foods.size(); i++)
			{
				//将每个商品添加到返回的店铺商品信息里面
				if(category.getCategoryId().equals(foods.get(i).getCategoryId()))
				{
					System.out.println("3:"+ foods.get(i).getName());
					JSONObject shangpin = new JSONObject();
					shangpin.put("goods_id", String.valueOf(foods.get(i).getFoodId()));
					shangpin.put("pic_url", foods.get(i).getImgUrl());
					shangpin.put("goods_name", foods.get(i).getName());
					shangpin.put("sales", String.valueOf(foods.get(i).getSaleNumber()));
					
					//TODO:商品信息在goodsmap已返回，这里可以优化，需要注意小程序同步优化
					if(foods.get(i).getPrice().matches("[0-9]+"))//纯数字
					{
						shangpin.put("price", foods.get(i).getPrice());
					}
					else						
					{	
						JSONArray subgoods = new JSONArray();
						String tmp = foods.get(i).getPrice();
						System.out.println("666:"+ tmp);
						tmp = tmp.replace('：', ':');
						tmp = tmp.replace('；', ';');
						System.out.println("777:"+ tmp);
						String[] strArray = tmp.split("\\;");
						int minPrice = 0;
						for (int j = 0; j < strArray.length; j++)
						{
							JSONObject nodeTmp = new JSONObject();
							System.out.println("5:"+ strArray[j]);
							String[] priceArray = strArray[j].split("\\:");
							System.out.println("5:"+ priceArray[0]);
							nodeTmp.put("sub_id", String.valueOf(j)); //子规格数组
							nodeTmp.put("sub_name", priceArray[0]); //子规格数组
							nodeTmp.put("price", priceArray[1]); //子规格数组
							nodeTmp.put("packing_fee", "0");
							subgoods.add(nodeTmp);							
							if(Integer.parseInt(priceArray[1]) < minPrice  || minPrice ==0)
							{
								minPrice = Integer.parseInt(priceArray[1]);
							}						
						}
						shangpin.put("sub_goods", subgoods);
						shangpin.put("price", String.valueOf(minPrice));
					}	
					//返回商品的属性
					//甜度：微糖，中糖，多糖；温度：常温，热，少冰
					if(!foods.get(i).getFoodFlag().isEmpty())
					{
						JSONArray property = new JSONArray();
						
						String tmp = foods.get(i).getFoodFlag().replace('；', ';');
						tmp = tmp.replace('：', ':');
						tmp = tmp.replace('，', ',');
						String[] strArray = tmp.split("\\;");

						for(int j = 0; j < strArray.length; j++)
						{
							JSONObject nodeTmp = new JSONObject();
							String[] strArray2 = strArray[j].split("\\:");
							nodeTmp.put("property_id", String.valueOf(j)); //子规格数组
							nodeTmp.put("property_name", strArray2[0]); //子规格数组
							
							JSONArray propertyValue = new JSONArray();
							String[] strArray3 = strArray2[1].split("\\,");
							for (int k = 0 ; k < strArray3.length; k++)
							{
								JSONObject nodeTmp2 = new JSONObject();
								nodeTmp2.put("value_id", String.valueOf(10*j + k)); //子规格数组
								nodeTmp2.put("value_name", strArray3[k]); //子规格数组
								propertyValue.add(nodeTmp2);
							}
							nodeTmp.put("property_value", propertyValue); //子规格数组
							property.add(nodeTmp);
						}
						shangpin.put("property", property);
					}
					else
					{
						shangpin.put("property", null); //没起作用
					}
					
					food.add(shangpin);
				}
			}
			categorytmp.put("goods2", food);
			foodCategory.add(categorytmp);						
		}
		
		node.put("menus", foodCategory);
		
		JSONArray goods_map = new JSONArray();
		for(int i = 0 ;i < foods.size(); i++)
		{
			JSONObject good1 = new JSONObject();
			good1.put("goods_id", String.valueOf(foods.get(i).getFoodId()));
			good1.put("goods_name", foods.get(i).getName());
			good1.put("price", "10");
			good1.put("packing_fee", "0");
			
			if(foods.get(i).getPrice().matches("[0-9]+"))//纯数字
			{
				good1.put("price", foods.get(i).getPrice());
			}
			else						
			{	
				JSONArray subgoods = new JSONArray();
				String tmp = foods.get(i).getPrice();
				tmp = tmp.replace('：', ':');
				tmp = tmp.replace('；', ';');
				String[] strArray = tmp.split("\\;");
				for (int j = 0; j < strArray.length; j++)
				{
					JSONObject nodeTmp = new JSONObject();
					String[] priceArray = strArray[j].split("\\:");
					nodeTmp.put("sub_id", String.valueOf(j)); //子规格数组
					nodeTmp.put("sub_name", priceArray[0]); //子规格数组
					nodeTmp.put("price", priceArray[1]); //子规格数组
					nodeTmp.put("packing_fee", "0");
					subgoods.add(nodeTmp);
				}
				good1.put("sub_goods", subgoods);
			}	
			//返回商品的属性
			//甜度：微糖，中糖，多糖；温度：常温，热，少冰
			if(!foods.get(i).getFoodFlag().isEmpty())
			{
				JSONArray property = new JSONArray();
				
				String tmp = foods.get(i).getFoodFlag().replace('；', ';');
				tmp = tmp.replace('：', ':');
				tmp = tmp.replace('，', ',');
				String[] strArray = tmp.split("\\;");

				for(int j = 0; j < strArray.length; j++)
				{
					JSONObject nodeTmp = new JSONObject();
					String[] strArray2 = strArray[j].split("\\:");
					nodeTmp.put("property_id", String.valueOf(j));
					nodeTmp.put("property_name", strArray2[0]); //子规格数组
					
					JSONArray propertyValue = new JSONArray();
					String[] strArray3 = strArray2[1].split("\\,");
					for (int k = 0 ; k < strArray3.length; k++)
					{
						JSONObject nodeTmp2 = new JSONObject();
						nodeTmp2.put("value_id", String.valueOf(k)); //子规格数组
						nodeTmp2.put("value_name", strArray3[k]); //子规格数组
						propertyValue.add(nodeTmp2);
					}
					nodeTmp.put("property_value", propertyValue); //子规格数组
					
					property.add(nodeTmp);
				}
				good1.put("property", property);
			}
			else
			{
				System.out.println("here2 enter");
				good1.put("property", null);
			}
			
			goods_map.add(good1);
		}
		node.put("goods_map", goods_map);
		System.out.println("return:" + node.toString());

		Map<String,String> data = new HashMap<String, String>();
		data.put("State", "Success");
		data.put("data", node.toString());				
		return data;
		
	}	
	
	/**
	 * 获得商家信息
	 * add by ljt
	 */
	
	@RequestMapping("/getReviews")
    public @ResponseBody Map<String,String> getReviews(@RequestParam String seller_id,@RequestParam String page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Campus> campuslist = campusService.getAllCampus(paramMap);
		
		JSONArray jsonarray = new JSONArray(); 
				
		for (Campus seller: campuslist)
		{
			JSONObject node = new JSONObject();  
			node.put("timeFormat", "");
			node.put("time", "12345678");
			jsonarray.add(node);
		}
		System.out.println(jsonarray.toString());
		Map<String,String> data = new HashMap<String, String>();
		data.put("State", "Success");
		data.put("data", jsonarray.toString());				
		return data;
		
	}	
}
