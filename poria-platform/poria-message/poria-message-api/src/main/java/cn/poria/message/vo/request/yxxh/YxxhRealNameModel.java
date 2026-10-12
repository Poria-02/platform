package cn.poria.message.vo.request.yxxh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YxxhRealNameModel {

    private String mobile;

    private String name;

    private String idCard;

    private String photo;

}
