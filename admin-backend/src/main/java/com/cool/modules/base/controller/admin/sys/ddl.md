-- cool.base_sys_conf definition

CREATE TABLE `base_sys_conf` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`c_key` varchar(255) NOT NULL COMMENT '配置键',
`c_value` text NOT NULL COMMENT '值',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_base_sys_conf_c_key` (`c_key`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- cool.base_sys_department definition

CREATE TABLE `base_sys_department` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`name` varchar(255) NOT NULL COMMENT '部门名称',
`parent_id` bigint DEFAULT NULL COMMENT '上级部门ID',
`order_num` int DEFAULT '0' COMMENT '排序',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统部门';

-- cool.base_sys_log definition

CREATE TABLE `base_sys_log` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`user_id` bigint DEFAULT NULL COMMENT '用户ID',
`action` varchar(1000) DEFAULT NULL COMMENT '行为',
`ip` varchar(50) DEFAULT NULL COMMENT 'IP',
`params` json DEFAULT NULL COMMENT '参数',
PRIMARY KEY (`id`),
KEY `auto_idx_base_sys_log_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志表';

-- cool.base_sys_menu definition

CREATE TABLE `base_sys_menu` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`parent_id` bigint DEFAULT NULL COMMENT '父菜单ID',
`name` varchar(255) DEFAULT NULL COMMENT '菜单名称',
`perms` text COMMENT '权限',
`type` int DEFAULT '0' COMMENT '类型 0：目录 1：菜单 2：按钮',
`icon` varchar(255) DEFAULT NULL COMMENT '图标',
`order_num` int DEFAULT '0' COMMENT '排序',
`router` varchar(255) DEFAULT NULL COMMENT '菜单地址',
`view_path` varchar(255) DEFAULT NULL COMMENT '视图地址',
`keep_alive` bit(1) DEFAULT b'1' COMMENT '路由缓存',
`is_show` bit(1) DEFAULT b'1' COMMENT '是否显示',
PRIMARY KEY (`id`),
KEY `auto_idx_base_sys_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- cool.base_sys_param definition

CREATE TABLE `base_sys_param` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`key_name` varchar(255) NOT NULL COMMENT '键',
`name` varchar(255) DEFAULT NULL COMMENT '名称',
`data` text COMMENT '数据',
`data_type` int DEFAULT '0' COMMENT '数据类型 0:字符串 1:数组 2:键值对',
`remark` varchar(255) DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`id`),
KEY `auto_idx_base_sys_param_key_name` (`key_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统参数配置';

-- cool.base_sys_role definition

CREATE TABLE `base_sys_role` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`user_id` bigint NOT NULL COMMENT '用户ID',
`name` varchar(255) NOT NULL COMMENT '名称',
`label` varchar(255) NOT NULL COMMENT '角色标签',
`remark` varchar(255) DEFAULT NULL COMMENT '备注',
`relevance` int DEFAULT '1' COMMENT '数据权限是否关联上下级',
`menu_id_list` json DEFAULT NULL COMMENT '菜单权限',
`department_id_list` json DEFAULT NULL COMMENT '部门权限',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_base_sys_role_label` (`label`),
KEY `auto_idx_base_sys_role_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- cool.base_sys_role_department definition

CREATE TABLE `base_sys_role_department` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`role_id` bigint DEFAULT NULL COMMENT '角色ID',
`department_id` bigint DEFAULT NULL COMMENT '部门ID',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色部门';

-- cool.base_sys_role_menu definition

CREATE TABLE `base_sys_role_menu` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`menu_id` bigint DEFAULT NULL COMMENT '菜单',
`role_id` bigint DEFAULT NULL COMMENT '角色ID',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色菜单表';

-- cool.base_sys_user definition

CREATE TABLE `base_sys_user` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`department_id` bigint DEFAULT NULL COMMENT '部门ID',
`name` varchar(255) DEFAULT NULL COMMENT '姓名',
`username` varchar(100) NOT NULL COMMENT '用户名',
`password` varchar(255) NOT NULL COMMENT '密码',
`password_v` int DEFAULT '1' COMMENT '密码版本',
`nick_name` varchar(255) NOT NULL COMMENT '昵称',
`head_img` varchar(255) DEFAULT NULL COMMENT '头像',
`phone` varchar(255) DEFAULT NULL COMMENT '手机号',
`email` varchar(255) DEFAULT NULL COMMENT '邮箱',
`remark` varchar(255) DEFAULT NULL COMMENT '备注',
`status` int DEFAULT '1' COMMENT '状态 0:禁用 1：启用',
`socket_id` varchar(255) DEFAULT NULL COMMENT 'socketId',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_base_sys_user_username` (`username`),
KEY `auto_idx_base_sys_user_department_id` (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- cool.base_sys_user_role definition

CREATE TABLE `base_sys_user_role` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`user_id` bigint DEFAULT NULL COMMENT '用户ID',
`role_id` bigint DEFAULT NULL COMMENT '角色ID',
PRIMARY KEY (`id`),
KEY `auto_idx_base_sys_user_role_role_id` (`role_id`),
KEY `auto_idx_base_sys_user_role_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户角色表';

-- cool.dict_info definition

CREATE TABLE `dict_info` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`type_id` bigint NOT NULL COMMENT '类型ID',
`parent_id` bigint DEFAULT NULL COMMENT '父ID',
`name` varchar(255) NOT NULL COMMENT '名称',
`value` varchar(255) DEFAULT NULL COMMENT '值',
`order_num` int DEFAULT '0' COMMENT '排序',
`remark` varchar(255) DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典信息';

-- cool.dict_type definition

CREATE TABLE `dict_type` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`name` varchar(255) NOT NULL COMMENT '名称',
`key` varchar(255) NOT NULL COMMENT '标识',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_dict_type_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型';

-- cool.leaf_alloc definition

CREATE TABLE `leaf_alloc` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`key` varchar(20) NOT NULL COMMENT '业务key ，比如orderId',
`max_id` bigint NOT NULL DEFAULT '1' COMMENT '当前最大id',
`step` int NOT NULL DEFAULT '500' COMMENT '步长',
`description` varchar(255) DEFAULT NULL COMMENT '描述',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_uk_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='唯一id分配';

-- cool.plugin_info definition

CREATE TABLE `plugin_info` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`name` varchar(255) DEFAULT NULL COMMENT '名称',
`description` varchar(255) DEFAULT NULL COMMENT '简介',
`key` varchar(255) DEFAULT NULL COMMENT '实例对象',
`hook` varchar(50) DEFAULT NULL COMMENT 'Hook',
`readme` text COMMENT '描述',
`version` varchar(255) DEFAULT NULL COMMENT '版本',
`logo` text NOT NULL COMMENT 'Logo(base64)',
`author` varchar(255) DEFAULT NULL COMMENT '作者',
`status` int DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
`plugin_json` json NOT NULL COMMENT '插件的plugin.json',
`config` json DEFAULT NULL COMMENT '配置',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_plugin_info_key` (`key`),
KEY `auto_idx_plugin_info_hook` (`hook`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='插件信息';

-- cool.recycle_data definition

CREATE TABLE `recycle_data` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`entity_info` json DEFAULT NULL COMMENT '表信息',
`user_id` bigint NOT NULL COMMENT '操作人',
`data` json DEFAULT NULL COMMENT '被删除的数据',
`url` varchar(255) NOT NULL COMMENT '请求的接口',
`params` json NOT NULL COMMENT '请求参数',
`count` int DEFAULT '1' COMMENT '删除数据条数',
PRIMARY KEY (`id`),
KEY `auto_idx_recycle_data_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据回收站表';

-- cool.space_info definition

CREATE TABLE `space_info` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`url` varchar(255) NOT NULL COMMENT '地址',
`type` varchar(255) NOT NULL COMMENT '类型',
`classify_id` int DEFAULT NULL COMMENT '分类ID',
`file_id` varchar(255) DEFAULT NULL COMMENT '文件id',
`name` varchar(255) DEFAULT NULL COMMENT '文件名',
`size` int DEFAULT NULL COMMENT '文件大小',
`version` bigint DEFAULT '1' COMMENT '文档版本',
`file_path` varchar(255) DEFAULT NULL COMMENT '文件位置',
PRIMARY KEY (`id`),
KEY `auto_idx_space_info_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件空间信息';

-- cool.space_type definition

CREATE TABLE `space_type` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`name` varchar(255) NOT NULL COMMENT '类别名称',
`parent_id` int DEFAULT NULL COMMENT '父分类ID',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片空间信息分类';

-- cool.task_info definition

CREATE TABLE `task_info` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`name` varchar(255) NOT NULL COMMENT '名称',
`job_id` varchar(255) DEFAULT NULL COMMENT '任务ID',
`repeat_count` int DEFAULT NULL COMMENT '最大执行次数 不传为无限次',
`every` int DEFAULT NULL COMMENT '每间隔多少毫秒执行一次 如果cron设置了 这项设置就无效',
`status` int NOT NULL DEFAULT '1' COMMENT '状态 0:停止 1：运行',
`service` varchar(255) DEFAULT NULL COMMENT '服务实例名称',
`task_type` int DEFAULT '0' COMMENT '状态 0:cron 1：时间间隔',
`type` int DEFAULT '0' COMMENT '状态 0:系统 1：用户',
`data` varchar(255) DEFAULT NULL COMMENT '任务数据',
`remark` varchar(255) DEFAULT NULL COMMENT '备注',
`cron` varchar(255) DEFAULT NULL COMMENT 'cron',
`next_run_time` datetime DEFAULT NULL COMMENT '下一次执行时间',
`start_date` datetime DEFAULT NULL COMMENT '开始时间',
`end_date` datetime DEFAULT NULL COMMENT '结束时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务信息';

-- cool.task_log definition

CREATE TABLE `task_log` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`task_id` bigint NOT NULL COMMENT '任务ID',
`status` int DEFAULT '0' COMMENT '状态 0：失败 1：成功',
`detail` text COMMENT '详情',
PRIMARY KEY (`id`),
KEY `auto_idx_task_log_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务日志';

-- cool.user_address definition

CREATE TABLE `user_address` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`user_id` bigint NOT NULL COMMENT '用户ID',
`contact` varchar(255) NOT NULL COMMENT '联系人',
`phone` varchar(11) NOT NULL COMMENT '手机号',
`province` varchar(255) NOT NULL COMMENT '省',
`city` varchar(255) NOT NULL COMMENT '市',
`district` varchar(255) NOT NULL COMMENT '区',
`address` varchar(255) NOT NULL COMMENT '地址',
`is_default` bit(1) DEFAULT b'0' COMMENT '是否默认',
PRIMARY KEY (`id`),
KEY `auto_idx_user_address_phone` (`phone`),
KEY `auto_idx_user_address_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户模块-收货地址';

-- cool.user_info definition

CREATE TABLE `user_info` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`unionid` varchar(255) DEFAULT NULL COMMENT '登录唯一ID',
`avatar_url` varchar(255) DEFAULT NULL COMMENT '头像',
`nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
`phone` varchar(255) DEFAULT NULL COMMENT '手机号',
`gender` int DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
`status` int DEFAULT '1' COMMENT '状态 0-禁用 1-正常 2-已注销',
`login_type` varchar(255) DEFAULT '0' COMMENT '登录方式 0-小程序 1-公众号 2-H5',
`password` varchar(255) DEFAULT NULL COMMENT '密码',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_user_info_phone` (`phone`),
UNIQUE KEY `auto_idx_user_info_unionid` (`unionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息';

-- cool.user_wx definition

CREATE TABLE `user_wx` (
`id` bigint NOT NULL AUTO_INCREMENT,
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`unionid` varchar(255) DEFAULT NULL COMMENT '微信unionid',
`openid` varchar(255) NOT NULL COMMENT '微信openid',
`avatar_url` varchar(255) DEFAULT NULL COMMENT '头像',
`nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
`gender` int DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
`language` varchar(255) DEFAULT NULL COMMENT '语言',
`city` varchar(255) DEFAULT NULL COMMENT '城市',
`province` varchar(255) DEFAULT NULL COMMENT '省份',
`country` varchar(255) DEFAULT NULL COMMENT '国家',
`type` int DEFAULT '0' COMMENT '类型 0-小程序 1-公众号 2-H5 3-APP',
PRIMARY KEY (`id`),
UNIQUE KEY `auto_idx_user_wx_openid` (`openid`),
KEY `auto_idx_user_wx_unionid` (`unionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信用户';
