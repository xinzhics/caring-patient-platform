package com.caring.sass.user.dto;

import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName DoctorAudit
 * @Description
 * @Author yangShuai
 * @Date 2022/2/22 11:13
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "DoctorAuditPageDTO", description = "医生审核表")
@AllArgsConstructor
public class DoctorAuditPageDTO extends Entity<Long> {


    @ApiModelProperty(value = "医生名字")
    @Length(max = 32, message = "医生名字长度不能超过32")
    private String name;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "医助")
    @Length(max = 100, message = "医助长度不能超过100")
    private String nursingName;

    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    private String mobile;

    @ApiModelProperty(value = "医院名称")
    @Length(max = 50, message = "医院名称长度不能超过50")
    private String hospitalName;

    @ApiModelProperty(value = "审核排序, 已审核 1，未审核 0，审核失败 2")
    private Integer auditSort;

    @ApiModelProperty(value = "审核状态, pass_through 通过， reject 拒绝")
    private String auditStatus;


}
