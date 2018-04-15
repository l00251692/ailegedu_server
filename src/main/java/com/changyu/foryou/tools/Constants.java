package com.changyu.foryou.tools;

public  class Constants {
	final static public String STATUS = "status";
	final static public String SUCCESS = "success";
	final static public String FAILURE = "failure";
	public static final String MESSAGE = "message";
	//public static final String localIp = "http://www.enjoyfu.com.cn:7777/ForyouImage";
	//public static final String localIp = "https://localhost/JiMuImage"; //存放上传的图片的服务器JiMuImage为上传图片时创建的目录
	public static final String localIp = "https://www.ailogic.xin/JiMuImage";
	public static final String REFUND_KEY_PATH = "classpath:apiclient_cert.p12";
	
	
    public static String appId="app_La1y14yrPa10SeHS";
    public static String apiKey="sk_live_vBNcIdIOKPBJEU9YOq3C02PU";
    public static String mchId="1111";
    public static String mchKey="JIMU";
    
    public static final String REDISPREFIX = "orderId=";
    
    public static final int COUNTDELAY = 10; //服务器返回给前台的剩余时间增加10s防止前台时间计数器到了后后端还未及时更细数据
    public static final int PAYDELAYTIME = 60;//600; //300即为等待5min 300
    public static final int WAITRCVTIME = 60;//300;  //300 商家接单时间
    public static final int WAITCONFIRM = 60;//24*60*60; //24小时自动确认收货 24*60*60
    public static final int WAITREVIEW  = 60;//2*24*60*60; //2天自动评价为5星  2*24*60*60
    
    public static final Long REDISSAVETIME =  365*24*60l;   //缓存一年，分钟
    
    public static final short ORDER_CREATE = 1;
    public static final short ORDER_PAY_SUCCESS = 2;
    public static final short ORDER_RECEIVE = 3;
    public static final short ORDER_SUCCESS = 4;
    public static final short ORDER_CANCEL = 5;
    public static final short ORDER_REFUND = 6;
    public static final short ORDER_REFUND_SUCCESS = 7;
    public static final short ORDER_REVIEWED = 8;
    public static final short ORDER_REJECT = 9;
    
    
    //七牛云相关配置
    public  static final String QINIU_AK = "YxW2_V1FQj2yOYNlHlzhHiAHI4cwWkPWNIxiT_ae";
    public  static final String QINIU_SK = "1d1Uo7S3x7qJXSL8ljbW46b2dKgWL0fPjHxG4PdI";
    public  static final String QINIU_BUCKET = "lanjing";
    public static final String QINIU_IP = "https://img.ailogic.xin/"; //采用绑定的域名，否则真机上不显示
  
    //public static final String QINIU_IP = "http://p6sm3pvn3.bkt.clouddn.com/";
	
    
}
