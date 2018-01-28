package com.changyu.foryou.mapper;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;

public interface ProjectMapper {
	
	public List<Banner> getBannerInfo();
	
	public List<Project> getProjectList(Map<String, Object> paramMap);

}
