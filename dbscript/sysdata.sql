/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50519
Source Host           : localhost:3306
Source Database       : sysdata

Target Server Type    : MYSQL
Target Server Version : 50519
File Encoding         : 65001

Date: 2013-08-28 11:32:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_quote_status`
-- ----------------------------
DROP TABLE IF EXISTS `t_quote_status`;
CREATE TABLE `t_quote_status` (
  `symbol` varchar(32) NOT NULL DEFAULT '',
  `status` char(1) DEFAULT NULL,
  `last_check_time` timestamp NULL DEFAULT NULL,
  `quote_source` varchar(32) DEFAULT NULL,
  `last_check_ask` double DEFAULT NULL,
  `last_check_bid` double DEFAULT NULL,
  `last_quote_change` timestamp NULL DEFAULT NULL,
  `ip` varchar(32) NOT NULL DEFAULT '',
  `port` varchar(16) NOT NULL DEFAULT '',
  `check_period` int(11) NOT NULL,
  `max_unchange_time` int(2) NOT NULL,
  PRIMARY KEY (`symbol`,`ip`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_quote_status
-- ----------------------------
INSERT INTO `t_quote_status` VALUES ('AAAAAA', '0', '2013-08-28 10:01:27', null, '0', '0', '2013-08-28 10:00:53', '54.250.124.129', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('AAAAAB', '0', '2013-08-28 10:02:02', null, '0', '0', '2013-08-28 10:01:41', '54.250.124.129', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('AAAAAC', '0', '2013-08-28 10:27:30', null, '0', '0', '2013-08-28 10:27:09', '54.250.124.125', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('AAAAAD', '0', '2013-08-28 10:33:11', null, '0', '0', null, '54.250.124.125', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('AUDUSD', '1', '2013-08-28 10:33:12', null, '0.89633', '0.89625', '2013-08-28 10:33:12', '54.250.124.129', '444', '20', '4');
INSERT INTO `t_quote_status` VALUES ('EURUSD', '1', '2013-08-28 10:33:22', null, '1.33948', '1.33942', '2013-08-28 10:32:51', '54.250.124.129', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('GBPUSD', '1', '2013-08-28 10:33:22', null, '1.55427', '1.55415', '2013-08-28 10:33:22', '54.250.124.129', '444', '10', '5');
INSERT INTO `t_quote_status` VALUES ('USDCAD', '1', '2013-08-28 10:33:11', null, '1.04771', '1.04769', '2013-08-28 10:33:11', '54.250.124.129', '444', '20', '4');
INSERT INTO `t_quote_status` VALUES ('USDJPY', '1', '2013-08-28 10:33:22', null, '97.092', '97.089', '2013-08-28 10:33:22', '54.250.124.129', '444', '10', '5');

-- ----------------------------
-- Table structure for `t_sys_status`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_status`;
CREATE TABLE `t_sys_status` (
  `name` varchar(32) NOT NULL DEFAULT '',
  `status` char(1) DEFAULT NULL,
  `last_check_time` timestamp NULL DEFAULT NULL,
  `descr` varchar(128) DEFAULT NULL,
  `ip` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`,`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_sys_status
-- ----------------------------

-- ----------------------------
-- Table structure for `t_trade_status`
-- ----------------------------
DROP TABLE IF EXISTS `t_trade_status`;
CREATE TABLE `t_trade_status` (
  `ip` varchar(32) NOT NULL DEFAULT '',
  `group_name` varchar(32) DEFAULT NULL,
  `symbol` varchar(32) NOT NULL,
  `status` char(1) DEFAULT NULL,
  `last_check_time` timestamp NULL DEFAULT NULL,
  `port` varchar(16) NOT NULL,
  `login` varchar(32) NOT NULL DEFAULT '',
  `password` varchar(32) DEFAULT NULL,
  `check_period` int(11) NOT NULL,
  `api_version` varchar(16) NOT NULL,
  PRIMARY KEY (`login`,`symbol`,`ip`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_trade_status
-- ----------------------------
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'AUDUSD', '1', '2013-08-28 11:27:09', '444', '1013', 'a123456', '20', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'CCCCCC', '0', '2013-08-28 10:39:31', '444', '1013', 'a123456', '10', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.125', null, 'CCCCCD', '0', '2013-08-28 10:40:18', '444', '1013', 'a123456', '10', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'CCCCCE', '0', '2013-08-28 11:27:09', '444', '1013', 'a123456', '10', '161');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'EURUSD', '1', '2013-08-28 11:27:09', '444', '1013', 'a123456', '10', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'GBPUSD', '1', '2013-08-28 11:27:09', '444', '1013', 'a123456', '10', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'USDCAD', '1', '2013-08-28 11:27:09', '444', '1013', 'a123456', '20', '160');
INSERT INTO `t_trade_status` VALUES ('54.250.124.129', null, 'USDJPY', '1', '2013-08-28 11:27:09', '444', '1013', 'a123456', '10', '160');
