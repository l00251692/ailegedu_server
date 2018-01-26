package com.changyu.foryou.model;

import java.util.Date;

public class FoodComment {
	
    private Long orderId;
    
    private String campusId;
    
    private String userId;
    
    public String getCampusId() {
		return campusId;
	}

	public void setCampusId(String campusId) {
		this.campusId = campusId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Short getService() {
		return service;
	}

	public void setService(Short service) {
		this.service = service;
	}

	public Short getQuality() {
		return quality;
	}

	public void setQuality(Short quality) {
		this.quality = quality;
	}

	private Date date;

    private Short service;
    
    private Short quality;
    
    private String comment;
    
    private Short tag;

    private String imgUrl;
    
    private String nickName;
    
    private String phone;
   
    private Integer orderCount;
    


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public Short getTag() {
        return tag;
    }

    public void setTag(Short tag) {
        this.tag = tag;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}