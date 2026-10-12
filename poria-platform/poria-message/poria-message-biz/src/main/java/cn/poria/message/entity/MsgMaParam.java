package cn.poria.message.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * (MsgMaParam)表实体类
 *
 * @author 张春蕾
 * @since 2024-08-06 19:48:46
 */
@Data
public class MsgMaParam {


 @TableId(type = IdType.ASSIGN_ID)

    private String id;




    private String name;




    private String description;




    private String templateKey;
}
