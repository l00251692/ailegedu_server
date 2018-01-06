package com.changyu.foryou.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.changyu.foryou.model.Sellers;

public interface SellerMapper {

    Sellers selectByCampusAdmin(String campusAdmin);

    void updateLastLoginTime(@Param(value = "date") Date date, @Param(value = "campusAdmin") String campusAdmin);

    int insertSellective(Sellers seller);
    
    List<Sellers> getAllSellers();
    
    Sellers getSellerInfo(Integer seller_id);

}
