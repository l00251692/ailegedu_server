package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.mapper.CampusMapper;
import com.changyu.foryou.mapper.ProjectMapper;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.ProjectComment;
import com.changyu.foryou.service.ProjectService;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectMapper projectMapper;
	
	public List<Banner> getBannerInfo()
	{
		return projectMapper.getBannerInfo();
	}
	
	public List<Project> getProjectList(Map<String, Object> paramMap)
	{
		return projectMapper.getProjectList(paramMap);
	}
	
	public List<Project> getMyProjectList(Map<String, Object> paramMap)
	{
		return projectMapper.getMyProjectList(paramMap);
	}
	
	public Project getProjectInfo(Map<String, Object> paramMap)
	{
		return projectMapper.getProjectInfo(paramMap);
	}
	
	public int createProject(Map<String, Object> paramMap)
	{
		return projectMapper.insertSelective(paramMap);
	}

	public int updateProjectHeadImg(Map<String, Object> paramMap)
	{
		return projectMapper.updateProjectHeadImg(paramMap);
	}
	
	public int commitProjectComment(Map<String, Object> paramMap)
	{
		return projectMapper.commitComment(paramMap);
	}
	
	public List<ProjectComment> getCommentList(Map<String, Object> paramMap)
	{
		return projectMapper.getCommentList(paramMap);
	}
}
