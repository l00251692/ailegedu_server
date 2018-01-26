package com.changyu.foryou.service;


import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Receiver;

public interface ReceiverService {
	int deleteByPrimaryKey(Map<String, Object> paramMap);

	int insertSelective(Map<String, Object> paramMap);

	Receiver selectByPrimaryKey(Map<String, Object> paramMap);

	int updateByPrimaryKeySelective(Map<String, Object> paramMap);

	int setDefaultAddress(String phone,String rank);

	int getReceiverCounts(String phoneId);

	List<Receiver> selectByPhoneId(String phoneId);

	int insert(Receiver receiver);

	int setReceiverTag(String phoneId);

	Receiver getReceiver(Map<String, Object> paramMap);
	
	Receiver getReceiverDefault(Map<String, Object> paramMap);
	
	List<Receiver> getReceiverList(Map<String, Object> paramMap);
}
