package com.changyu.foryou.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.omg.CORBA.portable.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Campus;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.ProjectComment;
import com.changyu.foryou.model.University;
import com.changyu.foryou.model.UserLikeProject;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.HttpRequest;
import com.changyu.foryou.tools.PayUtil;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;

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
    public @ResponseBody Map<String,Object> getProjectListWx(@RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
        List<Project> projectlist = projectService.getProjectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", projectlist.size());
				
		for (Project project: projectlist)
		{
			JSONObject node = new JSONObject(); 
			node.put("project_id", project.getProjectId());
			node.put("item_title", project.getTitle());
			node.put("subtitle", project.getSubtitle());
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			node.put("create_time", formattmp.format(project.getCreateTime()));
			node.put("project_head", project.getHeadImg());
			
			Users user = userService.selectByUserId(project.getCreateUserId());
			node.put("create_userName", user.getNickname());
			node.put("create_userHead", user.getImgUrl());
			
			node.put("location", project.getLocation());
			
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("projectId", project.getProjectId());
			int likecount = userService.getProjectLikeCount(paramMap2);
			node.put("like", likecount);
			
			if (project.getDeadLineTime() != null)
			{
				Date  deadLineTime =  project.getDeadLineTime();	
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(deadLineTime);
				Calendar  today = Calendar.getInstance();
				double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
				node.put("days", days);
			}
			else
			{
				node.put("days", 0);
			}
			

			Map<String, Object> paramMap3 = new HashMap<String, Object>();
			paramMap3.put("projectId", project.getProjectId());
	        int count = projectService.getCommentCount(paramMap3);
			node.put("comments", count);
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getMyProjectListWx")
    public @ResponseBody Map<String,Object> getMyProjectListWx(@RequestParam String user_id, @RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
		paramMap.put("createUserId", user_id);
        List<Project> projectlist = projectService.getMyProjectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", projectlist.size());
				
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
			
			node.put("location", project.getLocation());
			
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("projectId", project.getProjectId());
			int likecount = userService.getProjectLikeCount(paramMap2);
			node.put("like", likecount);
			
			if (project.getDeadLineTime() != null)
			{
				Date  deadLineTime =  project.getDeadLineTime();	
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(deadLineTime);
				Calendar  today = Calendar.getInstance();
				double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
				node.put("days", days);
			}
			else
			{
				node.put("days", 0);
			}
			
			Map<String, Object> paramMap3 = new HashMap<String, Object>();
			paramMap3.put("projectId", project.getProjectId());
	        int count = projectService.getCommentCount(paramMap3);
			node.put("comments", count);
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getProjectInfoWx")
    public @ResponseBody Map<String,Object> getProjectInfoWx(@RequestParam String user_id, @RequestParam String project_id) {
		Map<String,Object> data = new HashMap<String, Object>();
			
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		System.out.println("getProjectInfoWx:" + project_id);
        Project project = projectService.getProjectInfo(paramMap);
        if(project == null)
        {
        	data.put("State", "Fail");
    		data.put("data", null);
    		return data;
        }
        System.out.println("getProjectInfoWx:" + project_id);
		JSONObject node = new JSONObject(); 
		node.put("project_id", project.getProjectId());
		node.put("item_title", project.getTitle());
		node.put("subtitle", project.getSubtitle());
		node.put("concat", project.getConcat());
		DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		node.put("create_time", formattmp.format(project.getCreateTime()));
		node.put("project_head", project.getHeadImg());
		
		Date  deadLineTime =  project.getDeadLineTime();	
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(deadLineTime);
		Calendar  today = Calendar.getInstance();
		double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
		node.put("days", days);
		
		node.put("location", project.getLocation());
		
		Users user = userService.selectByUserId(project.getCreateUserId());
		node.put("create_userName", user.getNickname());
		node.put("create_userHead", user.getImgUrl());
		
		JSONArray arr = JSON.parseArray(project.getAddImgs());
		node.put("addImgarr", arr);
		
		//查看我是否已经关注了项目
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("userId", user_id);
		paramMap2.put("projectId", project_id);
		UserLikeProject result = userService.checkIsLike(paramMap2);
		if(result != null)
		{
			node.put("isLike", true);
		}
		else
		{
			node.put("isLike", false);
		}
		
		Map<String, Object> paramMap3 = new HashMap<String, Object>();
		paramMap3.put("projectId", project_id);
		int count = userService.getProjectLikeCount(paramMap3);
		node.put("interest", count);
		
		
		
		data.put("State", "Success");
		data.put("data", node);				
		return data;
		
	}
	
	@RequestMapping("/createProjectWx")
    public @ResponseBody Map<String,Object> createProjectWx(@RequestParam String user_id,@RequestParam String title,@RequestParam String concat, @RequestParam String date, @RequestParam String selectUniv, @RequestParam String instruction) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		Calendar calendar=Calendar.getInstance();
		
		String projectId = String.valueOf(calendar.getTimeInMillis());
		
		paramMap.put("projectId", projectId);
		paramMap.put("title", title);
		paramMap.put("concat", concat);
		paramMap.put("subtitle", instruction);
		paramMap.put("createTime", new Date());
		paramMap.put("createUserId", user_id);
		paramMap.put("headImg", "");
		paramMap.put("interest", 0);
		paramMap.put("deadLineTime", date);
		paramMap.put("location", selectUniv);
		paramMap.put("addImgs", "");
        int flag = projectService.createProject(paramMap);
        
        if(flag != -1 && flag !=0)
        {
        	JSONObject data = new JSONObject();
        	data.put("project_id", projectId);
        	
        	map.put("State", "Success");
        	map.put("data", data);	
        }
        else
        {
        	map.put("State", "False");
        	map.put("data", null);	
        }
								
		return map;	
	}
	
	@RequestMapping("/updateProjectImgWx")
    public @ResponseBody Map<String,Object> updateProjectImgWx(@RequestParam MultipartFile[] image,HttpServletRequest request)throws Exception{
		
		Map<String,Object> map = new HashMap<String, Object>();
		//获取文件需要上传到的路径
        String path = request.getSession().getServletContext().getRealPath("/");
        path = path.concat("JiMuImage/project/");
        
        String projectId = request.getParameter("project_id");
   
        
   
        List<String> imageUrl = new ArrayList<String>();
        for (MultipartFile file : image) 
        {
            if (file.isEmpty()) {
                System.out.println("文件未上传");
                imageUrl.add(null);
            } else 
            {
                String contentType = file.getContentType();

                if (contentType.startsWith("image"))
                {
                    String newFileName = projectId + ".jpg";
                    FileUtils.copyInputStreamToFile(file.getInputStream(),new File(path, newFileName)); // 写文件
                    imageUrl.add(Constants.localIp + "/project/" + newFileName);
                }
            }
        }
        System.out.println("project_id="+ projectId);
        System.out.println("headImg="+ imageUrl.get(0));
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", projectId);
		paramMap.put("headImg", imageUrl.get(0));
        int flag = projectService.updateProjectHeadImg(paramMap);
        
        if(flag != -1 && flag !=0)
        {  
        	JSONObject data = new JSONObject();
        	data.put("project_id", projectId);
        	map.put("State", "Success");
        	map.put("data", data);	
        	return map;	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传项目图片失败");	
        }
    	return map;	
	}
	
	
	@RequestMapping("/updateProjectInfoImgWx")
    public @ResponseBody Map<String,String> updateProjectInfoImgWx(@RequestParam MultipartFile[] image,HttpServletRequest request)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		
		String projectId = request.getParameter("project_id");
		//获取文件需要上传到的路径
        String path = request.getSession().getServletContext().getRealPath("/");
        
        path = path.concat("JiMuImage/project/"+ projectId + "/");
        
   
        List<String> imageUrl = new ArrayList<String>();
        for (MultipartFile file : image) 
        {
            if (file.isEmpty()) {
                System.out.println("文件未上传");
                imageUrl.add(null);
            } else 
            {
                String contentType = file.getContentType();

                if (contentType.startsWith("image"))
                {
                	Calendar calendar=Calendar.getInstance();
            		String random = String.valueOf(calendar.getTimeInMillis());
            		
                    String newFileName = random + ".jpg";
                    System.out.println(newFileName);
                    FileUtils.copyInputStreamToFile(file.getInputStream(),new File(path, newFileName)); // 写文件
                    imageUrl.add(Constants.localIp + "/project/"+ projectId + "/" + newFileName);
                }
            }
        }
  
        System.out.println("url="+ imageUrl.get(0));   
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", projectId);
		
		Project project = projectService.getProjectInfo(paramMap);
		
		JSONArray arr;
		
		if(project.getAddImgs() != null && !project.getAddImgs().isEmpty())
		{
			arr = JSON.parseArray(project.getAddImgs());
		}
		else
		{
			arr = new JSONArray();
		}
		
		
		JSONObject obj = new JSONObject();
		obj.put("url",imageUrl.get(0));
		arr.add(obj);
		paramMap.put("addImgs", arr.toJSONString());
        int flag = projectService.updateProjectAddImgs(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传图片成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传图片失败");	
        }
    	return map;	
	}
	
	
	@RequestMapping("/sendProjdecCommentWx")
    public @ResponseBody Map<String,Object> sendProjdecCommentWx(@RequestParam String user_id,@RequestParam String project_id,@RequestParam String comment) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		System.out.println("sendProjdecCommentWx:" + comment);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", user_id);
		paramMap.put("projectId", project_id);
		paramMap.put("comment", comment);
		paramMap.put("commentTime", new Date());
        int flag = projectService.commitProjectComment(paramMap);
        if(flag != -1 && flag !=0)
        {
  
        	map.put("State", "Success");
        	map.put("data", null);	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "提交评论失败");	
        }
								
		return map;	
	}
	
	@RequestMapping("/getCommentListWx")
    public @ResponseBody Map<String,Object> getCommentListWx(@RequestParam String project_id,@RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
		
        List<ProjectComment> commentlist = projectService.getCommentList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", commentlist.size());
				
		for (ProjectComment comment: commentlist)
		{
			JSONObject node = new JSONObject(); 
			
			node.put("user_id", comment.getUserId());
			node.put("comment", comment.getComment());
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			node.put("time", formattmp.format(comment.getCommentTime()));
			node.put("user_head", comment.getUserHead());
			node.put("user_name", comment.getUserName());
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	
	@RequestMapping("/getUnivListWx")
    public @ResponseBody Map<String,Object> getUnivListWx(@RequestParam int flag) {
		
		JSONArray provinceList = new JSONArray(); 
		JSONArray univList = new JSONArray(); 
        
        List<University> provicelist = projectService.getProviceList(flag);
        for(University provTmp: provicelist)
        {
        	JSONObject provice = new JSONObject();
        	
        	provice.put("proviceId", provTmp.getProviceId());
        	provice.put("name", provTmp.getProvice());
        	
        	provinceList.add(provice);
        	
        	List<University> universitylist = projectService.getUnivList(provTmp.getProviceId());
   
        	JSONArray univs = new JSONArray();
            for(University univTmp: universitylist)
            {
            	
            	JSONObject tmp = new JSONObject();
            	tmp.put("id", univTmp.getUnivId());
            	tmp.put("name", univTmp.getUniversity());
            	univs.add(tmp);
            }
            
            JSONObject univ = new JSONObject();
        	univ.put("proviceId", provTmp.getProviceId());
            univ.put("univs", univs);
            univList.add(univ);
        }
        
		
		
		
		
		JSONObject rtn = new JSONObject();
		rtn.put("provinceList", provinceList);
		rtn.put("univList", univList);
				
		
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getShareQrWx")
    public @ResponseBody Map<String,Object> getShareQrWx(@RequestParam String project_id, HttpServletRequest request) throws Exception {
		
		//接口B：生成无限制但需要先发布的小程序
		//String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
		
		//接口C：调试用
		String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode";

		String access_token = (String) PayUtil.getAccessToken().get("access_token");
		//取access_token
    
		url = url + "?access_token=" + access_token;
		
		Map<String, Object> params = new HashMap<>();
        //params.put("scene", "test");
        //params.put("page", "pages/index/index");
		params.put("path", "pages/project/detail?id=" + project_id);
        params.put("width", 160);
        String body = JSON.toJSONString(params);
        System.out.println("createQr:" + body);
        
        String path = request.getSession().getServletContext().getRealPath("/");
        path = path.concat("JiMuImage/project/QrCode/");
        
        HttpRequest.httpPostWithJSON(url,body,path, project_id);
        
        JSONObject rtn = new JSONObject();
        String putpath = Constants.localIp + "/project/QrCode/" + project_id + ".jpg";
        rtn.put("path", putpath);
        
        Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
	}
}
