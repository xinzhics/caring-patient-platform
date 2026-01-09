package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName NursingPlanPatientBaseInfo
 * @Description
 * @Author yangShuai
 * @Date 2021/9/1 14:05
 * @Version 1.0
 */
@Data
public class NursingPlanPatientBaseInfoDTO {



    private Long id;


    private String name;
    /**
     * 微信openid
     */
    @ApiModelProperty(value = "微信openid")
    private String openId;

    /**
     * 随访计划完成次数
     */
    @ApiModelProperty(value = "随访计划完成次数")
    private Integer examineCount;
    /**
     * 随访计划开始时间
     */
    @ApiModelProperty(value = "随访计划开始时间")
    private LocalDateTime nursingTime;

    @ApiModelProperty(value = "入组时间")
    private LocalDateTime completeEnterGroupTime;

    @ApiModelProperty(value = "是否完成入组信息（0：否  1：是）")
    private Integer isCompleteEnterGroup;

    @ApiModelProperty(value = "患者的机构code")
    private String classCode;


    @ApiModelProperty(value = "患者的机构Id")
    private Long organId;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "医助ID")
    private Long serviceAdvisorId;

    private String avatar;

    private String doctorName;

    private String imAccount;

    private Boolean defaultDoctorPatient;

    @ApiModelProperty(value = "患者手机号")
    private String mobile;

}
