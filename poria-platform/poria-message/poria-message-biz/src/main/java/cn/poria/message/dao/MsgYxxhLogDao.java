package cn.poria.message.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import cn.poria.message.entity.MsgYxxhLog;

/**
 * 云信小号通话记录(MsgYxxhLog)表数据库访问层
 *
 * @author wuyifan
 * @since 2024-03-26 10:23:00
 */
@Mapper
public interface MsgYxxhLogDao extends BaseMapper<MsgYxxhLog> {

}

