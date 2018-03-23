package com.changyu.foryou.service;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.ProjectComment;
import com.changyu.foryou.model.University;

public interface ProjectService {
	
	public List<Banner> getBannerInfo();
	
	public List<Project> getProjectList(Map<String, Object> paramMap);
	
	public List<Project> getMyProjectList(Map<String, Object> paramMap);
	
	public Project getProjectInfo(Map<String, Object> paramMap);
	
	public int createProject(Map<String, Object> paramMap);
	
	public int updateProjectHeadImg(Map<String, Object> paramMap);
	
	public int updateProjectAddImgs(Map<String, Object> paramMap);
	
	public int commitProjectComment(Map<String, Object> paramMap);
	
	public List<ProjectComment> getCommentList(Map<String, Object> paramMap);
	
	public int getCommentCount(Map<String, Object> paramMap);
	
	public List<ProjectComment> getProjectCommentMsg(String userId);
	
	public int getCommentUnreadMsgCount(String userId);
	
	public int setProjectCommentRead(Map<String, Object> paramMap);
	
	public List<University> getUnivList(int proviceId);
	
	public List<University> getProviceList(int flag);
}
