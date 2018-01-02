package com.changyu.foryou.service;

import java.util.Date;
import java.util.List;

import com.changyu.foryou.model.Sellers;

public interface SellerService {

	Sellers selectByCampusAdmin(String campusAdmin);

	void updateLastLoginTime(Date date, String campusAdmin);

	void addASeller(Sellers seller);
	
	List<Sellers> getAllSellers();
}
