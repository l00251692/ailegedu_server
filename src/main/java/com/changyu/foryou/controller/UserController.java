package com.changyu.foryou.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.synth.SynthSeparatorUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Feedback;
import com.changyu.foryou.model.ProjectComment;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.model.WeChatContext;
import com.changyu.foryou.service.OrderService;
import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.AesCbcUtil;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.HttpRequest;
import com.changyu.foryou.tools.Md5;
import com.google.gson.JsonObject;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProjectService projectService;
	
	private WeChatContext context = WeChatContext.getInstance();

	/**
	 * 取得用户信息
	 * @param phone
	 * @return 
	 */
	@RequestMapping(value="/getUser",method=RequestMethod.POST)
	public @ResponseBody
	Users selectUserById(String phone) {
		return userService.selectByUsername(phone);
	}

	/**
	 * 将某一用户设置为管理员
	 * @param phone
	 * @return
	 */
	@RequestMapping(value="setUserAdmin")
	public @ResponseBody Integer setUserAdmin(@RequestParam String phone,@RequestParam Integer campusId){
		return userService.setUserAdmin(phone,campusId);
	}

	/**
	 * 将某一用户设为常规用户
	 * @param phone
	 * @return
	 */
	@RequestMapping(value="setUserCommon")
	public @ResponseBody Integer setUserCommon(@RequestParam String phone,@RequestParam Integer campusId){
		return userService.setUserCommon(phone,campusId);
	}

	/**
	 * 将某一用户设置为超级管理员
	 * @param phone
	 * @return
	 */
	@RequestMapping(value="setUserSuperAdmin")
	public @ResponseBody Integer setUserSuperAdmin(@RequestParam String phone,@RequestParam Integer campusId){
		return userService.setUserSuperAdmin(phone,campusId);
	}

	/**
	 * 取得所有用戶信息
	 * @return
	 */
	@RequestMapping(value="/getAllUser")
	public @ResponseBody
	Map<String, Object> getAllUser(Integer limit,Integer offset,String sort,String order,String search) {
		Map<String, Object> map = new HashMap<String, Object>();

		if(sort!=null&&sort.equals("lastLoginDate")){
			sort="last_login_date";
		}

		if(sort!=null&&sort.equals("createTime")){
			sort="create_time";
		}
		
		if(sort!=null&&sort.equals("defaultAddress")){
			sort="default_address";
		}
		
		if(search!=null&&!search.trim().equals("")){
			search="%"+search+"%";
		}

		List<Users> userlist=userService.getAllUser(limit,offset,sort,order,search);
		JSONArray  json=JSONArray.parseArray(JSON.toJSONStringWithDateFormat(userlist,"yyyy-MM-dd"));
		map.put("total", userService.getUserCount(search));
		map.put("rows", json);
		return map;
	}

	/**
	 * 用户登陆
	 * @param phone
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/toLogin")
	public @ResponseBody
	Map<String, Object> toLogin(@RequestParam String phone,@RequestParam String password,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (phone!=null&&password!=null&&!phone.trim().equals("") && !password.trim().equals("")) {
			Users users = userService.checkLogin(phone);
			if (users != null) {
				System.out.println(Md5.GetMD5Code(password));
				if (users.getPassword().equals(Md5.GetMD5Code(password))) {
					map.put(Constants.STATUS, Constants.SUCCESS);
					map.put(Constants.MESSAGE, "登陆成功");
					map.put("type", users.getType());
					HttpSession session=request.getSession();
					session.setAttribute("type", users.getType());
					session.setAttribute("phone", users.getPhone());
					Date date=new Date();
					userService.updateLastLoginTime(date,phone);
					orderService.deleteStatus7Order(phone);
				} else {
					map.put(Constants.STATUS, Constants.FAILURE);
					map.put(Constants.MESSAGE, "账号或密码错误，请检查后输入");
				}
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "账号或密码错误，请检查后输入");
			}
		}

		return map;
	}
	
	/**
	 * 用户登陆
	 * @param phone
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/toLoginWx")
	public @ResponseBody
	Map<String, Object> toLoginWx(@RequestParam String wx_code,@RequestParam String encryptedData,@RequestParam String iv) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		System.out.println("toLoginWx enter");
		
		//登录凭证不能为空 
		if (wx_code == null || wx_code.length() == 0) 
		{ 
			map.put("State", "False"); 
			map.put("data", "登陆失败"); 
			return map; 	
		} 

		//小程序唯一标识  (在微信小程序管理后台获取) 
		String wxspAppid = context.getAppId(); 
		//小程序的 app secret (在微信小程序管理后台获取) 
		String wxspSecret = context.getAppSecrct();
	    //授权（必填） 
		String grant_type = "authorization_code"; 
	
	
		//////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid //////////////// 
		//请求参数 
		String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + wx_code + "&grant_type=" + grant_type; 
		//发送请求 
		String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params); 
		//解析相应内容（转换成json对象） 
		//JSONObject json = JSONObject.fromObject(sr); 
		JSONObject json = JSONObject.parseObject(sr);
		//获取会话密钥（session_key） 
		String session_key = json.get("session_key").toString(); 
		//用户的唯一标识（openid） 
		String openid = (String) json.get("openid"); 
	
		//////////////// 2、对encryptedData加密数据进行AES解密 //////////////// 
		try { 
		  String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
		  System.out.println(result);
		  if (null != result && result.length() > 0) 
		  { 
		
			JSONObject userInfoJSON = JSONObject.parseObject(result); 
			
			JSONObject userInfo = new JSONObject(); 
			userInfo.put("openId", userInfoJSON.get("openId")); 
			userInfo.put("nickName", userInfoJSON.get("nickName")); 
			userInfo.put("gender", userInfoJSON.get("gender")); 
			userInfo.put("city", userInfoJSON.get("city")); 
			userInfo.put("province", userInfoJSON.get("province")); 
			userInfo.put("country", userInfoJSON.get("country")); 
			userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl")); 
			userInfo.put("user_id", userInfoJSON.get("openId")); 
			userInfo.put("user_token", userInfoJSON.get("unionId"));

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("is_login", "1");
			loginInfo.put("session_key", session_key);
			loginInfo.put("userInfo", userInfo); 
			
			try {
				//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				// = df.format(new Date());// new Date()为获取当前系统时间
				
				Users users = userService.checkLogin(userInfoJSON.get("openId").toString());
				if (users != null)
				{
					System.out.println("用户已存在，更新用户信息");
					users.setImgUrl(userInfoJSON.get("avatarUrl").toString());
					users.setNickname(userInfoJSON.get("nickName").toString());
					users.setLastLoginDate(new Date());
					users.setSex(new Short(userInfoJSON.get("gender").toString()));
					userService.updateUserInfo(users);
				}
				else
				{
					Users users2 = new Users(userInfoJSON.get("openId").toString(), userInfoJSON.get("nickName").toString(),
							userInfoJSON.get("avatarUrl").toString(),userInfoJSON.get("gender").toString());
					userService.addUsers(users2);
				}				
			} catch (Exception e) {
				System.out.println(e);
			}
			
			
			
			map.put("State", "Success"); 
			map.put("data", loginInfo); 
			return map; 
		  }
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 
		
		map.put("State", "False"); 
		map.put("data", "登陆失败"); 
		return map; 	
	
	}

	/**
	 * 检查用户是否已经注册
	 * @param phone
	 * @return
	 */
	@RequestMapping(value="/checkUserIsExist")
	public @ResponseBody
	Map<String, Object> checkUserIsExist(@RequestParam String phone) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (userService.selectByUsername(phone) != null) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "该账号已经被注册");
		} else {
			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE, "该账号可以注册");
		}
		return map;
	}

	/**
	 * 用户注册
	 * @param phone
	 * @param password
	 * @param nickname
	 * @return
	 */
	@RequestMapping(value="/registerIn")
	public @ResponseBody
	Map<String, Object> registerIn(@RequestParam String phone,
			@RequestParam String password, @RequestParam String nickname) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			if (!phone.equals("") && phone.length() == 11
					&& !password.equals("")) {
				String passwordMd5=Md5.GetMD5Code(password);

				Users users = new Users(phone, passwordMd5, nickname);
				userService.addUsers(users);
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "注册成功");
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "注册失败");
			}
		} catch (Exception e) {
			map.put(Constants.STATUS, Constants.FAILURE); // 捕捉异常
			map.put(Constants.MESSAGE, "注册失败");
			return map;
		}

		return map;
	}


	/**
	 * 用户更改密码
	 * @param phone
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "/resetPassword")
	public @ResponseBody
	Map<String, Object> resetPassword(@RequestParam String phone,
			@RequestParam String newPassword) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Users users = userService.selectByUsername(phone);
			if (users != null) {
				userService.updatePassword(phone,Md5.GetMD5Code(newPassword));  //对密码做md5加密
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "修改密码成功");
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "修改密码失败");
			}
		} catch (Exception e) {
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "修改密码失败");
		}
		return map;
	}

	/**
	 * 更新用户个人信息
	 * @param phone
	 * @param password
	 * @param nickname
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/updateUserInfo")
	public @ResponseBody Map<String,Object> updateUserInfo(@RequestParam String phone,
			String nickname,String type,String sex,String academy,String qq,String weiXin){
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Users users=new Users();
			users.setPhone(phone);
			if(weiXin!=null)
			{
				users.setWeiXin(weiXin);
			}
			
			if(qq!=null)
			{
				users.setQq(qq);
			}
			
			if(academy!=null)
			{
				users.setAcademy(academy);
			}
			
			if(sex!=null){
				users.setSex((short)Integer.parseInt(sex));
			}
			
			if(nickname!=null){
				users.setNickname(nickname);
			}


			if(type!=null){
				users.setType((short)Integer.parseInt(type));
			}

			if(userService.updateUserInfo(users)!=-1){
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "修改用户信息成功");
			}else{
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "修改用户信息失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "修改用户信息失败");
		}
		return map;
	}


	/**
	 * 获取我的用户总信息
	 * @param phone 用户id
	 * @return
	 */
	@RequestMapping(value="getMineInfoWx")
	public @ResponseBody Map<String, Object> getMineInfoWx(@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
	
		Users users=userService.selectByUserId(user_id);
		System.out.println("getMineInfoWx:" + user_id);
		if(users == null)
		{
			map.put("State", "False"); 
			map.put("info", "获得我的信息失败"); 
			return map; 
		}
		int msgCount = projectService.getCommentUnreadMsgCount(user_id);
		
		JSONObject obj = new JSONObject();
		obj.put("campus_id", users.getCampusId());
		obj.put("unread_msg_count", msgCount);
		obj.put("weixin", users.getWeiXin());
			
		map.put("State", "Success"); 
		map.put("data", obj); 
		return map;
	}

	/**
	 * 提交用户反馈信息
	 * @param phoneId
	 * @param suggestion
	 * @return
	 */
	@RequestMapping(value="feedbackMessage")
	public @ResponseBody Map<String, Object> feedbackMessage(@RequestParam Integer campusId,@RequestParam String phoneId,@RequestParam String suggestion){
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			Feedback feedback=new Feedback();
			feedback.setCampusId(campusId);
			feedback.setPhoneId(phoneId);
			feedback.setSuggestion(suggestion);
			Calendar calendar=Calendar.getInstance();
			Date date=calendar.getTime();   //设置反馈时的日期
			feedback.setDate(date);
             System.out.println(suggestion);
			if(userService.addFeedbackSuggestion(feedback)!=-1){
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "添加意见成功");
			}else{
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "添加意见失败");
			}
		}catch(Exception e){
			e.getStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "添加意见失败");
		}

		return map;
	}


	/**
	 * 更新用户头像
	 * @param image
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/uploadUserImage")
	public @ResponseBody Map<String, Object> uploadNewsImage(@RequestParam String image, HttpServletRequest request)throws IOException{
		String phone=request.getParameter("phoneId");

		Map<String, Object> map = new HashMap<String, Object>();
		image=image.replaceAll(" ", "+");

		String realPath = request.getSession().getServletContext().getRealPath("/"); 		
		realPath=realPath.replace("foryou", "ForyouImage");
		realPath=realPath.concat("users/");
		String fileNameString=new Random().nextLong()+""+new Date().getTime()+".jpg";

		if (image == null) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "没有文件");
		}

		@SuppressWarnings("restriction")
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			//Base64解码
			@SuppressWarnings("restriction")
			byte[] b = decoder.decodeBuffer(image);
			for(int i=0;i< b.length;++i)
			{
				if(b[i]< 0)
				{//调整异常数据
					b[i]+=256;
				}
			}
			//生成jpeg图片
			String imgFilePath = realPath+fileNameString;//新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);    
			out.write(b);
			out.flush();
			out.close();

			//删除原头像
			String orignUrl=userService.getImageUrl(phone);
			if(orignUrl!=null){
				String[] temp=orignUrl.split("/");
				String imageName=temp[(temp.length-1)];

				String name=realPath+imageName;

				File file=new File(name);
				if(file.isFile()){
					file.delete();//删除
				}
			}

			String imageUrl=Constants.localIp+"/users/"+fileNameString;
			Integer flag=userService.updateImageUrl(imageUrl, phone);
			if(flag==1){
				map.put("imageUrl", imageUrl);
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "头像更新成功!");
			}else{
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "头像更新失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "头像更新失败");
		}

		return map;
	}


	@RequestMapping("/uploadAndroidLog")
	public @ResponseBody Map<String, Object> uploadAndroidLog(@RequestParam String log, HttpServletRequest request)throws IOException{

		Map<String, Object> map = new HashMap<String, Object>();

		String realPath = request.getSession().getServletContext().getRealPath("/"); 		
		realPath=realPath.replace("foryou", "ForyouImage");
		realPath=realPath.concat("android/");
		String fileNameString=new Random().nextInt(100)+""+new Date().getTime()+"log.log";

		if (log == null) {
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "没有文件");
			return map;
		}

		@SuppressWarnings("restriction")
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			//Base64解码
			@SuppressWarnings("restriction")
			byte[] b = decoder.decodeBuffer(log);
			for(int i=0;i< b.length;++i)
			{
				if(b[i]< 0)
				{//调整异常数据
					b[i]+=256;
				}
			}
			//生成jpeg图片
			String imgFilePath = realPath+fileNameString;//新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);    
			out.write(b);
			out.flush();
			out.close();

			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE, "log日志上传成功!");

		} catch (Exception e) {
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "log日志上传失败！");
		}

		return map;
	}

	/**
	 * 获取配送员名单
	 * @return
	 */
	@RequestMapping(value="getDeliverAdmin")
	public @ResponseBody Map<String,Object> getDeliverAdmin(@RequestParam Integer campusId) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> paramMap=new HashMap<String, Object>();
		
		paramMap.put("campusId", campusId);
		
		try{
			List<Users> users=userService.getDeliverAdmin(paramMap);
			map.put(Constants.STATUS, Constants.SUCCESS);
			map.put(Constants.MESSAGE, "获取配送员成功!");
			map.put("deliverAdmins", users);
		}catch(Exception e){
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "获取配送员失败！");
		}

		return map;
	}

	/**
	 * ios post token到数据表
	 * @param phoneId
	 * @param token
	 * @return
	 */
	@RequestMapping(value="postIOSToken")
	public @ResponseBody Map<String, Object> postIosToken(String phoneId,String token){
		Map<String, Object> map=new HashMap<String, Object>();

		try{
			userService.clearOldToken(token);                   //如果该token以前别的用户使用过，就将该
			int flag=userService.setUserToken(phoneId,token);
			
			if(flag!=0&flag!=-1){
				map.put(Constants.STATUS, Constants.SUCCESS);
				map.put(Constants.MESSAGE, "设置用户token成功!");
			}
		}catch(Exception e){
			e.printStackTrace();
			map.put(Constants.STATUS, Constants.FAILURE);
			map.put(Constants.MESSAGE, "设置用户token失败");
		}

		return map;
	}

	/**
	 * 获取用户反馈
	 * @return
	 */
	@RequestMapping(value="/getFeedbacks")
	public @ResponseBody JSONArray getFeedbacks(Integer campusId,Integer limit,Integer page){
		try {
			Map<String, Object>paramMap=new HashMap<String, Object>();
			paramMap.put("campusId", campusId);
			if(limit!=null&&page!=null)
			{
				paramMap.put("limit",limit);
				paramMap.put("offset", (page-1)*limit);
			}
			
			List<Feedback> feedbacks=userService.getFeedbacks(paramMap);
			return JSONArray.parseArray(JSON.toJSONStringWithDateFormat(feedbacks, "yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 忘记密码的跳转
	 * @return
	 */
	@RequestMapping(value="/forgetPassword")
	public  String forgetPassword(){	
		return "redirect:/kidding.html";
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/logout")
	public  String logout(HttpServletRequest request){	
		request.getSession().removeAttribute("phone");
		return "redirect:/login.html";
	}
	
	/**
	 * 获取用户类型
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getUserType")
	public @ResponseBody Short getUserType(HttpServletRequest request){
		return (Short) request.getSession().getAttribute("type");
	}
	
	/**
	 * 获取用户设备占比
	 */
	@RequestMapping(value="/getUserDevicePercent")
	public @ResponseBody Map<String,Object> getUserDevicePercent(){
		Map<String, Object> resultMap=new HashMap<String, Object>();
		
		try {
			DecimalFormat df = new DecimalFormat("##.0");
		    Map<String,Object> paramMap=new HashMap<String,Object>();
		    paramMap.put("device","0");   //设备号0表示ios
			Integer iosCount=userService.getCountsByDevice(paramMap);
			paramMap.put("device","1");
			Integer androidCount=userService.getCountsByDevice(paramMap);
			resultMap.put("android",Float.valueOf(df.format(androidCount*1.0/(androidCount+iosCount)*100)));
			resultMap.put("ios",Float.valueOf(df.format(iosCount*1.0/(androidCount+iosCount)*100)));
		} catch (Exception e) {
			e.getStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 用户更改密码(用老密码更改）
	 * @param phone
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@RequestMapping("/changePassword")
	public @ResponseBody Map<String,Object> changePassword(@RequestParam String phone
			,@RequestParam String oldPassword,@RequestParam String newPassword)
			{
				Map<String, Object> map=new HashMap<String, Object>();
				try {
					Map<String, Object> paramMap=new HashMap<String, Object>();
					paramMap.put("phone",phone);
					String passwordMd5=Md5.GetMD5Code(oldPassword);
					paramMap.put("password",passwordMd5);
					List<Users> users=userService.selectByPhoneAndPassword(paramMap);
					if(users.size()==0)
					{
						map.put(Constants.STATUS, Constants.FAILURE);
						map.put(Constants.MESSAGE, "更改失败，原密码错误");
					}
					else
					{
						userService.updatePassword(phone,Md5.GetMD5Code(newPassword));  //对密码做md5加密
						map.put(Constants.STATUS, Constants.SUCCESS);
						map.put(Constants.MESSAGE, "修改密码成功");
					}	
					
				} catch (Exception e) {
					e.printStackTrace();	
					map.put(Constants.STATUS, Constants.FAILURE);
					map.put(Constants.MESSAGE, "修改密码失败");					
				}				
				return map;	
	}
	
	/**
	 * 获取我的用户总信息
	 * @param phone 用户id
	 * @return
	 */
	@RequestMapping(value="getMsgListWx")
	public @ResponseBody Map<String, Object> getMsgListWx(@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray list = new JSONArray();
		int readCount=0;
	
		List <ProjectComment> commentMsgList =projectService.getProjectCommentMsg(user_id);
		
		for(int i = 0; i < commentMsgList.size();i++)
		{
			if(commentMsgList.get(i).getIsRead())
			{
				readCount++;
			}
			JSONObject obj = new JSONObject();
			obj.put("msg_id", i);
			obj.put("type", 1);
			obj.put("project_id", commentMsgList.get(i).getProjectId());
			obj.put("time", commentMsgList.get(i).getCommentTime());
			obj.put("title", commentMsgList.get(i).getProjectTitle());
			obj.put("user", commentMsgList.get(i).getUserName());
			obj.put("is_read", commentMsgList.get(i).getIsRead());
			list.add(obj);
		}
		
		JSONObject rslt = new JSONObject();
		rslt.put("count", readCount);
		rslt.put("list", list);
			
		map.put("State", "Success"); 
		map.put("data", rslt); 
		return map;
	}
	
	@RequestMapping(value="setProjectCommentReadWx")
	public @ResponseBody Map<String, Object> setProjectCommentReadWx(@RequestParam String project_id,@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
	
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userId",user_id);
		paramMap.put("projectId",project_id);
	
		int flag =projectService.setProjectCommentRead(paramMap);
			
		map.put("State", "Success"); 
		map.put("data", flag); 
		return map;
	}
	
	@RequestMapping(value="setProjectLikeStatusWx")
	public @ResponseBody Map<String, Object> setProjectLikeStatusWx(@RequestParam Boolean status, @RequestParam String project_id,@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userId",user_id);
		paramMap.put("projectId",project_id);
		
		int flag;
		if(status == true)
		{
			flag =userService.addLike(paramMap);
		}
		else
		{
			flag =userService.delLike(paramMap);
		}
		
			
		map.put("State", "Success"); 
		map.put("data", flag); 
		return map;
	}
}
