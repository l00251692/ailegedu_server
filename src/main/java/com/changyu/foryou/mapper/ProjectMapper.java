package com.changyu.foryou.mapper;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.ProjectComment;

public interface ProjectMapper {
	
	public List<Banner> getBannerInfo();
	
	public List<Project> getProjectList(Map<String, Object> paramMap);
	
	public Project getProjectInfo(Map<String, Object> paramMap);
	
	public int insertSelective(Map<String, Object> paramMap);
	
	public int updateProjectHeadImg(Map<String, Object> paramMap);
	
	public int commitComment(Map<String, Object> paramMap);
	
	public List<ProjectComment> getCommentList(Map<String, Object> paramMap);
}
