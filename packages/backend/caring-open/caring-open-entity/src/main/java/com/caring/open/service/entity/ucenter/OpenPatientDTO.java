package com.caring.open.service.entity.ucenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 开放平台患者信息
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
@ApiModel(value = "OpenPatientDTO", description = "开放平台患者信息")
public class OpenPatientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long caringId;

    /**
     * 医助名称
     */
    @ApiModelProperty(value = "医助名称")
    private String serviceAdvisorName;

    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "微信昵称")
    private String nickName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String mobile;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Boolean sex;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    private String name;

    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    private String birthday;

    /**
     * 小组名
     */
    @ApiModelProperty(value = "小组名")
    private String groupName;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    private String doctorName;

    /**
     * 会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)
     */
    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)")
    private Integer status;

    /**
     * 诊断类型名称
     */
    @ApiModelProperty(value = "诊断类型名称")
    private String diagnosisName;

    /**
     * 微信union_id
     */
    @ApiModelProperty(value = "微信union_id")
    private String unionId;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "")
    private String remark;


    /**
     * 医院名称
     */
    @ApiModelProperty(value = "医院名称")
    private String hospitalName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String certificateNo;


    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer height;
    /**
     * 体重
     */
    @ApiModelProperty(value = "体重")
    private Integer weight;

    /**
     * 所属机构名称
     */
    @ApiModelProperty(value = "所属机构名称")
    private String organName;

    /**
     * 入组时间
     */
    @ApiModelProperty(value = "入组时间")
    private LocalDateTime completeEnterGroupTime;

    @ApiModelProperty(value = "最后修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
