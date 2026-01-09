package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 医生表
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
@ApiModel(value = "DoctorPageDTO", description = "医生表")
public class DoctorPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生名字
     */
    @ApiModelProperty(value = "医生名字")
    @Length(max = 32, message = "医生名字长度不能超过32")
    private String name;
    /**
     * 小组ID
     */
    @ApiModelProperty(value = "小组ID")
    private Long groupId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 100, message = "备注长度不能超过100")
    private String remarks;
    /**
     * 小组名字
     */
    @ApiModelProperty(value = "小组名字")
    @Length(max = 32, message = "小组名字长度不能超过32")
    private String groupName;
    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    private Long nursingId;
    /**
     * 医助
     */
    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    private String nursingName;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @Length(max = 32, message = "机构ID长度不能超过32")
    private Long organId;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @Length(max = 32, message = "省长度不能超过32")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    @Length(max = 32, message = "市长度不能超过32")
    private String city;
    /**
     * 医院
     */
    @ApiModelProperty(value = "医院")
    private Long hospitalId;
    /**
     * 科室
     */
    @ApiModelProperty(value = "科室")
    @Length(max = 32, message = "科室长度不能超过32")
    private String departmentId;
    /**
     * 职称
     */
    @ApiModelProperty(value = "职称")
    @Length(max = 32, message = "职称长度不能超过32")
    private String title;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    private String mobile;
    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Length(max = 250, message = "头像长度不能超过250")
    private String avatar;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Length(max = 50, message = "昵称长度不能超过50")
    private String nickName;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Length(max = 20, message = "身份证号长度不能超过20")
    private String idCard;
    /**
     * 身份证正面照
     */
    @ApiModelProperty(value = "身份证正面照")
    @Length(max = 500, message = "身份证正面照长度不能超过500")
    private String idCardImgF;
    /**
     * 身份证背面照
     */
    @ApiModelProperty(value = "身份证背面照")
    @Length(max = 500, message = "身份证背面照长度不能超过500")
    private String idCardImgB;
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
     * 环信账号
     */
    @ApiModelProperty(value = "环信账号")
    @Length(max = 255, message = "环信账号长度不能超过255")
    private String imAccount;

    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    private String hospitalName;
    /**
     * 患者关注码
     */
    @ApiModelProperty(value = "患者关注码")
    @Length(max = 1000, message = "患者关注码长度不能超过1000")
    private String qrCode;
    /**
     * 医生名片 跟患者关住码一样的
     */
    @ApiModelProperty(value = "医生名片 跟患者关住码一样的")
    @Length(max = 1000, message = "医生名片 跟患者关住码一样的长度不能超过1000")
    private String businessCardQrCode;
    /**
     * 科室名称
     */
    @ApiModelProperty(value = "科室名称")
    @Length(max = 100, message = "科室名称长度不能超过100")
    private String deptartmentName;
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
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    private String birthday;

    /**
     * 性别 0:男 1：女
     */
    @ApiModelProperty(value = "性别 0:男 1：女")
    private Integer sex;

    @ApiModelProperty(value = "微信关注状态 null 或 0 未关注， 1 关注, 2 取消关注, 3 退出登录")
    private Integer wxStatus;

    @ApiModelProperty(value = "不需要统计患者")
    private Boolean noNeedCountPatient;

    @ApiModelProperty(value = "当前用户类型")
    private String currentUserType;

    @ApiModelProperty(value = "内置医生：0否，1是")
    private Integer buildIn;

}
