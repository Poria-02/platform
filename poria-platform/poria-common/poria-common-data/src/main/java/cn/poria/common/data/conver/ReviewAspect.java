package cn.poria.common.data.conver;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.data.conver.annotation.ReviewData;
import cn.poria.common.data.conver.annotation.ReviewDatas;
import cn.poria.common.data.conver.model.ReviewReq;
import cn.poria.common.security.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
/**
 * author qiaodi
 * date 2025/9/18 09:08
 * version 6.7.3
 * description
 *
 * 根据类型判断，
 * 评论内容，
 *   先发后审：
 *    1.保存记录
 *   先审后发：
 *    1.保存记录，设置评论状态为不公开
 *
 * 修改内容
 *  先发后审：
 *      1.查询原纪录，保存到review服务，再执行原流程审核;
 *      2.若不通过，则还原内容（在不通过时的业务中处理）
 *  先审后发：
 *      1.保存审核记录,查询原字段,执行原方法;
 *      2.将待审核字段更新上去(更新在Around执行)
 *
 */
@Aspect
@Component
@Slf4j
public class ReviewAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private Environment environment;

    @Around("@annotation(reviewDatas)")
    public Object invoked(ProceedingJoinPoint point, ReviewDatas reviewDatas) throws Throwable {
        //多个字段循环处理
        if(reviewDatas.value().length == 0){
            log.error("ReviewDatas 未配置转换规则。");
        }
        for(ReviewData reviewData : reviewDatas.value()){
            setting(point, reviewData);
        }
        return point.proceed();
    }

    @Around("@annotation(reviewData)")
    @Transactional(rollbackFor = Exception.class)
    public Object invoked(ProceedingJoinPoint point, ReviewData reviewData) throws Throwable {
        setting(point, reviewData);
        return point.proceed();
    }

    @Nullable
    private void setting(ProceedingJoinPoint point, ReviewData reviewData) throws NoSuchFieldException, IllegalAccessException {
        Object[] args = point.getArgs();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String[] params = parameterNameDiscoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        String id = getModelValue(reviewData.id(), context);
        String machineReview = getModelValue(reviewData.machineReview(), context); //绝对值/参考值
        String reviewMethod = getModelValue(reviewData.reviewMethod(), context); //先发后审/先审后发
        String value = getModelValue(reviewData.value(), context);
        String type = reviewData.type();

        if (ObjectUtils.isEmpty(machineReview) || ObjectUtils.isEmpty(reviewMethod) || ObjectUtils.isEmpty(value)){
            log.info("machineReview:{},reviewMethod:{},value:{}为空，则退出审核",machineReview, reviewMethod, value);
            return ;
        }

        Integer rm = getReviewMethod();
        reviewMethod = rm == 1 ? "review_push" : reviewMethod;

        log.info("返回结果:{},账户是否异常:{}，审核方式reviewMethod :{}", rm, rm == 1, reviewMethod);

        if (type.equals("comment")){ //评论类(新增)

            saveReview(id, machineReview, null, value, reviewData);

            if (reviewMethod.equals("review_push")){
                Class<?> aClass = args[0].getClass();
                Field declaredField = aClass.getDeclaredField("reviewStatus");
                declaredField.setAccessible(true);
                declaredField.set(args[0], 1); //先审后发为不公开
            }

        }else if (type.equals("modify")){
            //查询原字段
            ResponseEntity<R<String>> response = getData(reviewData, id);
            if (response.getBody().isSuccess()){
                R<String> r = response.getBody();

                //先发后审,先审后发: 保存审核记录
                saveReview(id, machineReview, r.getData(), value, reviewData);

                //先审后发：若原字段不为空，则替换
                if (reviewMethod.equals("review_push") && !ObjectUtils.isEmpty(r.getData())){
                    //修改参数为原数据，执行原方法;
                    //待审核通过后更新成审核通过后字段（更新在确认通过时的业务中处理）
                    Class<?> aClass = args[0].getClass();
                    int index = reviewData.value().indexOf(".");
                    Field declaredField = aClass.getDeclaredField(reviewData.value().substring(index + 1));
                    declaredField.setAccessible(true);
                    declaredField.set(args[0], r.getData());
                }
            }
        }
    }


    private Integer getReviewMethod(){
        ReviewReq req = new ReviewReq();
        req.setAccountId(SecurityUtils.getSId());
        HttpHeaders saveRequestHeaders = new HttpHeaders();
        saveRequestHeaders.add(SecurityConstants.FROM, SecurityConstants.FROM_IN);
        HttpEntity<ReviewReq> saveRequestEntity = new HttpEntity<>(req, saveRequestHeaders);
        ResponseEntity<R<Integer>> response = restTemplate.exchange(environment.resolvePlaceholders(ServiceNameConstants.REVIEW_SERVICE) + "/inner/review/count", HttpMethod.POST, saveRequestEntity, new ParameterizedTypeReference<R<Integer>>() {});
        return response.getBody().getData();
    }


    @Nullable
    private String getModelValue(String spel, EvaluationContext context) {
        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, String.class);
    }

    private ResponseEntity<R<String>> getData(ReviewData reviewData, String id) {
        ReviewReq getReq = new ReviewReq(id, reviewData.table(),reviewData.dbKey(), reviewData.dbValue(), reviewData.condition());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(SecurityConstants.FROM, SecurityConstants.FROM_IN);
        HttpEntity<ReviewReq> requestEntity = new HttpEntity<>(getReq, requestHeaders);
        ResponseEntity<R<String>> response = restTemplate.exchange(environment.resolvePlaceholders(reviewData.serviceId()) + "/inner/review/select", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<R<String>>() {});
        return response;
    }

    private void saveReview(String id, String machineReview, String oldValue, String value, ReviewData reviewData) {
        ReviewReq saveReq = new ReviewReq();
        saveReq.setTable(reviewData.table());
        saveReq.setAccountId(SecurityUtils.getSId());
        saveReq.setReviewMethod(reviewData.aliYunService());
        saveReq.setOldContent(oldValue);
        saveReq.setContent(value);
        saveReq.setMachineReview(machineReview);
        saveReq.setOrgId(SecurityUtils.getUser().getOrgId());
        saveReq.setBusinessId(id);
        JSONObject obj = JSONUtil.createObj();
        obj.set("serviceId", reviewData.serviceId());
        obj.set("table", reviewData.table());
        obj.set("dbKey", reviewData.dbKey());
        obj.set("dbValue", reviewData.dbValue());
        saveReq.setData(JSONUtil.toJsonStr(obj));
        HttpHeaders saveRequestHeaders = new HttpHeaders();
        saveRequestHeaders.add(SecurityConstants.FROM, SecurityConstants.FROM_IN);
        HttpEntity<ReviewReq> saveRequestEntity = new HttpEntity<>(saveReq, saveRequestHeaders);
        restTemplate.exchange( environment.resolvePlaceholders(ServiceNameConstants.REVIEW_SERVICE) + "/inner/review/save", HttpMethod.POST, saveRequestEntity, new ParameterizedTypeReference<R<Map<String,String>>>(){});
    }
}
