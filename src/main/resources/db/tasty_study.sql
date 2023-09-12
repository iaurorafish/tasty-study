/*
 Navicat MySQL Data Transfer

 Source Server         : 本机MySQL
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : 127.0.0.1:3306
 Source Schema         : tasty_study

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 10/09/2023 16:51:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_blog
-- ----------------------------
DROP TABLE IF EXISTS `tb_blog`;
CREATE TABLE `tb_blog`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(20) NOT NULL COMMENT '商户id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '探店的照片，最多9张，多张以\",\"隔开',
  `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '探店的文字描述',
  `liked` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '点赞数量',
  `comments` int(8) UNSIGNED NULL DEFAULT NULL COMMENT '评论数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_blog
-- ----------------------------
INSERT INTO `tb_blog` VALUES (3, 1, 3, '自习天堂，你值得拥有~', '/imgs/blogs/11/4/714f7d618c02454680ccde0da95d85c9.jpg,/imgs/blogs/14/5/16b86a1aa09942cfb9145698744a264d.jpg,/imgs/blogs/15/8/9706c8641c9c4034b8aa00de21380fe3.jpeg', '装修很新，环境很优美，设施很齐全，很适合自习！', 1, NULL, '2023-09-02 20:26:16', '2023-09-02 20:26:31');
INSERT INTO `tb_blog` VALUES (4, 12, 3, '阅读天堂', '/imgs/blogs/4/9/6cea991329e0409198033b2820864fec.jpeg,/imgs/blogs/9/7/677f173a356f4cd39cd632ac96e1b38f.jpg', '老联锁书店了，数目很全，在里面看书半天不会赶人，很自由！', 0, NULL, '2023-09-02 20:29:34', '2023-09-02 20:29:34');
INSERT INTO `tb_blog` VALUES (5, 7, 2, '“白嫖”攻略', '/imgs/blogs/15/0/c414cbc03d444e58b88802b93b5c65dc.jpeg', '首次体验有优惠券抵扣，四小时只要一块钱，巨划算，走过路过不要错过！', 0, NULL, '2023-09-02 20:37:25', '2023-09-02 20:37:25');
INSERT INTO `tb_blog` VALUES (6, 4, 1, '城市摄影展，仓山图书馆，速来!', '/imgs/blogs/3/6/c16a208f0c5a4455a2dea4d88548e7b5.jpeg', '不止福州，厦门、漳州等其他省内城市都有，种类很多~', 0, NULL, '2023-09-02 20:55:28', '2023-09-02 20:55:28');
INSERT INTO `tb_blog` VALUES (7, 8, 4, '家附近的自习室', '/imgs/blogs/2/9/f58aa4c355154946a9179d86126d1d14.png', '在家自习静不下心，没想到家附近就有一个很不错的自习室，好幸福！', 0, NULL, '2023-09-02 20:57:29', '2023-09-02 20:57:29');
INSERT INTO `tb_blog` VALUES (8, 14, 1, '自助打印店', '/imgs/blogs/15/5/de23d0c20669469595d197d8bdc6800c.jpg', '打印文件，电脑自动计费，扫码付钱即拿即走，很强，真·自助打印店！', 0, NULL, '2023-09-03 14:21:23', '2023-09-03 14:21:23');
INSERT INTO `tb_blog` VALUES (9, 9, 1, '今天也是沉浸在知识海洋中的一天', '/imgs/blogs/11/10/dd22e75e2e5f42f999dfb45fa907df3a.jpg', '法考倒计时100天，加油！', 0, NULL, '2023-09-03 14:39:32', '2023-09-03 14:39:32');
INSERT INTO `tb_blog` VALUES (10, 7, 1, '秒杀异步好难啊！', '/imgs/blogs/13/13/7abd2ec9b21543aaaa186cb17ffac71e.jpg', '但网速还是不错的~', 1, NULL, '2023-09-03 16:59:19', '2023-09-03 17:15:55');
INSERT INTO `tb_blog` VALUES (11, 10, 1, '这附近有个很不错的泡椒田鸡店，安利！', '/imgs/blogs/7/3/fec59a135a9f4f4399335a1c393a5aa5.jpg', '安利大家下自习尝尝！', 0, NULL, '2023-09-03 17:13:19', '2023-09-03 17:13:19');

-- ----------------------------
-- Table structure for tb_blog_comments
-- ----------------------------
DROP TABLE IF EXISTS `tb_blog_comments`;
CREATE TABLE `tb_blog_comments`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `blog_id` bigint(20) UNSIGNED NOT NULL COMMENT '博客id',
  `parent_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的1级评论id，如果是一级评论，则值为0',
  `answer_id` bigint(20) UNSIGNED NOT NULL COMMENT '回复的评论id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `liked` int(8) UNSIGNED NULL DEFAULT NULL COMMENT '点赞数',
  `status` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '状态，0：正常，1：被举报，2：禁止查看',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_blog_comments
-- ----------------------------

-- ----------------------------
-- Table structure for tb_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_follow
-- ----------------------------
INSERT INTO `tb_follow` VALUES (4, 2, 1, '2023-09-02 20:20:24');
INSERT INTO `tb_follow` VALUES (6, 4, 3, '2023-09-02 21:08:09');
INSERT INTO `tb_follow` VALUES (7, 4, 2, '2023-09-02 21:08:22');
INSERT INTO `tb_follow` VALUES (8, 1, 3, '2023-09-02 22:15:06');
INSERT INTO `tb_follow` VALUES (9, 1, 2, '2023-09-02 22:15:15');
INSERT INTO `tb_follow` VALUES (10, 1, 4, '2023-09-02 22:15:20');
INSERT INTO `tb_follow` VALUES (11, 4, 1, '2023-09-02 22:35:12');

-- ----------------------------
-- Table structure for tb_seckill_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_seckill_voucher`;
CREATE TABLE `tb_seckill_voucher`  (
  `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的优惠券id',
  `stock` int(8) NOT NULL COMMENT '库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`voucher_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀优惠券表，与优惠券是一对一关系' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_seckill_voucher
-- ----------------------------
INSERT INTO `tb_seckill_voucher` VALUES (2, 200, '2023-08-23 20:31:27', '2023-01-01 10:00:00', '2023-12-31 22:00:00', '2023-08-27 16:13:06');
INSERT INTO `tb_seckill_voucher` VALUES (3, 200, '2023-08-27 10:54:43', '2023-01-01 20:00:00', '2023-12-31 20:00:00', '2023-08-30 20:11:58');

-- ----------------------------
-- Table structure for tb_shop
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop`;
CREATE TABLE `tb_shop`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `type_id` bigint(20) UNSIGNED NOT NULL COMMENT '商铺类型的id',
  `images` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商铺图片，多个图片以\',\'隔开',
  `area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商圈，例如陆家嘴',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址',
  `x` double UNSIGNED NOT NULL COMMENT '经度',
  `y` double UNSIGNED NOT NULL COMMENT '维度',
  `avg_price` bigint(10) UNSIGNED NULL DEFAULT NULL COMMENT '均价，取整数',
  `sold` int(10) UNSIGNED ZEROFILL NOT NULL COMMENT '销量',
  `comments` int(10) UNSIGNED ZEROFILL NOT NULL COMMENT '评论数量',
  `score` int(2) UNSIGNED ZEROFILL NOT NULL COMMENT '评分，1~5分，乘10保存，避免小数',
  `open_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业时间，例如10:00-22:00',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `foreign_key_type`(`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_shop
-- ----------------------------
INSERT INTO `tb_shop` VALUES (1, '福州市图书馆', 1, 'https://poi-pic.cdn.bcebos.com/5217e61d0439d99676bacf1150ab2eba.jpg,https://taojin-his.cdn.bcebos.com/79f0f736afc37931902be5b7e2c4b74542a911e8.jpg', '台江', '福州市台江区望龙一路2号', 119.28992, 26.06342, 0, 0000000000, 0000003035, 37, '周一 08:30-12:00 周二至周日 08:30-21:00', '2023-08-20 19:42:57', '2023-09-04 01:11:59');
INSERT INTO `tb_shop` VALUES (2, '福建省图书馆', 1, 'https://taojin-his.cdn.bcebos.com/8718367adab44aed33c46261ba1c8701a08bfba9.jpg', '鼓楼', '福州市鼓楼区湖东路227号', 119.31945, 26.09962, 0, 0000000000, 0000001460, 38, '周一至周三,周五至周日 08:30-18:30', '2023-08-20 19:42:57', '2023-09-04 01:20:12');
INSERT INTO `tb_shop` VALUES (3, '福州市台江区图书馆', 1, 'https://lbsugc.cdn.bcebos.com/images/Ha044ad345982b2b721657fd33aadcbef76099b2b.jpg,https://lbsugc.cdn.bcebos.com/images/Ha2cc7cd98d1001e9929ce9f8b00e7bec55e797a1.jpg,https://taojin-his.cdn.bcebos.com/8d5494eef01f3a2983023f4b9025bc315d607c69.jpg', '台江', '福州市台江区学军路92号台江区文化活动中心7层', 119.31067, 26.06156, 0, 0000000000, 0000008045, 37, '周一08:30-11:30，周二至周日08:30-17:00', '2023-08-20 19:42:57', '2023-09-04 01:20:44');
INSERT INTO `tb_shop` VALUES (4, '福州市仓山区图书馆', 1, 'https://lbsugc.cdn.bcebos.com/images/H1b4c510fd9f9d72a912d7da7d22a2834349bbb3b.jpg,https://lbsugc.cdn.bcebos.com/images/Hac6eddc451da81cb51b7d3f95e66d016092431b4.jpghttps://lbsugc.cdn.bcebos.com/images/B639be4d6983a583cc.jpeg', '仓山', '福州市仓山区亭头支路66号仓山区少体校体育综合馆5-6层', 119.27741, 26.04438, 0, 0000000000, 0000009529, 28, '周一至周五 08:30-17:30 周六至周日 09:00-20:30', '2023-08-20 19:42:57', '2023-09-04 01:21:15');
INSERT INTO `tb_shop` VALUES (5, '福州市晋安区图书馆', 1, 'https://taojin-his.cdn.bcebos.com/622762d0f703918fa9fb115c5d3d269759eec479.jpg,https://lbsugc.cdn.bcebos.com/images/H63d0f703918fa0ec4884b8c2209759ee3c6ddbba.jpg,https://lbsugc.cdn.bcebos.com/images/H6f061d950a7b02082e01cafe6fd9f2d3562cc8e1.jpg', '晋安', '福州市晋安区红光路119号', 119.36468, 26.08007, 0, 0000000000, 0000002764, 43, '周一至周五 08:30-12:30,13:30-18:30 周六至周日 08:30-12:30,13:30-20:30', '2023-08-20 19:42:57', '2023-09-04 01:21:39');
INSERT INTO `tb_shop` VALUES (6, '福州市马尾区图书馆', 1, 'https://poi-pic.cdn.bcebos.com/15d87cae5b28b7579003b9fc7b97424d.jpg,https://lbsugc.cdn.bcebos.com/images/H5ab5c9ea15ce36d39394beae31f33a87e850b19f.jpghttps://poi-pic.cdn.bcebos.com/1c47802a61190cf4ad9e9c07a0ec964b.jpg', '马尾', '福州市马尾区济安西路2号', 119.46719, 25.98934, 0, 0000000000, 0000007324, 46, '周一至周三,周五至周日 09:00-18:00', '2023-08-20 19:42:57', '2023-09-04 01:22:04');
INSERT INTO `tb_shop` VALUES (7, '书鱼自习室', 2, 'https://photo-meituan.cdn.bcebos.com/photo/1692188291bc51ec82137a58f3dd7d02969b0a0f8b,https://lbsugc.cdn.bcebos.com/images/B5ee785a9bbfa613d61.jpeg,https://lbsugc.cdn.bcebos.com/images/B5ee785aa3ee6710189.jpeg', '鼓楼', '福州市鼓楼区古田路9号广场明珠21层(近五一广场)', 119.31002, 26.08195, 25, 0000004215, 0000003035, 47, '09:00-23:00', '2023-08-20 19:42:57', '2023-09-04 01:22:24');
INSERT INTO `tb_shop` VALUES (8, '勤为径沉浸式自习室(广场明珠店)', 2, 'https://poi-pic.cdn.bcebos.com/783fb4b002dc40ea1a66190dc2b51cc1.jpg,https://lbsugc.cdn.bcebos.com/images/H78310a55b319ebc44003b4788c26cffc1f1716dc.jpg', '鼓楼', '福州市鼓楼区古田路9号广场明珠21层(近五一广场)', 119.31002, 26.08195, 30, 0000002406, 0000001206, 48, '09:00-23:00', '2023-08-20 19:42:57', '2023-09-04 01:22:51');
INSERT INTO `tb_shop` VALUES (9, '晨点自习室', 2, 'https://lbsugc.cdn.bcebos.com/images/B5f3f607e743fa14bc4.jpeg,https://lbsugc.cdn.bcebos.com/images/B5f3f607e7a22492ae.jpeg,https://lbsugc.cdn.bcebos.com/images/B5f3f607e65fbd12de6.jpeg', '台江', '上杭路17号金马大厦21楼(大厦西北门)', 119.31391, 26.06123, 28, 0000002763, 0000001363, 47, '周一至周日 09:00-22:30', '2023-08-20 19:42:57', '2023-09-04 01:23:16');
INSERT INTO `tb_shop` VALUES (10, '未来AI自习室(台江万达店)', 2, 'https://photo-meituan.cdn.bcebos.com/photo/16922053061840ed662df8dac78d2d4d7e626b7885', '台江', '福州市台江区鳌峰路68号', 119.35005, 26.06061, 26, 0000000356, 0000000138, 38, '08:00-23:00', '2023-08-20 19:42:57', '2023-09-04 01:23:37');
INSERT INTO `tb_shop` VALUES (11, '览虫自习室(金山店)', 2, 'https://photo-meituan.cdn.bcebos.com/photo/16921871258da6456f10b559cd30fb7bc5c6ebf420', '仓山', '金洲南路汇创名居二期23号楼501(近金港路口公交站正背后小区第一幢楼)', 119.27789, 26.03805, 27, 0000035977, 0000005684, 47, '11:30-06:00', '2023-08-20 19:42:57', '2023-09-04 01:24:00');
INSERT INTO `tb_shop` VALUES (12, '新华书店(五一北路店)', 3, 'https://taojin-his.cdn.bcebos.com/9358d109b3de9c82d100550b6281800a18d843c8.jpg,https://lbsugc.cdn.bcebos.com/images/Hd53f8794a4c27d1e64bf1eff17d5ad6eddc4386b.jpghttps://lbsugc.cdn.bcebos.com/images/H242dd42a2834349bb269cc2ac5ea15ce36d3be36.jpg', '鼓楼', '福州市鼓楼区五一北路128号闽台书城原新华教育书店', 119.31612, 26.08667, 55, 0000006444, 0000000235, 46, '09:00-21:00', '2023-08-20 19:42:57', '2023-09-04 01:24:27');
INSERT INTO `tb_shop` VALUES (13, '西西弗书店(泰禾广场东二环店)', 3, 'https://poi-pic-gz.cdn.bcebos.com/pic_acfc2f02f5d292890759fed33756bba4.jpeg,https://lbsugc.cdn.bcebos.com/images/B63945d3a2f9365920.jpeg,https://lbsugc.cdn.bcebos.com/images/B63945d2df10021430d.jpeg', '晋安', '福州市晋安区岳峰镇竹屿路6号泰禾广场F4', 119.34558, 26.09585, 58, 0000018997, 0000001857, 46, '周一至周四,周日 10:00-22:00 周五至周六 10:00-22:30', '2023-08-20 19:42:57', '2023-09-04 01:24:47');
INSERT INTO `tb_shop` VALUES (14, '海天图文', 4, 'https://taojin-his.cdn.bcebos.com/d50735fae6cd7b89619ad7af022442a7d9330e2c.jpg,https://lbsugc.cdn.bcebos.com/images/H95eef01f3a292df5e18915dab6315c6035a873b2.jpg', '台江', '福州市台江区鳌峰路221号', 119.35196, 26.05887, 15, 0000017771, 0000000685, 50, '00:00-24:00', '2023-08-20 19:42:57', '2023-09-04 01:25:08');

-- ----------------------------
-- Table structure for tb_shop_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop_type`;
CREATE TABLE `tb_shop_type`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(3) UNSIGNED NULL DEFAULT NULL COMMENT '顺序',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_shop_type
-- ----------------------------
INSERT INTO `tb_shop_type` VALUES (1, '图书馆', '/types/library.png', 1, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (2, '自习室', '/types/studyroom.png', 2, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (3, '书店', '/types/bookstore.png', 3, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (4, '文具', '/types/stationery.png', 4, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (5, '影印', '/types/copyshop.png', 5, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (6, '便利店·超市', '/types/shop.png', 6, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (7, '快餐', '/types/fastfood.png', 7, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (8, '饮品', '/types/drink.png', 8, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (9, '甜品', '/types/dessert.png', 9, '2023-08-20 19:42:57', '2023-08-20 19:42:57');
INSERT INTO `tb_shop_type` VALUES (10, '健身', '/types/exercise.png', 10, '2023-08-20 19:42:57', '2023-08-20 19:42:57');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码，加密存储',
  `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称，默认是用户id',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '人物头像',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_key_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, '13456789001', '', 'Taylor', '/imgs/icons/taylor.jpg', '2023-08-25 12:00:56', '2023-09-02 15:27:20');
INSERT INTO `tb_user` VALUES (2, '13456789002', '', 'Leonardo', '/imgs/icons/leo.png', '2023-08-26 20:01:21', '2023-09-02 15:28:38');
INSERT INTO `tb_user` VALUES (3, '13456789003', '', 'Nicole', '/imgs/icons/nicole.jpg', '2023-08-26 20:01:21', '2023-09-02 15:28:46');
INSERT INTO `tb_user` VALUES (4, '13456789004', '', 'Kaka', '/imgs/icons/kaka.jpg', '2023-08-26 20:01:21', '2023-09-02 15:30:07');

-- ----------------------------
-- Table structure for tb_user_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_info`;
CREATE TABLE `tb_user_info`  (
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '主键，用户id',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '城市名称',
  `introduce` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人介绍，不要超过128个字符',
  `fans` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '粉丝数量',
  `followee` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '关注的人的数量',
  `gender` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '性别，0：男，1：女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `credits` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '积分',
  `level` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '会员级别，0~9级，0代表未开通会员',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for tb_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher`;
CREATE TABLE `tb_voucher`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '店铺id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代金券标题',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `rules` varchar(1034) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用规则',
  `pay_value` bigint(10) UNSIGNED NOT NULL COMMENT '支付金额，单位是分。例如200代表2元',
  `actual_value` bigint(10) NOT NULL COMMENT '抵扣金额，单位是分。例如200代表2元',
  `type` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券类型，0：普通券，1：秒杀券',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '优惠券状态，1：上架，2：下架，3：过期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_voucher
-- ----------------------------
INSERT INTO `tb_voucher` VALUES (1, 7, '15元代金券', '周一至周日均可使用', '全场通用\\n无需预约\\n可无限叠加\\n不兑现、不找零\\n仅限堂食', 1000, 1500, 0, 1, '2023-08-21 09:42:39', '2023-08-21 09:43:31');
INSERT INTO `tb_voucher` VALUES (2, 7, '100元代金券', '周一至周五均可使用', '全场通用\\n无需预约\\n可无限叠加\\不兑现、不找零\\n仅限堂食', 8000, 10000, 1, 1, '2023-08-23 20:31:27', '2023-08-23 20:31:27');
INSERT INTO `tb_voucher` VALUES (3, 7, '10元代金券', '周一至周五均可使用', '全场通用\\n无需预约\\n可无限叠加\\不兑现、不找零\\n仅限堂食', 900, 1000, 1, 1, '2023-08-27 10:54:43', '2023-08-27 10:54:43');

-- ----------------------------
-- Table structure for tb_voucher_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher_order`;
CREATE TABLE `tb_voucher_order`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '下单的用户id',
  `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '购买的代金券id',
  `pay_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '支付方式，1：余额支付，2：支付宝，3：微信',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态，1：未支付，2：已支付，3：已核销，4：已取消，5：退款中，6：已退款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `use_time` timestamp NULL DEFAULT NULL COMMENT '核销时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_voucher_order
-- ----------------------------


SET FOREIGN_KEY_CHECKS = 1;
