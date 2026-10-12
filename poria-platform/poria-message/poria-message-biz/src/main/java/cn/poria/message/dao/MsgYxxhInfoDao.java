package cn.poria.message.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import cn.poria.message.entity.MsgYxxhInfo;

/**
 * (MsgYxxhInfo)表数据库访问层
 *
 * @author wuyifan
 * @since 2024-03-25 16:15:24
 */
@Mapper
public interface MsgYxxhInfoDao extends BaseMapper<MsgYxxhInfo> {

}

