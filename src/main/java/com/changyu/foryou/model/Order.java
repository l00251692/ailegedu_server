package com.changyu.foryou.model;

import java.util.Date;

public class Order {
    private Long orderId;
    
    private Integer campusId;
    
    private String userId;

    private Date createTime;

    //0:无效，1:准订单  ，2：待支付 ，3：已支付， 4：配送中， 5：配送完成
    private Short status;

    private String goods;
    
    private String message;
    
    private String addrId;
    
    private Float cutMoney;
    private Float couponMoney;
    
    private Short payWay;
    private Float orderPrice;
    private Float payPrice;
    
    
    public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getCampusId() {
		return campusId;
	}

	public void setCampusId(Integer campusId) {
		this.campusId = campusId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAddrId() {
		return addrId;
	}

	public void setAddrId(String addrId) {
		this.addrId = addrId;
	}

	public Float getCutMoney() {
		return cutMoney;
	}

	public void setCutMoney(Float cutMoney) {
		this.cutMoney = cutMoney;
	}

	public Float getCouponMoney() {
		return couponMoney;
	}

	public void setCouponMoney(Float couponMoney) {
		this.couponMoney = couponMoney;
	}

	public Float getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Float orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Float getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Float payPrice) {
		this.payPrice = payPrice;
	}

	public Short getPayWay() {
		return payWay;
	}

	public void setPayWay(Short payWay) {
		this.payWay = payWay;
	}


	public Order(){
    	
    }
    
    public Order(Integer campusId2,String userId2, String addrId2, String goods2) {
    	campusId = campusId2;
    	userId=userId2;
    	addrId=addrId2;
    	goods = goods2;
    	createTime=new Date();
    	status=1; //准订单
    	orderId=createTime.getTime();
	}

	
}