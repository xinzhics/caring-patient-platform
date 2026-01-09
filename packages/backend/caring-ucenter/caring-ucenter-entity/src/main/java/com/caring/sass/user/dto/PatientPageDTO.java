package com.caring.sass.user.dto;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

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
@ApiModel(value = "PatientPageDTO", description = "患者表")
public class PatientPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    private Long serviceAdvisorId;
    /**
     * 医助名称
     */
    @ApiModelProperty(value = "医助名称")
    @Length(max = 50, message = "医助名称长度不能超过50")
    private String serviceAdvisorName;
    /**
     * 微信昵称
     */
    @ApiModelProperty(value = "微信昵称")
    @Length(max = 180, message = "微信昵称长度不能超过180")
    private String nickName;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 50, message = "手机号码长度不能超过50")
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
    @Length(max = 180, message = "真实姓名长度不能超过180")
    private String name;
    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    private String birthday;
    /**
     * 小组ID
     */
    @ApiModelProperty(value = "小组ID")
    private Long groupId;
    /**
     * 小组名
     */
    @ApiModelProperty(value = "小组名")
    @Length(max = 100, message = "小组名长度不能超过100")
    private String groupName;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long doctorId;
    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @Length(max = 50, message = "医生名称长度不能超过50")
    private String doctorName;
    /**
     * 会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)
     */
    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)")
    private Integer status;
    /**
     * 诊断类型ID
     */
    @ApiModelProperty(value = "诊断类型ID")
    private String diagnosisId;
    /**
     * 诊断类型名称
     */
    @ApiModelProperty(value = "诊断类型名称")
    @Length(max = 255, message = "诊断类型名称长度不能超过255")
    private String diagnosisName;
    /**
     * 所属公众号ID
     */
    @ApiModelProperty(value = "所属公众号ID")
    @Length(max = 32, message = "所属公众号ID长度不能超过32")
    private String wxAppId;
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
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 250, message = "头像长度不能超过250")
    private String avatar;
    /**
     * 环信账号
     */
    @ApiModelProperty(value = "环信账号")
    @Length(max = 255, message = "环信账号长度不能超过255")
    private String imAccount;
    @ApiModelProperty(value = "")
    @Length(max = 180, message = "长度不能超过180")
    private String remark;
    /**
     * 是否完成入组信息（0：否  1：是）
     */
    @ApiModelProperty(value = "是否完成入组信息（0：否  1：是）")
    private Integer isCompleteEnterGroup;
    /**
     * 随访计划完成次数
     */
    @ApiModelProperty(value = "随访计划完成次数")
    private Integer examineCount;
    /**
     * 病分期
     */
    @ApiModelProperty(value = "病分期")
    private Integer ckd;
    /**
     * 随访计划开始时间
     */
    @ApiModelProperty(value = "随访计划开始时间")
    private LocalDateTime nursingTime;
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
    /**
     * 会员标识码
     */
    @ApiModelProperty(value = "会员标识码")
    @Length(max = 32, message = "会员标识码长度不能超过32")
    private String code;
    /**
     * 是否同意 入组协议 （0； 同意 1 ）
     */
    @ApiModelProperty(value = "是否同意 入组协议 （0； 同意 1 ）")
    private Integer agreeAgreement;
    /**
     * 用户累计打卡天数
     */
    @ApiModelProperty(value = "用户累计打卡天数")
    private Integer grandTotalCheck;
    /**
     * 用户连续打卡天数
     */
    @ApiModelProperty(value = "用户连续打卡天数")
    private Integer successiveCheck;
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
     * 所属机构ID
     */
    @ApiModelProperty(value = "所属机构ID")
    private String organId;

    /**
     * 所属机构代码
     */
    @ApiModelProperty(value = "所属机构代码")
    @Length(max = 200, message = "所属机构代码长度不能超过200")
    private String organCode;

    /**
     * 所属机构名称
     */
    @ApiModelProperty(value = "所属机构名称")
    @Length(max = 50, message = "所属机构名称长度不能超过50")
    private String organName;

    /**
     * 入组时间
     */
    @ApiModelProperty(value = "入组时间")
    private LocalDateTime completeEnterGroupTime;

    @ApiModelProperty(value = "随访阶段ID")
    private String followStageId;

    @ApiModelProperty(value = "随访阶段名称")
    private String followStageName;

    @ApiModelProperty(value = "当前用户类型")
    private String currentUserType;


    @ApiModelProperty(value = "租户")
    private String tenantCode;

}
