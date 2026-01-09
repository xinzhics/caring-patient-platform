package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName MeetingGroup
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 9:42
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConsultationSaveGroup", description = "会诊组")
public class ConsultationUpdateGroup extends Entity<Long> {


    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nurse_id", condition = EQUAL)
    @Excel(name = "医助ID")
    private Long nurseId;

    @ApiModelProperty(value = "会诊组名字")
    @Length(max = 255, message = "会议组名字长度不能超过20")
    @Excel(name = "会议组名字")
    private String groupName;

    @ApiModelProperty(value = "会诊组描述")
    @Length(max = 200, message = "会诊组描述长度不能超过200")
    @Excel(name = "会议组名字")
    private String groupDesc;


    @ApiModelProperty(value = "状态(processing, finish)")
    private String consultationStatus;

    @ApiModelProperty(value = "患者ID")
    private List<Long> patientIds;


}
