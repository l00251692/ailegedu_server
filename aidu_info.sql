/*
MySQL Backup
Source Server Version: 5.7.21
Source Database: db_aidu_info
Date: 2018-05-07 20:55:58
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
  `univ_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `over_all` float(6,1) DEFAULT '5.0',
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
  `records` varchar(255) DEFAULT NULL,
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
  `concat` varchar(64) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_userid` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `head_img` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `interest` int(10) DEFAULT NULL,
  `deadline_time` date DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `add_imgs` text CHARACTER SET utf8 COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `project_comment`
-- ----------------------------
DROP TABLE IF EXISTS `project_comment`;
CREATE TABLE `project_comment` (
  `project_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `comment_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_read` tinyint(4) DEFAULT '0'
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
--  Table structure for `university`
-- ----------------------------
DROP TABLE IF EXISTS `university`;
CREATE TABLE `university` (
  `provice_id` int(4) DEFAULT NULL,
  `provice` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `univ_id` int(6) DEFAULT NULL,
  `university` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `choose_flag` int(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` varchar(64) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `type` smallint(6) DEFAULT NULL,
  `create_time` date NOT NULL COMMENT '注册时间',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `last_login_date` date DEFAULT NULL,
  `token` varchar(256) DEFAULT NULL,
  `sex` smallint(1) DEFAULT NULL,
  `academy` varchar(50) DEFAULT NULL,
  `qq` varchar(50) DEFAULT NULL,
  `weixin` varchar(50) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `campus_id` varchar(64) DEFAULT NULL,
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
--  Table structure for `user_focus_project`
-- ----------------------------
DROP TABLE IF EXISTS `user_focus_project`;
CREATE TABLE `user_focus_project` (
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `project_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  View definition for `deliver_order`
-- ----------------------------
DROP VIEW IF EXISTS `deliver_order`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `deliver_order` AS select `orders`.`order_id` AS `order_id`,`users`.`nickname` AS `user_name`,`orders`.`campus_id` AS `campus_id`,`orders`.`goods` AS `goods`,`orders`.`message` AS `message`,`orders`.`status` AS `STATUS`,`orders`.`create_time` AS `create_time`,`orders`.`order_price` AS `order_price`,`orders`.`pay_price` AS `pay_price`,`receiver`.`name` AS `name`,`receiver`.`phone` AS `phone`,`receiver`.`address` AS `address`,`receiver`.`detail` AS `detail` from ((`orders` left join `receiver` on(((`orders`.`user_id` = `receiver`.`user_id`) and (`orders`.`addr_id` = `receiver`.`address_id`)))) left join `users` on((`users`.`user_id` = `orders`.`user_id`))) where (`orders`.`status` <> 0);

-- ----------------------------
--  View definition for `project_comment_user`
-- ----------------------------
DROP VIEW IF EXISTS `project_comment_user`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `project_comment_user` AS select `project_comment`.`user_id` AS `user_id`,`project_comment`.`project_id` AS `project_id`,`project_comment`.`comment` AS `comment`,`project_comment`.`comment_time` AS `comment_time`,`users`.`nickname` AS `user_name`,`users`.`img_url` AS `user_head`,`project_comment`.`is_read` AS `is_read`,`project`.`title` AS `project_title`,`project`.`create_userid` AS `owner_id` from ((`project_comment` left join `users` on((convert(`project_comment`.`user_id` using utf8) = `users`.`user_id`))) left join `project` on((convert(`project_comment`.`project_id` using utf8) = `project`.`project_id`)));

-- ----------------------------
--  Records 
-- ----------------------------
INSERT INTO `app_key` VALUES ('iOS','2fbf3345716d7ae1840bc11aa09e0b19'), ('server','56846a8a2fee49d14901d39cc48b8b2a'), ('andorid','7ceaa4db3e3be2ec5d66200337f54a1f');
INSERT INTO `banner_info` VALUES ('1','https://img.ailogic.xin/banner1.png','无'), ('2','https://img.ailogic.xin/banner2.png','我'), ('3','https://img.ailogic.xin/banner3.png','我');
INSERT INTO `campus` VALUES ('1525677891749','漫时光','1','10:30:00','21:00:00',NULL,NULL,'1',NULL,'18261149716','0','15.00','30','https://img.ailogic.xin/shop_4cdc998ca2424bb9a25763c201e8df90','南京林业大学学而思小商品市场内','从前慢，一生只够爱一个人','商家自配送','2.00','南京林业大学','5.0'), ('1525678213948','深夜食堂（9栋）','1','19:00:00','00:00:00',NULL,NULL,'1',NULL,'18261149716','0','15.00','20','https://img.ailogic.xin/shop_06239827bce5450dab9c2aa8b03ffaf0','南京林业大学学生宿舍公寓9栋','夜深了，饿了有我','商家配送','2.00','南京林业大学',NULL);
INSERT INTO `campus_admin` VALUES ('18261149716','e10adc3949ba59abbe56e057f20f883e','0','1525677891749','2018-05-07'), ('18551410942','e10adc3949ba59abbe56e057f20f883e','0','1525678213948','2018-01-25'), ('admin','e10adc3949ba59abbe56e057f20f883e','1','1','2018-05-07');
INSERT INTO `campus_admin_type` VALUES ('0','商户'), ('1','系统管理员');
INSERT INTO `campus_status` VALUES ('1','nomal');
INSERT INTO `city` VALUES ('1','南京');
INSERT INTO `food` VALUES ('1','1525677891749','鲜榨西瓜汁','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_15256778917492981b62fba2c4e8aa721d18baf36c383',NULL,'2018-05-07 15:49:34','1',NULL,'温度：常温，加冰',NULL,'1',NULL,'5','1','1000','0','香甜西瓜汁',NULL,'0'), ('2','1525677891749','鲜榨雪梨汁','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_152567789174978433d0a3e0b481a923f49e710bce436',NULL,'2018-05-07 15:51:14','1',NULL,'温度：常温，加冰',NULL,'1',NULL,'1','1','1000','0','',NULL,'0'), ('3','1525677891749','奇异果雪梨汁','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_152567789174949edd7705fd84db3a9547e31975e2d0c',NULL,'2018-05-07 15:56:04','1',NULL,'温度：常温，加冰',NULL,'1',NULL,'1','1','1000','0','',NULL,'0'), ('4','1525677891749','芒果雪梨汁','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_1525677891749be694cbf9c2a42edbc498b877732d08d',NULL,'2018-05-07 15:58:44','1',NULL,'温度：常温，加冰',NULL,'1',NULL,'0','1','1000','0','',NULL,'0'), ('5','1525677891749','香橙雪梨汁','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_15256778917490d51ef2f0c6b4776b231f168613ada96',NULL,'2018-05-07 15:59:15','1',NULL,'温度：常温，加冰',NULL,'1',NULL,'0','1','1000','0','',NULL,'0'), ('6','1525677891749','鲜榨菠萝汁','中杯：13；大杯：15',NULL,'https://img.ailogic.xin/food_1525677891749b030d61f8e214334b714d670ef305411',NULL,'2018-05-07 15:59:50','1',NULL,'温度：常温，少冰',NULL,'1',NULL,'0','1','1000','0','',NULL,'0'), ('7','1525677891749','鲜榨橙汁','中杯：14；大杯：16',NULL,'https://img.ailogic.xin/food_15256778917493df288659b144d73beafa0544e6b885b',NULL,'2018-05-07 16:00:36','1',NULL,'温度：常温，少冰',NULL,'1',NULL,'0','1','1000','0','',NULL,'0'), ('8','1525677891749','香蕉奶昔','中杯：8；大杯：10',NULL,'https://img.ailogic.xin/food_152567789174994c445ef3f2940f19c4b9d68f337bd6b',NULL,'2018-05-07 16:03:17','1',NULL,'',NULL,'2',NULL,'3','1','1000','0','',NULL,'0'), ('9','1525677891749','芒果奶昔','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_1525677891749a3b3ff3a5eb8471dae775bed692af124',NULL,'2018-05-07 16:03:48','1',NULL,'',NULL,'2',NULL,'0','1','1000','0','',NULL,'0'), ('10','1525677891749','草莓奶昔','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_152567789174923ca5a9796aa457397c6833d95c007ad',NULL,'2018-05-07 16:36:14','1',NULL,'',NULL,'2',NULL,'0','1','1000','0','',NULL,'0'), ('11','1525677891749','香蕉牛奶','中杯：8；大杯：10',NULL,'https://img.ailogic.xin/food_152567789174903c4206db5fb45b2abb310c42d1ebef9',NULL,'2018-05-07 16:09:15','1',NULL,'温度：常温，热',NULL,'3',NULL,'1','1','1000','0','',NULL,'0'), ('12','1525677891749','芒果牛奶','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_15256778917499f0ebd70a43e4dfba9c39e63670f455b',NULL,'2018-05-07 16:09:58','1',NULL,'温度：常温，热',NULL,'3',NULL,'1','1','1000','0','',NULL,'0'), ('13','1525677891749','椰奶西米露','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_1525677891749c4e4b8a9edae4fc0be1ec877b862503e',NULL,'2018-05-07 16:11:06','1',NULL,'温度：冰',NULL,'3',NULL,'1','1','1000','0','',NULL,'0'), ('14','1525677891749','芒果椰奶西米露','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_152567789174958da5ba54d1b43be82b173a6b9956099',NULL,'2018-05-07 16:37:11','1',NULL,'温度：冰',NULL,'3',NULL,'1','1','1000','0','',NULL,'0'), ('15','1525677891749','杂果椰奶西米露','中杯：13；大杯：15',NULL,'https://img.ailogic.xin/food_152567789174983838836e71d4b6bafcae7cae649a33e',NULL,'2018-05-07 16:12:27','1',NULL,'温度：冰',NULL,'3',NULL,'0','1','1000','0','',NULL,'0'), ('16','1525677891749','香蕉优格','中杯：10；大杯：13',NULL,'https://img.ailogic.xin/food_1525677891749673eacb4de034eaf8fc3cb73cab48c41',NULL,'2018-05-07 16:16:22','1',NULL,'',NULL,'4',NULL,'0','1','1000','0','',NULL,'0'), ('17','1525677891749','芒果优格','中杯：12；大杯：15',NULL,'https://img.ailogic.xin/food_15256778917491e97bbac526e4666ba1bc70c49de4270',NULL,'2018-05-07 16:16:50','1',NULL,'',NULL,'4',NULL,'0','1','1000','0','',NULL,'0'), ('18','1525677891749','菠萝优格','中杯：12；大杯：15',NULL,'https://img.ailogic.xin/food_15256778917497ec9d9586a8b449ea628e3fb7dca169c',NULL,'2018-05-07 16:17:19','1',NULL,'',NULL,'4',NULL,'0','1','1000','0','',NULL,'0'), ('19','1525677891749','草莓优格','中杯：12；大杯：15',NULL,'https://img.ailogic.xin/food_1525677891749c0943b4978c84814b2baf81328c729bd',NULL,'2018-05-07 16:37:40','1',NULL,'',NULL,'4',NULL,'0','1','1000','0','',NULL,'0'), ('20','1525677891749','西瓜优格','中杯：12；大杯：15',NULL,'https://img.ailogic.xin/food_1525677891749e068be84ae9e4be9aa565668bd0963bd',NULL,'2018-05-07 16:37:56','1',NULL,'',NULL,'4',NULL,'0','1','1000','0','',NULL,'0'), ('21','1525677891749','柠檬红茶','中杯：8；大杯：10',NULL,'https://img.ailogic.xin/food_15256778917497ce25b506bc34be8a64f54d71f7a4267',NULL,'2018-05-07 16:22:00','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('22','1525677891749','柠檬绿茶','中杯：8；大杯：10',NULL,'https://img.ailogic.xin/food_15256778917490afcc3bbabc649b98d35acad81b4c4c2',NULL,'2018-05-07 16:22:36','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('23','1525677891749','金桔柠檬','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_1525677891749a26d9f49a5814d30a22ade8745f7629f',NULL,'2018-05-07 16:23:09','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('24','1525677891749','原味奶茶','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_1525677891749e77ebe1f038f470ebcbe73e34cb42916',NULL,'2018-05-07 16:23:38','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('25','1525677891749','茉香奶绿','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_15256778917493b881be872fe4589a30f6026011d7981',NULL,'2018-05-07 16:24:24','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('26','1525677891749','茉莉奶茶','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_152567789174969eb56f20b364c31955473f8a33e0f76',NULL,'2018-05-07 16:24:48','1',NULL,'甜度：微糖，中糖，多糖；温度：常温，热，少冰',NULL,'5',NULL,'0','1','1000','0','',NULL,'0'), ('27','1525677891749','热牛奶','中杯：8；大杯：10',NULL,'https://img.ailogic.xin/food_1525677891749320f4725c91b4c828bcd2d212430f902',NULL,'2018-05-07 16:29:15','1',NULL,'甜度：微糖，中糖，多糖',NULL,'6',NULL,'0','1','1000','0','',NULL,'0'), ('28','1525677891749','红豆红枣牛奶','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_15256778917491947c85e8f134dca9d0776c14a4f477e',NULL,'2018-05-07 16:30:02','1',NULL,'甜度：微糖，中糖，多糖',NULL,'6',NULL,'0','1','1000','0','',NULL,'0'), ('29','1525677891749','燕麦红枣牛奶','中杯：10；大杯：12',NULL,'https://img.ailogic.xin/food_1525677891749ca953f7cefb64d88ace88157746e5fe7',NULL,'2018-05-07 16:30:30','1',NULL,'甜度：微糖，中糖，多糖；',NULL,'6',NULL,'0','1','1000','0','',NULL,'0'), ('30','1525677891749','热可可','中杯：12；大杯：14',NULL,'https://img.ailogic.xin/food_152567789174962bcd9dc5ce445a48ca47d0c73970b0c',NULL,'2018-05-07 16:30:47','1',NULL,'',NULL,'6',NULL,'0','1','1000','0','',NULL,'0'), ('31','1525677891749','珍珠','2',NULL,'https://img.ailogic.xin/food_15256778917499652b413bf324166beff049bb0550db8',NULL,'2018-05-07 16:35:25','1',NULL,'',NULL,'7',NULL,'0','1','2222','0','',NULL,'0'), ('32','1525677891749','红豆','2',NULL,'https://img.ailogic.xin/food_152567789174935b106daea9b4246a2b03e33fb8c938f',NULL,'2018-05-07 16:33:43','1',NULL,'',NULL,'7',NULL,'0','1','2222','0','',NULL,'0'), ('33','1525677891749','燕麦','2',NULL,'https://img.ailogic.xin/food_152567789174901ab93ad3a114ce4b325d1aaf3a11b46',NULL,'2018-05-07 16:34:02','1',NULL,'',NULL,'7',NULL,'0','1','2222','0','',NULL,'0'), ('34','1525677891749','椰果','2',NULL,'https://img.ailogic.xin/food_1525677891749dc4cc2591ebc45369db72016160010f7',NULL,'2018-05-07 16:34:15','1',NULL,'',NULL,'7',NULL,'0','1','2222','0','',NULL,'0');
INSERT INTO `food_category` VALUES ('1','1525677891749','鲜榨果汁',NULL,'0','1',NULL,'1'), ('2','1525677891749','香浓奶昔',NULL,'0','1',NULL,'1'), ('3','1525677891749','果色果香',NULL,'0','1',NULL,'1'), ('4','1525677891749','美味优格',NULL,'0','1',NULL,'1'), ('5','1525677891749','趣味茶香',NULL,'0','1',NULL,'1'), ('6','1525677891749','暖心热饮',NULL,'0','1',NULL,'1'), ('7','1525677891749','加料区',NULL,'0','1',NULL,'1');
INSERT INTO `orders` VALUES ('1525683021396','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 16:50:21','5','[{\"status\":1,\"time\":1525683021396},{\"status\":5,\"time\":1525683081511},{\"status\":2,\"time\":1525683087940},{\"status\":5,\"time\":1525683148247}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"加冰\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"2\",\"goods_name\":\"鲜榨雪梨汁\",\"sub_id\":\"1\",\"sub_name\":\"大杯\",\"select_property\":\"常温\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]','较低阿红的','0.00','2.00','0.00','0.00','0','22.00','24.00','1525683066057'), ('1525684351325','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 17:12:31','4','[{\"status\":1,\"time\":1525684351325},{\"status\":2,\"time\":1525684365068},{\"status\":3,\"time\":1525684381412},{\"status\":4,\"time\":1525684441480}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"3\",\"goods_name\":\"奇异果雪梨汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]','','0.00','2.00','0.00','0.00','0','22.00','24.00','1525683066057'), ('1525684682899','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 17:18:03','5','[{\"status\":1,\"time\":1525684682899},{\"status\":5,\"time\":1525684743037}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"4\",\"goods_name\":\"芒果雪梨汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"2\",\"goods_name\":\"鲜榨雪梨汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1}]',NULL,'0.00','2.00','0.00','0.00','0','32.00','34.00',NULL), ('1525684779993','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 17:19:40','5','[{\"status\":1,\"time\":1525684779993},{\"status\":5,\"time\":1525684840069}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":2}]',NULL,'0.00','2.00','0.00','0.00','0','20.00','22.00',NULL), ('1525685889006','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 17:38:09','5','[{\"status\":1,\"time\":1525685889006},{\"status\":5,\"time\":1525685949098}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"4\",\"goods_name\":\"芒果雪梨汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]',NULL,'0.00','2.00','0.00','0.00','0','22.00','24.00',NULL), ('1525686137041','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 17:42:17','5','[{\"status\":1,\"time\":1525686137041},{\"status\":2,\"time\":1525686152552},{\"status\":5,\"time\":1525686212975}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":3}]','','0.00','2.00','0.00','0.00','0','30.00','32.00','1525683066057'), ('1525688078122','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:14:38','4','[{\"status\":1,\"time\":1525688078122},{\"status\":2,\"time\":1525688116199},{\"status\":3,\"time\":1525688165617},{\"status\":4,\"time\":1525688225725}]','[{\"goods_id\":\"8\",\"goods_name\":\"香蕉奶昔\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":null,\"price\":\"8\",\"packing_fee\":\"0\",\"num\":3}]','','0.00','2.00','0.00','0.00','0','24.00','26.00','1525683066057'), ('1525688267136','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:17:47','5','[{\"status\":1,\"time\":1525688267136},{\"status\":5,\"time\":1525688327422}]','[{\"goods_id\":\"31\",\"goods_name\":\"珍珠\",\"select_property\":null,\"price\":\"2\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"11\",\"goods_name\":\"香蕉牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"8\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"17\",\"goods_name\":\"芒果优格\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":null,\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]',NULL,'0.00','2.00','0.00','0.00','0','22.00','24.00',NULL), ('1525688743724','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:25:44','5','[{\"status\":1,\"time\":1525688743724},{\"status\":5,\"time\":1525688803873}]','[{\"goods_id\":\"1\",\"goods_name\":\"鲜榨西瓜汁\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":3}]',NULL,'0.00','2.00','0.00','0.00','0','30.00','32.00','1525686552837'), ('1525688769927','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:26:10','5','[{\"status\":1,\"time\":1525688769927},{\"status\":5,\"time\":1525688830004}]','[{\"goods_id\":\"27\",\"goods_name\":\"热牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"微糖\",\"price\":\"8\",\"packing_fee\":\"0\",\"num\":3}]',NULL,'0.00','2.00','0.00','0.00','0','24.00','26.00',NULL), ('1525689222934','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:33:43','5','[{\"status\":1,\"time\":1525689222934},{\"status\":5,\"time\":1525689283061}]','[{\"goods_id\":\"11\",\"goods_name\":\"香蕉牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"8\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"12\",\"goods_name\":\"芒果牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1}]',NULL,'0.00','2.00','0.00','0.00','0','18.00','20.00',NULL), ('1525689256378','oJOT00FckX0Ts9chCtN1KavrZfrA','1525677891749','2018-05-07 18:34:16','5','[{\"status\":1,\"time\":1525689256378},{\"status\":2,\"time\":1525689266586},{\"status\":5,\"time\":1525689326822}]','[{\"goods_id\":\"11\",\"goods_name\":\"香蕉牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"8\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"13\",\"goods_name\":\"椰奶西米露\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"冰\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"12\",\"goods_name\":\"芒果牛奶\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"常温\",\"price\":\"10\",\"packing_fee\":\"0\",\"num\":1},{\"goods_id\":\"14\",\"goods_name\":\"芒果椰奶西米露\",\"sub_id\":\"0\",\"sub_name\":\"中杯\",\"select_property\":\"冰\",\"price\":\"12\",\"packing_fee\":\"0\",\"num\":1}]','','0.00','2.00','0.00','0.00','0','40.00','42.00','1525683066057');
INSERT INTO `order_pay` VALUES ('1','支付宝支付'), ('2','微信支付');
INSERT INTO `project` VALUES ('1525689416699','测试项目','这是一个段他就嗲的回复','123','2018-05-07 18:36:57','oJOT00FckX0Ts9chCtN1KavrZfrA','https://img.ailogic.xin/prj_1525689416699','0','2018-06-06','不限','[{\"url\":\"https://img.ailogic.xin/prj_1525689416699_o6zAJszSyJB0uJQcZXsHhr_6NI8M.RL97FNgAQyqidde1717d2\"},{\"url\":\"https://img.ailogic.xin/prj_1525689416699_o6zAJszSyJB0uJQcZXsHhr_6NI8M.3kgfazwV69Iw18ffa3120\"}]'), ('1525689683297','卡号的卡了回复啊快点恢复','安科技的活法儿和克拉的合法快乐和拉客的合法开发和啊拉客的合法开发和阿里的画法几何发按领导和发掘h啊肯定发哈客户\n打款发货啊奥地利看哈发货阿里的货款付了看哈了肯定会发科技孵化 啊肯定会发就','undefined','2018-05-07 18:41:23','oJOT00FckX0Ts9chCtN1KavrZfrA','https://img.ailogic.xin/prj_1525689683297','0','2018-06-06','南京航空航天大学','');
INSERT INTO `project_comment` VALUES ('1525689416699','oJOT00FckX0Ts9chCtN1KavrZfrA','😍','2018-05-07 18:37:35','0'), ('1525689416699','oJOT00FckX0Ts9chCtN1KavrZfrA','😡😣','2018-05-07 18:37:47','0');
INSERT INTO `receiver` VALUES ('oJOT00FckX0Ts9chCtN1KavrZfrA','1525683066057','18261149716','可爱多','南京市委','8栋101',NULL,'南京市','玄武区','320100','320102','118.80309018206312','32.06483027359552');
INSERT INTO `university` VALUES ('1','江苏','1','南京大学','0'), ('1','江苏','2','东南大学','0'), ('1','江苏','3','中国矿业大学','0'), ('1','江苏','4','河海大学','1'), ('1','江苏','5','江南大学','0'), ('1','江苏','6','南京农业大学','0'), ('1','江苏','7','中国药科大学','0'), ('1','江苏','8','南京理工大学','0'), ('1','江苏','9','南京航空航天大学','0'), ('1','江苏','10','苏州大学','0'), ('1','江苏','11','南京师范大学','0'), ('1','江苏','12','扬州大学','0'), ('1','江苏','13','江苏大学','0'), ('1','江苏','14','南京信息工程大学','0'), ('1','江苏','15','南京邮电大学','0'), ('1','江苏','16','江苏科技大学','0'), ('1','江苏','17','南京工业大学','0'), ('1','江苏','18','南京林业大学','1'), ('1','江苏','19','南京医科大学','0'), ('1','江苏','20','南京中医药大学','0'), ('1','江苏','21','江苏师范大学','0'), ('1','江苏','22','南京财经大学','0'), ('1','江苏','23','南通大学','0'), ('1','江苏','24','常州大学','0'), ('1','江苏','25','西交利物浦大学','0'), ('1','江苏','26','淮阴工学院','0'), ('1','江苏','27','徐州工程学院','0'), ('1','江苏','28','淮海工学院','0'), ('1','江苏','29','常州工学院','0'), ('1','江苏','30','盐城工学院','0'), ('1','江苏','31','金陵科技学院','0'), ('1','江苏','32','南京工程学院','0'), ('1','江苏','33','徐州医科大学','0'), ('1','江苏','34','江苏理工学院','0'), ('1','江苏','35','淮阴师范学院','0'), ('1','江苏','36','南京晓庄学院','0'), ('1','江苏','37','盐城师范学院','0'), ('1','江苏','38','苏州科技大学','0'), ('1','江苏','39','南京审计大学','0'), ('1','江苏','40','江苏警官学院','0'), ('1','江苏','41','南京体育学院','0'), ('1','江苏','42','南京艺术学院','0'), ('1','江苏','43','常熟理工学院','0'), ('1','江苏','44','三江学院','0'), ('1','江苏','45','南京森林警察学院','0'), ('1','江苏','46','宿迁学院','0'), ('1','江苏','47','泰州学院','0'), ('1','江苏','48','江苏第二师范学院','0'), ('1','江苏','49','南通理工学院','0');
INSERT INTO `users` VALUES ('oJOT00FckX0Ts9chCtN1KavrZfrA',NULL,'2','2018-05-07','xxx、defined','https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epuibz5Qwf2IYwnGBLwbWsn8aRHXcrYKvQoVqS5Ls3fnksQfiaQMz9nJLIwJLzpBFoIPMYDKLQdDaPg/132','2018-05-07',NULL,'1',NULL,NULL,NULL,NULL,'1525677891749','1',NULL);
INSERT INTO `users_type` VALUES ('0','系统管理员'), ('1','商户'), ('2','普通用户');
INSERT INTO `user_focus_project` VALUES ('oJOT00FckX0Ts9chCtN1KavrZfrA','1525689416699');
