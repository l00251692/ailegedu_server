package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.mapper.CampusMapper;
import com.changyu.foryou.mapper.ProjectMapper;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;
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

}
