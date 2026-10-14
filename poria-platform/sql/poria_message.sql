-- Poria 消息服务新库初始化，供人工审核后执行；不包含业务数据。
-- 前 7 张表取自 unhm 的建表脚本，并按当前实体补充字段；其余表按实体定义补齐。
-- 此脚本只创建缺失的表，不用于已有数据库的结构升级。
CREATE DATABASE IF NOT EXISTS `poria_message` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `poria_message`;
CREATE TABLE IF NOT EXISTS `msg_email_log`  (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `business_type` int NOT NULL COMMENT '业务类型，见 BusinessType',
  `receive_user_id` int NOT NULL DEFAULT 0 COMMENT '接收者ID',
  `receive_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收手机号',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `task_id` int NOT NULL DEFAULT 0 COMMENT '任务ID',
  `send_status` int NOT NULL DEFAULT 0 COMMENT '发送状态，1：发送成功，2：失败，0：未发送，3：正在发送',
  `send_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送结果',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信发送具体详情' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `msg_pending_task`  (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `user_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `relate_user_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联用户ID',
  `relate_user_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联用户姓名',
  `relate_user_head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联用户头像',
  `count` int NULL DEFAULT NULL COMMENT '次数',
  `business_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务ID',
  `type` int NOT NULL COMMENT '代办任务类型(字典pending_task_type)',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务跳转路径',
  `status` int NOT NULL COMMENT '状态，0-未处理，1-已处理',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务标题',
  `task_time` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `is_delete` int NULL DEFAULT NULL COMMENT '删除标示',
  `commit_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `content` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `redirect_type` int NULL DEFAULT NULL COMMENT '0 不跳转 1 h5 2 原生',
  `group_type` int NULL DEFAULT 0 COMMENT '0-单人任务,1-多人任务',
  `level` int NULL DEFAULT 3 COMMENT '优先级 1-5',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代办任务' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `msg_sms_log`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `business_type` int NULL DEFAULT NULL COMMENT '业务类型，见 BusinessType',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收手机号',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内容',
  `task_id` int NULL DEFAULT NULL COMMENT '任务ID',
  `status` int NOT NULL COMMENT '发送状态，1：发送成功，2：失败，0：未发送，3：正在发送',
  `result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发送结果',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `platform` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '短信平台',
  `template_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tenant_id` int NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信发送具体详情' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `msg_template`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板标题,用于描述',
  `template_key` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '模版KEY 模版使用时传入',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容,需替换变量写在${}中',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT 'ID创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '模板类型 1-短信模板，2-消息推送模版，3-站内信',
  `status` int NOT NULL DEFAULT 0 COMMENT '停用标志  1-正常，2-停用',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除0-没删除 1-删除',
  `tenant_id` int NULL DEFAULT NULL COMMENT '租户ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息跳转URL，支持模板变量',
  `platform` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '短信平台 短信消息传入',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模版分类',
  `param_mapping` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '参数映射配置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息模板' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `msg_wx_log`  (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `task_id` int NULL DEFAULT NULL COMMENT '任务ID',
  `status` int NOT NULL COMMENT '发送状态，1：发送成功，2：失败，0：未发送，3：正在发送',
  `result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发送结果',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `template_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模版KEY',
  `msg_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信返回消息id',
  `account_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接收消息用户账户',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信发送具体详情' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `msg_yxxh_info` (
                                 `id` varchar(20) NOT NULL COMMENT '主键ID',
                                 `patient_mobile` varchar(20) DEFAULT NULL COMMENT '患者手机号',
                                 `staff_mobile` varchar(20) DEFAULT NULL COMMENT '医护手机号',
                                 `bind_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小号绑定关系唯一标识',
                                 `middle_number` varchar(255) NOT NULL COMMENT '绑定的小号号码\r\n',
                                 `business_id` varchar(20) NOT NULL COMMENT '业务id',
                                 `is_delete` int DEFAULT NULL COMMENT '删除标示 0:未删除 1:已删除',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='云信小号信息';

CREATE TABLE IF NOT EXISTS `msg_yxxh_log` (
                                `id` varchar(20) NOT NULL COMMENT '主键ID',
                                `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户账号',
                                `bind_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小号绑定关系唯一标识',
                                `call_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单个呼叫时返回，用于标识每通电话',
                                `display_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主显号码--小号',
                                `caller_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主叫号码',
                                `callee_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被叫号码',
                                `start_call_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '呼叫开始时间',
                                `caller_answered_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主叫接通时间',
                                `callee_answered_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '被叫接通时间',
                                `end_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结束时间',
                                `caller_state` int NOT NULL COMMENT '主叫呼叫状态',
                                `callee_state` int NOT NULL COMMENT '被叫呼叫状态',
                                `caller_duration` int DEFAULT NULL COMMENT '主叫通话时长--单位秒',
                                `callee_duration` int DEFAULT NULL COMMENT '被叫通话时长--单位秒',
                                `duration` int DEFAULT NULL COMMENT '时长（计费）--单位秒',
                                `rec_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录音文件下载',
                                `business_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户自定义数据--业务id',
                                `is_delete` int DEFAULT NULL COMMENT '删除标示 0:未删除 1:已删除',
                                `create_time` datetime NOT NULL COMMENT '创建时间',
                                `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='云信小号通话记录';

CREATE TABLE IF NOT EXISTS `msg_im_template` (
  `id` varchar(64) NOT NULL,
  `account_id` varchar(64) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_delete` int DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `msg_ma_template` (
  `id` varchar(64) NOT NULL,
  `title` varchar(64) DEFAULT NULL,
  `template_key` varchar(200) DEFAULT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_template_key` (`template_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `msg_ma_param` (
  `id` varchar(64) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `template_key` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_template_key` (`template_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `msg_mission_log` (
  `id` varchar(64) NOT NULL,
  `from_user` varchar(64) DEFAULT NULL,
  `to_user` varchar(64) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `type` int DEFAULT NULL,
  `content` text DEFAULT NULL,
  `form` int DEFAULT NULL,
  `business_id` varchar(64) DEFAULT NULL,
  `is_delete` int DEFAULT 0,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_to_user_status_type` (`to_user`, `status`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
