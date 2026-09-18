package cn.poria.base.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ReservePushDto {
    @ExcelProperty("org_id")
    private String orgId;

    @ExcelProperty("name")
    private String orgName;

    @ExcelProperty("姓名")
    private String patientName;

    @ExcelProperty("手机号码")
    private String mobile;

    @ExcelProperty("身份证号码")
    private String idCard;

    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("项目名称")
    private String itemName;

    @ExcelProperty("报告文件")
    private String reportUrl;

    @ExcelProperty("报告路径")
    private String reportU;

    @ExcelProperty("报告时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String reportTime;

    @ExcelProperty("报告数据")
    private String reportInfo;

    @ExcelProperty("预约筛查时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String reserveTime;

    @ExcelProperty("预约创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ExcelProperty("时间区间")
    private String reserveTimeRange;

    @ExcelProperty("签到时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String signTime;

    @ExcelProperty("条码号")
    private String thirdId;

    @ExcelProperty("签到人")
    private String signInPerson;

    @ExcelProperty("项目类型")
    private Integer itemType;

    @ExcelProperty("检查项目 ID")
    private String itemId;

    @ExcelProperty("检查项目顺序")
    private Integer itemSort;

    @ExcelProperty("新增项目医生")
    private String staffId;

    @ExcelProperty("检查医生ID")
    private String inspectStaff;

    @ExcelProperty("报告医生ID")
    private String reportStaff;

    @ExcelProperty("检查创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String itemCreateTime;

    @ExcelProperty("异常结果")
    private Integer abnormalResult;

    @ExcelProperty("开始筛查时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String scrBeginTime;

    @ExcelProperty("质控状态")
    private Integer qualityStatus;

    @ExcelProperty("检查完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String scrEndTime;

    @ExcelProperty("审阅状态（0未审 1已审）")
    private Integer readStatus;

    @ExcelProperty("审阅医护ID")
    private String readBy;

    @ExcelProperty("文件名称")
    private String fileName;

    @ExcelProperty("项目状态")
    private Integer ItemStatus;

    @ExcelProperty("随访年限")
    private Integer followYear;

    @ExcelProperty("性别")
    private String sex;
}

