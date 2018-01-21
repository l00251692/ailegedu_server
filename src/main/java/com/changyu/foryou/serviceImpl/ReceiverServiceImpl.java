package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.mapper.ReceiverMapper;
import com.changyu.foryou.model.Receiver;
import com.changyu.foryou.model.ReceiverKey;
import com.changyu.foryou.service.ReceiverService;


@Service("receiverService")
public class ReceiverServiceImpl implements ReceiverService {
    private ReceiverMapper receiverMapper;
    
	public ReceiverMapper getReceiverMapper() {
		return receiverMapper;
	}
	
	@Autowired
	public void setReceiverMapper(ReceiverMapper receiverMapper) {
		this.receiverMapper = receiverMapper;
	}

	public int deleteByPrimaryKey(String userId, String addressId) {
		return receiverMapper.deleteByPrimaryKey(userId,addressId);
		
	}

	public int insertSelective(Map<String, Object> paramMap) {
		return receiverMapper.insertSelective(paramMap);
	}

	public Receiver selectByPrimaryKey(Map<String, Object> paramMap) {
		return receiverMapper.selectByPrimaryKey(paramMap);
	}

	public int updateByPrimaryKeySelective(Map<String, Object> paramMap) {
		return receiverMapper.updateByPrimaryKeySelective(paramMap);
	}

	public int setDefaultAddress(String phone,String rank) {
		return receiverMapper.setDefaultAddress(phone,rank);
	}

	public int getReceiverCounts(String phoneId) {
		
		return receiverMapper.getReceiverCounts(phoneId);
	}

	public List<Receiver> selectByPhoneId(String phoneId) {
		return receiverMapper.selectByPhoneId(phoneId);
	}

	public int insert(Receiver receiver) {
		return receiverMapper.insert(receiver);
	}

	public int setReceiverTag(String phoneId) {
		return receiverMapper.setReceiverTag(phoneId);
	}

	@Override
	public Receiver getReceiver(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return receiverMapper.getReceiver(paramMap);
	}
	

	public Receiver getReceiverDefault(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return receiverMapper.getReceiverDefault(paramMap);
	}
	
	public List<Receiver> getReceiverList(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return receiverMapper.getReceiverList(paramMap);
	}


}
