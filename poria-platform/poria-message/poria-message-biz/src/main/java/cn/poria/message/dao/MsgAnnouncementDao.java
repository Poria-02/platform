package cn.poria.message.dao;

import cn.poria.message.entity.MsgAnnouncement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公示公告(MsgAnnouncement)表数据库访问层
 */
@Mapper
public interface MsgAnnouncementDao extends BaseMapper<MsgAnnouncement> {

}
