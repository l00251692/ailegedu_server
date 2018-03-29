package com.changyu.foryou.model;

import java.sql.Time;


public class Campus {
	private String campusId;

	private String campusName;

	private Integer cityId;
	
	private String cityName;

	private Time openTime;

	private Time closeTime;

	private Short status;

	private String closeReason;

	private String customService;
	/* private Double locationX;

    private Double locationY;*/
	
	private String address;
	private String notice;
	private String deliver;
	
	private int sales;
	private Float min_price;
	private Float delivery_fee;
	private int reach_time;
	private String pic_url;
	
	private String univName;
	
	private Float overAll;

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getDeliver() {
		return deliver;
	}
	public void setDeliver(String delivier) {
		this.deliver = delivier;
	}
	
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public int getSales() {
		return sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
	public Float getMin_price() {
		return min_price;
	}
	public void setMin_price(Float min_price) {
		this.min_price = min_price;
	}
	public int getReach_time() {
		return reach_time;
	}
	public void setReach_time(int reach_time) {
		this.reach_time = reach_time;
	}

	public String getCampusId() {
		return campusId;
	}

	public void setCampusId(String campusId) {
		this.campusId = campusId;
	}

	public String getCampusName() {
		return campusName;
	}

	public void setCampusName(String campusName) {
		this.campusName = campusName == null ? null : campusName.trim();
	}

	public Integer getCityId() {
		return cityId;
	}

	public Time getOpenTime() {
		return openTime;
	}

	public String getCustomService() {
		return customService;
	}

	public void setCustomService(String customService) {
		this.customService = customService;
	}

	public void setOpenTime(Time openTime) {
		this.openTime = openTime;
	}

	public Time getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Time closeTime) {
		this.closeTime = closeTime;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Float getDelivery_fee() {
		return delivery_fee;
	}
	public void setDelivery_fee(Float delivery_fee) {
		this.delivery_fee = delivery_fee;
	}
	public String getUnivName() {
		return univName;
	}
	public void setUnivName(String univName) {
		this.univName = univName;
	}
	public Float getOverAll() {
		return overAll;
	}
	public void setOverAll(Float overAll) {
		this.overAll = overAll;
	}

	/* public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Double getLocationX() {
        return locationX;
    }

    public void setLocationX(Double locationX) {
        this.locationX = locationX;
    }

    public Double getLocationY() {
        return locationY;
    }

    public void setLocationY(Double locationY) {
        this.locationY = locationY;
    }*/
}