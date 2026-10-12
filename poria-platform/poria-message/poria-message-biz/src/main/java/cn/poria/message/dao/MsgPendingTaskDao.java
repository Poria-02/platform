package cn.poria.message.dao;

import cn.poria.message.entity.MsgPendingTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代办任务(MsgPendingTask)表数据库访问层
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@Mapper
public interface MsgPendingTaskDao extends BaseMapper<MsgPendingTask> {

}
