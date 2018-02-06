/*
MySQL Backup
Source Server Version: 5.7.20
Source Database: db_aidu_info
Date: 2018/2/1 星期四 19:20:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
--  Table structure for `app_key`
-- ----------------------------
DROP TABLE IF EXISTS `app_key`;
CREATE TABLE `app_key` (
  `key` varchar(255) NOT NULL,
  `secrect` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `banner_info`
-- ----------------------------
DROP TABLE IF EXISTS `banner_info`;
CREATE TABLE `banner_info` (
  `banner_id` int(10) DEFAULT NULL,
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `campus`
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus` (
  `campus_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '校区id',
  `campus_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '校区名称',
  `city_id` int(11) NOT NULL,
  `open_time` time NOT NULL,
  `close_time` time NOT NULL,
  `location_x` double DEFAULT NULL,
  `location_y` double DEFAULT NULL,
  `status` smallint(6) NOT NULL DEFAULT '1',
  `close_reason` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `custom_service` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sales` int(11) DEFAULT NULL,
  `min_price` float(11,2) DEFAULT NULL,
  `reach_time` int(11) DEFAULT NULL,
  `pic_url` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `notice` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `deliver` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `delivery_fee` float(6,2) DEFAULT NULL,
  PRIMARY KEY (`campus_id`,`city_id`),
  KEY `campus_city` (`city_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
--  Table structure for `campus_admin`
-- ----------------------------
DROP TABLE IF EXISTS `campus_admin`;
CREATE TABLE `campus_admin` (
  `campus_admin` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `type` smallint(6) DEFAULT NULL,
  `campus_id` varchar(64) NOT NULL,
  `last_login_date` date DEFAULT NULL,
  PRIMARY KEY (`campus_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `campus_admin_type`
-- ----------------------------
DROP TABLE IF EXISTS `campus_admin_type`;
CREATE TABLE `campus_admin_type` (
  `campus_admin_type` smallint(3) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `campus_status`
-- ----------------------------
DROP TABLE IF EXISTS `campus_status`;
CREATE TABLE `campus_status` (
  `status_id` smallint(6) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `city`
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `city_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '城市id',
  `city_name` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`city_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
--  Table structure for `feedback`
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `campus_id` int(11) NOT NULL,
  `phone_id` varchar(20) NOT NULL,
  `date` date NOT NULL,
  `suggestion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `food`
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `food_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `campus_id` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` varchar(255) NOT NULL,
  `discount_price` float(6,2) DEFAULT NULL,
  `img_url` varchar(100) DEFAULT NULL,
  `info` varchar(1000) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL COMMENT '食品状态，1上架，0下架',
  `grade` float(2,0) DEFAULT NULL,
  `food_flag` varchar(255) DEFAULT NULL COMMENT '食品标签',
  `is_discount` smallint(1) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `prime_cost` float(6,2) DEFAULT NULL,
  `sale_number` bigint(11) DEFAULT NULL,
  `tag` smallint(6) NOT NULL DEFAULT '1',
  `food_count` int(11) NOT NULL DEFAULT '500',
  `to_home` smallint(6) NOT NULL DEFAULT '0',
  `message` varchar(255) DEFAULT NULL,
  `home_image` varchar(255) DEFAULT NULL,
  `is_full_discount` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`food_id`,`campus_id`),
  KEY `campus_foodid` (`campus_id`,`food_id`)
) ENGINE=InnoDB AUTO_INCREMENT=333334 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `food_category`
-- ----------------------------
DROP TABLE IF EXISTS `food_category`;
CREATE TABLE `food_category` (
  `category_id` int(11) NOT NULL COMMENT '分类id',
  `campus_id` varchar(64) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `parent_id` int(11) DEFAULT '0',
  `tag` smallint(6) NOT NULL DEFAULT '1',
  `serial` smallint(2) DEFAULT NULL,
  `is_open` smallint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`category_id`,`campus_id`),
  KEY `campus_cateogry` (`campus_id`,`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `food_comment`
-- ----------------------------
DROP TABLE IF EXISTS `food_comment`;
CREATE TABLE `food_comment` (
  `order_id` bigint(40) NOT NULL,
  `campus_id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `date` date DEFAULT NULL,
  `service` smallint(6) DEFAULT NULL,
  `quality` smallint(6) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `tag` smallint(6) DEFAULT '1',
  PRIMARY KEY (`order_id`,`user_id`,`campus_id`),
  KEY `campus_food_comment` (`campus_id`,`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `hot_search`
-- ----------------------------
DROP TABLE IF EXISTS `hot_search`;
CREATE TABLE `hot_search` (
  `hot_id` int(10) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(255) NOT NULL,
  `search_tag` varchar(255) NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `is_display` tinyint(4) NOT NULL DEFAULT '1',
  `campus_id` int(11) NOT NULL,
  PRIMARY KEY (`hot_id`),
  KEY `index_search` (`campus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `news`
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `news_id` bigint(20) NOT NULL,
  `campus_id` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `title` varchar(100) NOT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `content` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`news_id`,`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `orders`
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `order_id` bigint(40) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `campus_id` varchar(64) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` smallint(6) NOT NULL,
  `goods` longtext,
  `message` varchar(255) DEFAULT NULL COMMENT '留言',
  `packing_fee` float(6,2) DEFAULT NULL,
  `delivery_fee` float(6,2) DEFAULT NULL,
  `cut_money` float(6,2) DEFAULT '0.00',
  `coupon_money` float(6,2) DEFAULT '0.00',
  `pay_way` smallint(2) DEFAULT '0',
  `order_price` float(6,2) DEFAULT NULL,
  `pay_price` float(6,2) DEFAULT NULL,
  `addr_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`order_id`,`user_id`),
  KEY `create_time_index` (`create_time`),
  KEY `campus_order` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `order_pay`
-- ----------------------------
DROP TABLE IF EXISTS `order_pay`;
CREATE TABLE `order_pay` (
  `pay_id` smallint(6) NOT NULL,
  `category` varchar(255) NOT NULL,
  PRIMARY KEY (`pay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `preferential`
-- ----------------------------
DROP TABLE IF EXISTS `preferential`;
CREATE TABLE `preferential` (
  `preferential_id` int(11) NOT NULL AUTO_INCREMENT,
  `need_number` int(11) NOT NULL,
  `discount_num` int(11) NOT NULL DEFAULT '0',
  `campus_id` int(11) NOT NULL,
  PRIMARY KEY (`preferential_id`),
  KEY `prefer_campus` (`campus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subtitle` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_userid` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `head_img` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `interest` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `project_comment`
-- ----------------------------
DROP TABLE IF EXISTS `project_comment`;
CREATE TABLE `project_comment` (
  `project_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `comment_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `receiver`
-- ----------------------------
DROP TABLE IF EXISTS `receiver`;
CREATE TABLE `receiver` (
  `user_id` varchar(64) NOT NULL,
  `address_id` varchar(64) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `is_default` smallint(2) DEFAULT NULL,
  `city_name` varchar(255) DEFAULT NULL,
  `district_name` varchar(255) DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  `district_id` int(11) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `latitude` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`address_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_sellers`
-- ----------------------------
DROP TABLE IF EXISTS `tb_sellers`;
CREATE TABLE `tb_sellers` (
  `seller_id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT '商户ID',
  `seller_name` varchar(50) NOT NULL COMMENT '商户店名',
  `sales` int(10) NOT NULL COMMENT '月销量',
  `min_price` int(10) NOT NULL COMMENT '起送价',
  `reach_time` int(10) NOT NULL DEFAULT '0' COMMENT '送达时间',
  `pic_url` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`seller_id`),
  KEY `列 1` (`seller_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='商户表';

-- ----------------------------
--  Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` varchar(64) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `type` smallint(6) DEFAULT NULL,
  `create_time` date NOT NULL COMMENT '注册时间',
  `nickname` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `last_login_date` date DEFAULT NULL,
  `token` varchar(256) DEFAULT NULL,
  `sex` smallint(1) DEFAULT NULL,
  `academy` varchar(50) DEFAULT NULL,
  `qq` varchar(50) DEFAULT NULL,
  `weixin` varchar(50) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `campus_id` int(11) DEFAULT NULL,
  `last_campus` int(11) DEFAULT '1',
  `phone` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `mail` (`mail`),
  UNIQUE KEY `mail_2` (`mail`),
  KEY `campus_index` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `users_type`
-- ----------------------------
DROP TABLE IF EXISTS `users_type`;
CREATE TABLE `users_type` (
  `user_type` smallint(6) NOT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  View definition for `deliver_order`
-- ----------------------------
DROP VIEW IF EXISTS `deliver_order`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `deliver_order` AS select `orders`.`order_id` AS `order_id`,`users`.`nickname` AS `user_name`,`orders`.`campus_id` AS `campus_id`,`orders`.`goods` AS `goods`,`orders`.`message` AS `message`,`orders`.`status` AS `STATUS`,`orders`.`create_time` AS `create_time`,`orders`.`order_price` AS `order_price`,`orders`.`pay_price` AS `pay_price`,`receiver`.`name` AS `name`,`receiver`.`phone` AS `phone`,`receiver`.`address` AS `address`,`receiver`.`detail` AS `detail` from ((`orders` left join `receiver` on(((`orders`.`user_id` = `receiver`.`user_id`) and (`orders`.`addr_id` = `receiver`.`address_id`)))) left join `users` on((`users`.`user_id` = `orders`.`user_id`))) where (`orders`.`status` <> 0);

-- ----------------------------
--  View definition for `project_comment_user`
-- ----------------------------
DROP VIEW IF EXISTS `project_comment_user`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `project_comment_user` AS select `project_comment`.`user_id` AS `user_id`,`project_comment`.`project_id` AS `project_id`,`project_comment`.`comment` AS `comment`,`project_comment`.`comment_time` AS `comment_time`,`users`.`nickname` AS `user_name`,`users`.`img_url` AS `user_head` from (`project_comment` left join `users` on((convert(`project_comment`.`user_id` using utf8) = `users`.`user_id`)));

-- ----------------------------
--  Records 
-- ----------------------------
INSERT INTO `app_key` VALUES ('iOS','2fbf3345716d7ae1840bc11aa09e0b19'), ('server','56846a8a2fee49d14901d39cc48b8b2a'), ('andorid','7ceaa4db3e3be2ec5d66200337f54a1f');
INSERT INTO `banner_info` VALUES ('1','banner1.png','无'), ('2','banner2.png','我'), ('3','banner3.png','我');
INSERT INTO `campus` VALUES ('1','爱渡饮品','1','06:00:00','22:00:00',NULL,NULL,'0','该店铺已暂停营业,对您造成不便深感抱歉','11111111',NULL,'0.00',NULL,'aidu.png','学而思哪个','没有公告','自配送','0.00'), ('1516707220559','林德积木','1','06:00:00','22:00:00',NULL,NULL,'1',NULL,'15656565555',NULL,'0.00','30','http://localhost/JiMuImage/shop/1516787226066-70707567.jpg','南京林业大学学而思小商品市场内','新店开业酬宾','商家配送','2.00');
INSERT INTO `campus_admin` VALUES ('admin','e10adc3949ba59abbe56e057f20f883e','0','1516707220559','2018-01-25'), ('admin_1','e10adc3949ba59abbe56e057f20f883e','1','1','2018-01-23');
INSERT INTO `campus_admin_type` VALUES ('0','商户'), ('1','系统管理员');
INSERT INTO `campus_status` VALUES ('1','nomal');
INSERT INTO `city` VALUES ('1','南京');
INSERT INTO `feedback` VALUES ('0','15151883572','2015-04-30','结算页面怎么没有配送时间？备注功能也没有，下单下的好突然哦'), ('0','15295567537','2015-04-13','啊擦擦擦'), ('0','15295567537','2015-05-06','加油'), ('0','15295567537','2015-05-26','周老板，我要干你'), ('0','18094390667','2015-05-13','不可以退货啊'), ('0','18705199209','2015-04-28','不能取消订单？'), ('0','18705199209','2015-04-29','是否可以加上网上支付功能…不一定每次身上都有零钱给的…'), ('0','18705199233','2015-04-27','东西有点少，希望多加点高大上的，不缺钱，不过，画面挺好的，方便快捷'), ('1','18896554880','2015-07-20','好吃'), ('1','18896554880','2015-07-22','好吃'), ('1','18860902573','2015-08-19','你们的网站做得很好'), ('1','18896554888','2015-08-19',''), ('1','18896554880','2015-08-19','yyy'), ('1','18260108169','2015-08-27','test'), ('1','18896554880','2015-08-28','好吃'), ('1','18896554880','2015-08-28','好吃&#39;'), ('1','18035735959','2015-09-11','你好'), ('1','18896554880','2015-09-11','好吃'), ('1','18896554880','2015-09-11','aaaaaaaa'), ('1','18896554880','2015-09-11','test1'), ('1','18896554880','2015-09-11','wwww'), ('1','18896554880','2015-09-11','12weEWQE'), ('1','18896554880','2015-09-11','ss'), ('1','18896554880','2015-09-11','For优，为你，为更好的生活'), ('1','18896554880','2015-09-11','123'), ('1','18896554880','2015-09-15','aaa'), ('1','18260108169','2015-09-15','12357886'), ('1','18013116680','2015-09-15','453062821  垃圾咯考虑图就拒绝啦咯'), ('1','18896554880','2015-09-17','难用'), ('2','18896554880','2015-10-09','牛严'), ('1','18896558935','2015-10-09','你好，我是朱丽叶，你是谁呀'), ('2','18896554880','2015-10-09','啊'), ('1','18896558935','2015-10-09','网好慢啊'), ('1','18762885079','2015-10-09','爸爸'), ('1','18762885079','2015-10-09','爸爸'), ('1','18035735959','2015-10-09','我\n但'), ('1','18035735959','2015-10-09','巴巴爸爸巴巴爸爸啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊巴巴爸爸巴巴爸爸啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊'), ('1','18035735959','2015-10-09','？'), ('1','18260108169','2015-10-12',''), ('1','18260108169','2015-10-12','ss'), ('1','18862342354','2015-10-16','好棒'), ('1','18862342354','2015-10-16','好棒'), ('1','18896558935','2015-10-18','嗨');
INSERT INTO `food` VALUES ('1','1','香蕉牛奶','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/1515914743343-280344262.jpg',NULL,'2018-01-14 15:25:43','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰；拉肚：维拉，重辣',NULL,'4711',NULL,'0','1','1111','0','',NULL,'0'), ('1','1516707220559','苹果汁','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/1516728941055-488940380.jpg',NULL,'2018-01-24 01:35:41','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'4',NULL,'0','0','666','0','111',NULL,'0'), ('2','1','火龙果牛奶','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/15159147824771291095412.jpg',NULL,'2018-01-14 15:26:22','1',NULL,'温度：常温，热，少冰',NULL,'4711',NULL,'0','1','333','0','',NULL,'0'), ('2','1516707220559','草莓3','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/1516729566094-709900922.jpg',NULL,'2018-01-24 01:47:15','1',NULL,'温度：常温，热，少冰',NULL,'4',NULL,'0','1','666','0','草莓',NULL,'0'), ('3','1516707220559','哈哈哈','12',NULL,'http://localhost/JiMuImage/food/1516788027012196938187.jpg',NULL,'2018-01-24 18:00:27','1',NULL,'甜度：微糖，中糖，多糖',NULL,'4',NULL,'0','1','555','0','哈哈哈',NULL,'0'), ('10101','1','无属性商品','12',NULL,'http://localhost/JiMuImage/food/1515923502745-845072517.jpg',NULL,'2018-01-14 17:51:43','1',NULL,'',NULL,'4712',NULL,'0','1','666','0','',NULL,'0'), ('10102','1','红豆奶茶','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/1515914804501-1658683573.jpg',NULL,'2018-01-14 15:26:45','1',NULL,'',NULL,'4712',NULL,'0','1','444','0','',NULL,'0'), ('333333','1','牛奶','中杯：12；大杯：14',NULL,'http://localhost/JiMuImage/food/1516695189194-63054039.jpg',NULL,'2018-01-23 16:13:09','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'4711',NULL,'0','1','5555','0','我',NULL,'0');
INSERT INTO `food_category` VALUES ('1','1516707220559','牛奶',NULL,'0','0',NULL,'1'), ('2','1516707220559','水果',NULL,'0','0',NULL,'1'), ('3','1516707220559','牛奶',NULL,'0','0',NULL,'1'), ('4','1516707220559','果汁1',NULL,'0','1',NULL,'1'), ('5','1516707220559','奶盖',NULL,'0','1',NULL,'1'), ('6','1516707220559','酸奶',NULL,'0','0',NULL,'1'), ('7','1516707220559','抹茶',NULL,'0','0',NULL,'1'), ('4711','1','水果牛奶',NULL,'0','1',NULL,'1'), ('4712','1','牛奶奶茶',NULL,'0','1',NULL,'1');
INSERT INTO `food_comment` VALUES ('1516811641102','1516707220559','oJOT00FckX0Ts9chCtN1KavrZfrA','2018-01-25','3','3','','1'), ('1516884699827','1516707220559','oJOT00FckX0Ts9chCtN1KavrZfrA','2018-01-25','5','5','号','1'), ('1516884723593','1516707220559','oJOT00FckX0Ts9chCtN1KavrZfrA','2018-01-31','5','3','ddd','1');
INSERT INTO `hot_search` VALUES ('1','新鲜牛奶','牛奶',NULL,'1','1'), ('3','美味面包','面包',NULL,'1','1'), ('4','休闲零食','零食',NULL,'1','1'), ('5','可口饮料','饮料',NULL,'1','1'), ('6','家政服务','家政',NULL,'1','1'), ('7','新鲜水果','水果',NULL,'1','2'), ('8','美味奶酪','奶酪','1440771281755','1','1');
INSERT INTO `news` VALUES ('1443714657869','1','2015-10-01 23:50:57','','http://pic1.win4000.com/wallpaper/3/53ad42ed97f0a.jpg',''), ('1443714679985','1','2015-10-01 23:51:19','','http://img1.3lian.com/2015/a1/18/d/150.jpg',''), ('1443714725063','1','2015-10-01 23:52:05','','http://bizhi.4493.com/uploads/allimg/140924/4-140924104310.jpg',''), ('1443715227770','2','2015-10-02 00:00:27','','http://www.bz55.com/uploads/allimg/130207/1-13020F92228-50.jpg',''), ('1443715243855','2','2015-10-02 00:00:43','','http://pic2.ooopic.com/11/77/11/38bOOOPICdf_1024.jpg',''), ('1443715273675','2','2015-10-02 00:01:13','','http://pic41.nipic.com/20140519/18505720_094637688147_2.jpg','');
INSERT INTO `orders` VALUES ('1516884699827','oJOT00FckX0Ts9chCtN1KavrZfrA','1516707220559','2018-01-25 20:51:40','4','[{\"goods_id\":\"3\",\"goods_name\":\"哈哈哈\",\"select_property\":\"微糖\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]','','0.00','2.00','0.00','0.00','0','12.00','14.00','1'), ('1516884723593','oJOT00FckX0Ts9chCtN1KavrZfrA','1516707220559','2018-01-25 20:52:04','8','[{\"goods_id\":\"3\",\"goods_name\":\"哈哈哈\",\"select_property\":\"微糖\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]','','0.00','2.00','0.00','0.00','0','12.00','14.00','1');
INSERT INTO `order_pay` VALUES ('1','支付宝支付'), ('2','微信支付');
INSERT INTO `preferential` VALUES ('1','20','3','1'), ('2','30','5','1'), ('4','20','6','2'), ('5','10','2','32'), ('6','15','3','32'), ('7','20','5','32'), ('8','40','8','32'), ('9','50','20','32');
INSERT INTO `project` VALUES ('1517387332720','1','1','2018-01-31 16:28:53','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517387365815','2','2','2018-01-31 16:29:26','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517388689415','3','3','2018-01-31 16:51:29','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517388909249','4','4','2018-01-31 16:55:09','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517390863327','5','5','2018-01-31 17:27:43','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517390956537','6','6','2018-01-31 17:29:17','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517391653766','7','7','2018-01-31 17:40:54','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517391803532','8','8','2018-01-31 17:43:24','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517393440727','9','9','2018-01-31 18:10:41','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517393489920','10','10','2018-01-31 18:11:30','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517393515053','11','11','2018-01-31 18:11:55','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517393956121','12','12','2018-01-31 18:19:16','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517394014692','13','13','2018-01-31 18:20:15','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517394050054','14','14','2018-01-31 18:20:50','oJOT00FckX0Ts9chCtN1KavrZfrA','','0'), ('1517394187682','15','15','2018-01-31 18:23:07','oJOT00FckX0Ts9chCtN1KavrZfrA','http://localhost/JiMuImage/project/1517394187682.jpg','0');
INSERT INTO `project_comment` VALUES ('1517394187682','oJOT00FckX0Ts9chCtN1KavrZfrA','你好啊','2018-02-01 01:36:35'), ('1517394187682','oJOT00FckX0Ts9chCtN1KavrZfrA','你好啊','2018-02-01 18:49:22');
INSERT INTO `receiver` VALUES ('oJOT00FckX0Ts9chCtN1KavrZfrA','1516888904123','18666662256','222','南京市政府','',NULL,'南京市','玄武区','320100','320102','118.8028913066771','32.0647345948144');
INSERT INTO `tb_sellers` VALUES ('0000000001','爱渡饮品','200','0','30','aidu.png'), ('0000000002','创世纪','10','0','30','aidu.png');
INSERT INTO `users` VALUES ('oJOT00FckX0Ts9chCtN1KavrZfrA',NULL,'2','2018-01-18','xxx、defined','https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epuibz5Qwf2IYwnGBLwbWsn8aRHXcrYKvQoVqS5Ls3fnksQfiaQMz9nJLIwJLzpBFoIPMYDKLQdDaPg/0','2018-02-01',NULL,'1',NULL,NULL,NULL,NULL,NULL,'1',NULL);
INSERT INTO `users_type` VALUES ('0','系统管理员'), ('1','商户'), ('2','普通用户');
