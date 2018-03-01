package com.changyu.foryou.listener;

import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.changyu.foryou.model.WeChatContext;

/**
 * 上下文监听
 * @Author wuwz
 * @TypeName InitContextListener
 */
public class InitContextListener implements ServletContextListener {
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//初始化微信上下文
		InitWeChatContext();
	}

	private void InitWeChatContext() {
		ResourceBundle wb = ResourceBundle.getBundle("wechat");
		WeChatContext.getInstance().setAppId(wb.getString("appID"));
		WeChatContext.getInstance().setAppSecrct(wb.getString("appSecrct"));
		WeChatContext.getInstance().setValidateUrl(wb.getString("validateUrl"));
		WeChatContext.getInstance().setToken(wb.getString("WeChatToken"));
		WeChatContext.getInstance().setTemplateIdPaySuccess(wb.getString("TemplateIdPaySuccess"));
		WeChatContext.getInstance().setTemplateIdPayFail(wb.getString("TemplateIdPayFail"));
		WeChatContext.getInstance().setTemplateIdPayCancel(wb.getString("TemplateIdPayCancel"));
		WeChatContext.getInstance().setMchId(wb.getString("mchId"));
		WeChatContext.getInstance().setMchKey(wb.getString("mchKey"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}