package com.changyu.foryou.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Campus;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.Constants;

@Controller
@RequestMapping("/project")
public class ProjectController {
	
	private ProjectService projectService;
	
	private UserService userService;

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	
	


	/**
	 * 获得首页广告信息
	 * add by ljt
	 */
	
	@RequestMapping("/getBannerInfoWx")
    public @ResponseBody Map<String,String> getBannerInfoWx(HttpServletRequest request) {
		
		//String realPath = request.getSession().getServletContext().getRealPath("/");
        //realPath = realPath.concat("JiMuImage/banner/");
        
        List<Banner> bannerlist = projectService.getBannerInfo();
        Map<String,String> data = new HashMap<String, String>();
        JSONArray banner_arr = new JSONArray();
        for(Banner banner: bannerlist)
        {
        	JSONObject obj = new JSONObject();
        	obj.put("banner_id",banner.getBannerId());
        	String url = Constants.localIp + "/banner/" + banner.getImgUrl();
        	obj.put("carousel_img",url);
        	banner_arr.add(obj);
        }
        data.put("State", "Success");
		data.put("data", banner_arr.toString());				
		return data;
	}
	
	
	@RequestMapping("/getProjectListWx")
    public @ResponseBody Map<String,String> getProjectListWx(@RequestParam String user_id,@RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page);
		paramMap.put("limit", 10);
        List<Project> projectlist = projectService.getProjectList(paramMap);
						
		JSONArray jsonarray = new JSONArray(); 
				
		for (Project project: projectlist)
		{
			JSONObject node = new JSONObject(); 
			node.put("project_id", project.getProjectId());
			node.put("item_title", project.getTitle());
			node.put("subtitle", project.getSubtitle());
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			node.put("create_time", formattmp.format(project.getCreateTime()));
			node.put("project_head", project.getHeadImg());
			node.put("interest", String.valueOf(project.getInterest()));
			
			Users user = userService.selectByUserId(project.getCreateUserId());
			node.put("create_userName", user.getNickname());
			node.put("create_userHead", user.getImgUrl());
			
			jsonarray.add(node);
		}
		Map<String,String> data = new HashMap<String, String>();
		data.put("State", "Success");
		data.put("data", jsonarray.toString());				
		return data;
		
	}
}
