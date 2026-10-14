-- 在 UPMS 数据库执行，添加待办任务字典及 PendingTaskType 枚举对应的字典项。
-- 仅补充缺失的有效记录，可重复执行。
START TRANSACTION;

INSERT INTO sys_dict (type, description, create_time, update_time, `system`, remarks, del_flag)
SELECT 'pending_task_type', '待办任务', NOW(), NOW(), '0', '对应 PendingTaskType 枚举', '0'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict WHERE type = 'pending_task_type' AND del_flag = '0'
);

SET @poria_pending_task_dict = (
    SELECT id FROM sys_dict
    WHERE type = 'pending_task_type' AND del_flag = '0'
    ORDER BY id LIMIT 1
);

INSERT INTO sys_dict_item (dict_id, value, label, type, description, sort, create_time, update_time, remarks, del_flag)
SELECT @poria_pending_task_dict, items.value, items.label, 'pending_task_type', items.label,
       items.sort, NOW(), NOW(), '', '0'
FROM (
    SELECT '0' AS value, '占位' AS label, 0 AS sort
    UNION ALL SELECT '99', '站内信', 1
    UNION ALL SELECT '98', '公示公告', 2
) AS items
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_item existing
    WHERE existing.type = 'pending_task_type' AND existing.value = items.value AND existing.del_flag = '0'
);

COMMIT;
