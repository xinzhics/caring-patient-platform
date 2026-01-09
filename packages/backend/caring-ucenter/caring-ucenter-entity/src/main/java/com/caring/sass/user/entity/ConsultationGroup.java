package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName MeetingGroup
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 9:42
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_consultation_group")
@ApiModel(value = "ConsultationGroup", description = "会诊组")
@AllArgsConstructor
public class ConsultationGroup extends Entity<Long> {

    /**
     * 会诊进行中
     */
    public static final String PROCESSING = "processing";

    /**
     * 会诊已经结束
     */
    public static final String FINISH = "finish";

    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nurse_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long nurseId;

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    @Excel(name = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "环信群组ID")
    @TableField(value = "im_group_id", condition = EQUAL)
    @Excel(name = "环信群组ID")
    private String imGroupId;

    @ApiModelProperty(value = "成员头像")
    @Length(max = 500, message = "成员头像长度不能超过20")
    @TableField(value = "member_avatar", condition = LIKE)
    @Excel(name = "成员头像")
    private String memberAvatar;

    @ApiModelProperty(value = "会诊组名字")
    @Length(max = 255, message = "会议组名字长度不能超过20")
    @TableField(value = "group_name", condition = LIKE)
    @Excel(name = "会议组名字")
    private String groupName;

    @ApiModelProperty(value = "会诊组描述")
    @Length(max = 255, message = "会议组名字长度不能超过20")
    @TableField(value = "group_desc", condition = LIKE)
    @Excel(name = "会议组名字")
    private String groupDesc;


    @ApiModelProperty(value = "结束时间")
    @Length(max = 20, message = "结束时间长度不能超过20")
    @TableField(value = "end_time")
    @Excel(name = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "持续时间(分钟)")
    @TableField(value = "continued")
    @Excel(name = "持续时间")
    private Long continued;

    @ApiModelProperty(value = "状态(processing, finish)")
    @TableField(value = "consultation_status")
    @Excel(name = "状态")
    private String consultationStatus;

    @ApiModelProperty(value = "会诊入口")
    @TableField(value = "consultation_entrance")
    @Excel(name = "会诊入口")
    private String consultationEntrance;

    @ApiModelProperty(value = "创建人员身份")
    @TableField(value = "create_user_type")
    @Excel(name = "创建人员身份")
    private String createUserType;

    @ApiModelProperty(value = "成员")
    @TableField(exist = false)
    private List<ConsultationGroupMember> consultationGroupMembers;

    @ApiModelProperty(value = "成员状态(-1 不可移除，0 扫码未加入 1 加入状态， 2 已移除， -2 邀请中, -3 拒绝 )")
    @TableField(exist = false)
    private Integer memberStatus;

    @ApiModelProperty(value = "创建人员身份")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "未读消息数量")
    @TableField(exist = false)
    private int noReadMessage;


}
