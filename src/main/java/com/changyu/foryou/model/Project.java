package com.changyu.foryou.model;

import java.util.Date;

public class Project {
	
	private String projectId;
	
	private String title; //标题
	
	private String subtitle; //内容
	
	private String concat;  //联系方式
	
	private Date createTime;
	
	private String createUserId;
	
	private String headImg; //封面
	
	private Date deadLineTime;
	
	private String location;
	
	private String addImgs;
	
	private int interest;
	

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getConcat() {
		return concat;
	}

	public void setConcat(String concat) {
		this.concat = concat;
	}

	public Date getDeadLineTime() {
		return deadLineTime;
	}

	public void setDeadLineTime(Date deadLineTime) {
		this.deadLineTime = deadLineTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddImgs() {
		return addImgs;
	}

	public void setAddImgs(String addImgs) {
		this.addImgs = addImgs;
	}
	
	

}
