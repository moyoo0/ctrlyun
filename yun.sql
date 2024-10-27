/*
 Navicat Premium Data Transfer

 Source Server         : SQL
 Source Server Type    : MySQL
 Source Server Version : 80017 (8.0.17)
 Source Host           : localhost:3306
 Source Schema         : yun

 Target Server Type    : MySQL
 Target Server Version : 80017 (8.0.17)
 File Encoding         : 65001

 Date: 06/09/2023 23:56:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `mem_id` char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户ID',
  `name` char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名字',
  `type` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '更新时间',
  `collection` tinyint(4) NULL DEFAULT 0 COMMENT '是否收藏',
  `f_dir` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '/root',
  `filetype` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'file',
  `video_id` char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for group_mem
-- ----------------------------
DROP TABLE IF EXISTS `group_mem`;
CREATE TABLE `group_mem`  (
  `record` int(11) NOT NULL COMMENT '记录，作为主键',
  `group_id` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `mem_id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群成员id',
  PRIMARY KEY (`record`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_request
-- ----------------------------
DROP TABLE IF EXISTS `group_request`;
CREATE TABLE `group_request`  (
  `request_id` int(11) NOT NULL COMMENT '请求id',
  `group_id` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `user_id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求状态，如等待批准、已批准、已拒绝等',
  PRIMARY KEY (`request_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` char(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员id',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '更新时间',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `neicun` bigint(20) NULL DEFAULT NULL COMMENT '内存',
  `publickey` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公钥',
  `sessionkey` varbinary(1000) NULL DEFAULT NULL COMMENT '会话密钥',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_dir
-- ----------------------------
DROP TABLE IF EXISTS `user_dir`;
CREATE TABLE `user_dir`  (
  `mem_id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mem_dir` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户目录结构',
  PRIMARY KEY (`mem_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group`  (
  `group_id` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `master_id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群主id',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `addUserDir`;
delimiter ;;
CREATE TRIGGER `addUserDir` AFTER INSERT ON `user` FOR EACH ROW BEGIN

       insert into user_dir values(NEW.id,'{"childrenList":[],"id":1,"name":"root/","parentId":0}');
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `delUserDir`;
delimiter ;;
CREATE TRIGGER `delUserDir` AFTER DELETE ON `user` FOR EACH ROW BEGIN
    DELETE from user_dir WHERE mem_id =OLD.id;

END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
