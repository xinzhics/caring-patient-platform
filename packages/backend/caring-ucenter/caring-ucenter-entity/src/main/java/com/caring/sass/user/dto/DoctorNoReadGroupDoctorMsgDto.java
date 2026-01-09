package com.caring.sass.user.dto;

import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DoctorGroup
 * @Description 记录医生对小组中哪些医生设置了 不看他患者的消息
 * @Author yangShuai
 * @Date 2021/9/14 15:26
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ApiModel(value = "DoctorNoReadGroupDoctorMsgDto", description = "医生是否查看小组其他医生消息状态")
@AllArgsConstructor
public class DoctorNoReadGroupDoctorMsgDto extends Entity<Long> {

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "医生名字")
    private String name;

    @ApiModelProperty(value = "是否查看消息(医生进入小组后，默认查看小组所有医生患者的消息)")
    private Boolean readMsgStatus;


}
