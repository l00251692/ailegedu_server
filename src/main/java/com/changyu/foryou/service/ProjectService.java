package com.changyu.foryou.service;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Project;

public interface ProjectService {
	
	public List<Banner> getBannerInfo();
	
	public List<Project> getProjectList(Map<String, Object> paramMap);

}
