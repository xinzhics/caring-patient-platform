package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ReferralPageDTO", description = "转诊卡")
public class ReferralCardVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 性别
     */
    @ApiModelProperty(value = "患者性别，0男，1女")
    private Integer patientSex;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "患者年龄")
    private Integer patientAge;

    /**
     * 头像
     */
    @ApiModelProperty(value = "患者头像")
    private String patientAvatar;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    private String doctorName;

    /**
     * 二维码地址
     */
    @ApiModelProperty(value = "二维码地址")
    private String qrUrl;
}
