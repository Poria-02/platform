package cn.poria.message.util;

import cn.hutool.json.JSONUtil;
import cn.poria.message.vo.request.SmsSend;
import cn.poria.message.vo.response.template.TemplateVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author qd
 * date 2023/8/9 11:02
 * description
 */
public class TemplateParamUtil {

    public static Map<String, String> settingTemplateParam(TemplateVo templateVo, Map<String, String> param) {

        // {"name1":["就诊人","text","患者姓名"],"name4":["医生姓名","text",""]}
        Map<String, Object> map = JSONUtil.parseObj(templateVo.getParamMapping());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            List<String> result = new ArrayList<>();
            for (Object o : (List<?>) v) {
                result.add((String) o);
            }
            // ["医生姓名","text",""]，存在模板数据不包含第三项，如["医生姓名","text"]，则过虑掉不满足的模板参数
            if (result.size() >= 3){
                String templateParam = result.get(2);
                if (StringUtils.isNotBlank(templateParam)){
                    if(param.get(templateParam) != null){
                        param.put(k, param.get(templateParam));
                    }
                }
            }
        }
        return param;
    }
}
