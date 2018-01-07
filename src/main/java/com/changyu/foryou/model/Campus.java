package com.changyu.foryou.model;

import java.sql.Time;


public class Campus {
	private Integer campusId;

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
	
	private int sales;
	private int min_price;
	private int reach_time;
	private String pic_url;
	

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
	public int getMin_price() {
		return min_price;
	}
	public void setMin_price(int min_price) {
		this.min_price = min_price;
	}
	public int getReach_time() {
		return reach_time;
	}
	public void setReach_time(int reach_time) {
		this.reach_time = reach_time;
	}

	public Integer getCampusId() {
		return campusId;
	}

	public void setCampusId(Integer campusId) {
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