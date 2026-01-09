package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.user.entity.ConsultationGroupMember;
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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConsultationGroup", description = "会诊组")
public class ConsultationPageGroup extends Entity<Long> {


    @ApiModelProperty(value = "医助ID")
    private Long nurseId;

    @ApiModelProperty(value = "会诊组名字")
    private String groupName;


    @ApiModelProperty(value = "持续时间(分钟)")
    @Excel(name = "持续时间")
    private Integer continued;

    @ApiModelProperty(value = "状态")
    @TableField(value = "consultation_status")
    @Excel(name = "持续时间")
    private String consultationStatus;


}
