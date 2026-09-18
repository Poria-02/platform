package cn.poria.common.data.conver;

import cn.poria.common.core.util.IdUtils;
import cn.poria.common.core.util.R;
import cn.poria.common.data.conver.dao.ReviewDao;
import cn.poria.common.data.conver.model.ReviewReq;
import cn.poria.common.data.mybatis.MybatisPlusConfiguration;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * author qiaodi
 * date 2025/9/18 09:42
 * version 6.7.3
 * description
 */
@Hidden
@RestController
@AutoConfigureAfter(MybatisPlusConfiguration.class)
public class ReviewController {

    @Autowired(required = false)
    private ReviewDao reviewDao;

    @Inner
    @PostMapping("/inner/review/select")
    public R<String> selectData(@RequestBody ReviewReq req){
        String content = reviewDao.selectData(req.getBusinessId(),req.getTable(),req.getKeyField(), req.getValueField(), req.getCondition());
        return R.ok(content);
    }

    @Inner
    @PostMapping("/inner/review/save")
    public void saveData(@RequestBody ReviewReq req){
        reviewDao.saveData(IdUtils.getSnowflakeId(), req);
    }


    @Inner
    @PostMapping("/inner/review/update")
    public void updateData(@RequestBody ReviewReq req){
        reviewDao.updateData(req);
    }

    @Inner
    @PostMapping("/inner/review/count")
    public R<Integer> count(@RequestBody ReviewReq req){
        return R.ok(reviewDao.count(req));
    }
}
