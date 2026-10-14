-- 在 UPMS 数据库执行，登记消息中心菜单和按钮权限，可重复执行。
-- 所有现有角色获得消息查看入口；按钮权限由菜单权限管理授予系统管理员。
START TRANSACTION;

INSERT INTO sys_menu (name, permission, parent_id, icon, path, sort, type, keep_alive, create_time, update_time, del_flag, platform)
SELECT '消息中心', '', 0, 'message', '/message', 30, '0', '0', NOW(), NOW(), '0', 'admin'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE platform = 'admin' AND del_flag = '0' AND (path = '/message' OR name = '消息中心')
);

SET @poria_message_root = (
    SELECT menu_id FROM sys_menu
    WHERE platform = 'admin' AND del_flag = '0' AND (path = '/message' OR name = '消息中心')
    ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (name, permission, parent_id, icon, path, sort, type, keep_alive, create_time, update_time, del_flag, platform)
SELECT '站内信', '', @poria_message_root, 'message', '/message/station-letter/index', 10, '0', '0', NOW(), NOW(), '0', 'admin'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE platform = 'admin' AND del_flag = '0' AND path = '/message/station-letter/index'
);

INSERT INTO sys_menu (name, permission, parent_id, icon, path, sort, type, keep_alive, create_time, update_time, del_flag, platform)
SELECT '公示公告', '', @poria_message_root, 'bell', '/message/announcement/index', 20, '0', '0', NOW(), NOW(), '0', 'admin'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE platform = 'admin' AND del_flag = '0' AND path = '/message/announcement/index'
);

UPDATE sys_menu SET parent_id = @poria_message_root, update_time = NOW()
WHERE platform = 'admin' AND del_flag = '0'
  AND path IN ('/message/station-letter/index', '/message/announcement/index');

SET @poria_station_menu = (
    SELECT menu_id FROM sys_menu WHERE platform = 'admin' AND del_flag = '0' AND path = '/message/station-letter/index'
    ORDER BY menu_id LIMIT 1
);
SET @poria_announcement_menu = (
    SELECT menu_id FROM sys_menu WHERE platform = 'admin' AND del_flag = '0' AND path = '/message/announcement/index'
    ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (name, permission, parent_id, icon, path, sort, type, keep_alive, create_time, update_time, del_flag, platform)
SELECT actions.name, actions.permission, actions.parent_id, '', '', actions.sort, '1', '0', NOW(), NOW(), '0', 'admin'
FROM (
    SELECT '发送站内信' AS name, 'message_station_add' AS permission, @poria_station_menu AS parent_id, 10 AS sort
    UNION ALL SELECT '新增公告', 'message_announcement_add', @poria_announcement_menu, 10
    UNION ALL SELECT '编辑及发送公告', 'message_announcement_edit', @poria_announcement_menu, 20
    UNION ALL SELECT '删除公告', 'message_announcement_del', @poria_announcement_menu, 30
) AS actions
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu existing
    WHERE existing.platform = 'admin' AND existing.del_flag = '0' AND existing.permission = actions.permission
);

UPDATE sys_menu
SET parent_id = CASE WHEN permission = 'message_station_add' THEN @poria_station_menu ELSE @poria_announcement_menu END,
    update_time = NOW()
WHERE platform = 'admin' AND del_flag = '0'
  AND permission IN ('message_station_add', 'message_announcement_add', 'message_announcement_edit', 'message_announcement_del');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT roles.role_id, menus.menu_id
FROM sys_role roles
CROSS JOIN sys_menu menus
WHERE roles.del_flag = '0' AND menus.platform = 'admin' AND menus.del_flag = '0'
  AND menus.menu_id IN (@poria_message_root, @poria_station_menu, @poria_announcement_menu)
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu granted WHERE granted.role_id = roles.role_id AND granted.menu_id = menus.menu_id
  );

COMMIT;
