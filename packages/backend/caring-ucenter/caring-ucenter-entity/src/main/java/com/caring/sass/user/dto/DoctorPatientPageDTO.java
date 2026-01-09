package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AppPatientPageDTO", description = "app患者列表")
public class DoctorPatientPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "维度: all, 或者不传")
    private String dimension;

    @ApiModelProperty(value = "真实姓名")
    @Length(max = 180, message = "真实姓名长度不能超过180")
    private String name;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "诊断类型ID")
    private String diagnosisId;

    @ApiModelProperty(value = "会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)")
    private Integer status;

    @ApiModelProperty(value = "拼音字母大写")
    private String nameFirstLetter;

    @ApiModelProperty(value = "标签ID")
    private Long tagId;

}
