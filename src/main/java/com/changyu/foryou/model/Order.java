package com.changyu.foryou.model;

import java.util.Date;

public class Order {
    private Long orderId;
    
    private String campusId;
    
    private String userId;

    private Date createTime;

    //0:无效，1:订单待支付  ，2：等待商家接单 ，3：订单进行中， 4：订单已完成， 5：订单取消,6 :退款中,7:已退款，8：订单完成评论
    private Short status;

    private String goods;
    
    private String message;
    
    private String addrId;
    
    private Float packingFee;
    private Float deliveryFee;
    
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
    
    public Order(String campusId2,String userId2, String goods2,Float deliveryFee2 ) {
    	campusId = campusId2;
    	userId=userId2;
    	goods = goods2;
    	deliveryFee = deliveryFee2;
    	createTime=new Date();
    	status=1; //准订单
    	orderId=createTime.getTime();
	}

	public Float getPackingFee() {
		return packingFee;
	}

	public void setPackingFee(Float packingFee) {
		this.packingFee = packingFee;
	}

	public Float getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(Float deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	
}