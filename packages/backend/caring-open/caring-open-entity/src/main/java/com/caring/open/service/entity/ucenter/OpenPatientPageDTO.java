package com.caring.open.service.entity.ucenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;


/**
 * <p>
 * 实体类
 * 患者表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "OpenPatientPageDTO", description = "开放平台患者分页查询参数")
public class OpenPatientPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 50, message = "手机号码长度不能超过50")
    private String mobile;

    /**
     * 会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)
     */
    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)")
    private Integer status;

    /**
     * 诊断类型名称
     */
    @ApiModelProperty(value = "诊断类型名称")
    @Length(max = 255, message = "诊断类型名称长度不能超过255")
    private String diagnosisName;

    /**
     * 微信openid
     */
    @ApiModelProperty(value = "微信openid")
    @Length(max = 32, message = "微信openid长度不能超过32")
    private String openId;

    /**
     * 微信union_id
     */
    @ApiModelProperty(value = "微信union_id")
    @Length(max = 32, message = "微信union_id长度不能超过32")
    private String unionId;

    /**
     * 医院名称
     */
    @ApiModelProperty(value = "医院名称")
    @Length(max = 100, message = "医院名称长度不能超过100")
    private String hospitalName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Length(max = 50, message = "身份证号长度不能超过50")
    private String certificateNo;
}
