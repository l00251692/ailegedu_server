package com.changyu.foryou.model;

import java.util.Date;
import java.util.List;

public class DeliverOrder {

	private Long orderId;
	
	private String userName;

	private String campusId;
	
	private String goods;
	
	private String message;
	
	private Date createTime;

	private Short status;

	private Float orderPrice;
	
	private Float payPrice;

	private String name;   //配送地址

	private String phone;  

	private String address;
	
	private String detail;

	
	public Long getOrderId() {
		return orderId;
	}


	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getCampusId() {
		return campusId;
	}


	public void setCampusId(String campusId) {
		this.campusId = campusId;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public List<DeliverChildOrder> getOrderList() {
		return orderList;
	}


	public void setOrderList(List<DeliverChildOrder> orderList) {
		this.orderList = orderList;
	}


	public String getGoods() {
		return goods;
	}


	public void setGoods(String goods) {
		this.goods = goods;
	}


	private List<DeliverChildOrder> orderList;

	



}