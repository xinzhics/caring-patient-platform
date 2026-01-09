package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "DoctorUpdateDTO", description = "医生表")
public class DoctorUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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
    @Deprecated
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
     * 医生姓名
     */
    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    private String hospitalName;

    /**
     * 科室名称
     */
    @ApiModelProperty(value = "科室名称")
    @Length(max = 100, message = "科室名称长度不能超过100")
    private String deptartmentName;

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


    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    private Integer imGroupStatus;

    @ApiModelProperty(value = "独立医生 1 为独立医生， 0 为非独立医生")
    private Integer independence;

    @ApiModelProperty(value = "是否接收IM消息 0 不接收, 1接收")
    private Integer imMsgStatus;

    @ApiModelProperty(value = "是否接收Im微信模板消息 0 不接收, 1接收")
    private Integer imWxTemplateStatus;

    @ApiModelProperty(value = "关闭预约(1 为关闭，0 或者 null 为开启 )")
    private Integer closeAppoint;

    @ApiModelProperty(value = "是否AI托管")
    private Boolean aiHosted;

    @ApiModelProperty(value = "医生挂号信息")
    private String registrationInformation;

    /**
     * {@link com.caring.sass.common.constant.DoctorAppointmentReviewEnum}
     */
    @ApiModelProperty(value = "审核开关(无须审核：no_review， 需要审核 need_review)")
    private String appointmentReview;
    /**
     * 扩展内容
     */
    @ApiModelProperty(value = "扩展内容{\"Specialties\":\"\",\"Introduction\":\"\"}专业特长,详细介绍作为一个json对象的属性保存在这个字段。")
    private String extraInfo;
}
