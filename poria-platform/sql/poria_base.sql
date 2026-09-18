-- Poria Base 服务初始化脚本
-- 适用数据库：MySQL 8.0+
-- 字符集与排序规则：utf8mb4 / utf8mb4_0900_ai_ci

CREATE DATABASE IF NOT EXISTS `poria_base`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `poria_base`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `base_activity` (
  `id` varchar(64) NOT NULL COMMENT '主键，雪花算法 ID',
  `name` varchar(255) DEFAULT NULL COMMENT '页面标题',
  `is_status` tinyint DEFAULT 0 COMMENT '发布状态：0-未发布，1-已发布',
  `jump_link` varchar(1024) DEFAULT NULL COMMENT '跳转链接',
  `content_focused` text COMMENT '活动页内容',
  `is_skip` tinyint DEFAULT 0 COMMENT '是否跳转：0-否，1-是',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  `title_parameter` varchar(1000) DEFAULT NULL COMMENT '页面参数',
  `button_script` varchar(255) DEFAULT NULL COMMENT '跳转按钮文字',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_activity_status_deleted` (`is_status`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动页管理';

CREATE TABLE IF NOT EXISTS `base_app_version` (
  `id` varchar(64) NOT NULL COMMENT '主键，雪花算法 ID',
  `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称，字典 app_name',
  `app_os` varchar(32) DEFAULT NULL COMMENT '操作系统，字典 app_os',
  `version` varchar(64) DEFAULT NULL COMMENT '版本号',
  `app_update_type` varchar(32) DEFAULT NULL COMMENT '升级类型，字典 app_update_type',
  `prompt` varchar(600) DEFAULT NULL COMMENT '升级提示语',
  `url` varchar(255) DEFAULT NULL COMMENT '下载地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `tenant_id` int DEFAULT NULL COMMENT '租户 ID',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_version_lookup` (`app_name`, `app_os`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='APP 版本管理';

CREATE TABLE IF NOT EXISTS `base_banner` (
  `id` varchar(64) NOT NULL COMMENT '主键，雪花算法 ID',
  `title` varchar(200) DEFAULT NULL COMMENT 'Banner 标题',
  `code` varchar(20) DEFAULT NULL COMMENT 'Banner 编码',
  `description` varchar(600) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-启用，2-停用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `tenant_id` int DEFAULT NULL COMMENT '租户 ID',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_banner_code_deleted` (`code`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Banner 主表';

CREATE TABLE IF NOT EXISTS `base_banner_item` (
  `id` varchar(64) NOT NULL COMMENT '主键，雪花算法 ID',
  `banner_id` varchar(64) NOT NULL COMMENT '所属 Banner 主键',
  `banner_item_type` varchar(64) DEFAULT NULL COMMENT '广告项类型，字典值',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `image_url` varchar(1024) DEFAULT NULL COMMENT '图片地址',
  `begin_date` datetime DEFAULT NULL COMMENT '起始投放时间',
  `end_date` datetime DEFAULT NULL COMMENT '截止投放时间',
  `content` varchar(5000) DEFAULT NULL COMMENT '内容',
  `sort` int DEFAULT 0 COMMENT '排序值',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-启用，2-停用',
  `need_login` tinyint DEFAULT 2 COMMENT '是否需要登录：1-需要，2-不需要',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `tenant_id` int DEFAULT NULL COMMENT '租户 ID',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_banner_item_query` (`banner_id`, `status`, `is_delete`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Banner 项';

CREATE TABLE IF NOT EXISTS `base_file` (
  `id` varchar(64) NOT NULL COMMENT '主键，雪花算法 ID',
  `file_name` varchar(1024) DEFAULT NULL COMMENT '对象存储中的文件名',
  `bucket_name` varchar(255) DEFAULT NULL COMMENT '对象存储桶名称',
  `original` varchar(1024) DEFAULT NULL COMMENT '原始文件名',
  `type` varchar(8) DEFAULT NULL COMMENT '文件类型：1-公共文件，2-私有文件',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小，单位字节',
  `create_by` varchar(64) DEFAULT NULL COMMENT '上传人',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `tenant_id` int DEFAULT NULL COMMENT '所属租户',
  PRIMARY KEY (`id`),
  KEY `idx_file_bucket_name` (`bucket_name`, `file_name`(255)),
  KEY `idx_file_tenant_deleted` (`tenant_id`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件管理';
