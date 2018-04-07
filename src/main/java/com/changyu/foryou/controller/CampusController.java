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
import com.changyu.foryou.model.FoodComment;
import com.changyu.foryou.model.Preferential;
import com.changyu.foryou.service.CampusService;
import com.changyu.foryou.service.FoodService;
import com.changyu.foryou.service.PreferentialService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.Md5;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import javax.security.auth.message.callback.PrivateKeyCallback.IssuerSerialNumRequest;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/campus")
public class CampusController {
    private CampusService campusService;
    
    private FoodService foodService;
    
	private PreferentialService preferentialService;
    

    @Autowired
    public void setCampusService(CampusService campusService) {
        this.campusService = campusService;
    }

    
    @Autowired
    public void setFoodService(FoodService foodService) {
        this.foodService = foodService;
    }
    
    @Autowired
    public void setPreferentialService(PreferentialService preferentialService) {
        this.preferentialService = preferentialService;
    }
    
    /**
     * 获取所有店铺
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
            String campusId = campusService.getIdByName(paramMap);
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
    Map<String, Object> getCampusById(@RequestParam String campusId) {
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
    
    /**
     * 获取校区
     *
     * @return
     */
    @RequestMapping("/getCampusById2")
    public @ResponseBody
    JSONArray getCampusById2(@RequestParam String campusId) {

    	try 
        {
    		System.out.println("getCampusById2 enter:" + campusId);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("campusId", campusId);
            Campus campus = campusService.getCampusById(paramMap);  
            System.out.println("campus:" + campus.toString());
            JSONArray array = new JSONArray();
            array.add(campus);
            //JSONArray array = JSON.parseArray(JSON.toJSONStringWithDateFormat(
            //        campus, "HH:mm:ss"));//yyyy-MM-dd HH:mm:ss
            return array;

        } catch (Exception e) 
        {
            e.getStackTrace();
        }       
    	return null;       
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
     * @throws IOException 
     */
    @RequestMapping("/addCampus")
    public String addCampus(@RequestParam MultipartFile[] myfile,HttpServletRequest request){
        Map<String, Object> responseMap;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        System.out.println("addCampus enter");
        
        String campusName = request.getParameter("campusName"); 
        String cityName = "南京" ;
        String address = request.getParameter("address"); 
        String notice = request.getParameter("notice"); 
        String deliver = request.getParameter("deliver"); 
        String openTime = request.getParameter("openTime"); 
        String closeTime = request.getParameter("closeTime");
        String status = request.getParameter("status");//1为营业，0为休息
        String customService = request.getParameter("customService");
        String minPrice = request.getParameter("minPrice");
        String deliveryFee = request.getParameter("deliveryFee");
        String reachTime = request.getParameter("reachTime");
        
        try{
	        String realPath = request.getSession().getServletContext().getRealPath("/");
	
	        realPath = realPath.concat("JiMuImage/shop/");
	        
	        List<String> imageUrl = new ArrayList<String>();
	        String pic_url = "";
	        for (MultipartFile file : myfile) {
	            if (file.isEmpty()) {
	                System.out.println("文件未上传");
	                imageUrl.add(null);
	            } else 
	            {
	                String contentType = file.getContentType();
	                System.out.println("contentType:" + contentType);
	                if (contentType.startsWith("image"))
	                {
	                    //String newFileName = new Date().getTime() + "" + new Random().nextInt() + ".jpg";
	                    //FileUtils.copyInputStreamToFile(file.getInputStream(),new File(realPath, newFileName)); // 写文件
	                    //将文件上传到七牛云
	                    Configuration cfg = new Configuration(Zone.zone0()); //zone0为华东
	                    //...其他参数参考类注释
	                    UploadManager uploadManager = new UploadManager(cfg);
	                    String key = null;//默认不指定key的情况下，以文件内容的hash值作为文件名
	                    Auth auth = Auth.create(Constants.QINIU_AK, Constants.QINIU_SK);
	            		String upToken = auth.uploadToken(Constants.QINIU_BUCKET);
	            		
	            		try{
	            			Response response2 = uploadManager.put(file.getInputStream(),key,upToken,null, null);
	                      
	            			//解析上传成功的结果
	            			DefaultPutRet putRet = new Gson().fromJson(response2.bodyString(), DefaultPutRet.class);
	            			
	            			pic_url = Constants.QINIU_IP + putRet.key; //文件名
	      			  
	            		}
	            		catch(QiniuException ex)
	            		{
							Response r = ex.response;
							System.err.println(r.toString());
	            		}
	                }
	            }
	        }
	
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	        Date openTimeDate = sdf.parse(openTime);
	        Date closeTimeDate = sdf.parse(closeTime);
	        
	        Calendar calendar=Calendar.getInstance();
	
	        paramMap.put("campusId", String.valueOf(calendar.getTimeInMillis()));
	        paramMap.put("campusName", campusName);
	        paramMap.put("cityId", campusService.getCityByName(cityName).getCityId());
	        paramMap.put("address", address);
	        paramMap.put("notice", notice);
	        paramMap.put("deliver", deliver);
	        paramMap.put("openTime", openTimeDate);
	        paramMap.put("closeTime", closeTimeDate);
	        paramMap.put("status", status);   
	        paramMap.put("sales", 0);//默认开启校区
	        paramMap.put("pic_url", pic_url);   
	        paramMap.put("customService", customService);
	        paramMap.put("min_price", minPrice);
	        paramMap.put("delivery_fee", deliveryFee);
	        paramMap.put("reach_time", reachTime);
	
	        int flag = campusService.addCampus(paramMap);
	        
	        
	        if (flag != -1 && flag != 0) 
	        {
	            return "redirect:/pages/campus.html";
	        }
        }
        catch (Exception e)
        {
        	System.out.println(e);
        	return "redirect:/pages/uploadError.html";
        }
        return "redirect:/pages/campus.html";
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
    public String updateCampus(@RequestParam MultipartFile[] myfile,HttpServletRequest request) throws IOException, ParseException {
        //管理端这些值都要传过来，传之前判空
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        String campusId = request.getParameter("campusId"); 
        String campusName = request.getParameter("campusName"); 
        String address = request.getParameter("address"); 
        String notice = request.getParameter("notice"); 
        String deliver = request.getParameter("deliver"); 
        String openTime = request.getParameter("openTime"); 
        String closeTime = request.getParameter("closeTime");
        String status = request.getParameter("status");//1为营业，0为休息
        String customService = request.getParameter("customService");
        String minPrice = request.getParameter("minPrice");
        String deliveryFee = request.getParameter("deliveryFee");
        String reachTime = request.getParameter("reachTime");
        
        System.out.println("updateCampus:customService=" + customService);
        
        String realPath = request.getSession().getServletContext().getRealPath("/");

        realPath = realPath.concat("JiMuImage/shop/");
        
        System.out.println("openTime:" + openTime);
        System.out.println("closeTime:" + closeTime);
        
        List<String> imageUrl = new ArrayList<String>();
        String pic_url = "";
        for (MultipartFile file : myfile) {
            if (file.isEmpty()) {
                System.out.println("文件未上传");
                imageUrl.add(null);
            } else 
            {
                String contentType = file.getContentType();

                if (contentType.startsWith("image"))
                {
                    String newFileName = new Date().getTime() + "" + new Random().nextInt() + ".jpg";
                    //FileUtils.copyInputStreamToFile(file.getInputStream(),new File(realPath, newFileName)); // 写文件
                    //将文件上传到七牛云
                    Configuration cfg = new Configuration(Zone.zone0()); //zone0为华东
                    //...其他参数参考类注释
                    UploadManager uploadManager = new UploadManager(cfg);
                    String key = null;//默认不指定key的情况下，以文件内容的hash值作为文件名
                    Auth auth = Auth.create(Constants.QINIU_AK, Constants.QINIU_SK);
            		String upToken = auth.uploadToken(Constants.QINIU_BUCKET);
            		
            		try{
            			Response response2 = uploadManager.put(file.getInputStream(),key,upToken,null, null);
                      
            			//解析上传成功的结果
            			DefaultPutRet putRet = new Gson().fromJson(response2.bodyString(), DefaultPutRet.class);
            			
            			pic_url = Constants.QINIU_IP + putRet.key; //文件名
      			  
            		}
            		catch(QiniuException ex)
            		{
						Response r = ex.response;
						System.err.println(r.toString());
            		}
                }
            }
        }


        paramMap.put("campusId", campusId);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date openTimeDate = sdf.parse(openTime);
        Date closeTimeDate = sdf.parse(closeTime);

        paramMap.put("campusId", campusId);
        paramMap.put("campusName", campusName);
        paramMap.put("cityId", campusService.getCityByName("南京").getCityId());
        paramMap.put("address", address);
        paramMap.put("notice", notice);
        paramMap.put("deliver", deliver);
        paramMap.put("openTime", openTimeDate);
        paramMap.put("closeTime", closeTimeDate);
        paramMap.put("status", status); //默认开启校区

        if (pic_url != null && !pic_url.isEmpty())
        {
        	paramMap.put("pic_url", pic_url);  
        }
         
        paramMap.put("customService", customService);
        paramMap.put("min_price", minPrice);
        paramMap.put("delivery_fee", deliveryFee);
        paramMap.put("reach_time", reachTime);

        Integer result = campusService.updateCampus(paramMap);

        if (result != 0 && result != -1) {
        	return "redirect:/pages/campus.html";
        } else {
            return responseMap.put(Constants.MESSAGE, "更新店铺信息失败！").toString();
        }

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
     * @throws ParseException 
	 */
	
	@RequestMapping("/getAllCampusWx")
    public @ResponseBody Map<String,Object> getAllCampusWx(@RequestParam Integer page,@RequestParam String selectUniv) throws ParseException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("univName", selectUniv);
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
        List<Campus> campuslist = campusService.getAllCampus(paramMap);
						
		JSONArray jsonarray = new JSONArray(); 
				
		for (Campus campus: campuslist)
		{
			JSONObject node = new JSONObject();  
			node.put("seller_id", String.valueOf(campus.getCampusId()));
			node.put("seller_name", campus.getCampusName());
			node.put("sales", String.valueOf(campus.getSales()));
			node.put("min_price", String.valueOf(campus.getMin_price()));
			node.put("delivery_fee", String.valueOf(campus.getDelivery_fee()));
			node.put("reach_time", String.valueOf(campus.getReach_time()));
			node.put("distance", "98000");//设置店铺与买家地址的距离，先写死
			node.put("pic_url", campus.getPic_url());
			
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    		Date now = df.parse(df.format(new Date()));
            Date beginTime = df.parse(df.format(campus.getOpenTime()));
            Date endTime = df.parse(df.format(campus.getCloseTime()));
            if(belongCalendar(now, beginTime, endTime) && (campus.getStatus()==1)){
            	node.put("status", 1);//status 为营业
            }
            else{
            	node.put("status", 0);//status 为休息
            }
			
			node.put("overall", campus.getOverAll());//综合评分
			jsonarray.add(node);
		}
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", jsonarray);				
		return data;
		
	}	
	
	/**
     * 判断时间是否在时间段内
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
    
	//一键关店、开店

    /**
     * @param campusId
     * @param closeReason 关店原因
     * @param status      关店传0，开店传1。
     * @return
     */
    @RequestMapping("/setCampusStatusWx")
    public @ResponseBody
    Map<String, Object> setCampusStatusWx(@RequestParam String seller_id, @RequestParam Boolean status) {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        System.out.println("setCampusStatusWx:" +seller_id + String.valueOf(status));
        try {
            requestMap.put("campusId", seller_id);
            requestMap.put("closeReason", "");
            if(status == true)
            {
            	requestMap.put("status", 1);
            }
            else if(status == false)
            {
            	requestMap.put("status", 0);
            }
            
            Integer flag = campusService.closeCampus(requestMap);

            if (flag == 0 || flag== -1) {
            	
            	map.put("State", "Fail");
            	map.put("data", "关店操作失败");
            } else  {
            	map.put("State", "Success");
            	map.put("data", "操作成功");
            }
        } catch (Exception e) {
            e.getStackTrace();
            map.put("State", "Fail");
        	map.put("data", "操作失败");
        }
        	
        return map;
    }
	
    
    @RequestMapping("/getCampusStatusWx")
    public @ResponseBody
    Map<String, Object> getCampusStatusWx(@RequestParam String seller_id) throws ParseException {
    	
    	Map<String,Object> map = new HashMap<String, Object>();
    	
    	Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("campusId", seller_id);
        Campus campus = campusService.getCampusById(paramMap);
        
        JSONObject obj = new JSONObject();
        if(campus != null)
        {
        	SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    		Date now = df.parse(df.format(new Date()));
            Date beginTime = df.parse(df.format(campus.getOpenTime()));
            Date endTime = df.parse(df.format(campus.getCloseTime()));
            if(belongCalendar(now, beginTime, endTime) && (campus.getStatus()==1)){
            	obj.put("status", 1);//status 为营业
            }
            else{
            	obj.put("status", 0);//status 为休息
            }
        	map.put("State", "Success");
            map.put("data", obj);
        }
        else{
        	map.put("State", "Fail");
            map.put("data", null);
        }				
		return map; 
    }
    
	/**
	 * 获得商家信息
	 * add by ljt
	 */
	
	@RequestMapping("/getCampusByIdWx")
    public @ResponseBody Map<String,Object> getCampusByIdWx(@RequestParam String seller_id) {
		
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
		
		//获得优惠活动信息
		List<Preferential> list = preferentialService.getPreferential(paramMap);
		JSONArray promotion =  new JSONArray();
		for(Preferential pref: list)
		{
			JSONObject obj = new JSONObject();
			obj.put("info", " 满"+String.valueOf(pref.getNeedNumber())+" 减 "+String.valueOf(pref.getDiscountNum()));
			promotion.add(obj);
		}
		node.put("promotion",promotion);//优惠活动数组
		
		
		node.put("delivery_fee", campus.getDelivery_fee());//配送费
		
		//获得用户最近30单订单的评分
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
        
		paramMap2.put("limit", 30);
		paramMap2.put("offset", 0);
		paramMap2.put("sort", "date");
		paramMap2.put("order", "desc");
		paramMap2.put("campusId", seller_id);
		
		float totalQuality = 0.0f;
		float totalService = 0.0f;
        
        List<FoodComment> commentlist = foodService.getAllComments(paramMap2);
        String overAll = "5";
        if(commentlist.size() == 0) //没有评论
        {
        	node.put("overall", 5);//综合评分
    		node.put("quality", 5);//商家评分
    		node.put("service", 5);//配送评分
        }
        else
        {
        	for (FoodComment comment:commentlist){
            	
            	totalQuality = totalQuality + comment.getQuality();
            	totalService = totalService + comment.getService();  	
            }
            DecimalFormat decimalFormat=new DecimalFormat(".0");
            
            float quality = totalQuality/commentlist.size();
            float service = totalService/commentlist.size();
            
            overAll = decimalFormat.format((quality + service)/2);
            
            node.put("overall", overAll);//综合评分
    		node.put("quality", decimalFormat.format(quality));//商家评分
    		node.put("service", decimalFormat.format(service));//配送评分
        }
        
		
		
		//更新店铺评分（可能会稍晚）
		Map<String, Object> paramMap3 = new HashMap<String, Object>();
		paramMap3.put("campusId", seller_id);
		paramMap3.put("overAll", overAll);
		int flag = campusService.updateCampusOverAll(paramMap3);
		
		if(flag == 0 || flag == -1){
			System.out.println("更新店铺评分失败");
		}
		
		//商家基本信息
		node.put("phone", campus.getCustomService());
		node.put("notice", campus.getNotice());//商家公告
		node.put("address", campus.getAddress());
		node.put("sell_time", campus.getOpenTime()+ "~" + campus.getCloseTime());
		node.put("deliver", campus.getDeliver());
					
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

		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", node);				
		return data;
		
	}	
	
	/**
	 * 获得商家信息
	 * add by ljt
	 */
	
	@RequestMapping("/getReviews")
    public @ResponseBody Map<String,String> getAllReviewsWx(@RequestParam String seller_id,@RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Campus> campuslist = campusService.getAllCampus(paramMap);
        
        paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
        paramMap.put("sort", "date");
        paramMap.put("order", "asc");
        paramMap.put("search", null);
        paramMap.put("campusId", seller_id);
        
        List<FoodComment> commentlist = foodService.getAllComments(paramMap);

		JSONArray jsonarray = new JSONArray(); 
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				
		for (FoodComment comment: commentlist)
		{
			JSONObject node = new JSONObject();
			node.put("head_pic", comment.getImgUrl());
			node.put("nick", comment.getNickName());
			node.put("quality", comment.getQuality());
			node.put("content", comment.getComment());
			node.put("time", sdf.format(comment.getDate()));
			jsonarray.add(node);
		}
		
		System.out.println("reviews return:" + jsonarray.toString());		
		Map<String, String> map = new HashMap<String, String>();
        map.put("State", "Success");
        map.put("data", jsonarray.toString());				
		return map;
		
	}	
}
