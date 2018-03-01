package com.changyu.foryou.model;

/**
 * 微信基础配置上下文
 * @Author wuwz
 * @TypeName WeChatContext
 */
public class WeChatContext {
	private WeChatContext() {}
	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getMchKey() {
		return mchKey;
	}

	public void setMchKey(String mchKey) {
		this.mchKey = mchKey;
	}
	private static WeChatContext context;
	private String appId;
	private String appSecrct;
	private String validateUrl;
	private String token;
	private String templateIdPaySuccess;
	private String templateIdPayFail;
	private String templateIdPayCancel;
	private String mchId;
	private String mchKey;
	
	public static WeChatContext getInstance() {
		if(context == null) {
			context = new WeChatContext();
		}
		return context;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecrct() {
		return appSecrct;
	}

	public void setAppSecrct(String appSecrct) {
		this.appSecrct = appSecrct;
	}

	public String getValidateUrl() {
		return validateUrl;
	}

	public void setValidateUrl(String validateUrl) {
		this.validateUrl = validateUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getTemplateIdPaySuccess() {
		return templateIdPaySuccess;
	}
	public void setTemplateIdPaySuccess(String templateIdPaySuccess) {
		this.templateIdPaySuccess = templateIdPaySuccess;
	}
	public String getTemplateIdPayFail() {
		return templateIdPayFail;
	}
	public void setTemplateIdPayFail(String templateIdPayFail) {
		this.templateIdPayFail = templateIdPayFail;
	}
	public String getTemplateIdPayCancel() {
		return templateIdPayCancel;
	}
	public void setTemplateIdPayCancel(String templateIdPayCancel) {
		this.templateIdPayCancel = templateIdPayCancel;
	}

}